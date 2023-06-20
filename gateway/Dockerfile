FROM amazoncorretto:11
#ENV TZ=Europe/Moscow
COPY target/*.jar shareit-gateway.jar
ENTRYPOINT ["java","-jar","/shareit-gateway.jar"]