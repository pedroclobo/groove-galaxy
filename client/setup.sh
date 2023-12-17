#!/bin/bash

REPO="t52-andre-pedro-goncalo"
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

# Ask user for passwords
echo -n "Enter a password to protect the private key: "
read -rs KEYPASS
echo
echo -n "Enter a password to protect the keystore: "
read -rs STOREPASS
echo

# Generate RSA key pair and self-signed certificate
sudo -u $SUDO_USER keytool -genkey \
	-alias client \
	-keyalg RSA -keysize 2048 -keypass $KEYPASS \
	-keystore client-keystore.jks -storepass $STOREPASS \
	-dname "C=PT, L=Lisbon, O=SIRS, OU=GrooveGalaxy, CN=Client" \
	-validity 365 \
	-ext "SAN=IP:$IP"

# Generate certificate sign request
sudo -u $SUDO_USER keytool -certreq \
	-alias client \
	-keyalg RSA \
	-file client.csr \
	-keystore client-keystore.jks -storepass $STOREPASS

# Ask user to copy client certificate request
systemctl start ssh
echo "This step requires manual intervention"
echo "Before continuing, the application needs to sign the client certificate request"
echo "Please follow the instructions in the README.md and press any key when done"
echo "When you are finished, press any key to continue"
read -r

# Wait for user to copy client certificate to this machine
echo "This step requires manual intervention"
echo "The application has signed the client certificate request, and it need to be copied back to this machine"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Import application certificate as a trusted CA
sudo -u $SUDO_USER keytool -import \
	-trustcacerts -file application.crt \
	-keypass $KEYPASS \
	-keystore client-keystore.jks -storepass $STOREPASS \
	<<< "y"

# Re-import client certificate, signed by application CA
sudo -u $SUDO_USER keytool -import \
	-alias client \
	-file client.crt \
	-keystore client-keystore.jks -storepass $STOREPASS

# Wait for user to copy client certificate to this machine
echo "This step requires manual intervention"
echo "The application has added the client certificate to its trusted keystore"
echo "The keystore need to be copied to the this machine"
echo "Please follow the instructions in the README.md"
echo "When you are finished, press any key to continue"
read -r

# Remove certificate sign request
rm client.csr

# Convert Java keystore to PKCS12 keystore
sudo -u $SUDO_USER keytool -importkeystore \
	-srckeystore client-keystore.jks -srcstorepass $STOREPASS \
	-destkeystore client.p12 -deststoretype PKCS12 -deststorepass $STOREPASS

# Copy client keystore to maven resources directory
sudo -u $SUDO_USER mv client.p12 client-keystore.jks /home/$SUDO_USER/$REPO/client

# Change hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hostname
sed -i "s/kali/$HOSTNAME/g" /etc/hosts

# Install dependencies
apt install maven -y
