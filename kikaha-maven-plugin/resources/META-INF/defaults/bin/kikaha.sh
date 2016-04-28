#!/bin/sh
cd "`dirname $(readlink -f $0)`/.."
. ./bin/inc.utils.sh

# CONFIGURABLE VARIABLES
LIBDIR="./lib"

# RUNTIME VARIABLES
JAVA="java -Dconfig.app.dir=`pwd`"
JAVA_OPTS=
MAIN_CLASS=kikaha.core.cdi.ApplicationRunner
NULL=/dev/null

retrieve_server_pid(){
	ps -o uid,pid,cmd ax | grep "config.app.dir=$(pwd)" | grep -v grep| head -n 1 | tr '\t' '@' | sed 's/  */@/g;s/^@//' | cut -d '@' -f 2
}

start_server(){
	PID=$(retrieve_server_pid)
	if [ ! "$PID" = "" ]; then
		warn "Server already running"
		exit 1
	else
		info "Starting server in background..."
		nohup ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} > $NULL 2> $NULL &
	fi
}

stop_server(){
	PID=$(retrieve_server_pid)
	if [ ! "$PID" = "" ]; then
		info "Sending graceful shutdown signal..."
		kill $PID && info "Signal sent." || exit 1
		retries=1
		while [ ! "$PID" = "" -a "$retries" -lt 10 ]; do
			sleep 1
			PID=$(retrieve_server_pid)
			retries=`expr $retries + 1`
		done
		info "Service was shut down."
	else
		warn "Server not running"
		exit 1
	fi
}

show_help(){
cat<<EOF
Usage: $0 <command>

$(yellow 'Available commands'):
 - $(red start):	starts the server in background
 - $(red stop):	stops the server that is running in background
 - $(red restart):	restarts the server in background
 - $(red debug):	run the server in foreground
 - $(red help):	shows this help message

EOF
}

# READ CUSTOM CONFIGURATIONS
if [ -e bin/kikaha.conf ]; then
	. bin/kikaha.conf
fi

# MAIN
CLASSPATH="${LIBDIR}/*:."

if [ ! "$NO_LOGO" = "true" ]; then
	print_logo
fi
case "$1" in
	"stop" ) stop_server ;;
	"debug" ) ${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS} ;;
	"start" ) start_server ;;
	"restart" ) stop_server && start_server ;;
	"help" | * ) show_help ;;
esac
