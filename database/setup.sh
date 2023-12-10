#!/bin/bash

HOSTNAME="database"
DB_NAME="groove"
IP="192.168.0.1"
SUBNET="192.168.0.0/24"
GATEWAY=""

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

EOL

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts

# Install dependencies
apt install postgresql-16 -y

# Configure postgresql
cp /etc/postgresql/16/main/postgresql.conf /etc/postgresql/16/main/postgresql.conf.bak
cp /etc/postgresql/16/main/pg_hba.conf /etc/postgresql/16/main/pg_hba.conf.bak
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/16/main/postgresql.conf
sed -i "s/port = 5433/port = 5432/g" /etc/postgresql/16/main/postgresql.conf
grep -q --perl-regex "host\tall\t\tall\t\t$SUBNET\t\tscram-sha-256" /etc/postgresql/16/main/pg_hba.conf || echo -e "host\tall\t\tall\t\t$SUBNET\t\tscram-sha-256" >> /etc/postgresql/16/main/pg_hba.conf

# Enable postgresql
systemctl enable --now postgresql@16-main

# Change password and create database
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"
sudo -u postgres createdb $DB_NAME
