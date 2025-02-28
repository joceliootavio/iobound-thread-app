#!/bin/sh

echo $SERVER_SSL_KEY_STORE

mkdir /app/certs

ls -la /app/certs

# Se o keystore não existir, gera um novo
if [ ! -f "$SERVER_SSL_KEY_STORE" ]; then
    echo "🔐 Gerando certificado SSL..."
    keytool -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 \
        -keystore "$SERVER_SSL_KEY_STORE" -validity 3650 \
        -dname "CN=java-app-mock, OU=Dev, O=Company, L=City, ST=State, C=BR" \
        -storepass changeit -keypass changeit

    keytool -exportcert -alias mycert -keystore "$SERVER_SSL_KEY_STORE" -storepass changeit -file /app/certs/mycert.cer -rfc
else
    echo "✅ Certificado SSL já existe, pulando a geração."
fi

ls -la /app/certs

exec "$@"
