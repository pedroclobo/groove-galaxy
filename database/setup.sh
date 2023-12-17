#!/bin/bash

HOSTNAME="database"
IP="192.168.0.1"
SUBNET="192.168.1.0/24"
GATEWAY="192.168.0.254"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_NAME="groove"

# Configure eth0 interface (sw-1)
cat <<EOL > /etc/network/interfaces
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

source /etc/network/interfaces.d/*

# The loopback network interface
auto lo eth0
iface lo inet loopback

# sw-1
iface eth0 inet static
	address $IP
	netmask 255.255.255.0
	gateway $GATEWAY

EOL

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts

# Install dependencies
apt install postgresql-16 -y

# Generate RSA key pair
sudo -u $SUDO_USER openssl genrsa -out database.key

# Generate certificate sign request
sudo -u $SUDO_USER openssl req -new \
	-key database.key \
	-out database.csr \
	-subj "/C=PT/L=Lisbon/O=SIRS/OU=GrooveGalaxy/CN=Database"

# Ask user to copy database certificate request
systemctl start ssh
echo "This step requires manual intervention"
echo "Before continuing, the application needs to sign the database certificate request"
echo "Please follow the instructions in the README.md and press any key when done"
echo "When you are finished, press any key to continue"
read -r

# Wait for user to copy database certificate to this machine
echo "This step requires manual intervention"
echo "The application has signed the database certificate request, and it need to be copied back to this machine"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Remove certificate sign request
rm database.csr

# Copy keys and certificate
mv database.key /etc/ssl/private/
mv database.crt /etc/ssl/certs/
mv application.crt /etc/ssl/certs/
chmod 600 /etc/ssl/certs/database.crt
chown postgres /etc/ssl/certs/database.crt
chmod 600 /etc/ssl/certs/application.crt
chown postgres /etc/ssl/certs/application.crt
chmod 600 /etc/ssl/private/database.key
chown postgres /etc/ssl/private/database.key

# Configure postgresql
cp /etc/postgresql/16/main/postgresql.conf /etc/postgresql/16/main/postgresql.conf.bak
cp /etc/postgresql/16/main/pg_hba.conf /etc/postgresql/16/main/pg_hba.conf.bak
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/16/main/postgresql.conf
sed -i "s/port = 5433/port = 5432/g" /etc/postgresql/16/main/postgresql.conf
sed -i "s/ssl_cert_file = '\/etc\/ssl\/certs\/ssl-cert-snakeoil.pem'/ssl_cert_file = '\/etc\/ssl\/certs\/database.crt'/g" /etc/postgresql/16/main/postgresql.conf
sed -i "s/#ssl_ca_file = ''/ssl_ca_file = '\/etc\/ssl\/certs\/application.crt'/g" /etc/postgresql/16/main/postgresql.conf
sed -i "s/ssl_key_file = '\/etc\/ssl\/private\/ssl-cert-snakeoil.key'/ssl_key_file = '\/etc\/ssl\/private\/database.key'/g" /etc/postgresql/16/main/postgresql.conf
grep -q --perl-regex "hostssl\t$DB_NAME\t\t$DB_USER\t\t$SUBNET\t\tmd5" /etc/postgresql/16/main/pg_hba.conf || echo -e "hostssl\t$DB_NAME\t\t$DB_USER\t\t$SUBNET\t\tmd5" >> /etc/postgresql/16/main/pg_hba.conf

# Enable postgresql
systemctl enable --now postgresql@16-main

# Change password and create database
sudo -u postgres psql -c "ALTER USER $DB_USER WITH PASSWORD '$DB_PASSWORD';"
sudo -u postgres createdb $DB_NAME
