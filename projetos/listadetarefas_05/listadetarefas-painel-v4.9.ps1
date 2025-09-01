<#
.SYNOPSIS
    Painel de controle para gerenciar o projeto To-Do List (API, Web, Desktop, Mobile).
.DESCRIPTION
    Este script PowerShell fornece um menu interativo para iniciar, parar, construir e depurar
    os diferentes componentes do projeto. Ele detecta automaticamente o status de cada serviço
    e torna o ambiente de desenvolvimento mais produtivo.
.VERSION
    4.9 - Aprimorada a detecção da janela do App Desktop para maior robustez.
#>

# Força o uso do protocolo TLS 1.2 para compatibilidade com downloads HTTPS (ex: Maven Wrapper).
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

#==============================================================================
# --- CONFIGURAÇÕES GLOBAIS ---
#==============================================================================
$ScriptVersion = "4.9"

$basePath = $PSScriptRoot
$apiPath = Join-Path $basePath "listadetarefas-api"
$webPath = Join-Path $basePath "listadetarefas-web"
$desktopPath = Join-Path $basePath "listadetarefas-desktop"
$mobilePath = Join-Path $basePath "listadetarefas-mobile"

# --- VALIDAÇÃO DE CAMINHOS ---
$projectPaths = @{ "API" = $apiPath; "Web" = $webPath; "Desktop" = $desktopPath; "Mobile" = $mobilePath }
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
$emulatorName = $null
$availableEmulators = @()

# --- CONFIGURAÇÕES DOS ARTEFATOS E URLs ---
$apiJar = Join-Path $apiPath "target\listadetarefas-api-0.0.1-SNAPSHOT.jar"
$desktopJar = Join-Path $desktopPath "target\listadetarefas-desktop-1.0-SNAPSHOT.jar"
$androidPackage = "br.com.curso.listadetarefas.android"
$webUrl = "http://localhost:4200"
$desktopWindowTitle = "Minha Lista de Tarefas (Desktop)"

#==============================================================================
# --- FUNÇÕES DE DETECÇÃO E SELEÇÃO DE EMULADOR ---
#==============================================================================

function Initialize-EmulatorSelection {
    Write-Host "Detectando emuladores instalados..." -ForegroundColor Cyan
    try {
        $outputLines = & "$emulatorPath\emulator.exe" -list-avds
        $validEmulators = New-Object System.Collections.Generic.List[string]
        foreach ($line in $outputLines) {
            $trimmedLine = $line.Trim()
            if ($trimmedLine.Length -gt 0 -and -not $trimmedLine.StartsWith("INFO")) {
                $validEmulators.Add($trimmedLine)
            }
        }
        $script:availableEmulators = $validEmulators.ToArray()
    } catch {
        Write-Host "AVISO: Não foi possível executar o comando do emulador. Verifique o caminho do SDK." -ForegroundColor Yellow
        $script:availableEmulators = @()
    }

    if ($availableEmulators.Count -ge 1) {
        $script:emulatorName = $availableEmulators[0]
        Write-Host "Emulador '$emulatorName' selecionado como padrão." -ForegroundColor Green
        if ($availableEmulators.Count -gt 1) { Write-Host "Use a opção 'S' no menu para selecionar outro." }
    } else {
        Write-Host "AVISO: Nenhum emulador (AVD) foi encontrado." -ForegroundColor Yellow
    }
    Start-Sleep -Seconds 1
}

