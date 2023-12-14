docker-compose up -d --force-recreate task_management_system_db
CUR_DIR=$(pwd)
cd ..
./gradlew runWithJavaExec
cd "$CUR_DIR"