FROM openjdk:8-jdk-alpine
RUN mkdir -p /home/gallery
WORKDIR /home/gallery
COPY . .
RUN ["./gradlew", "war"]
EXPOSE 7222
ENTRYPOINT ["./gradlew", "appStart"]
