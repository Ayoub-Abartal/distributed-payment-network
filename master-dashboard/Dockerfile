# Stage 1: Build stage 
FROM node:18-alpine AS build 

# Set working directory
WORKDIR /app

# Copy package files for layer caching (dependencies don't change often)
COPY package*.json .

# Install dependencies
RUN npm install 

# Copy all source code (src/, public/, configs)
COPY . .

# Build the React app (creates /app/build folder)
RUN npm run build 

# Stage 2: Production stage with Nginx
FROM nginx:alpine 

# Copy built files from build stage to nginx html directory
COPY --from=build /app/build /usr/share/nginx/html 

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start nginx 
CMD ["nginx", "-g", "daemon off;"]