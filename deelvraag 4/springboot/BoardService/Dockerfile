FROM maven:3.9.4-sapmachine-21 as maven
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn clean package -DskipTests

FROM sapmachine:21-jdk-ubuntu as build
ARG JAR_FILE=/usr/src/app/target/*.jar
COPY --from=maven ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM sapmachine:21-jdk-ubuntu
COPY --from=build dependencies/ ./
COPY --from=build snapshot-dependencies/ ./
COPY --from=build spring-boot-loader/ ./
COPY --from=build application/ ./
COPY --from=maven ${JAR_FILE} application.jar
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]