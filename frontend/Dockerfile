FROM node:20-alpine

WORKDIR /app

# Copy all files
COPY . .

# Install dependencies
RUN npm install

# Build Angular app
RUN npm run build

EXPOSE 4200

# Run Angular dev server
CMD ["npm", "start", "--", "--host", "0.0.0.0"]