function Select-Emulator {
    Clear-Host
    Write-Host "--- SELECIONAR EMULADOR ANDROID ---" -ForegroundColor Yellow
    if ($availableEmulators.Count -eq 0) { Read-Host "Nenhum emulador detectado. Pressione Enter..."; return }

    Write-Host "`nEmuladores disponíveis:"
    for ($i = 0; $i -lt $availableEmulators.Count; $i++) {
        $indicator = if ($availableEmulators[$i] -eq $emulatorName) { "[SELECIONADO]" } else { "" }
        Write-Host "  $($i + 1). $($availableEmulators[$i]) $indicator"
    }

    $choice = Read-Host "`nDigite o número do emulador (ou Enter para cancelar)"
    if ($choice -eq "") { return }
    $index = $choice -as [int]

    if ($index -and $index -ge 1 -and $index -le $availableEmulators.Count) {
        $script:emulatorName = $availableEmulators[$index - 1]
        Write-Host "Emulador '$emulatorName' selecionado." -ForegroundColor Green
    } else {
        Write-Host "Seleção inválida." -ForegroundColor Red
    }
    Start-Sleep -Seconds 2
}

#==============================================================================
# --- FUNÇÃO PARA BUILD ANDROID ---
#==============================================================================
function Build-And-Install-Android {
    if (-not (Wait-For-AdbDevice)) { Read-Host "`nOperação cancelada. Pressione Enter..."; return }
    Clear-Host
    Write-Host "--- CONSTRUINDO E INSTALANDO APP MOBILE ---" -ForegroundColor Yellow
    Write-Host "`nEste processo pode levar alguns minutos..." -ForegroundColor Cyan
    Push-Location $mobilePath
    try {
        & ".\gradlew.bat" installDebug *>&1 | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0) { throw "O build do Gradle falhou (código: $LASTEXITCODE)." }
        Write-Host "`nApp Mobile instalado com sucesso no dispositivo!" -ForegroundColor Green
    } catch {
        Write-Host "`nERRO ao construir/instalar o App Mobile: $($_.Exception.Message)" -ForegroundColor Red
    } finally {
        Pop-Location
        Read-Host "`nPressione Enter para voltar ao menu."
    }
}

#==============================================================================
# --- FUNÇÕES DE GERENCIAMENTO DE SERVIÇOS ---
#==============================================================================

