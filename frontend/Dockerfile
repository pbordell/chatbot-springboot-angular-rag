FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration=production

FROM nginx:1.25-alpine

# Eliminar la página por defecto de Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiar los archivos compilados de Angular al directorio de Nginx
# NOTA: Asegúrate de que la ruta coincida con el nombre de tu aplicación en angular.json
# En Angular 17/18/19 la estructura suele ser dist/[nombre-app]/browser
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

# Arrancar Nginx en primer plano
CMD ["nginx", "-g", "daemon off;"]
