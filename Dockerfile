# 1. Usamos una imagen oficial de Java (Eclipse Temurin es estándar para Spring Boot)
# Asumo que usas Java 17. Si usas la versión 21, cambia el número aquí abajo.
FROM eclipse-temurin:21-jdk-alpine

# 2. Creamos una carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiamos el archivo JAR que generaste en el Paso 1 hacia el contenedor
# Renombramos el archivo a "app.jar" para que sea más fácil de manejar
COPY target/*.jar app.jar

# COPIAMOS TU WALLET AL CONTENEDOR (Asegúrate de estar en la carpeta del backend)
# Asumimos que la carpeta 'wallets' está accesible o la copias a la carpeta del proyecto
COPY wallets /app/wallets

# 4. Exponemos el puerto de tu backend
EXPOSE 8082

# Se activa el perfil "docker" para usar application-docker.properties
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]