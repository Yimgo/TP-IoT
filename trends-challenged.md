Despite the heavy research led in the Internet of Things domain and the massive involvement by both public laboratories and private research and development groups, there still are challenges that need to be addressed and taken up.
Most of the state of the art address MAC protocols, architectures, and frameworks abstracting the topology of IoT networks. Nevertheless, standardization groups are still working on standards that could fill the gap between the Internet and the protocol associated to it to the characteristics of connected devices that have limited resources.
One other main challenge is the development of innovative use cases that will reach the general public and enter in everyone's home.

# Challenges

## Addressing and transport layer protocols.

Most of the IP networks making up the Internet are based on the IPv4 protocol which has became a de-facto standard in the network industry. It somehow responds to the characteristics of these networks and their interconnection, but the pool of addresses available to identify the equipments connected to these is almost empty. Workarounds have been found to fix the lack of available addresses, such as address mapping - NAT -, but this hides the devices within a NATted network.
To respond to this issue and fix other bad designs of the iPv4 protocol, the IPv6 protocol has been standardized in xxxx but is struggling to spread because of the inertia of network manufactures, ISPs,  hosting companies and. This protocol, addressing machines according to a 128-bit identifier, is the standard of choice for IoT architectures to keep the compatibility with Internet standards and protocol.

However, most of the devices within an IoT networks are communicating through the 802.15.4 protocol or are RFID devices. 802.15.4 allows identifier in the range 96-128 bits while RFID addresses are even shorter, which is not enough to fill the 128-bit identifier required by the IPv6 protocol.
A general response to the issue is maintaining a table at the gateway which makes the mapping between the device identifier to an IPv6 identifier. Unfortinately, this solution doesn't scale as it increases the complexity of the gateway, limiting the usage of common nodes as gateways, and requires to maintain multiple tables in case of multiple gateways.
Another solution consists in considering the connected devices as objects identified by a name within a namespace - the network - and querying these objects using their names. Similar to the DNS protocol, this would abstract the heterogeneous identifiers and ease the query of the devices against an increase of the complexity and the completion time of such requests.
A group has also been formed at the IETF, named 6loWPAN - in order to agree around a command standard of interoperating IP and IoT networks, including the addressing problem.

This group has also been working on the fundamental differences between such networks, such as the packet sizes, the packet formats, the routing and security. The 802.15.4 research group has also released an IPv6 compatible standard, but the evaluations of these standards and their implementation still need to done.

As said above, due to the differences in computing resources, packet sizes or medium throughput, the traditional TCP/IP stack is very difficult to transpose. TCP and its algorithms to handle congestion and fragmentation don't work very well in these networks.

A big research effort has been made on deterministic MAC protocols that allows parole slots to every node of the network, but current solutions don't scale to big networks (> 100 nodes). This effort has thus to keep going in order to allow bugger network constructs.
At the routing and transport layer though, very few research have been made on Quality of Service and stream prioritization, particularly in RFID systems.

## Standardization

Standardization groups and efforts are nowadays dispersed between pure standardization activities, national or supranational agencies, telecommunications consortiums and Internet consortiums.
All these groupes actually slow down efforts towards standardization as they fragment and "complexify" the workload.

## Security

The security of sensor networks is a fundamental issue that has been to be treated. Connected devices have indeed very low energy and computing resources and cannot thus implement complex security systems.
It is really easy to have access to the data collected by the sensors, by physically attacking them or intercepting the packets they send. Man-in-he-middle attacks are also pretty straightforward to set up when no security and low intelligence is put on the sensors.

These issues generate concerns about the data integrity and the privacy of the users. Both 802.15.4 and IPv6 provide AES support but these are often not taken advantage of.

# Trends

Numerous topics related to Internet of Things and taking advantage of the state of the art research are exists and markets associated are entered by industrials.
They develop applications and services on top of IoT devices and architectures to provide innovative use cases to their clients.

## Artificial Intelligence

An interesting perspective of IoT solutions is the creation of smart environments for end-users that ease, automatize or monitor pans of their lifes. These cities aims power consumption lowering, home comfort ease or transports fluidification.
The acute is also put on green and environment by national or international agencies.
These systems combines multiple sensors that often communicate through proprietary protocols, where the company has the control of the system. Administration is made possible by a gateway which provides an HTTP interface.


The trend is nowadays to make the sensors learn from the information they and their peers collect in order to adapt to each environment. Such actions require computing resources but can be deported to super nodes or even to the cloud.
http://www.wired.co.uk/news/archive/2010-10/14/augmented-reality-internet-of-things

## Wearables

2013 has been the year of connected bracelets, sensing body parameters. Such devices have applications in health domain, body monitoring or performance improvement in sport and can help you to get more active, eat better, manage weight or 	sleep better.
Raw data can be transmitted to a smartphone or a computer via Bluetooth or NFC which allows a very low power consumption, or even via Wi-Fi on specific models.
Some are also able to connect with other bracelets or access points and share aggregated data.

Wearables have however drawbacks as they have a limited battery life and non-standard or documented APIs.

http://www.fastcodesign.com/3025180/why-wearable-devices-will-never-be-as-disruptive-as-smartphones
http://todhq.com/
## Big data

Behind the proposition of free or cheap services by data-oriented companies is hiding the ghost of data retention in the cloud.
Such data allow these companies to profile their users and provide them conectualized advertisement or services.
Internet of Things services are a very efficient way to collect quality data as this have the ability to get to know what one is doing at a specific time through according to the kind of measures they collect.

## Augmented reality

Augmented reality is a trendy topic as it is modifies perception and analyse of the environment.
This is a reality in the automative industry where manufacturers such as Audi or Tesla Motors display conextual information over the pare-brise like, speed, humidity, travel information and GPS visualization.

http://software.intel.com/en-us/blogs/2014/01/28/three-non-wearable-internet-of-things-that-need-building
