[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0) 

# system-integration-starter


## Description
This repository contains the source code for the System Integration Starter within Dell Project Symphony. 

The System Integration Starter abstracts messaging logic to communicate with rabbit-mq server. The project is written in Java and is based on the Spring Boot Library.

System Integration Starter is customized spring boot starter application which provides methods/APIs for the PAQXs through which they can send messages to and receive messages from intended queues.

## Documentation

You can find additional documentation for Project Symphony at [dellemc-symphony.readthedocs.io][documentation].

## Before you begin

Verify that the following tools are installed:
 
* Apache Maven 3.0.5+
* Java Development Kit (version 8)
* RabbitMQ 3.6.6

## Building

Run the following command to build this project:
```bash
mvn clean install
```
Run the following command to build this project without tests:
```bash
mvn clean install –Dmaven.test.skip=true
```

Run the following command to build this project with all tests, execute the below command, else integration tests will fail.:
```bash
mvn clean install -DVM_FQDN="localhost" -Drabbit_user="username" -Drabbit_password="password"
```

To run only tests (Unit+Integration)
```bash
mvn clean verify -DVM_FQDN="localhost" -Drabbit_user="username " -Drabbit_password="password"
```

To run only unit Tests
```bash
mvn clean test
```

Note: Rabbitmq and capability registry should be running in your localhost. Else provide the parameter details of any VM which has them running.

## Contributing
Project Symphony is a collection of services and libraries housed at [GitHub][github].

Contribute code and make submissions at the relevant GitHub repository level. See [our documentation][contributing] for details on how to contribute.
## Community
Reach out to us on the Slack [#symphony][slack] channel by requesting an invite at [{code}Community][codecommunity].

You can also join [Google Groups][googlegroups] and start a discussion.

[slack]: https://codecommunity.slack.com/messages/symphony
[googlegroups]: https://groups.google.com/forum/#!forum/dellemc-symphony
[codecommunity]: http://community.codedellemc.com/
[contributing]: http://dellemc-symphony.readthedocs.io/en/latest/contributingtosymphony.html
[github]: https://github.com/dellemc-symphony
[documentation]: https://dellemc-symphony.readthedocs.io/en/latest/
