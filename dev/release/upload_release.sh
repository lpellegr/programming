#! /bin/sh

VERSION=$2
TMP=/tmp

# /mnt/scratch is a tmpfs mount point for faster builds on schubby
if [ -w "/mnt/scratch" ] ; then
	TMP=/mnt/scratch
fi

workingDir=`dirname $0`

function warn_and_exit {
	echo "$1" 1>&2
	exit 1
}


function check_dir {
	if [ ! -d "$1" ] ; then
		warn_and_exit "$1 does not exist"
	fi
}



DIST_BASE=/net/servers/www-sop/teams/oasis/proactive/dist
check_dir "$DIST_BASE"

DIST_PA=$DIST_BASE/ProActive
check_dir "$DIST_PA"

DIST_PAPROG=$DIST_PA/Programming
check_dir "$DIST_PAPROG"

RELEASE_DIR=$DIST_PAPROG/$VERSION
if [ -d "$RELEASE_DIR" ] ; then
	warn_and_exit "Release directory already exists. Aborted..."
fi

echo "Building the release"
$workingDir/build_release.sh "$@" $TMP || warn_and_exit "Build failed"

mkdir "$RELEASE_DIR"
cp $TMP/ProActive-${VERSION}.tar.gz "$RELEASE_DIR"
cp $TMP/ProActive-${VERSION}.zip    "$RELEASE_DIR"
