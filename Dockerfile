FROM tomcat:8.0-alpine

VOLUME /tmp

MAINTAINER "gautamjain2011@gmail.com"

EXPOSE 8080 

COPY target/*.war /usr/local/tomcat/webapps/

RUN echo "Creation of your docker image is in progress, please hold on for a moment!"

CMD ["catalina.sh", "run"]
