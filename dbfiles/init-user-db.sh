#!/bin/bash
set -e

echo "🚀 Ejecutando script de inicialización..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE pintter;
    GRANT ALL PRIVILEGES ON DATABASE pintter TO $POSTGRES_USER;
    \echo '✅ Base de datos pintter creada'
EOSQL