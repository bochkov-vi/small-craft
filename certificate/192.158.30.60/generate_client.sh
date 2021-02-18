#!/bin/bash


echo 'удаление client.p12'
rm client.p12

echo 'запись ../rootCA в client.p12'
keytool -import -alias root -keystore client.p12 -trustcacerts -file ../rootCA.crt -storepass 123456 -noprompt



echo 'генерация client сертификата'
keytool -genkey -alias client -keyalg RSA -keystore client.p12 -storetype PKCS12 -storepass 123456 -keypass 123456 -dname "CN=client,OU=SO,O=PUVAR,L=PK,ST=Kamchatka,C=RU" -validity 999999
echo 'создание запроса client'
keytool -certreq -alias client -file X.csr -keystore client.p12 -storetype PKCS12 -storepass 123456
echo 'подписываем сертификат client'
openssl x509 -req -in X.csr -CA ../rootCA.crt -CAkey ../rootCA.key -CAcreateserial -out X.crt -days 999999 -sha256
echo 'запись client  в client.p12'
keytool -import -alias client -keystore client.p12 -file X.crt  -storetype PKCS12 -storepass 123456 -noprompt
echo Итого
keytool -list -keystore client.p12 -storepass 123456




rm X.crt
rm X.csr
cp client.p12 conf/client.p12
