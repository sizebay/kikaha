#!/bin/sh
cd "`dirname $(readlink -f $0)`/.."
. ././bin/inc.utils.sh

# CONFIGURABLE VARIABLES
LIBDIR="./lib"

# RUNTIME VARIABLES
JAVA="java -Dconfig.app.dir=`pwd`"
JAVA_OPTS=
MAIN_CLASS=kikaha.core.cdi.ApplicationRunner
NULL=/dev/null

show_help(){
cat<<EOF
Usage: $0 <command>"

$(yellow 'Available commands'):
 - $(red start):	starts the server in background
 - $(red stop):	stops the server that is running in background
 - $(red restart):	restarts the server in background
 - $(red debug):	run the server in foreground
 - $(red help):	shows this help message

EOF
}

# READ CUSTOM CONFIGURATIONS
if [ -e ./bin/kikaha.conf ]; then
	. ./bin/kikaha.conf
fi

# MAIN
case "`uname`" in
CYGWIN*) . ./bin/inc.cmds-cygwin.sh;;
*) . ./bin/inc.cmds-unix.sh;;
esac

case "$1" in
	"stop" ) stop_server ;;
	"debug" ) debug_server ;;
	"start" ) start_server ;;
	"restart" ) stop_server && start_server ;;
	"is_running" )
		pid=$(retrieve_server_pid)
		if [ ! "$pid" = "" ]; then
			exit 1
		else
			exit 0
		fi
	;;
	"version" )
		print_logo
		info "Version: ${project.version}"
	;;
	"help" | * )
		print_logo
		show_help
	;;
esac
