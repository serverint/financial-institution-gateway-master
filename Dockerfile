FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER "Ushahemba Ukange <ushahemba.ukange@arca.network>"
# Define working directory.
WORKDIR /work
ADD target/financial-institution-gateway-0.0.1-SNAPSHOT.jar /work/financial-institution-gateway-0.0.1-SNAPSHOT.jar
# Expose Ports
EXPOSE 8080
#EXPOSE 8443
ENTRYPOINT exec java $JAVA_OPTS -jar /work/financial-institution-gateway-0.0.1-SNAPSHOT.jar --spring.config.location=/properties/application.properties