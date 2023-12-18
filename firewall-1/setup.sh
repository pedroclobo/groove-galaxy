#!/bin/bash

HOSTNAME="firewall-1"
IP0="192.168.0.254"
IP1="192.168.1.254"

# Install dependencies
echo iptables-persistent iptables-persistent/autosave_v4 boolean true | debconf-set-selections
echo iptables-persistent iptables-persistent/autosave_v6 boolean true | debconf-set-selections
apt install iptables-persistent -y

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

# Apply rules
iptables -F
iptables -A INPUT -j DROP # block all input traffic
iptables -A OUTPUT -j DROP # block all output traffic
iptables -A FORWARD -s 192.168.0.0/24 -d 192.168.1.0/24 -p tcp --sport 5432 -j ACCEPT # allow traffic from database to the DMZ on source port 5432
iptables -A FORWARD -s 192.168.1.0/24 -d 192.168.0.0/24 -p tcp --dport 5432 -j ACCEPT # allow traffic from DMZ to database on destination port 5432
iptables -A FORWARD -j DROP # block all other traffic
ip6tables -A INPUT -j DROP # block all input ipv6 traffic
ip6tables -A FORWARD -j DROP # block all forwarded ipv6 traffic
ip6tables -A OUTPUT -j DROP # block all output ipv6 traffic

# Save rules
sh -c 'iptables-save > /etc/iptables/rules.v4'
sh -c 'ip6tables-save > /etc/iptables/rules.v6'

# Persist rules
systemctl enable --now netfilter-persistent.service
