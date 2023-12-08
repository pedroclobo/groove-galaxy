#!/bin/bash

HOSTNAME="database"
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
	address 192.168.0.1
	netmask 255.255.255.0

EOL

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts

# Install dependencies
apt install postgresql -y
systemctl enable postgresql

# Change password and create database
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"
sudo -u postgres createdb $DB_NAME

# Configure postgresql
cp /etc/postgresql/15/main/postgresql.conf /etc/postgresql/15/main/postgresql.conf.bak
cp /etc/postgresql/15/main/pg_hba.conf /etc/postgresql/15/main/pg_hba.conf.bak
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/15/main/postgresql.conf
grep -q "host\tall\t\tall192.168.0.0/24\t\tscram-sha-256" /etc/postgresql/15/main/pg_hba.conf || echo "host\tall\t\tall192.168.0.0/24\t\tscram-sha-256" >> /etc/postgresql/15/main/pg_hba.conf
