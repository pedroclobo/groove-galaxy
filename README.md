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

To see the project in action, it is necessary to setup a virtual environment, with N networks and M machines.

The following diagram shows the networks and machines:

*(include a text-based or an image-based diagram)*

### Prerequisites

All the virtual machines are based on: Linux 64-bit, Kali 2023.3

[Download](https://cdimage.kali.org/kali-2023.4/kali-linux-2023.4-installer-amd64.iso) and [install](https://www.kali.org/docs/virtualization/install-virtualbox-guest-vm/) a virtual machine of Kali Linux 2023.3.
Clone the base machine to create the other machines. Don't forget to choose the option **Generate new MAC addresses for all network adapters**, under **MAC Address Policy**.

### Machine configurations

For each machine, there is an initialization script with the machine name, with name `script` and suffix `.sh`, that installs all the necessary packages and makes all required configurations in the clean machine.

Inside each machine, use Git to obtain a copy of all the scripts and code.

```sh
$ git clone https://github.com/tecnico-sec/t52-andre-pedro-goncalo.git
```

Next we have custom instructions for each machine.

#### Database Machine

This machine runs the database server (PostgreSQL 16.1).

To setup the machine, start by adding a new **Adapter 1** (`eth0`) to the virtual machine and attach it to a new Internal Network `sw-1`.

Then, proceed by booting up the machine and cloning the repository. Then, run the following command in the root of the cloned repository:

```sh
$ cd database
$ chmod +x setup.sh
$ sudo ./setup.sh
$ reboot
```

The expected results look like the following:

```sh
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
postgresql is already the newest version (16+256).
0 upgraded, 0 newly installed, 0 to remove and 0 not upgraded.
Synchronizing state of postgresql.service with SysV service script with /lib/systemd/systemd-sysv-install.
Executing: /lib/systemd/systemd-sysv-install enable postgresql
ALTER ROLE
could not change directory to "<dir>": Permission denied
```

Any of the following error messages can be ignored:

```sh
could not change directory to "<dir>": Permission denied
createdb: error: database creation failed: ERROR:  database "groove" already exists
```

After the machine reboots, running `ip a` should reveal IP `192.168.0.1` under the `eth0` interface.
Running `sudo nmap localhost` should reveal the following open ports:

```
PORT     STATE SERVICE
5432/tcp open  postgresql
```

#### Backend Machine

This machine runs the backend server (Java 17 / Spring-Boot 2.4.1).

To setup the machine, start by adding a new **Adapter 1** (`eth0`) to the virtual machine and attach it to a new Internal Network `sw-1`.

Then, proceed by booting up the machine and cloning the repository. Then, run the following command in the root of the cloned repository:

```sh
$ cd backend
$ chmod +x setup.sh
$ sudo ./setup.sh
$ reboot
```

```sh
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
maven is already the newest version (3.8.7-1).
0 upgraded, 0 newly installed, 0 to remove and 0 not upgraded.
```

After the machine reboots, running `ip a` should reveal IP `192.168.0.2` under the `eth0` interface.

To start the application, run the following command in the `backend` folder of the cloned repository:

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
