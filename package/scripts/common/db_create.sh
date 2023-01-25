#!/bin/bash
#
###############################################
# HIRS DB creation
# Conditions to address
# a. Install is called mutiple times
# b. Another app sets the root password 
# c. ACA is updated 
# d. ACA is updated after a DB password change
################################################

if [ -z ${HIRS_DB_PWD+x} ]; then
 DB_DEFAULT_PWD="hirs_db";
 else 
 DB_DEFAULT_PWD=$HIRS_DB_PWD;
fi

# Check if we're in a Docker container
if [ -f /.dockerenv ]; then
    DOCKER_CONTAINER=true
else
    DOCKER_CONTAINER=false
fi

# Check if mysql is already running, if not initialize
if [[ $(pgrep -c -u mysql mysqld) -eq 0 ]]; then
# Check if running in a container
   if [ $DOCKER_CONTAINER  = true ]; then
   # if in Docker container, avoid services that invoke the D-Bus
       echo "ACA is running in a container..."
       # Check if mariadb is setup
       if [ ! -d "/var/lib/mysql/mysql/" ]; then
           echo "Installing mariadb"
           /usr/bin/mysql_install_db 
           chown -R mysql:mysql /var/lib/mysql/  
       fi
       echo "Starting mysql...."
       #nohup /usr/bin/mysqld_safe > /dev/null 2>&1 &
       chown -R mysql:mysql /var/log/mariadb
       /usr/bin/mysqld_safe &
   else
       SQL_SERVICE=`/opt/hirs/scripts/common/get_db_service.sh`
       systemctl $SQL_SERVICE enable
       systemctl $SQL_SERVICE start
   fi
fi

# Wait for mysql to start before continuing.
echo "Checking mysqld status..."
while ! mysqladmin ping -h "$localhost" --silent; do
  sleep 1;
done

# Set intial password, ingore result in case its already been set
echo "Setting Mysql password"
mysqladmin -u root --silent password $DB_DEFAULT_PWD || true > /dev/null 2>&1

# Create the hirs_db database
echo "Creating HIRS Database..."  
DB_CREATE_SCRIPT=/opt/hirs/scripts/common/db_create.sql.el7
mysql -u root --password="$DB_DEFAULT_PWD" < $DB_CREATE_SCRIPT
