#!/bin/bash

#создаем наше CA
echo 'генерация закрытого ключа'
openssl genrsa -out rootCA.key 2048
echo 'генерация сертификата открытого ключа'
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 999999 -out rootCA.crt -subj "/CN=bochkov.com/OU=SO/O=PUVAR/L=PK/ST=Kamchatka/C=RU"
#openssl x509 -in rootCA.crt -out rootCA.pem -outform PEM


echo 'удаление tomcat из tomcat.p12'
keytool -delete -alias tomcat -keystore tomcat.p12 -storepass 123456
echo 'удаление root из tomcat.p12'
keytool -delete -alias root -keystore tomcat.p12 -storepass 123456
echo 'генерация tomcat сертификата'
keytool -genkey -alias tomcat -keyalg RSA -keystore tomcat.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=localhost,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999999
echo 'создание запроса tomcat'
keytool -certreq -alias tomcat -file tomcat.csr -keystore tomcat.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат tomcat'
openssl x509 -req -in tomcat.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out tomcat.crt -days 999999 -sha256
echo 'запись rootCA в tomcat.p12'
keytool -import -alias root -keystore tomcat.p12 -trustcacerts -file rootCA.crt -storepass 123456 -noprompt
echo 'запись tomcat  в tomcat.p12'
keytool -import -alias tomcat -keystore tomcat.p12 -file tomcat.crt -trustcacerts -storetype PKCS12 -storepass 123456 -noprompt




echo 'удаление client из client.p12'
keytool -delete -alias client -keystore client.p12 -storepass 123456
echo 'удаление root из client.p12'
keytool -delete -alias root -keystore client.p12 -storepass 123456
echo 'генерация client сертификата'
keytool -genkey -alias client -keyalg RSA -keystore client.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=client,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999999
echo 'создание запроса client'
keytool -certreq -alias client -file client.csr -keystore client.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат client'
openssl x509 -req -in client.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out client.crt -days 999999 -sha256
echo 'запись rootCA в client.p12'
keytool -import -alias root -keystore client.p12 -trustcacerts -file rootCA.crt -storepass 123456 -noprompt
echo 'запись client  в client.p12'
keytool -import -alias client -keystore client.p12 -file client.crt -trustcacerts -storetype PKCS12 -storepass 123456 -noprompt
