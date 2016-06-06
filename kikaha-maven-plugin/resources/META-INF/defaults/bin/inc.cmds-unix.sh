#!/usr/bin/env bash

# FUNCTIONS
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

debug_server(){
	PID=$(retrieve_server_pid)
	if [ ! "$PID" = "" ]; then
		warn "Server already running"
		exit 1
	else
		info "Starting server in debug mode..."
		${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS}
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

# VARIABLES
CLASSPATH="${LIBDIR}/*:."