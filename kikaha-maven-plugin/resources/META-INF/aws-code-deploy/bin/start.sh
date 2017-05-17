#!/bin/sh
if [ -d /opt/application ]; then
	cd /opt/application
	chmod +x ./bin/kikaha.sh
	./bin/kikaha.sh start
fi