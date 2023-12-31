#!/bin/bash
#Run it like that: sh maven-install.sh 3.8.4 && source /etc/profile.d/maven.sh and you're good to go

TMP_MAVEN_VERSION=${1:-"3.8.4"}
USER=$2

# Download Maven
DOWNLOAD_DIR="/tmp"
MAVEN_BASE="/opt"

APACHE_ARCHIVE="archive.apache.org"
TAR_NAME="apache-maven-$TMP_MAVEN_VERSION"
TAR="$TAR_NAME-bin.tar.gz"
URL4="https://$APACHE_ARCHIVE/dist/maven/maven-3/$TMP_MAVEN_VERSION/binaries/$TAR -P $DOWNLOAD_DIR"

MAVEN_DIR="$MAVEN_BASE/$TAR_NAME"

echo "checking file '$MAVEN_DIR' exists"
if ! test -d $MAVEN_DIR; then
  wget -U "Any User Agent" -q $URL4
  tar -xvf "$DOWNLOAD_DIR/$TAR"
  sudo mv "$TAR_NAME" /opt/
else
  echo "\"$MAVEN_DIR\" already exists"
fi

M2_HOME="$MAVEN_BASE/$TAR_NAME"
PATH="$M2_HOME/bin:$PATH"

export PATH
echo "setting up PATH=$PATH"
