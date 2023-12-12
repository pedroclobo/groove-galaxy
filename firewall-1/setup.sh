#!/bin/bash

HOSTNAME="firewall-1"
IP0="192.168.0.254"
IP1="192.168.1.254"

# Configure eth0 and eth1 interfaces (sw-1, sw-2)
cat <<EOL > /etc/network/interfaces
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

source /etc/network/interfaces.d/*

# The loopback network interface
auto lo eth0 eth1
iface lo inet loopback

# sw-1
iface eth0 inet static
	address $IP0
	netmask 255.255.255.0

# sw-2
iface eth1 inet static
	address $IP1
	netmask 255.255.255.0

EOL

# Enable IPv4 forwarding
sed -i "s/#net.ipv4.ip_forward=1/net.ipv4.ip_forward=1/g" /etc/sysctl.conf

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts
