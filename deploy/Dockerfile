FROM alpine

MAINTAINER Timur Gogolev <timurns@gmail.com>

ENV TOMCAT_MAJOR=8 \
    TOMCAT_VERSION=8.5.20 \
    TOMCAT_HOME=/opt/tomcat \
    TOMCAT_ROOT_DIR=/opt/tomcat \
    TOMCAT_PORT=8000 \
    CATALINA_HOME=/opt/tomcat \
    CATALINA_OUT=/dev/null \
    PHP_FPM_USER="www" \
    PHP_FPM_GROUP="www" \
    PHP_FPM_LISTEN_MODE="0660" \
    PHP_MEMORY_LIMIT="512M" \
    PHP_MAX_UPLOAD="50M" \
    PHP_MAX_FILE_UPLOAD="200" \
    PHP_MAX_POST="100M" \
    PHP_DISPLAY_ERRORS="On" \
    PHP_DISPLAY_STARTUP_ERRORS="On" \
    PHP_ERROR_REPORTING="E_COMPILE_ERROR\|E_RECOVERABLE_ERROR\|E_ERROR\|E_CORE_ERROR" \
    PHP_CGI_FIX_PATHINFO=0 \
    TIMEZONE="Asia/Yekaterinburg" \
    COMPOSER_VERSION="1.5.2"


# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

RUN /bin/sh /app/deploy.sh

# tomcat
EXPOSE 8000
# nginx
EXPOSE 9090
