#!/bin/sh
cd "`dirname $0`/.."

# CONFIGURABLE VARIABLES
LIBDIR="./lib"
APPDIR="./webapp"
HOST=localhost
PORT=9000

# RUNTIME VARIABLES
JAVA=java
JAVA_OPTS=
CLASSPATH="WEB-INF/lib"
MAIN_CLASS=com.texoit.undertow.standalone.Main

# READ CUSTOM CONFIGURATIONS
if [ -e bin/undertow.conf ]; then
	. bin/undertow.conf
fi

# MAIN
JAVA_OPTS="-Dundertow.standalone.libdir=${LIBDIR} \
	-Dundertow.standalone.appdir=${APPDIR} \
	-Dundertow.standalone.host=${HOST} \
	-Dundertow.standalone.port=${PORT} ${JAVA_OPTS}"

LIB_CLASSPATH=`ls ${LIBDIR}/*.jar | tr '\n' ' ' | sed 's/  */ /g' | tr ' ' ':'`
CLASSPATH=${LIB_CLASSPATH}:${CLASSPATH}

${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS}
