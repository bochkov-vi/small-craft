sudo -u postgres psql -c 'create database smallcraft;'
sudo -u postgres psql -c "create user smallcraft password 'smallcraft';"
sudo -u postgres psql -c 'alter database smallcraft owner to smallcraft;'

