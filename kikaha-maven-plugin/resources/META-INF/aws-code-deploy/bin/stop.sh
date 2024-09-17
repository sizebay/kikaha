#!/bin/sh
if [ -d /opt/application ]; then
	cd /opt/application
	chmod +x ./bin/kikaha.sh
	./bin/kikaha.sh is_running
	status=$?
	if [ "$status" = "1" ]; then
		./bin/kikaha.sh stop || exit 1
	else
		exit 0
	fi
fi
