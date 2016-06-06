#!/usr/bin/env bash

# FUNCTIONS
start_server(){
	debug_server
}

debug_server(){
	info "Starting server in debug mode..."
	${JAVA} ${JAVA_OPTS} -classpath "${CLASSPATH}" ${MAIN_CLASS}
}

stop_server(){
	warn "Stop command not available at Windows"
}

# VARIABLES
CLASSPATH="${LIBDIR}/*;."