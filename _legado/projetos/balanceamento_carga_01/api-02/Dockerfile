# /api-02/Dockerfile

FROM node:18-alpine
WORKDIR /usr/src/app

# Copia explicitamente os arquivos
COPY ./api-02/package.json ./
COPY ./api-02/index.js ./

RUN npm install

EXPOSE 3000
CMD [ "npm", "start" ]