<#
.SYNOPSIS
    Painel de controle para gerenciar o projeto To-Do List (API, Web, Desktop, Android).
.DESCRIPTION
    Este script PowerShell fornece um menu interativo para iniciar, parar, construir e depurar
    os diferentes componentes do projeto. Ele detecta automaticamente o status de cada serviço
    e torna o ambiente de desenvolvimento mais produtivo.
.VERSION
    9.5 - Melhorada a detecção do App Desktop
#>

# Força o uso do protocolo TLS 1.2 para compatibilidade com downloads HTTPS (ex: Maven Wrapper).
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

#==============================================================================
# --- CONFIGURAÇÕES GLOBAIS ---
#==============================================================================

$basePath = $PSScriptRoot
$apiPath = Join-Path $basePath "listadetarefas-api"
$webPath = Join-Path $basePath "listadetarefas-web"
$desktopPath = Join-Path $basePath "listadetarefas-desktop"
$androidPath = Join-Path $basePath "listadetarefas-android"

# --- VALIDAÇÃO DE CAMINHOS ---
$projectPaths = @{ "API" = $apiPath; "Web" = $webPath; "Desktop" = $desktopPath; "Android" = $androidPath }
$pathsAreValid = $true
foreach ($project in $projectPaths.Keys) {
    if (-not (Test-Path $projectPaths[$project])) {
        Write-Host "ERRO: O diretório do projeto '$project' não foi encontrado em '$($projectPaths[$project])'" -ForegroundColor Red
        $pathsAreValid = $false
    }
}
if (-not $pathsAreValid) { Read-Host "`nVerifique os nomes das pastas. Pressione Enter para sair."; exit }

# --- CONFIGURAÇÕES ANDROID ---
$sdkPath = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$emulatorPath = Join-Path $sdkPath "emulator"
$platformToolsPath = Join-Path $sdkPath "platform-tools"
$emulatorName = "Medium_Phone"

# --- CONFIGURAÇÕES DOS ARTEFATOS ---
$apiJar = Join-Path $apiPath "target\listadetarefas-api-0.0.1-SNAPSHOT.jar"
$desktopJar = Join-Path $desktopPath "target\listadetarefas-desktop-1.0-SNAPSHOT.jar"
$androidPackage = "br.com.curso.listadetarefas.android"
$webUrl = "http://localhost:3000"

#==============================================================================
# ATENÇÃO: VERIFIQUE O TÍTULO DA JANELA DO APP DESKTOP
#==============================================================================
# O script detecta se o App Desktop está rodando ao procurar por uma janela
# com o título definido abaixo. Se o status estiver incorreto, verifique o
# título exato da janela da sua aplicação e atualize a variável aqui.
#
# Para descobrir o título exato, execute a aplicação manualmente e depois
# execute este comando no PowerShell:
# Get-Process -Name "java","javaw" | Select-Object MainWindowTitle
#==============================================================================
$desktopWindowTitle = "Minha Lista de Tarefas (Desktop)"


#==============================================================================
# --- FUNÇÕES AUXILIARES ---
#==============================================================================

