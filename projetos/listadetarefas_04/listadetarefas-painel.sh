#!/bin/bash

#==============================================================================
# --- CONFIGURAÇÕES GLOBAIS E CORES ---
#==============================================================================

# Cores para a interface
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # Sem Cor

# O caminho base do projeto é definido dinamicamente a partir da localização do script.
BASE_PATH=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
echo -e "${CYAN}Base do projeto detectada em: ${BASE_PATH}${NC}"

# Caminhos para cada módulo do projeto
API_PATH="$BASE_PATH/listadetarefas-api"
WEB_PATH="$BASE_PATH/listadetarefas-web"
DESKTOP_PATH="$BASE_PATH/listadetarefas-desktop"
ANDROID_PATH="$BASE_PATH/listadetarefas-android"

# --- VALIDAÇÃO DE CAMINHOS ---
declare -A PROJECT_PATHS=(
    ["API"]="$API_PATH"
    ["Web"]="$WEB_PATH"
    ["Desktop"]="$DESKTOP_PATH"
    ["Android"]="$ANDROID_PATH"
)

PATHS_ARE_VALID=true
for project in "${!PROJECT_PATHS[@]}"; do
    path="${PROJECT_PATHS[$project]}"
    if [[ ! -d "$path" ]]; then
        echo -e "${RED}ERRO: O diretório do projeto '$project' não foi encontrado em '$path'${NC}"
        PATHS_ARE_VALID=false
    fi
done

if [[ "$PATHS_ARE_VALID" == "false" ]]; then
    read -p "Verifique os nomes das pastas. Pressione Enter para sair."
    exit 1
fi

# --- CONFIGURAÇÕES ANDROID ---
# Prioriza as variáveis de ambiente padrão do Linux.
if [[ -n "$ANDROID_HOME" ]]; then
    SDK_PATH="$ANDROID_HOME"
elif [[ -n "$ANDROID_SDK_ROOT" ]]; then
    SDK_PATH="$ANDROID_SDK_ROOT"
else
    SDK_PATH="$HOME/Android/Sdk" # Fallback para um local comum
    echo -e "${YELLOW}AVISO: ANDROID_HOME não está definido. Usando o caminho padrão: $SDK_PATH${NC}"
fi

EMULATOR_PATH="$SDK_PATH/emulator"
PLATFORM_TOOLS_PATH="$SDK_PATH/platform-tools"
EMULATOR_NAME="Medium_Phone"

# --- CONFIGURAÇÕES DOS ARTEFATOS ---
API_JAR="$API_PATH/target/listadetarefas-api-0.0.1-SNAPSHOT.jar"
DESKTOP_JAR="$DESKTOP_PATH/target/listadetarefas-desktop-1.0-SNAPSHOT.jar"
ANDROID_PACKAGE="br.com.curso.listadetarefas.android"
WEB_URL="http://localhost:3000"

# Identificador único para o processo Java do App Desktop
DESKTOP_APP_ID="listadetarefas-desktop-app"

#==============================================================================
# --- FUNÇÕES AUXILIARES ---
#==============================================================================

get_service_status() {
    local service_name=$1
    case $service_name in
        'api')
            # lsof (list open files) verifica se a porta está sendo usada
            if lsof -i -P -n | grep -q "TCP.*:8080 (LISTEN)"; then echo "RUNNING"; else echo "STOPPED"; fi
            ;;
        'web')
            if lsof -i -P -n | grep -q "TCP.*:3000 (LISTEN)"; then echo "RUNNING"; else echo "STOPPED"; fi
            ;;
        'desktop')
            # pgrep (process grep) procura por um processo Java com o nosso ID único.
            if pgrep -f "java.*-Dapp.id=$DESKTOP_APP_ID" > /dev/null; then echo "RUNNING"; else echo "STOPPED"; fi
            ;;
        'android')
            if "$PLATFORM_TOOLS_PATH/adb" shell ps | grep -q "$ANDROID_PACKAGE"; then echo "RUNNING"; else echo "STOPPED"; fi
            ;;
        'emulator')
            # Verifica se o adb devices lista um dispositivo no estado 'device'.
            if "$PLATFORM_TOOLS_PATH/adb" devices | grep -q "device$"; then echo "RUNNING"; else echo "STOPPED"; fi
            ;;
    esac
}

