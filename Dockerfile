FROM amazoncorretto:25-alpine AS build

RUN apk add --no-cache bash curl unzip git

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

COPY src src

# Build the application (assemble jar)
RUN ./gradlew clean bootJar -x test --no-daemon

FROM amazoncorretto:25-alpine

WORKDIR /app

COPY --from=build /app/build/libs/ /app/
RUN mv /app/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
