#!/bin/sh
cd "`dirname $0`/.."

# CONFIGURABLE VARIABLES
LIBDIR="./lib"

# RUNTIME VARIABLES
JAVA=java
JAVA_OPTS=
MAIN_CLASS=kikaha.core.Main

# READ CUSTOM CONFIGURATIONS
if [ -e bin/kikaha.conf ]; then
	. bin/kikaha.conf
fi

# MAIN
CLASSPATH=".:${LIBDIR}/*"
${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS}
