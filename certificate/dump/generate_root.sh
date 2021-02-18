#!/bin/bash

#создаем наше CA
echo 'генерация закрытого ключа'
openssl genrsa -out rootCA.key 2048
echo 'генерация сертификата открытого ключа'
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 999999 -out rootCA.crt -subj "/CN=bochkov.com/OU=SO/O=PUVAR/L=PK/ST=Kamchatka/C=RU"
#openssl x509 -in rootCA.crt -out rootCA.pem -outform PEM
