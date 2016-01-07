#!/bin/sh
if [ ! "$#" = "1" ]; then
	echo "Usage: $0 package.zip"
	exit 1
fi

ZIPFILE=`readlink -f $1`

cd "`dirname $(readlink -f $0)`/.."
. ./bin/inc.utils.sh

if [ ! -f $ZIPFILE ]; then
	warn "Package not found: $ZIPFILE"
	exit 1
fi

# VARIABLES
BLACKLIST=./bin/kikaha-update.blacklist
tmp_dir="tmp/$$"

# FUNCTIONS
blacklist_content(){
	cat $BLACKLIST | egrep -v '^#'
}

remove_non_blacklisted_files(){
	files=`list_files_bl . $(blacklist_content)`
	rm -rf $files
}

move_new_files_to_the_root(){
	files=`list_files_wl $tmp_dir/* $(blacklist_content)`
	rm -rf $files
	cp -R $tmp_dir/*/* .
}

# list files at a specif directory. it will receive a list
# of names that will be ignored from search (blacklisted) 
list_files_bl(){
	dir=$1
	shift 1
	ignore="";
	for fignore in "$@"; do
		ignore=${ignore}"-not -wholename $dir/${fignore} "
	done
	find . -type f $ignore -not -wholename $dir'/tmp/*'
}

# list files at a specif directory. it will receive a list
# of names that will should be included in the search results (whitelisted) 
list_files_wl(){
	dir=$1
	shift 1
	for fignore in "$@"; do
		echo "$dir/${fignore} "
	done
}

extract_new_version_to_tmp_dir(){
	zip_file=`readlink -f $1`
	cd $tmp_dir
	unzip -q $zip_file -x
	cd - >/dev/null
}

print_logo
info "========================================================"
info "Updating app with package $ZIPFILE..."
info "========================================================"
echo

info "Creating tmp dir: $tmp_dir"
mkdir -p $tmp_dir

info "Extracting package..."
extract_new_version_to_tmp_dir $ZIPFILE

info "Removing files that is not blacklisted..."
remove_non_blacklisted_files $(blacklist_content)

info "Copying new files..."
move_new_files_to_the_root

info "Cleaning up..."
rm -rf tmp 
chmod +x bin/*.sh

info 'Done!'

