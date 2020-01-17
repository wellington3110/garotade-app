FROM maven:3.6.3-jdk-8-openj9 as build

WORKDIR /build/

ADD . .

RUN \
    mvn package && \
    mv target/garotadebank-1.0-SNAPSHOT.jar app.jar

# ---------------------------------------------

FROM openjdk:8-jdk-alpine as production

WORKDIR /data/

COPY --from=build /build/app.jar app.jar

ENV AXON_SERVER_HOSTNAME axonserver

EXPOSE 8080

CMD java  -Djava.security.egd=file:/dev/./urandom -jar app.jar