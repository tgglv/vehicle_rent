#!/bin/sh

apk upgrade --update && \
    apk add --update --no-cache curl nginx mysql redis bash git openssh nano openrc sed openjdk8 wget ca-certificates mysql-client sudo && \
    apk add php7 php7-fpm php7-mysqli php7-curl php7-json php7-iconv php7-phar php7-zlib php7-dom php7-mbstring php7-xml php7-xmlwriter php7-tokenizer php7-session php7-ctype tzdata && \
    curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer --version=${COMPOSER_VERSION} && \
    chmod +x /usr/local/bin/composer && \
# TOMCAT
    curl -jksSL -o /tmp/apache-tomcat.tar.gz http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    gunzip /tmp/apache-tomcat.tar.gz && \
    mkdir /opt && \
    tar -C /opt -xf /tmp/apache-tomcat.tar && \
    ln -s /opt/apache-tomcat-${TOMCAT_VERSION} ${TOMCAT_HOME} && \
    rm -rf ${TOMCAT_HOME}/webapps/* && \
    addgroup tomcat && \
    adduser -G tomcat -h ${CATALINA_HOME} -s /sbin/nologin -D tomcat && \
    mv /app/tomcat_server.xml /opt/tomcat/conf/server.xml
    cp /app/vehiclerent.war /opt/tomcat/webapps && \
# NGINX
    rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf && \
    cp /app/nginx.conf /etc/nginx && \
    cp /app/nginx_default.conf /etc/nginx/conf.d && \
    mkdir /var/run/nginx && \
    touch /var/run/nginx.pid && \
# PHP
    adduser -D -g 'www' www && \
    sed -i "s|;listen.owner\s*=\s*nobody|listen.owner = ${PHP_FPM_USER}|g" /etc/php7/php-fpm.conf && \
    sed -i "s|;listen.group\s*=\s*nobody|listen.group = ${PHP_FPM_GROUP}|g" /etc/php7/php-fpm.conf && \
    sed -i "s|;listen.mode\s*=\s*0660|listen.mode = ${PHP_FPM_LISTEN_MODE}|g" /etc/php7/php-fpm.conf && \
    sed -i "s|user\s*=\s*nobody|user = ${PHP_FPM_USER}|g" /etc/php7/php-fpm.conf && \
    sed -i "s|group\s*=\s*nobody|group = ${PHP_FPM_GROUP}|g" /etc/php7/php-fpm.conf && \
    sed -i "s|;log_level\s*=\s*notice|log_level = notice|g" /etc/php7/php-fpm.conf #uncommenting line && \
    sed -i "s|display_errors\s*=\s*Off|display_errors = ${PHP_DISPLAY_ERRORS}|i" /etc/php7/php.ini && \
    sed -i "s|display_startup_errors\s*=\s*Off|display_startup_errors = ${PHP_DISPLAY_STARTUP_ERRORS}|i" /etc/php7/php.ini && \
    sed -i "s|error_reporting\s*=\s*E_ALL & ~E_DEPRECATED & ~E_STRICT|error_reporting = ${PHP_ERROR_REPORTING}|i" /etc/php7/php.ini && \
    sed -i "s|;*memory_limit =.*|memory_limit = ${PHP_MEMORY_LIMIT}|i" /etc/php7/php.ini && \
    sed -i "s|;*upload_max_filesize =.*|upload_max_filesize = ${PHP_MAX_UPLOAD}|i" /etc/php7/php.ini && \
    sed -i "s|;*max_file_uploads =.*|max_file_uploads = ${PHP_MAX_FILE_UPLOAD}|i" /etc/php7/php.ini && \
    sed -i "s|;*post_max_size =.*|post_max_size = ${PHP_MAX_POST}|i" /etc/php7/php.ini && \
    sed -i "s|;*cgi.fix_pathinfo=.*|cgi.fix_pathinfo= ${PHP_CGI_FIX_PATHINFO}|i" /etc/php7/php.ini && \
    cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime && \
    echo "${TIMEZONE}" > /etc/timezone && \
    sed -i "s|;*date.timezone =.*|date.timezone = ${TIMEZONE}|i" /etc/php7/php.ini && \
# MySQL
    mkdir /app/data && \
    chown -Rv mysql /app/data && \
    chmod -Rv 0777 /app/data && \
    mkdir /run/mysqld && \
    chown -Rv mysql /run/mysqld && \
    chmod -Rv 0777 /run/mysqld && \
    touch /run
    mv /app/my.cnf /etc/mysql/my.cnf && \
# EXTRACT SOURCES
    cd /app && \
    git clone https://github.com/tgglv/vehicle_rent.git && \
    cd /app/vehicle_rent/frontend && \
    composer install && \
# SWITCH SOURCES TO PRODUCTION MODE
    cd /app/vehicle_rent && \
    sed -i "s|String.*MODE.*=.*\"DEV\";|String MODE = \"PROD\";|i" backend/src/main/java/com/timur/rent/util/Settings.java && \
    sed -i "s|const.*MODE.*=.*'DEV';|const MODE = 'PROD';|i" frontend/cron/vehicleLocationsUpdater.php && \
    sed -i "s|var.*mode.*=.*'DEV';|var mode = 'PROD';|i" frontend/public/js/common.js && \
    echo "BUILD DONE!"