function Get-ServiceStatus($serviceName) {
    try {
        switch ($serviceName) {
            'api'     { if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            'web'     { if (Get-NetTCPConnection -LocalPort 4200 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            # --- CORREÇÃO AQUI ---
            # Comando mais robusto para encontrar a janela, focando apenas em processos que TÊM uma janela principal.
            'desktop' { if (Get-Process -Name "java", "javaw" -ErrorAction Stop | Where-Object { $_.MainWindowTitle -eq $desktopWindowTitle }) { return "RUNNING" } }
            'android' { if ((& "$platformToolsPath\adb.exe" shell ps) -match $androidPackage) { return "RUNNING" } }
            'emulator'{ if ((& "$platformToolsPath\adb.exe" devices) -like "*`tdevice*") { return "RUNNING" } }
        }
    }
    catch { return "STOPPED" }
    return "STOPPED"
}

function Wait-For-AdbDevice {
    param([int]$TimeoutSeconds = 90)
    if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { return $true }
    Write-Host "Aguardando o emulador/dispositivo ficar online..." -ForegroundColor Cyan
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
    param([string]$ArtifactPath, [string]$ProjectPath, [string[]]$BuildCommand)
    if (!(Test-Path $ArtifactPath)) {
        $choice = Read-Host "Artefato de build não encontrado em '$ArtifactPath'. Deseja construir agora? (s/n)"
        if ($choice -eq 's') {
            Push-Location $ProjectPath
            Write-Host "Construindo em '$ProjectPath'..." -ForegroundColor Cyan
            try { & ".\mvnw.cmd" $BuildCommand *>&1 | ForEach-Object { Write-Host $_ } } catch { Write-Host "`nERRO DE BUILD" -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            if ($LASTEXITCODE -ne 0) { Write-Host "`nERRO DE BUILD (código: $LASTEXITCODE)." -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            Pop-Location
            if (!(Test-Path $ArtifactPath)) { Write-Host "Build concluído, mas o artefato '$ArtifactPath' não foi encontrado." -ForegroundColor Red; Start-Sleep 2; return $false }
        } else { Write-Host "Início cancelado." -ForegroundColor Red; Start-Sleep 2; return $false }
    }
    return $true
}

function Start-Service($serviceName, [switch]$ColdBoot, [switch]$FixDns) {
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
            if (-not (Ensure-BuildArtifact -ArtifactPath $apiJar -ProjectPath $apiPath -BuildCommand @("clean", "package"))) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title API-Backend && java -jar `"`"$apiJar`"`"`"" -WorkingDirectory $apiPath
            $commandExecuted = $true
        }
        'web' {
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title Frontend-Web && ng serve --open`"" -WorkingDirectory $webPath
            $commandExecuted = $true
        }
        'desktop' {
            if (-not (Ensure-BuildArtifact -ArtifactPath $desktopJar -ProjectPath $desktopPath -BuildCommand @("clean", "package"))) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title App-Desktop && java -jar `"`"$desktopJar`"`"`"" -WorkingDirectory $desktopPath
            # --- CORREÇÃO AQUI ---
            # Adiciona uma pausa para dar tempo ao processo Java de criar a janela.
            Start-Sleep -Seconds 3
            $commandExecuted = $true
        }
        'android' {
            if (-not (Wait-For-AdbDevice)) { Write-Host "Nenhum emulador/dispositivo detectado." -ForegroundColor Red; Start-Sleep 2; return $false }
            Write-Host "Criando túnel de rede (adb reverse)..." -ForegroundColor Cyan
            & "$platformToolsPath\adb.exe" reverse tcp:8080 tcp:8080
            Write-Host "Iniciando App Mobile..."; & "$platformToolsPath\adb.exe" shell am start -n "$androidPackage/$androidPackage.MainActivity"
            $commandExecuted = $true
        }
        'emulator' {
            if (-not $emulatorName) {
                Write-Host "Nenhum emulador está selecionado." -ForegroundColor Yellow
                Select-Emulator
                if (-not $emulatorName) { Read-Host "Operação cancelada. Pressione Enter..."; return $false }
            }
            if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { Write-Host "Emulador já parece estar rodando." -ForegroundColor Green; return $true }
            
            $arguments = "-avd", $emulatorName
            if ($ColdBoot) { 
                $arguments += "-no-snapshot-load"
                Write-Host "Iniciando emulador em modo Cold Boot..." -ForegroundColor Yellow 
            }
            if ($FixDns) {
                $arguments += "-dns-server", "8.8.8.8"
                Write-Host "Iniciando emulador com DNS Fixo (8.8.8.8)..." -ForegroundColor Yellow
            }

            Push-Location $emulatorPath; Start-Process ".\emulator.exe" -ArgumentList $arguments; Pop-Location
            if (Wait-For-AdbDevice) { return $true } else { return $false }
        }
    }
    if (-not $commandExecuted -and $serviceName -ne 'emulator') { Write-Host "Falha no pré-requisito para '$serviceName'." -ForegroundColor Red; Start-Sleep 2; return $false }
    Write-Host "Comando de início enviado. Verificando status..." -ForegroundColor Green
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt 20) { # Reduzido o tempo de espera geral
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
        'web' { $p = Get-NetTCPConnection -LocalPort 4200 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'desktop' { Get-Process -Name "java", "javaw" -EA 0 | Where-Object { $_.MainWindowTitle -eq $desktopWindowTitle } | Stop-Process -Force }
        'android' { & "$platformToolsPath\adb.exe" shell am force-stop $androidPackage }
        'emulator' { & "$platformToolsPath\adb.exe" emu kill }
    }
    Write-Host "Comando de parada enviado." -ForegroundColor Green; Start-Sleep 1
}

function Clean-Project {
    Clear-Host; Write-Host "--- LIMPANDO CACHES E BUILDS ---" -ForegroundColor Yellow
    Write-Host "`nLimpando API..." -ForegroundColor Cyan; Push-Location $apiPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Desktop..." -ForegroundColor Cyan; Push-Location $desktopPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Web (removendo .angular/ e dist/)..." -ForegroundColor Cyan
    $angularCache = Join-Path $webPath ".angular"; $angularDist = Join-Path $webPath "dist"
    if (Test-Path $angularCache) { Remove-Item -Recurse -Force $angularCache }
    if (Test-Path $angularDist) { Remove-Item -Recurse -Force $angularDist }
    Write-Host "`n--- LIMPEZA CONCLUÍDA ---" -ForegroundColor Green; Read-Host "Pressione Enter..."
}

function Invoke-AdbTool($toolName) {
    if (-not (Wait-For-AdbDevice)) { Read-Host "`nOperação ADB cancelada. Pressione Enter..."; return }
    Clear-Host; Write-Host "--- Ferramenta Mobile (ADB): $toolName ---" -ForegroundColor Yellow
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
    Write-Host "      PAINEL DE CONTROLE - PROJETO TO-DO LIST (v$ScriptVersion)      " -ForegroundColor White
    Write-Host "=================================================" -ForegroundColor Cyan
    
    $emulatorStatus = Get-ServiceStatus 'emulator'
    $emulatorDisplayName = if ($emulatorName) { $emulatorName } else { "Nenhum Selecionado" }
    
    $statuses = @{
        "Emulador ($emulatorDisplayName)" = $emulatorStatus;
        'API Backend'  = Get-ServiceStatus 'api';
        'Frontend Web' = Get-ServiceStatus 'web';
        'App Desktop'  = Get-ServiceStatus 'desktop';
        'App Mobile'   = if ($emulatorStatus -eq 'RUNNING') { Get-ServiceStatus 'android' } else { "OFFLINE" }
    }

    Write-Host "`nSTATUS ATUAL:"
    $statuses.GetEnumerator() | ForEach-Object {
        $color = if ($_.Value -eq 'RUNNING') { 'Green' } else { 'Red' }
        Write-Host ("  {0,-30}" -f $_.Name) -NoNewline; Write-Host $_.Value -ForegroundColor $color
    }
    Write-Host "`n--- OPÇÕES ---" -ForegroundColor Yellow
    Write-Host " GERAL                     SERVIÇOS INDIVIDUAIS"
    Write-Host "  9. Iniciar TUDO          1. Iniciar API          5. Iniciar Desktop"
    Write-Host " 10. Parar TUDO             2. Parar API            6. Parar Desktop"
    Write-Host "  L. Limpar Caches         3. Iniciar Web          7. Iniciar App Mobile"
    Write-Host "                           4. Parar Web            8. Parar App Mobile"
    Write-Host "-----------------------------------------------------------------"
    Write-Host " FERRAMENTAS MOBILE (ANDROID)                      NAVEGAÇÃO"
    Write-Host "  A. Iniciar Emulador      D. Resetar Servidor ADB R. Atualizar Status"
    Write-Host "  B. Parar Emulador        E. Listar Dispositivos  Q. Sair"
    Write-Host "  H. Ligar (Cold Boot)     F. Ver Logs (logcat)"
    Write-Host "  I. Ligar com DNS Fixo    T. Construir/Instalar App"
    Write-Host "  S. Selecionar Emulador   G. Criar Túnel de Rede"
    Write-Host "  C. Abrir Web no Browser`n"
}

#==============================================================================
# --- LÓGICA PRINCIPAL (LOOP DO MENU) ---
#==============================================================================

Initialize-EmulatorSelection

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
            if ((Get-ServiceStatus 'api') -eq 'STOPPED') { if (-not (Start-Service 'api')) { Read-Host "Falha ao iniciar API."; continue } }
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
        'i' { Start-Service 'emulator' -FixDns }
        's' { Select-Emulator }
        't' { Build-And-Install-Android }
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