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
 $(grape 'write fast microservices faster')

EOF
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
	echo "$(yellow '[INFO]') $@"
}

warn(){
	echo "$(red '[WARN]') $@"
}
