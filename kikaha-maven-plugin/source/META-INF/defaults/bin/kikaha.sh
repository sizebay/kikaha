#!/bin/sh
cd "`dirname $(readlink -f $0)`/.."

# CONFIGURABLE VARIABLES
LIBDIR="./lib"

# RUNTIME VARIABLES
JAVA=java
JAVA_OPTS=
MAIN_CLASS=kikaha.core.Main
OUTPUTFILE=server.stdout.log
PIDFILE=server.pid
NULL=/dev/null

print_logo(){
if [ ! "$QUIET" = "true" ]; then
cat <<EOF
      $(yellow " __  _  ____  __  _   ____  __ __   ____ ")
      $(yellow "|  |/ ]|    ||  |/ ] /    ||  |  | /    |")
      $(yellow "|  ' /  |  | |  ' / |  o  ||  |  ||  o  |")
      $(yellow "|    \  |  | |    \ |     ||  _  ||     |")
      $(yellow "|     | |  | |     ||  _  ||  |  ||  _  |")
      $(yellow "|  .  | |  | |  .  ||  |  ||  |  ||  |  |")
      $(yellow "|__|\_||____||__|\_||__|__||__|__||__|__|")
 $(grape 'an absurdly fast web server designed for microservices')

EOF
fi
}

yellow(){
        echo -n "\033[1;33m$@\033[m"
}

red(){
        echo -n "\033[1;31m$@\033[m"
}

grape(){
        echo -n "\033[1;35m$@\033[m"
}

start_server(){
	if [ -e $PIDFILE ]; then
		red "[WARN] Server already running"
	else
		yellow "[INFO] Starting server in background..."
		nohup ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} > $NULL 2> $NULL &
		echo "$!" > $PIDFILE
	fi
}

stop_server(){
	if [ -e $PIDFILE ]; then
		PID=`cat $PIDFILE`
		FOUNDPROCS=`ps aux | grep $PID | head -n 1 | grep java`
		if [ ! "$FOUNDPROCS" = "" ]; then
			kill $PID && echo "kill term sent." && rm -f $PIDFILE
		fi
	else
		red "[WARN] Server not running"
		exit 1
	fi
}

show_help(){
	echo "Usage: $0 <command>"
	echo
	echo "Available commands:"
	echo " - start:	starts the server in background"
	echo " - stop:	stops the server that is running in background"
	echo " - restart: restarts the server in background"
	echo " - debug:	run the server in foreground"
	echo " - help:	shows this help message"
	echo
	echo
}

# READ CUSTOM CONFIGURATIONS
if [ -e bin/kikaha.conf ]; then
	. bin/kikaha.conf
fi

# MAIN
CLASSPATH=".:${LIBDIR}/*"

print_logo
case "$1" in
	"stop" ) stop_server ;;
	"debug" ) ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} ;;
	"start" ) start_server ;;
	"restart" ) stop_server && start_server ;;
	"help" | * ) show_help ;;
esac
