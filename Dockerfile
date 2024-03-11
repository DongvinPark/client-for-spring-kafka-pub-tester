FROM openjdk:17-jdk-alpine
WORKDIR /
COPY /build/libs/app.jar ./
ENTRYPOINT ["java","-jar","/app.jar"]