wait_for_adb_device() {
    local timeout_seconds=${1:-60}
    if [[ $(get_service_status 'emulator') == "RUNNING" ]]; then return 0; fi

    echo -en "${CYAN}Aguardando um emulador/dispositivo ficar online...${NC}"
    SECONDS=0
    while (( SECONDS < timeout_seconds )); do
        if [[ $(get_service_status 'emulator') == "RUNNING" ]]; then
            echo -e "\n${GREEN}Dispositivo detectado.${NC}"
            sleep 1
            return 0
        fi
        echo -n "."
        sleep 2
    done
    echo -e "\n${RED}Tempo esgotado!${NC}"
    return 1
}

ensure_build_artifact() {
    local artifact_path=$1
    local project_path=$2
    local build_command=$3
    local build_tool_name=$4

    if [[ ! -f "$artifact_path" ]]; then
        read -p "Artefato de build não encontrado em '$artifact_path'. Deseja construir agora? (s/n) " choice
        if [[ "$choice" == "s" ]]; then
            ( # Executa em um subshell para não alterar o diretório do script principal
                cd "$project_path" || return 1
                echo -e "${CYAN}Construindo em '$project_path'...${NC}"

                if [[ "$build_tool_name" == "./mvnw" && ! -f "./pom.xml" ]]; then
                    echo -e "${RED}ERRO CRÍTICO: 'pom.xml' não encontrado em '$project_path'.${NC}"
                    return 1
                fi

                local executable_command=""
                if [[ "$build_tool_name" == "ng" ]]; then
                    if command -v ng &> /dev/null; then executable_command="ng"; else echo -e "${RED}ERRO: O comando 'ng' (Angular CLI) não foi encontrado.${NC}"; fi
                else # Lógica para Maven
                    if [[ -f "$build_tool_name" && -d "./.mvn/wrapper" ]]; then executable_command="$build_tool_name";
                    elif command -v mvn &> /dev/null; then executable_command="mvn"; echo -e "${YELLOW}AVISO: Usando Maven global ('mvn').${NC}";
                    else echo -e "${RED}ERRO: Nenhuma ferramenta de build do Maven foi encontrada.${NC}"; fi
                fi

                if [[ -z "$executable_command" ]]; then return 1; fi

                # shellcheck disable=SC2086
                $executable_command $build_command
                local build_exit_code=$?

                if [[ $build_exit_code -ne 0 ]]; then
                    echo -e "${RED}ERRO DE BUILD (código: $build_exit_code).${NC}"
                    return 1
                fi
            )
            local subshell_exit_code=$?
            if [[ $subshell_exit_code -ne 0 ]]; then sleep 2; return 1; fi
            if [[ ! -f "$artifact_path" ]]; then
                echo -e "${RED}Build concluído, mas o artefato '$artifact_path' não foi encontrado.${NC}"
                sleep 2; return 1
            fi
        else
            echo -e "${RED}Início cancelado.${NC}"; sleep 2; return 1
        fi
    fi
    return 0
}

#==============================================================================
# --- FUNÇÕES DE GERENCIAMENTO DE SERVIÇOS ---
#==============================================================================

