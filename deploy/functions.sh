function message() {
  echo '--- ['$(date +'%H:%M:%S %d.%m.%Y')'] --- ' $1
}

function checkResult() {
  if [[ $1 -ne 0 ]]; then
    message "ERROR on $2"
    exit $1
  else
    message "DONE $2"
  fi
}

function checkArg() {
  local value="$1"
  local number="$2"
  local -n possible=$3

  local result=0
  for i in "${possible[@]}"; do
    if [[ "$i" == "$value" ]]; then
      result=1
    fi
  done

  if [ $result == 0 ]; then
    echo "arg#$number:$value are NOT in [$(printArr possible)]"
    exit 0
  fi
}

function shouldBeEmpty() {
    if [ -z "$1" ]; then
        echo "Exceeding maximal number or arguments: $2"
        echo "Argument '$1', includes after, are unnecessary"
        exit 1
    fi
}

function printArr() {
  local -n array=$1
  delim=""
  joined=""
  for item in "${array[@]}"; do
    item=${item:-"<empty>"}
    joined="$joined$delim$item"
    delim=","
  done
  echo "$joined"
}

function checkTestArg() {
    args=("$TEST_ARG" '')
    checkArg "$1" 1 args
}

function checkProfileArg() {
    args=("dev" "default" "")
    checkArg "$1" 2 args
}
function checkRmiArg() {
    args=("rmi" "")
    checkArg "$1" 1 args
}

