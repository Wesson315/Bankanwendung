$env:MYSQLDATADIR="$pwd/mymysql/mysqldata"

& docker-compose up --force-recreate --renew-anon-volumes --remove-orphans

# Rebuild erzwingen
#& docker-compose up --rebuild

# Wenn irgendwas schief läuft:
# Mit --verbose genau sehen was docker-compose macht
# & docker-compose --verbose up --force-recreate --renew-anon-volumes --remove-orphans