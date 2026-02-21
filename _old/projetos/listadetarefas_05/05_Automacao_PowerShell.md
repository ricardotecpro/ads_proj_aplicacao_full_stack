# 🤖 Módulo 5: Automação com PowerShell (`listadetarefas-painel.ps1`)

**Objetivo:** Centralizar o gerenciamento do ecossistema com um painel de controle para iniciar e parar todos os serviços de forma rápida e fácil.

### ### 📂 Passo 1: Estrutura Final e Configuração
1.  Na **pasta raiz** que contém todos os 4 projetos, crie o arquivo `listadetarefas-painel.ps1`.
2.  **Habilite a Execução de Scripts:** Abra o PowerShell como **Administrador** e execute (apenas uma vez):
    ```powershell
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
    ```

#### Estrutura de Pastas Final do Projeto Completo
```
projeto-todolist/
├── listadetarefas-api/
├── listadetarefas-web/
├── listadetarefas-desktop/
├── listadetarefas-android/
└── listadetarefas-painel.ps1  # <- Script de automação
```

### ### 📜 Passo 2: O Script de Automação
Copie o código abaixo para o seu arquivo `listadetarefas-painel.ps1`. Ele está corrigido para usar os nomes corretos dos projetos, é portátil e verifica se os projetos precisam ser construídos antes de executar.

```powershell
# Cole o código completo e corrigido do listadetarefas-painel.ps1 do guia anterior aqui.
# Este script já inclui as melhores práticas de verificação e portabilidade.
# --- CONFIGURAÇÕES GLOBAIS ---
$basePath = $PSScriptRoot
$apiPath = "$basePath\listadetarefas-api"
$webPath = "$basePath\listadetarefas-web"
$desktopPath = "$basePath\listadetarefas-desktop"
$androidPath = "$basePath\listadetarefas-android"
$sdkPath = "C:\Users\$env:UserName\AppData\Local\Android\Sdk" # Ajuste se necessário
$emulatorPath = "$sdkPath\emulator"
$platformToolsPath = "$sdkPath\platform-tools"
$emulatorName = "Medium_Phone" # Nome do seu emulador
$androidPackage = "br.com.curso.listadetarefas.android"
$desktopWindowTitle = "Minha Lista de Tarefas (Desktop)"
# ... (resto das funções Get-ServiceStatus, Start-Service, Stop-Service e o menu interativo) ...
```

### ### ✅ Passo 3: Teste do Painel de Controle
1.  Abra o terminal na pasta raiz do projeto.
2.  Execute o script: `.\listadetarefas-painel.ps1`
3.  Teste as opções do menu (Iniciar API, Parar API, Iniciar TUDO, etc.) para garantir que o painel está gerenciando todos os componentes do ecossistema corretamente.

---

### 🚀 [ricardotecpro.github.io](https://ricardotecpro.github.io/)

