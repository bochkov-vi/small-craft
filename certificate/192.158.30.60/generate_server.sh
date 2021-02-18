#!/bin/bash

echo 'удаление server.p12'
rm server.p12
echo 'запись ../rootCA в server.p12'
keytool -import -alias root -keystore server.p12 -trustcacerts -file ../rootCA.crt -storepass 123456 -noprompt




#echo 'генерация 192.168.30.60 сертификата'
#keytool -genkey -alias 192.168.30.60 -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=192.168.30.60,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
#echo 'создание запроса 192.168.30.60'
#keytool -certreq -alias 192.168.30.60 -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456 -ext SAN=dns:192.168.30.60
#keytool -printcertreq -file X.csr
#echo 'подписываем сертификат 192.168.30.60'
#openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
#echo 'запись 192.168.30.60  в server.p12'
#keytool -import -alias 192.168.30.60 -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
#echo Итого
#keytool -list -keystore server.p12 -storepass 123456

echo 'генерация localhost сертификата'
keytool -genkey -alias localhost -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=localhost,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
echo 'создание запроса localhost'
keytool -certreq -alias localhost -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456 -ext SAN=dns:localhost,ip:192.168.30.60,ip:127.0.0.1,ip:::1
keytool -printcertreq -file X.csr
echo 'подписываем сертификат localhost'
openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
echo 'запись localhost  в server.p12'
keytool -import -alias localhost -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
echo Итого
keytool -list -keystore server.p12 -storepass 123456


#echo 'генерация inzh-60 сертификата'
#keytool -genkey -alias inzh-60 -keyalg RSA -keystore server.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=inzh-60,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999
#echo 'создание запроса inzh-60'
#keytool -certreq -alias inzh-60 -file X.csr -keystore server.p12 -storetype PKCS12 -storepass 123456 -ext SAN=dns:inzh-60
#keytool -printcertreq -file X.csr
#echo 'подписываем сертификат inzh-60'
#openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
#echo 'запись inzh-60  в server.p12'
#keytool -import -alias inzh-60 -keystore server.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
#echo Итого
#keytool -list -keystore server.p12 -storepass 123456

#keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -srcstorepass 123456 -deststorepass 123456

cp server.p12 conf/server.p12
cp server.p12 jetty/server.p12
#rm X.csr
#rm X.crt