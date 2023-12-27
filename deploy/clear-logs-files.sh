directory=$1

# Check if the target is not a directory
if [ ! -d "$directory" ]; then
  exit 1
fi

rm -f "$directory"/*.gz
# Loop through files in the target directory
for file in "$directory"/*; do
  if [ -f "$file" ]; then
    echo "$file"
    echo -n "" > "$file"
  fi
done