#!/bin/bash
set -e

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] - $1"
}

log "Starting SQL Server initialization script..."

/opt/mssql/bin/sqlservr &
sql_server_pid=$!
log "SQL Server process started with PID: $sql_server_pid"

log "Waiting for SQL Server to accept connections..."
retries=10
for i in $(seq 1 $retries); do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -Q "SELECT 1" -N -C &>/dev/null
    if [ $? -eq 0 ]; then
        log "SQL Server is ready."
        break
    else
        log "Not ready yet... waiting 5 seconds. (Attempt $i of $retries)"
        sleep 5
    fi
done

log "Executing init.sql to create crm_db..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -i "/usr/src/app/init/init.sql" -N -C
log "Database initialization script executed."

log "Initialization complete. SQL Server is running."
wait $sql_server_pid 