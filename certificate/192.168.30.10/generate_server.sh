#!/bin/bash

echo 'удаление server.p12'
rm server.p12
echo 'запись ../rootCA в server.p12'
keytool -import -alias root -keystore server.p12 -trustcacerts -file ../rootCA.crt -storepass 123456 -noprompt




echo 'генерация 192.168.30.10 сертификата'
keytool -genkey -alias 192.168.30.10 -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=192.168.30.10,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
echo 'создание запроса 192.168.30.10'
keytool -certreq -alias 192.168.30.10 -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456 -ext SAN=dns:localhost
echo 'подписываем сертификат 192.168.30.10'
openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
echo 'запись 192.168.30.10  в server.p12'
keytool -import -alias 192.168.30.10 -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
echo Итого
keytool -list -keystore server.p12 -storepass 123456

echo 'генерация localhost сертификата'
keytool -genkey -alias localhost -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=localhost,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
echo 'создание запроса localhost'
keytool -certreq -alias localhost -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456 -ext SAN=dns:localhost
echo 'подписываем сертификат localhost'
openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
echo 'запись localhost  в server.p12'
keytool -import -alias localhost -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
echo Итого
keytool -list -keystore server.p12 -storepass 123456

echo 'генерация app.so.kam.var сертификата'
keytool -genkey -alias app.so.kam.var -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=app.so.kam.var,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
echo 'создание запроса app.so.kam.var'
keytool -certreq -alias app.so.kam.var -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат app.so.kam.var'
openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
echo 'запись app.so.kam.var  в server.p12'
keytool -import -alias app.so.kam.var -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
echo Итого
keytool -list -keystore server.p12 -storepass 123456

#keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -srcstorepass 123456 -deststorepass 123456


#rm X.csr
#rm X.crt