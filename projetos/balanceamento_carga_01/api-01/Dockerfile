# /api-01/Dockerfile

FROM node:18-alpine
WORKDIR /usr/src/app

# Copia explicitamente os arquivos
COPY ./api-01/package.json ./
COPY ./api-01/index.js ./

RUN npm install

EXPOSE 3000
CMD [ "npm", "start" ]