# Distributed Systems Project (2015-2016)

### Contributors
- [@hugogaspar8](https://github.com/hugogaspar8) - Hugo Gaspar
- [@joaocarlos95](https://github.com/joaocarlos95) - João Carlos
- [@jtf16](https://github.com/jtf16) - João Freitas

### About

This project aims to develop a system based on a Web Services to aggregate goods transport services. There are three main entities:
  1. Customers who requests services to the broker. Can also check the status of their orders.
  1. Broker who is connected to the carrier services and look for the transporter company with the lowest price for the requested service. The broker also contacts the company to give them the trip requested by the client.
  1. A carrier which responds to budget and travel booking requests.

The following figure exemplifies the architecture of this system.

<p align="center">
  <img width="65%" height="65%" src="/Architecture.png">
</p>

A jUDDI server is used to register each web server of the carriers. In this way, the broker can find the best company for customer orders.
Customers can book a trip and request their status from the broker. They can also list their travels.

Last, but not least, security in communication and fault tolerance are also implemented:
  - Messages between broker and carrier services are authenticated and can not be repudiated by the sender. The recipient must also recognize when a message is replicated by a malicious agent.
  - There is a secondary broker to prevent system loss when there is a broker failure.
