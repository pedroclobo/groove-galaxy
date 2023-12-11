# T52 GrooveGalaxy Project Read Me

## Team

| Number | Name              | User                               | E-mail                                    |
| -------|-------------------|------------------------------------|-------------------------------------------|
| 99053  | André Torres      | <https://github.com/atorrres>      | <mailto:andre.torres@tecnico.ulisboa.pt>  |
| 99074  | Gonçalo Nunes     | <https://github.com/goncaloinunes> | <mailto:goncaloinunes@tecnico.ulisboa.pt> |
| 99115  | Pedro Lobo        | <https://github.com/pedroclobo>    | <mailto:pedro.lobo@tecnico.ulisboa.pt>    |

![André](img/andre.png) ![Gonçalo](img/goncalo.png) ![Pedro](img/pedro.png)

## Contents

This repository contains documentation and source code for the *Network and Computer Security (SIRS)* project.

The [REPORT](REPORT.md) document provides a detailed overview of the key technical decisions and various components of the implemented project.
It offers insights into the rationale behind these choices, the project's architecture, and the impact of these decisions on the overall functionality and performance of the system.

This document presents installation and demonstration instructions.

*(adapt all of the following to your project, changing to the specific Linux distributions, programming languages, libraries, etc)*

## Installation

To see the project in action, it is necessary to setup a virtual environment, with 3 networks and 5 machines.

The following diagram shows the networks and machines:

![Network diagram](img/network-diagram.svg)

The following table shows the network topology configuration:

|   # Interface   |      IP       | Adapter |
|:---------------:|:-------------:| :-----: |
|  **Database**   |               |
|        1        |  192.168.0.1  |  eth0   |
| **Firewall 1**  |
|        1        | 192.168.0.254 |  eth0   |
|        2        | 192.168.1.254 |  eth1   |
| **Application** |
|        1        |  192.168.1.1  |  eth0   |
| **Firewall 2**  |
|        1        | 192.168.1.253 |  eth0   |
|        2        | 192.168.2.254 |  eth1   |
|   **Client**    |
|        1        |  192.168.2.1  |  eth0   |

### Prerequisites

All virtual machines are based on: Linux 64-bit, Kali 2023.3

[Download](https://cdimage.kali.org/kali-2023.3/kali-linux-2023.3-installer-amd64.iso) and [install](https://www.kali.org/docs/virtualization/install-virtualbox-guest-vm/) a virtual machine of Kali Linux 2023.3.

### Machine configurations

#### Base Machine

This machine will be used as a base for the other machines.

Boot up the machine and update the system:

```sh
$ sudo apt update
```

Use Git to obtain a copy of the `T52 GrooveGalaxy Project`:

```sh
$ git clone https://github.com/tecnico-sec/t52-andre-pedro-goncalo.git
```

Our repository has the necessary scripts to initialize each machine.

Follow the next custom instructions to setup each machine.

Link clone this VM as needed to create new machines. Don't forget to choose the option **Generate new MAC addresses for all network adapters**, under **MAC Address Policy**.

#### Database Server

This machine runs the database server (PostgreSQL 16.1).

Boot up the machine.

If you cloned this machine from the Base VM, the system already has the project repository.

Run the following commands in the root of the project repository:

Click "enter" to everything that is asked while creating the SSL certificate.

```sh
$ cd database
$ chmod +x setup.sh
$ sudo ./setup.sh
$ shutdown now
```

The expected output should include the output from the `apt` package manager and the following lines:

```sh
Synchronizing state of postgresql.service with SysV service script with /lib/systemd/systemd-sysv-install.
Executing: /lib/systemd/systemd-sysv-install enable postgresql
Created symlink /etc/systemd/system/multi-user.target.wants/postgresql.service → /lib/systemd/system/postgresql.service.
ALTER ROLE
```

Before booting up the virtual machine again, replace the current **Adapter 1** with a **Internal Network** named `sw-1`.

Boot up the virtual machine and verify that the configuration was successful by checking the following:

Running `hostnamectl | grep 'hostname'` should reveal the hostname `database`.

Running `ip a` should reveal IP `192.168.0.1` under the `eth0` interface.

Running `sudo nmap localhost` should reveal the following open ports:

```
PORT     STATE SERVICE
5432/tcp open  postgresql
```

The following lines should be present in the file `/etc/postgresql/16/main/postgresql.conf`:

```
listen_addresses = '*'
port = 5432
ssl = on
ssl_cert_file = '/etc/ssl/certs/database.crt'
ssl_key_file = '/etc/ssl/private/database.key'
```

A line similar to the following should be present in the file `/etc/postgresql/16/main/pg_hba.conf`:

```
hostssl groove postgres 192.168.0.0/24 md5
```

#### Application Server

This machine runs the application server (Java 17 / Spring-Boot 2.4.1).

Boot up the machine.

If you cloned this machine from the Base VM, the system already has the project repository.

Run the following commands in the root of the project repository:

```sh
$ cd backend
$ chmod +x setup.sh
$ sudo ./setup.sh
$ mvn clean spring-boot:run
$ shutdown now
```

The expected output should include the output from the `apt` package manager and the output from `mvn` downloading the required dependencies. The output should terminate with a **BUILD FAILURE** message, as the backend can't connect to the Postgres database server.

Before booting up the virtual machine again, replace the current **Adapter 1** with a **Internal Network** named `sw-1`.

Boot up the virtual machine and verify that the configuration was successful by checking the following:

Running `hostnamectl | grep 'hostname'` should reveal the hostname `application`.

Running `ip a` should reveal IP `192.168.0.2` under the `eth0` interface.

To start the application, run the following command in the `application` folder of the cloned repository:

```sh
mvn clean spring-boot:run
```

## Demonstration

Now that all the networks and machines are up and running, ...

*(give a tour of the best features of the application; add screenshots when relevant)*

```sh
$ demo command
```

*(replace with actual commands)*

*(IMPORTANT: show evidence of the security mechanisms in action; show message payloads, print relevant messages, perform simulated attacks to show the defenses in action, etc.)*

This concludes the demonstration.

## Additional Information

### Links to Used Tools and Libraries

- [Java 11.0.16.1](https://openjdk.java.net/)
- [Maven 3.9.5](https://maven.apache.org/)
- ...

### Versioning

We use [SemVer](http://semver.org/) for versioning.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) for details.

*(switch to another license, or no license, as you see fit)*

----
END OF README
