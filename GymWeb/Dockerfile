# # ---------- Build Stage ----------
# FROM node:22.18.0-alpine as builder

# WORKDIR /app

# # Copy package files and install dependencies
# COPY package*.json ./
# RUN npm ci

# # Copy all source files
# COPY . .

# # Build Angular app in production mode
# RUN npm run build -- --configuration production

# # ---------- NGINX Stage ----------
# FROM nginx:1.27-alpine

# # Remove default NGINX static files
# RUN rm -rf /usr/share/nginx/html/*

# # Copy Angular production build output from builder stage
# COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# # Expose port 80 for HTTP traffic
# EXPOSE 80

# # Start NGINX in foreground
# CMD ["nginx", "-g", "daemon off;"]

# ---------- Build Stage ----------
FROM node:22-alpine as builder

# Set npm memory limits for Pi
ENV NODE_OPTIONS="--max_old_space_size=2048"
ENV NPM_CONFIG_PROGRESS=false
ENV NPM_CONFIG_LOGLEVEL=warn

WORKDIR /app

# Copy package files and install dependencies (INCLUDING devDependencies for build)
COPY package*.json ./
RUN npm ci --silent

# Copy source files
COPY . .

# Build Angular app using npx (Angular 20+ compatible flags)
RUN npx ng build \
    --configuration=production \
    --optimization \
    --output-hashing=all \
    --extract-licenses=false \
    --source-map=false

# ---------- NGINX Stage ----------
FROM nginx:1.27-alpine

# Install security updates
RUN apk update && apk upgrade && rm -rf /var/cache/apk/*

# Remove default NGINX static files
RUN rm -rf /usr/share/nginx/html/*

# ---------- NGINX Stage ----------
FROM nginx:1.27-alpine

# Install security updates
RUN apk update && apk upgrade && rm -rf /var/cache/apk/*

# Remove default NGINX static files
RUN rm -rf /usr/share/nginx/html/*

# Copy Angular production build output from builder stage
COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Add non-root user
RUN addgroup -g 101 -S nginx-user && adduser -S -D -H -u 101 -h /var/cache/nginx -s /sbin/nologin -G nginx-user nginx-user

# Set proper permissions
RUN chown -R nginx-user:nginx-user /usr/share/nginx/html && \
    chown -R nginx-user:nginx-user /var/cache/nginx

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Switch to non-root user
USER nginx-user

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start NGINX in foreground
CMD ["nginx", "-g", "daemon off;"]