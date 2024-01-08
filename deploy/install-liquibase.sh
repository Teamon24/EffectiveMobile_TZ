wget -O- https://repo.liquibase.com/liquibase.asc | gpg --dearmor > liquibase-keyring.gpg
cat liquibase-keyring.gpg | sudo tee /usr/share/keyrings/liquibase-keyring.gpg > /dev/null
echo 'deb [arch=amd64 signed-by=/usr/share/keyrings/liquibase-keyring.gpg] https://repo.liquibase.com stable main' | sudo tee /etc/apt/sources.list.d/liquibase.list
sudo apt-get update
sudo apt-get install liquibase

LIQUIBASE_HOME=$(whereis liquibase | cut -d " " -f 2)
[[ -z "$LIQUIBASE_HOME" ]] && { echo "liquibase home is empty"; exit 1; }
echo "$LIQUIBASE_HOME"
PATH="$LIQUIBASE_HOME:$PATH"
echo "after adding $LIQUIBASE_HOME:"
echo "PATH=$PATH"