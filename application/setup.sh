#!/bin/bash

HOSTNAME="application"
IP="192.168.1.1"

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
	post-up ip route add 192.168.0.0/24 via 192.168.1.254 dev eth0
	post-up ip route add 192.168.2.0/24 via 192.168.1.253 dev eth0

EOL

# Ask user for passwords
echo -n "Enter a password to protect the private key: "
read -rs KEYPASS
echo
echo -n "Enter a password to protect the keystore: "
read -rs STOREPASS
echo

# Generate RSA key pair and self-signed certificate
sudo -u $SUDO_USER keytool -genkey \
	-alias application \
	-keyalg RSA -keysize 2048 -keypass $KEYPASS \
	-keystore application-keystore.jks -storepass $STOREPASS \
	-dname "C=PT, L=Lisbon, O=SIRS, OU=GrooveGalaxy, CN=Application" \
	-validity 365 \
	-ext "SAN=IP:$IP"

# Ask user to copy certificates
systemctl start ssh
echo "This step requires manual intervention"
echo "For the application to sign the database and client certificates, the certificates need to be copied to the current folder"
echo "Setup the client and database machines until asked to copy the certificates to the application machine"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Sign database certificate
sudo -u $SUDO_USER keytool -gencert \
	-alias application \
	-keyalg RSA -keypass $KEYPASS \
	-keystore application-keystore.jks -storepass $STOREPASS \
	-infile database.csr \
	-outfile database.der

# Convert the database certificate
sudo -u $SUDO_USER openssl x509 \
	-inform der \
	-in database.der \
	-out database.crt

# Sign client certificate
sudo -u $SUDO_USER keytool -gencert \
	-alias application \
	-keyalg RSA -keypass $KEYPASS \
	-keystore application-keystore.jks -storepass $STOREPASS \
	-infile client.csr \
	-outfile client.der

# Convert the client certificate
sudo -u $SUDO_USER openssl x509 \
	-inform der \
	-in client.der \
	-out client.crt

# Extract application certificate from keystore
sudo -u $SUDO_USER keytool -exportcert \
	-alias application \
	-keystore application-keystore.jks -storepass $STOREPASS \
	-file application.der

# Convert the application certificate
sudo -u $SUDO_USER openssl x509 \
	-inform der \
	-in application.der \
	-out application.crt

echo "This step requires manual intervention"
echo "The application has signed the database and client certificates, and these need to be copied back to the respective machines"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Add the client certificate into the trusted keystore
sudo -u $SUDO_USER keytool -import \
	-trustcacerts -file client.crt \
	-keypass $KEYPASS \
	-keystore application-keystore.jks -storepass $STOREPASS

# Convert .crt to .pem
sudo -u $SUDO_USER mv application.crt application.pem

# Generate application.p12 from application-keystore.jks
sudo -u $SUDO_USER keytool -importkeystore \
	-srckeystore application-keystore.jks -srcstorepass $STOREPASS \
	-destkeystore application.p12 -deststoretype PKCS12 -deststorepass $STOREPASS

echo "This step requires manual intervention"
echo "The application has added the client certificate to its trusted keystore"
echo "The keystore need to be copied to the client machine"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Move application-keystore into application resources folder
sudo -u $SUDO_USER mv application-keystore.jks /home/$SUDO_USER/t52-andre-pedro-goncalo/application/src/main/resources

# Configure Spring Boot's SSL
cat <<EOL >> ./src/main/resources/application.properties
server.port=8443
server.ssl.enable=true
server.ssl.key-store=classpath:application-keystore.jks
server.ssl.key-store-password=$STOREPASS
server.ssl.key-alias=application
server.ssl.key-password=$KEYPASS

EOL

# Remove certificate requests
rm *.csr *.der

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts

# Install dependencies
apt install maven iptables-persistent -y

# Apply firewall rules
iptables -A FORWARD -j DROP # drop all forwarded traffic
iptables -A INPUT -s 192.168.0.0/24 -p tcp --sport 5432 -j ACCEPT # accept traffic from internal network from port 5432
iptables -A INPUT -s 192.168.2.0/24 -p tcp --dport 8443 -j ACCEPT # accept traffic from external network to port 8443
iptables -A INPUT -j DROP # drop all other incoming packets
iptables -A OUTPUT -d 192.168.2.0/24 -m state --state NEW -j DROP # don't start new connections with external network
iptables -A OUTPUT -d 192.168.2.0/24 -p tcp --sport 8443 -j ACCEPT # allow traffic from established connections from port 8443 to external network
iptables -A OUTPUT -d 192.168.0.0/24 -p tcp --dport 5432 -j ACCEPT # allow traffic to internal network to port 5432
iptables -A OUTPUT -j DROP # drop all other output traffic
ip6tables -A INPUT -j DROP # block all input ipv6 traffic
ip6tables -A FORWARD -j DROP # block all forwarded ipv6 traffic
ip6tables -A OUTPUT -j DROP # block all output ipv6 traffic

# Save firewall rules
sh -c 'iptables-save > /etc/iptables/rules.v4'
sh -c 'ip6tables-save > /etc/iptables/rules.v6'

# Persist firewall rules
systemctl enable --now netfilter-persistent.service