function Get-ServiceStatus($serviceName) {
    try {
        switch ($serviceName) {
            'api' { if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            'web' { if (Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            'desktop' { if (Get-Process -Name "java", "javaw" -ErrorAction Stop | Where-Object { $_.MainWindowTitle -like "*$desktopWindowTitle*" }) { return "RUNNING" } }
            'android' { if ((& "$platformToolsPath\adb.exe" shell ps) -match $androidPackage) { return "RUNNING" } }
            'emulator' { if ((& "$platformToolsPath\adb.exe" devices) -like "*`tdevice*") { return "RUNNING" } }
        }
    }
    catch { return "STOPPED" }
    return "STOPPED"
}

function Wait-For-AdbDevice {
    param([int]$TimeoutSeconds = 60)
    if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { return $true }
    Write-Host "Aguardando um emulador/dispositivo ficar online..." -ForegroundColor Cyan
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') {
            Write-Host "`nDispositivo detectado." -ForegroundColor Green; $stopwatch.Stop(); Start-Sleep 1; return $true
        }
        Write-Host "." -NoNewline; Start-Sleep 2
    }
    $stopwatch.Stop(); Write-Host "`nTempo esgotado!" -ForegroundColor Red; return $false
}

function Ensure-BuildArtifact {
    param([string]$ArtifactPath, [string]$ProjectPath, [string[]]$BuildCommand, [string]$BuildToolName)
    if (!(Test-Path $ArtifactPath)) {
        $choice = Read-Host "Artefato de build não encontrado em '$ArtifactPath'. Deseja construir agora? (s/n)"
        if ($choice -eq 's') {
            Push-Location $ProjectPath
            Write-Host "Construindo em '$ProjectPath'..." -ForegroundColor Cyan
            if ($BuildToolName -eq "mvnw.cmd" -and -not (Test-Path ".\pom.xml")) {
                Write-Host "ERRO CRÍTICO: 'pom.xml' não encontrado em '$ProjectPath'." -ForegroundColor Red
                Pop-Location; Start-Sleep 3; return $false
            }
            $executableCommand = $null
            if ($BuildToolName -eq "ng") {
                if (Get-Command ng -ErrorAction SilentlyContinue) { $executableCommand = "ng" } 
                else { Write-Host "ERRO: O comando 'ng' (Angular CLI) não foi encontrado." -ForegroundColor Red }
            }
            else {
                if ((Test-Path ".\$BuildToolName") -and (Test-Path ".\.mvn\wrapper")) { $executableCommand = ".\$BuildToolName" }
                elseif (Get-Command mvn -ErrorAction SilentlyContinue) { $executableCommand = "mvn"; Write-Host "AVISO: Usando Maven global ('mvn')." -ForegroundColor Yellow }
                else { Write-Host "ERRO: Nenhuma ferramenta de build do Maven foi encontrada." -ForegroundColor Red }
            }
            if (-not $executableCommand) { Pop-Location; Start-Sleep 2; return $false }
            try { & $executableCommand $BuildCommand *>&1 | ForEach-Object { Write-Host $_ } } catch { Write-Host "`nERRO DE BUILD" -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            if ($LASTEXITCODE -ne 0) { Write-Host "`nERRO DE BUILD (código: $LASTEXITCODE)." -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            Pop-Location
            if (!(Test-Path $ArtifactPath)) { Write-Host "Build concluído, mas o artefato '$ArtifactPath' não foi encontrado." -ForegroundColor Red; Start-Sleep 2; return $false }
        }
        else { Write-Host "Início cancelado." -ForegroundColor Red; Start-Sleep 2; return $false }
    }
    return $true
}

#==============================================================================
# --- FUNÇÕES DE GERENCIAMENTO DE SERVIÇOS ---
#==============================================================================

function Start-Service($serviceName, [switch]$ColdBoot) {
    if ($serviceName -in @('web', 'desktop', 'android')) {
        if ((Get-ServiceStatus 'api') -eq 'STOPPED') {
            $confirm = Read-Host "AVISO: A API está parada. Deseja iniciá-la primeiro? (s/n)"
            if ($confirm -eq 's') { if (-not (Start-Service 'api')) { Write-Host "Falha ao iniciar API." -ForegroundColor Red; Start-Sleep 2; return $false } }
            else { Write-Host "AVISO: '$serviceName' pode não funcionar sem a API." -ForegroundColor Yellow }
        }
    }
    Write-Host "`nTentando iniciar serviço: $serviceName..." -ForegroundColor Yellow
    $commandExecuted = $false
    switch ($serviceName) {
        'api' {
            if (-not (Ensure-BuildArtifact -ArtifactPath $apiJar -ProjectPath $apiPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title API-Backend && java -jar `"`"$apiJar`"`"`"" -WorkingDirectory $apiPath
            $commandExecuted = $true
        }
        'web' {
            if (-not (Ensure-BuildArtifact -ArtifactPath (Join-Path $webPath "dist") -ProjectPath $webPath -BuildCommand "build" -BuildToolName "ng")) { break }
            Push-Location $webPath; Start-Process npx -ArgumentList "serve", "dist\listadetarefas-web\browser"; Pop-Location
            $commandExecuted = $true
        }
        'desktop' {
            if (-not (Ensure-BuildArtifact -ArtifactPath $desktopJar -ProjectPath $desktopPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title App-Desktop && java -jar `"`"$desktopJar`"`"`"" -WorkingDirectory $desktopPath
            $commandExecuted = $true
        }
        'android' {
            if (-not (Wait-For-AdbDevice)) { Write-Host "Nenhum emulador/dispositivo detectado." -ForegroundColor Red; Start-Sleep 2; return $false }
            Write-Host "Criando túnel de rede (adb reverse)..." -ForegroundColor Cyan
            & "$platformToolsPath\adb.exe" reverse tcp:8080 tcp:8080
            Write-Host "Iniciando App Android..."; & "$platformToolsPath\adb.exe" shell am start -n "$androidPackage/$androidPackage.MainActivity"
            $commandExecuted = $true
        }
        'emulator' {
            if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { Write-Host "Emulador já parece estar rodando." -ForegroundColor Green; return $true }
            $arguments = "-avd", $emulatorName
            if ($ColdBoot) { $arguments += "-no-snapshot-load"; Write-Host "Iniciando emulador em modo Cold Boot..." -ForegroundColor Yellow }
            Push-Location $emulatorPath; Start-Process ".\emulator.exe" -ArgumentList $arguments; Pop-Location
            if (Wait-For-AdbDevice) { return $true } else { return $false }
        }
    }
    if (-not $commandExecuted -and $serviceName -ne 'emulator') { Write-Host "Falha no pré-requisito para '$serviceName'." -ForegroundColor Red; Start-Sleep 2; return $false }
    Write-Host "Comando de início enviado. Verificando status..." -ForegroundColor Green
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt 45) {
        if ((Get-ServiceStatus $serviceName) -eq 'RUNNING') { Write-Host "`nServiço '$serviceName' parece estar rodando." -ForegroundColor Green; return $true }
        Write-Host "." -NoNewline; Start-Sleep 2
    }
    if ($serviceName -ne 'emulator') { Write-Host "`nERRO: Serviço '$serviceName' não iniciou corretamente." -ForegroundColor Red; Start-Sleep 2 }
    return $false
}

function Stop-Service($serviceName) {
    Write-Host "`nParando serviço: $serviceName..." -ForegroundColor Yellow
    switch ($serviceName) {
        'api' { $p = Get-NetTCPConnection -LocalPort 8080 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'web' { $p = Get-NetTCPConnection -LocalPort 3000 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'desktop' { Get-Process -Name "java", "javaw" -EA 0 | Where-Object { $_.MainWindowTitle -like "*$desktopWindowTitle*" } | Stop-Process -Force }
        'android' { & "$platformToolsPath\adb.exe" shell am force-stop $androidPackage }
        'emulator' { & "$platformToolsPath\adb.exe" emu kill }
    }
    Write-Host "Comando de parada enviado." -ForegroundColor Green; Start-Sleep 1
}

function Clean-Project {
    Clear-Host; Write-Host "--- LIMPANDO CACHES E BUILDS ---" -ForegroundColor Yellow
    Write-Host "`nLimpando API..." -ForegroundColor Cyan; Push-Location $apiPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Desktop..." -ForegroundColor Cyan; Push-Location $desktopPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Web..." -ForegroundColor Cyan
    $angularCache = Join-Path $webPath ".angular"; $angularDist = Join-Path $webPath "dist"
    if (Test-Path $angularCache) { Remove-Item -Recurse -Force $angularCache }
    if (Test-Path $angularDist) { Remove-Item -Recurse -Force $angularDist }
    Write-Host "`n--- LIMPEZA CONCLUÍDA ---" -ForegroundColor Green; Read-Host "Pressione Enter..."
}

#==============================================================================
# --- FERRAMENTAS DE DEBUG (ANDROID) ---
#==============================================================================

function Invoke-AdbTool($toolName) {
    if (-not (Wait-For-AdbDevice)) { Read-Host "`nOperação ADB cancelada. Pressione Enter..."; return }
    Clear-Host; Write-Host "--- Ferramenta ADB: $toolName ---" -ForegroundColor Yellow
    switch ($toolName) {
        'reset' { & "$platformToolsPath\adb.exe" kill-server; & "$platformToolsPath\adb.exe" start-server }
        'devices' { & "$platformToolsPath\adb.exe" devices }
        'logcat' {
            Write-Host "Iniciando logcat... Feche a nova janela para parar."
            $command = "& `"$platformToolsPath\adb.exe`" logcat '*:S' `"$androidPackage:V`""
            Start-Process powershell -ArgumentList "-NoExit", "-Command", $command; return
        }
        'reverse' {
            & "$platformToolsPath\adb.exe" reverse tcp:8080 tcp:8080
            Write-Host "Verificando túneis:"; & "$platformToolsPath\adb.exe" reverse --list
        }
    }
    Read-Host "`nPressione Enter para voltar ao menu"
}

#==============================================================================
# --- INTERFACE DO USUÁRIO (MENU) ---
#==============================================================================

function Show-Menu {
    Clear-Host
    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host "      PAINEL DE CONTROLE - PROJETO TO-DO LIST      " -ForegroundColor White
    Write-Host "=================================================" -ForegroundColor Cyan
    
    $emulatorStatus = Get-ServiceStatus 'emulator'
    $statuses = @{
        'Emulador'     = $emulatorStatus;
        'API Backend'  = Get-ServiceStatus 'api';
        'Servidor Web' = Get-ServiceStatus 'web';
        'App Desktop'  = Get-ServiceStatus 'desktop';
        'App Android'  = if ($emulatorStatus -eq 'RUNNING') { Get-ServiceStatus 'android' } else { "OFFLINE" }
    }

    Write-Host "`nSTATUS ATUAL:"
    $statuses.GetEnumerator() | ForEach-Object {
        $color = if ($_.Value -eq 'RUNNING') { 'Green' } else { 'Red' }
        Write-Host ("  {0,-15}" -f $_.Name) -NoNewline; Write-Host $_.Value -ForegroundColor $color
    }
    Write-Host "`n--- OPÇÕES ---" -ForegroundColor Yellow
    Write-Host " GERAL                     SERVIÇOS INDIVIDUAIS"
    Write-Host "  9. Iniciar TUDO          1. Iniciar API          5. Iniciar Desktop"
    Write-Host " 10. Parar TUDO             2. Parar API            6. Parar Desktop"
    Write-Host "  L. Limpar Caches         3. Iniciar Web          7. Iniciar App Android"
    Write-Host "                           4. Parar Web            8. Parar App Android"
    Write-Host "-----------------------------------------------------------------"
    Write-Host " FERRAMENTAS ANDROID                               NAVEGAÇÃO"
    Write-Host "  A. Iniciar Emulador      D. Resetar Servidor ADB R. Atualizar Status"
    Write-Host "  B. Parar Emulador        E. Listar Dispositivos  Q. Sair"
    Write-Host "  H. Ligar (Cold Boot)     F. Ver Logs (logcat)"
    Write-Host "  C. Abrir Web no Browser  G. Criar Túnel de Rede`n"
}

#==============================================================================
# --- LÓGICA PRINCIPAL (LOOP DO MENU) ---
#==============================================================================

while ($true) {
    Show-Menu
    $choice = Read-Host "Digite sua opção e pressione Enter"
    switch ($choice.ToLower()) {
        '1' { Start-Service 'api' }
        '2' { Stop-Service 'api' }
        '3' { Start-Service 'web' }
        '4' { Stop-Service 'web' }
        '5' { Start-Service 'desktop' }
        '6' { Stop-Service 'desktop' }
        '7' { Start-Service 'android' }
        '8' { Stop-Service 'android' }
        '9' {
            if ((Get-ServiceStatus 'emulator') -eq 'STOPPED') { if (-not (Start-Service 'emulator')) { Read-Host "Falha ao iniciar Emulador."; continue } }
            if (-not (Start-Service 'api')) { Read-Host "Falha ao iniciar API."; continue }
            Start-Service 'web'; Start-Service 'desktop'; Start-Service 'android'
            Read-Host "`n--- SEQUÊNCIA CONCLUÍDA ---`nPressione Enter..."
        }
        '10' {
            Stop-Service 'android'; Stop-Service 'desktop'; Stop-Service 'web'; Stop-Service 'api'
            if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') {
                if ((Read-Host "Deseja parar o Emulador também? (s/n)") -eq 's') { Stop-Service 'emulator' }
            }
        }
        'a' { Start-Service 'emulator' }
        'b' { Stop-Service 'emulator' }
        'h' { Start-Service 'emulator' -ColdBoot }
        'c' { if ((Get-ServiceStatus 'web') -eq 'RUNNING') { Start-Process $webUrl } else { Write-Host "Servidor web precisa estar rodando." -ForegroundColor Red; Start-Sleep 2 } }
        'd' { Invoke-AdbTool 'reset' }
        'e' { Invoke-AdbTool 'devices' }
        'f' { Invoke-AdbTool 'logcat' }
        'g' { Invoke-AdbTool 'reverse' }
        'l' { Clean-Project }
        'r' { }
        'q' { break }
        default { Write-Host "Opção inválida!" -ForegroundColor Red; Start-Sleep 2 }
    }
}