start_service() {
    local service_name=$1
    local cold_boot=$2

    if [[ "$service_name" =~ ^(web|desktop|android)$ && $(get_service_status 'api') == "STOPPED" ]]; then
        read -p "AVISO: A API está parada. Deseja iniciá-la primeiro? (s/n) " confirm
        if [[ "$confirm" == "s" ]]; then
            start_service 'api' || { echo -e "${RED}Falha ao iniciar API.${NC}"; sleep 2; return 1; }
        else
            echo -e "${YELLOW}AVISO: '$service_name' pode não funcionar sem a API.${NC}"
        fi
    fi

    echo -e "\n${YELLOW}Tentando iniciar serviço: $service_name...${NC}"
    case $service_name in
        'api')
            ensure_build_artifact "$API_JAR" "$API_PATH" "clean package" "./mvnw" || return 1
            # Abre um novo terminal para executar a API
            gnome-terminal --title="API-Backend" -- bash -c "java -jar '$API_JAR'; exec bash" &
            ;;
        'web')
            # O build do Angular é necessário, mas ng serve faz isso em memória.
            # Para um build de produção, a lógica seria diferente.
            # Aqui, apenas iniciamos o servidor de desenvolvimento.
            (cd "$WEB_PATH" && gnome-terminal --title="Servidor-Web" -- bash -c "ng serve --open; exec bash") &
            ;;
        'desktop')
            ensure_build_artifact "$DESKTOP_JAR" "$DESKTOP_PATH" "clean package" "./mvnw" || return 1
            # Inicia o processo Java com um ID único para que possamos encontrá-lo depois.
            nohup java -Dapp.id=$DESKTOP_APP_ID -jar "$DESKTOP_JAR" > /dev/null 2>&1 &
            ;;
        'android')
            wait_for_adb_device || { echo -e "${RED}Nenhum emulador/dispositivo detectado.${NC}"; sleep 2; return 1; }
            echo -e "${CYAN}Criando túnel de rede (adb reverse)...${NC}"
            "$PLATFORM_TOOLS_PATH/adb" reverse tcp:8080 tcp:8080
            echo "Iniciando App Android..."
            "$PLATFORM_TOOLS_PATH/adb" shell am start -n "$ANDROID_PACKAGE/$ANDROID_PACKAGE.MainActivity"
            ;;
        'emulator')
            if [[ $(get_service_status 'emulator') == "RUNNING" ]]; then echo -e "${GREEN}Emulador já parece estar rodando.${NC}"; return 0; fi
            local arguments="-avd $EMULATOR_NAME"
            if [[ "$cold_boot" == "true" ]]; then
                arguments+=" -no-snapshot-load"
                echo -e "${YELLOW}Iniciando emulador em modo Cold Boot...${NC}"
            fi
            # shellcheck disable=SC2086
            ("$EMULATOR_PATH/emulator" $arguments &)
            wait_for_adb_device || return 1
            ;;
    esac
    
    echo -e "${GREEN}Comando de início enviado. Verificando status...${NC}"
    SECONDS=0
    while (( SECONDS < 45 )); do
        if [[ $(get_service_status "$service_name") == "RUNNING" ]]; then
            echo -e "\n${GREEN}Serviço '$service_name' parece estar rodando.${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
    done
    
    if [[ "$service_name" != "emulator" ]]; then
        echo -e "\n${RED}ERRO: Serviço '$service_name' não iniciou corretamente.${NC}"
        sleep 2
    fi
    return 1
}

stop_service() {
    local service_name=$1
    echo -e "\n${YELLOW}Parando serviço: $service_name...${NC}"
    case $service_name in
        'api')
            # Encontra o PID usando a porta e o mata.
            local pid
            pid=$(lsof -t -i:8080)
            if [[ -n "$pid" ]]; then kill -9 "$pid"; fi
            ;;
        'web')
            local pid
            pid=$(lsof -t -i:3000)
            if [[ -n "$pid" ]]; then kill -9 "$pid"; fi
            ;;
        'desktop')
            # pkill (process kill) mata o processo com base no nosso ID único.
            pkill -f "java.*-Dapp.id=$DESKTOP_APP_ID"
            ;;
        'android')
            "$PLATFORM_TOOLS_PATH/adb" shell am force-stop "$ANDROID_PACKAGE"
            ;;
        'emulator')
            "$PLATFORM_TOOLS_PATH/adb" emu kill
            ;;
    esac
    echo -e "${GREEN}Comando de parada enviado.${NC}"; sleep 1
}

clean_project() {
    clear; echo -e "${YELLOW}--- LIMPANDO CACHES E BUILDS ---${NC}"
    echo -e "\n${CYAN}Limpando API...${NC}"; (cd "$API_PATH" && ./mvnw clean)
    echo -e "\n${CYAN}Limpando Desktop...${NC}"; (cd "$DESKTOP_PATH" && ./mvnw clean)
    echo -e "\n${CYAN}Limpando Web...${NC}"
    rm -rf "$WEB_PATH/.angular" "$WEB_PATH/dist"
    echo -e "\n${GREEN}--- LIMPEZA CONCLUÍDA ---${NC}"; read -p "Pressione Enter..."
}

#==============================================================================
# --- FERRAMENTAS DE DEBUG (ANDROID) ---
#==============================================================================

invoke_adb_tool() {
    wait_for_adb_device || { read -p "Operação ADB cancelada. Pressione Enter..."; return; }
    clear; echo -e "${YELLOW}--- Ferramenta ADB: $1 ---${NC}"
    case $1 in
        'reset')   "$PLATFORM_TOOLS_PATH/adb" kill-server; "$PLATFORM_TOOLS_PATH/adb" start-server ;;
        'devices') "$PLATFORM_TOOLS_PATH/adb" devices ;;
        'logcat')
            echo "Iniciando logcat... Pressione Ctrl+C na nova janela para parar."
            gnome-terminal --title="Logcat" -- bash -c "'$PLATFORM_TOOLS_PATH/adb' logcat '*:S' '$ANDROID_PACKAGE:V'; exec bash" &
            return
            ;;
        'reverse')
            "$PLATFORM_TOOLS_PATH/adb" reverse tcp:8080 tcp:8080
            echo "Verificando túneis:"; "$PLATFORM_TOOLS_PATH/adb" reverse --list
            ;;
    esac
    read -p $'\nPressione Enter para voltar ao menu'
}

