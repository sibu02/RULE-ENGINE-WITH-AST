FROM openjdk:8-jdk-slim
WORKDIR /app
COPY target/ruleEngine.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ruleEngine.jar"]