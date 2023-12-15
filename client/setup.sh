#!/bin/bash

HOSTNAME="client"
IP="192.168.2.1"
GATEWAY="192.168.2.254"

# Configure eth0 interface (sw-3)
cat <<EOL > /etc/network/interfaces
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

source /etc/network/interfaces.d/*

# The loopback network interface
auto lo eth0
iface lo inet loopback

# sw-3
iface eth0 inet static
	address $IP
	netmask 255.255.255.0
	gateway $GATEWAY

EOL

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts
