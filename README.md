# Financial Institution Gateway
This API  type is used to get available Financial Institutions in the platform

## Installation instructions
Build a docker image from [here](./Dockerfile)
```
docker build -t financial-institution-gateway .
```
Start a docker container from the image
```
docker run -it -d -p 9026:8080 \
-v /etc/timezone:/etc/timezone \
-v /home/teamcity/apps_properties/wasp_financial-institution-gateway:/properties/ \
-v /home/teamcity/apps_logs/wasp_financial-institution-gateway:/var/log/ \
--name wasp-financial-institution-gateway wasp/financial-institution-gateway
```
Default properties are the following. If you want to override some or all default properties create a property file with the parameters you want to override and run the java jar with this environment property ```--spring.config.location=/properties/application.properties```.
```
spring.application.name=financial-institution-gateway
server.port=9032

#Kafka
spring.cloud.stream.bindings.logs.destination=logs
spring.cloud.stream.bindings.logs.contentType=application/json
spring.cloud.stream.kafka.binder.brokers=192.168.101.6
spring.cloud.stream.kafka.binder.zkNodes=192.168.101.6
spring.cloud.stream.kafka.binder.autoAddPartitions=true

#Zipkin
spring.zipkin.enabled=true
spring.zipkin.baseUrl=http://192.168.101.6:9012

#Logging
logging.level.root=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=INFO

#Filtering
ignore.jat.filter.paths=/v2/api-docs,/swagger.*,/.*.css,/.*.ico,/webjars/.*

```
## API docs
[API docs in Swagger](http://192.168.101.6:9032/swagger-ui.html)
