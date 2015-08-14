#!/bin/sh
cd "`dirname $0`/.."

# CONFIGURABLE VARIABLES
LIBDIR="./lib"

# RUNTIME VARIABLES
JAVA=java
JAVA_OPTS=
MAIN_CLASS=kikaha.core.Main
OUTPUTFILE=server.stdout.log
PIDFILE=server.pid

start_server(){
	echo "Starting server in background..."
	nohup ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} > $OUTPUTFILE &
	echo "$!" > $PIDFILE
}

stop_server(){
	if [ -e $PIDFILE ]; then
		local PID=`cat $PIDFILE`
		local FOUNDPROCS=`ps aux | grep $PID | head -n 1 | grep java`
		if [ ! "$FOUNDPROCS" = "" ]; then
			kill $PID && echo "kill term sent."
			rm -f $PIDFILE
		fi
	else
		echo "Server not running"
		exit 1
	fi
}

show_help(){
	echo "Usage: $0 <command>"
	echo
	echo "Available commands:"
	echo " - start:	starts the server in background"
	echo " - stop:	stops the server that is running in background"
	echo " - debug: run the server in foreground"
	echo
	echo "When no command is set, it starts the server in background by default"
	echo

}

# READ CUSTOM CONFIGURATIONS
if [ -e bin/kikaha.conf ]; then
	. bin/kikaha.conf
fi

# MAIN
CLASSPATH=".:${LIBDIR}/*"

case "$1" in
	"help" ) show_help ;;
	"stop" ) stop_server ;;
	"debug" ) ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} ;;
	"start" | * ) start_server ;;
esac

