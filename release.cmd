@ECHO OFF
robocopy assets target/assets /e
copy "Database.db" "target/Database.db"
copy "pokedex.db" "target/pokedex.db"