#==============================================================================
# --- INTERFACE DO USUÁRIO (MENU) ---
#==============================================================================

show_menu() {
    clear
    echo -e "${CYAN}=================================================${NC}"
    echo -e "${WHITE}      PAINEL DE CONTROLE - PROJETO TO-DO LIST      ${NC}"
    echo -e "${CYAN}=================================================${NC}"
    
    local emulator_status
    emulator_status=$(get_service_status 'emulator')
    
    declare -A statuses=(
        ['Emulador']="$emulator_status"
        ['API Backend']="$(get_service_status 'api')"
        ['Servidor Web']="$(get_service_status 'web')"
        ['App Desktop']="$(get_service_status 'desktop')"
        ['App Android']="$([[ "$emulator_status" == "RUNNING" ]] && get_service_status 'android' || echo "OFFLINE")"
    )

    echo -e "\n${WHITE}STATUS ATUAL:${NC}"
    for name in "${!statuses[@]}"; do
        local status="${statuses[$name]}"
        local color="$RED"
        if [[ "$status" == "RUNNING" ]]; then color="$GREEN"; fi
        printf "  %-15s %b%s%b\n" "$name:" "$color" "$status" "$NC"
    done

    echo -e "\n${YELLOW}--- OPÇÕES ---${NC}"
    echo " GERAL                     SERVIÇOS INDIVIDUAIS"
    echo "  9. Iniciar TUDO          1. Iniciar API          5. Iniciar Desktop"
    echo " 10. Parar TUDO             2. Parar API            6. Parar Desktop"
    echo "  L. Limpar Caches         3. Iniciar Web          7. Iniciar App Android"
    echo "                           4. Parar Web            8. Parar App Android"
    echo "-----------------------------------------------------------------"
    echo " FERRAMENTAS ANDROID                               NAVEGAÇÃO"
    echo "  A. Iniciar Emulador      D. Resetar Servidor ADB R. Atualizar Status"
    echo "  B. Parar Emulador        E. Listar Dispositivos  Q. Sair"
    echo "  H. Ligar (Cold Boot)     F. Ver Logs (logcat)"
    echo "  C. Abrir Web no Browser  G. Criar Túnel de Rede"
}

#==============================================================================
# --- LÓGICA PRINCIPAL (LOOP DO MENU) ---
#==============================================================================

while true; do
    show_menu
    read -rp $'\nDigite sua opção e pressione Enter: ' choice
    case ${choice,,} in # Converte para minúsculas
        '1')  start_service 'api' ;;
        '2')  stop_service 'api' ;;
        '3')  start_service 'web' ;;
        '4')  stop_service 'web' ;;
        '5')  start_service 'desktop' ;;
        '6')  stop_service 'desktop' ;;
        '7')  start_service 'android' ;;
        '8')  stop_service 'android' ;;
        '9')
            start_service 'emulator' || { read -p "Falha ao iniciar Emulador."; continue; }
            start_service 'api' || { read -p "Falha ao iniciar API."; continue; }
            start_service 'web'; start_service 'desktop'; start_service 'android'
            read -p $'\n--- SEQUÊNCIA CONCLUÍDA ---\nPressione Enter...'
            ;;
        '10')
            stop_service 'android'; stop_service 'desktop'; stop_service 'web'; stop_service 'api'
            if [[ $(get_service_status 'emulator') == "RUNNING" ]]; then
                read -p "Deseja parar o Emulador também? (s/n) " confirm
                if [[ "$confirm" == "s" ]]; then stop_service 'emulator'; fi
            fi
            ;;
        'a') start_service 'emulator' ;;
        'b') stop_service 'emulator' ;;
        'h') start_service 'emulator' "true" ;;
        'c') if [[ $(get_service_status 'web') == "RUNNING" ]]; then xdg-open "$WEB_URL"; else echo -e "${RED}Servidor web precisa estar rodando.${NC}"; sleep 2; fi ;;
        'd') invoke_adb_tool 'reset' ;;
        'e') invoke_adb_tool 'devices' ;;
        'f') invoke_adb_tool 'logcat' ;;
        'g') invoke_adb_tool 'reverse' ;;
        'l') clean_project ;;
        'r') continue ;;
        'q') break ;;
        *)   echo -e "${RED}Opção inválida!${NC}"; sleep 2 ;;
    esac
done

