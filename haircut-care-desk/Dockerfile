ARG NODE_IMAGE=dockerproxy.net/library/node:18-alpine
ARG NGINX_IMAGE=dockerproxy.net/library/nginx:alpine

FROM ${NODE_IMAGE} AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

FROM ${NGINX_IMAGE}
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
