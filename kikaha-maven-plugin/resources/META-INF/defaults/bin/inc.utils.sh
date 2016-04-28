print_logo(){
if [ ! "$QUIET" = "true" ]; then
cat <<EOF

      $(yellow "    __   _ __         __         ")
      $(yellow "   / /__(_) /______ _/ /_  ____ _")
      $(yellow "  / //_/ / //_/ __ \`/ __ \\/ __ \`/")
      $(yellow " / ,< / / ,< / /_/ / / / / /_/ / ")
      $(yellow "/_/|_/_/_/|_|\\__,_/_/ /_/\\__,_/  ")
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
	echo "$(yellow '::') $@"
}

warn(){
	echo "$(red '::') $@"
}
