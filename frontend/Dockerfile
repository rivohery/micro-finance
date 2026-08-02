# Étape 1 : Build de l'application
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production

# Étape 2 : Exécution avec Nginx
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]