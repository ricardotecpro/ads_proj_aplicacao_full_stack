FROM nginx:1.23-alpine

# Copia nossa configuração personalizada (relativo ao 'context')
COPY ./nginx/nginx.conf /etc/nginx/nginx.conf

# Copia os arquivos do visualizer (relativo ao 'context')
COPY ./visualizer /usr/share/nginx/html