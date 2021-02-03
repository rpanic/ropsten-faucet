FROM node:12.2.0 AS angular

WORKDIR /opt/ng
COPY ./faucetfrontend/package.json ./faucetfrontend/package-lock.json ./
RUN npm install

ENV PATH="./node_modules/.bin:$PATH" 

COPY ./faucetfrontend ./
RUN ng build --prod

FROM gradle:6.6.1-jdk8 as builder

COPY --chown=gradle:gradle ./faucetbackend /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:8
COPY --from=builder /home/gradle/src/build/libs/ropstenfaucet-0.0.1-all.jar /app/ropstenfaucet.jar
COPY --from=angular /opt/ng/dist/faucetfrontend /app/static
COPY --from=builder /home/gradle/src/config /app/config
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "ropstenfaucet.jar"]
