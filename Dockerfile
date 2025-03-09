FROM openjdk:25-ea-4-jdk-oraclelinux9

# Create working directory
WORKDIR /app

# Copy the exact JAR file you are running locally (mini1.jar)
COPY target/mini1.jar app.jar

# Set environment variables to point to mounted JSON paths
ENV USER_DATA_PATH=/app/data/users.json
ENV PRODUCT_DATA_PATH=/app/data/products.json
ENV ORDER_DATA_PATH=/app/data/orders.json
ENV CART_DATA_PATH=/app/data/carts.json

# Expose the application port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]