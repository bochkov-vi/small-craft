#!/bin/bash

echo 'удаление tomcat из tomcat.p12'
keytool -delete -alias tomcat -keystore tomcat.p12 -storepass 123456
echo 'удаление root из tomcat.p12'
keytool -delete -alias root -keystore tomcat.p12 -storepass 123456
echo 'запись rootCA в tomcat.p12'
keytool -import -alias root -keystore tomcat.p12 -trustcacerts -file rootCA.crt -storepass 123456 -noprompt


echo 'удаление localhost из tomcat.p12'
keytool -delete -alias localhost -keystore tomcat.p12 -storepass 123456
echo 'генерация localhost сертификата'
keytool -genkey -alias localhost -keyalg RSA -keystore tomcat.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=localhost,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999999
echo 'создание запроса localhost'
keytool -certreq -alias localhost -file localhost.csr -keystore tomcat.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат localhost'
openssl x509 -req -in localhost.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out localhost.crt -days 999999 -sha256
echo 'запись localhost  в tomcat.p12'
keytool -import -alias localhost -keystore tomcat.p12 -file localhost.crt -trustcacerts -storetype PKCS12 -storepass 123456 -noprompt


echo 'удаление 192.168.30.10 из tomcat.p12'
keytool -delete -alias 192.168.30.10 -keystore tomcat.p12 -storepass 123456
echo 'генерация 192.168.30.10 сертификата'
keytool -genkey -alias 192.168.30.10 -keyalg RSA -keystore tomcat.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=192.168.30.10,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999999
echo 'создание запроса 192.168.30.10'
keytool -certreq -alias 192.168.30.10 -file 192.168.30.10.csr -keystore tomcat.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат 192.168.30.10'
openssl x509 -req -in 192.168.30.10.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out 192.168.30.10.crt -days 999999 -sha256
echo 'запись 192.168.30.10  в tomcat.p12'
keytool -import -alias 192.168.30.10 -keystore tomcat.p12 -file 192.168.30.10.crt -trustcacerts -storetype PKCS12 -storepass 123456 -noprompt