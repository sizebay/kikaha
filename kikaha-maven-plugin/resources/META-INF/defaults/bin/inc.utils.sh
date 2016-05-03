#!/usr/bin/env bash
print_logo(){
if [ ! "$QUIET" = "true" ]; then
	info $(grape 'Kikaha: write fast microservices faster')
fi
}

yellow(){
        printf "\033[1;33m$@\033[m"
}

red(){
        printf "\033[1;31m$@\033[m"
}

grape(){
        printf "\033[1;35m$@\033[m"
}

info(){
	echo "$(yellow '::') $@"
}

warn(){
	echo "$(red '::') $@"
}
