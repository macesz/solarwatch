FROM node:23.11.1-alpine AS build
WORKDIR /app
COPY package*.json .

RUN npm install
COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80