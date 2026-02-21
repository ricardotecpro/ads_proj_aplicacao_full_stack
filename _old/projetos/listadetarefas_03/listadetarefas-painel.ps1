<#
.SYNOPSIS
    Painel de controle para gerenciar o projeto To-Do List (API, Web, Desktop).
.DESCRIPTION
    Este script PowerShell fornece um menu interativo para iniciar, parar e construir
    os componentes de backend, web e desktop do projeto.
.VERSION
    9.7 - Focado em API, Web e Desktop (funcionalidades Android removidas)
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

# --- VALIDAÇÃO DE CAMINHOS ---
$projectPaths = @{ "API" = $apiPath; "Web" = $webPath; "Desktop" = $desktopPath }
$pathsAreValid = $true
foreach ($project in $projectPaths.Keys) {
    if (-not (Test-Path $projectPaths[$project])) {
        Write-Host "ERRO: O diretório do projeto '$project' não foi encontrado em '$($projectPaths[$project])'" -ForegroundColor Red
        $pathsAreValid = $false
    }
}
if (-not $pathsAreValid) { Read-Host "`nVerifique os nomes das pastas. Pressione Enter para sair."; exit }


# --- CONFIGURAÇÕES DOS ARTEFATOS ---
$webUrl = "http://localhost:3000"

#==============================================================================
# ATENÇÃO: VERIFIQUE O TÍTULO DA JANELA DO APP DESKTOP
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
        }
    }
    catch { return "STOPPED" }
    return "STOPPED"
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

function Start-Service($serviceName) {
    if ($serviceName -in @('web', 'desktop')) {
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
            $targetDir = Join-Path $apiPath "target"
            if (-not (Ensure-BuildArtifact -ArtifactPath $targetDir -ProjectPath $apiPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }
            
            $apiJar = (Get-ChildItem -Path $targetDir -Filter "*.jar" | Where-Object { $_.Name -notlike "*-sources.jar" -and $_.Name -notlike "*-javadoc.jar" } | Select-Object -First 1).FullName
            if (-not $apiJar) { Write-Host "ERRO: Nenhum arquivo .jar executável foi encontrado em '$targetDir'." -ForegroundColor Red; Start-Sleep 2; break }
            
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title API-Backend && java -jar `"`"$apiJar`"`"`"" -WorkingDirectory $apiPath
            $commandExecuted = $true
        }
        'web' {
            $distPath = Join-Path $webPath "dist"
            if (-not (Ensure-BuildArtifact -ArtifactPath $distPath -ProjectPath $webPath -BuildCommand "build" -BuildToolName "ng")) { break }
            
            $projectDistDir = Get-ChildItem -Path $distPath -Directory | Select-Object -First 1
            if (-not $projectDistDir) { Write-Host "ERRO: Pasta do projeto compilado não encontrada em '$distPath'." -ForegroundColor Red; Start-Sleep 2; break }
            
            $servePath = Join-Path $projectDistDir.FullName "browser"
            if (-not (Test-Path $servePath)) { Write-Host "ERRO: Pasta 'browser' não encontrada em '$($projectDistDir.FullName)'." -ForegroundColor Red; Start-Sleep 2; break }

            Write-Host "Servindo aplicação web a partir de '$servePath'" -ForegroundColor Cyan
            Push-Location $webPath; Start-Process npx -ArgumentList "serve", $servePath; Pop-Location
            $commandExecuted = $true
        }
        'desktop' {
            $targetDir = Join-Path $desktopPath "target"
            if (-not (Ensure-BuildArtifact -ArtifactPath $targetDir -ProjectPath $desktopPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }

            $desktopJar = (Get-ChildItem -Path $targetDir -Filter "*.jar" | Where-Object { $_.Name -notlike "*-sources.jar" -and $_.Name -notlike "*-javadoc.jar" } | Select-Object -First 1).FullName
            if (-not $desktopJar) { Write-Host "ERRO: Nenhum arquivo .jar executável foi encontrado em '$targetDir'." -ForegroundColor Red; Start-Sleep 2; break }

            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title App-Desktop && java -jar `"`"$desktopJar`"`"`"" -WorkingDirectory $desktopPath
            $commandExecuted = $true
        }
    }
    if (-not $commandExecuted) { Write-Host "Falha no pré-requisito para '$serviceName'." -ForegroundColor Red; Start-Sleep 2; return $false }
    Write-Host "Comando de início enviado. Verificando status..." -ForegroundColor Green
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt 45) {
        if ((Get-ServiceStatus $serviceName) -eq 'RUNNING') { Write-Host "`nServiço '$serviceName' parece estar rodando." -ForegroundColor Green; return $true }
        Write-Host "." -NoNewline; Start-Sleep 2
    }
    Write-Host "`nERRO: Serviço '$serviceName' não iniciou corretamente." -ForegroundColor Red; Start-Sleep 2
    return $false
}

