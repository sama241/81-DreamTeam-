FROM openjdk:25-ea-4-jdk-oraclelinux9

# Create app directory
WORKDIR /app

# Copy JAR file
COPY target/*.jar app.jar

# Create data directory for mounting JSON files
RUN mkdir -p /app/data

# Set ENV variables to point to mounted data inside the container
ENV USER_DATA_PATH=/app/data/users.json
ENV PRODUCT_DATA_PATH=/app/data/products.json
ENV ORDER_DATA_PATH=/app/data/orders.json
ENV CART_DATA_PATH=/app/data/carts.json

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
