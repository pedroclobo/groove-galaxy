#!/bin/bash

HOSTNAME="firewall-2"
IP0="192.168.1.253"
IP1="192.168.2.254"

# Install dependencies
apt install iptables-persistent -y

# Configure eth0 and eth1 interfaces (sw-2, sw-3)
cat <<EOL > /etc/network/interfaces
# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

source /etc/network/interfaces.d/*

# The loopback network interface
auto lo eth0 eth1
iface lo inet loopback

# sw-2
iface eth0 inet static
	address $IP0
	netmask 255.255.255.0

# sw-3
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
iptables -A INPUT -j DROP # block all input traffic
iptables -A OUTPUT -j DROP # block all output traffic
iptables -A FORWARD -s 192.168.1.0/24 -d 192.168.2.0/24 -p tcp --sport 8443 -j ACCEPT # allow traffic from DMZ to the client on source port 8443
iptables -A FORWARD -s 192.168.2.0/24 -d 192.168.1.0/24 -p tcp --dport 8443 -j ACCEPT # allow traffic from external network to the DMZ on destination port 8443
iptables -A FORWARD -j DROP # block all other traffic
ip6tables -A INPUT -j DROP # block all input ipv6 traffic
ip6tables -A FORWARD -j DROP # block all forwarded ipv6 traffic
ip6tables -A OUTPUT -j DROP # block all output ipv6 traffic

# Save rules
sh -c 'iptables-save > /etc/iptables/rules.v4'
sh -c 'ip6tables-save > /etc/iptables/rules.v6'

# Persist rules
systemctl enable --now netfilter-persistent.service
