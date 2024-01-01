printTitle() {
    N=${2:-80}
    TITLE=$1
    EMPTY="$(repeat $(expr length "$1"))"
    PART="$(repeat $N)"

    echo "$PART $EMPTY $PART"
    echo "$PART $TITLE $PART"
    echo "$PART $EMPTY $PART"
}

repeat(){
    local start=1
    local end=${1:-80}
    local str="${2:-=}"
    local range=$(seq $start $end)
    for i in $range ; do echo -n "${str}"; done
}

printJavaHome() {
    TITLE=${1:-""}
    echo "============================== $TITLE ====================================="
    echo "JAVA_HOME=$JAVA_HOME"
    echo "PATH=$PATH"
}

existsInList() {
    LIST=$1
    DELIMITER=$2
    VALUE=$3
    LIST_WHITESPACES=$(split "$LIST" "$DELIMITER")
    for x in $LIST_WHITESPACES; do
        if [ "$x" = "$VALUE" ]; then
            return 0
        fi
    done
    return 1
}

printSplit() {
    LIST=$1
    for x in $LIST; do
        if [ "$x" = "$VALUE" ]; then
            echo x
        fi
    done
}

split() {
    STRING=$1
    DELIMITER=$2
    echo $STRING | tr "$DELIMITER" " "
}

isANumber() {
    ARG_NAME=$2
    ARG=$1
    re='^[0-9]+$'
    if ! [[ $ARG =~ $re ]] ; then
       echo "$ARG_NAME: '$ARG' is not a number" >&2; exit 1
    fi
}