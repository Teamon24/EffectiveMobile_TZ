function printJavaHome() {
    TITLE=${1:-""}
    echo "============================== $TITLE ====================================="
    echo "JAVA_HOME=$JAVA_HOME"
    echo "PATH=$PATH"
}

function existsInList() {
    LIST=$1
    DELIMITER=$2
    VALUE=$3
    LIST_WHITESPACES=`echo $LIST | tr "$DELIMITER" " "`
    for x in $LIST_WHITESPACES; do
        if [ "$x" = "$VALUE" ]; then
            return 0
        fi
    done
    return 1
}

function isANumber() {
    ARG_NAME=$2
    ARG=$1
    re='^[0-9]+$'
    if ! [[ $ARG =~ $re ]] ; then
       echo "$ARG_NAME: '$ARG' is not a number" >&2; exit 1
    fi
}