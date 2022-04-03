FROM maven:3.8.2-jdk-11
WORKDIR ./spring-boot-app
COPY . .
RUN mvn clean install -DskipTests=true
CMD mvn spring-boot:run