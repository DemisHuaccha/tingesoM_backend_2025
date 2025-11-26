FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/tingeso-m.jar
COPY ${JAR_FILE} tingeso-m.jar
ENTRYPOINT ["java","-jar","/tingeso-m.jar"]