function Stop-Service($serviceName) {
    Write-Host "`nParando serviço: $serviceName..." -ForegroundColor Yellow
    switch ($serviceName) {
        'api' { $p = Get-NetTCPConnection -LocalPort 8080 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'web' { $p = Get-NetTCPConnection -LocalPort 3000 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'desktop' { Get-Process -Name "java", "javaw" -EA 0 | Where-Object { $_.MainWindowTitle -like "*$desktopWindowTitle*" } | Stop-Process -Force }
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
# --- INTERFACE DO USUÁRIO (MENU) ---
#==============================================================================

function Show-Menu {
    Clear-Host
    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host "      PAINEL DE CONTROLE - PROJETO TO-DO LIST      " -ForegroundColor White
    Write-Host "=================================================" -ForegroundColor Cyan
    
    $statuses = @{
        'API Backend'  = Get-ServiceStatus 'api';
        'Servidor Web' = Get-ServiceStatus 'web';
        'App Desktop'  = Get-ServiceStatus 'desktop';
    }

    Write-Host "`nSTATUS ATUAL:"
    $statuses.GetEnumerator() | ForEach-Object {
        $color = if ($_.Value -eq 'RUNNING') { 'Green' } else { 'Red' }
        Write-Host ("  {0,-15}" -f $_.Name) -NoNewline; Write-Host $_.Value -ForegroundColor $color
    }
    Write-Host "`n--- OPÇÕES ---" -ForegroundColor Yellow
    Write-Host " GERAL                     SERVIÇOS INDIVIDUAIS"
    Write-Host "  9. Iniciar TUDO          1. Iniciar API"
    Write-Host " 10. Parar TUDO             2. Parar API"
    Write-Host "  L. Limpar Caches         3. Iniciar Web"
    Write-Host "                           4. Parar Web"
    Write-Host "                           5. Iniciar Desktop"
    Write-Host "                           6. Parar Desktop"
    Write-Host "-----------------------------------------------------------------"
    Write-Host " NAVEGAÇÃO"
    Write-Host "  C. Abrir Web no Browser"
    Write-Host "  R. Atualizar Status"
    Write-Host "  Q. Sair`n"
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
        '9' {
            if (-not (Start-Service 'api')) { Read-Host "Falha ao iniciar API."; continue }
            Start-Service 'web'
            Start-Service 'desktop'
            Read-Host "`n--- SEQUÊNCIA CONCLUÍDA ---`nPressione Enter..."
        }
        '10' {
            Stop-Service 'desktop'
            Stop-Service 'web'
            Stop-Service 'api'
        }
        'c' { if ((Get-ServiceStatus 'web') -eq 'RUNNING') { Start-Process $webUrl } else { Write-Host "Servidor web precisa estar rodando." -ForegroundColor Red; Start-Sleep 2 } }
        'l' { Clean-Project }
        'r' { }
        'q' { break }
        default { Write-Host "Opção inválida!" -ForegroundColor Red; Start-Sleep 2 }
    }
}