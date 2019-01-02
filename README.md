# Twitter Streaming App #

+ **Prerequisites**
+ **Description of the approach**
+ **Build the application**
+ **Run the application**
+ **Notes**

### 1: Prerequisites

##### The following languages and technologies need to be installed:
- **Java 9** - for the Streaming API. The application works in Publisher/Subscriber fashion
- **Maven 3.6.0** - for building the project and creating Docker image
- **Docker 18.09.0** - to run the application in Dockerized environment
- **IntelliJ 2018.3** with Lombok plugin installed - my preferred IDE

##### The following technologies are used (configured in pom.xml)
- **Twitter4J 4.0.4** - for connecting to Twitter Streaming API
- **Lombok 1.16.20** - for creating builders, loggers and some other fancy stuff. Reducing verbosity as well.
- **Spring Core 5.0.7.RELEASE** - for creating Spring beans via annotations
- **Google GSON 2.8.1** - for creating JSON output
- **Slf4J 1.7.16** - for logging together with Log4J
- **JUnit 5.2.0** - for unit testing
- **Mockito 2.23.0** - for mocking objects

### 2: Description of the approach
#### **2.1 packages**

**org.interview.oauth.twitter.config** - this package contains the application configuration

**org.interview.oauth.twitter.domain** - this package contains the domain objects

**org.interview.oauth.twitter.exception** - this package contains the exception classes

**org.interview.oauth.twitter.listener** - this package contains the Twitter4J listener

**org.interview.oauth.twitter.reactive** - this package contains the Java 9 reactive classes

**org.interview.oauth.twitter.repository** - this package contains the JSON creator

**org.interview.oauth.twitter.transform** - this package contains the transformations to our domain objects


#### **2.2 The approach**

The application uses Java 9 Stream API in order to handle the Twitter statuses in Publisher/Subscriber reactive fashion.
Spring is used for IOC beans creation. The application is configured via *twitteapp.properties* file.
Docker image with Ubuntu 16.04.5 LTS and Java 9 is created. The application is installed under */usr/local/twitter* directory
with the following structure:
- **bin** - this directory contains the bash script (runTwitterApp.sh) for running the application and the logger configuration
- **etc** - this directory contains the application configuration
- **log** - this directory contains logger file twitterapp.log
- **lib** - this directory contains shaded jar file with the application and all the dependencies

### 3: Build the application

##### 3.1 Edit *src/main/resources/twitterapp.properties* and set the following properties:
- **twitter.oauth.consumerKey** - the Twitter consumer key
- **twitter.oauth.consumerSecret** - the Twitter consumer secret
- **twitter.oauth.accessToken** - the Twitter access token
- **twitter.oauth.accessTokenSecret** - the Twitter access token secret
- **twitter.app.appname** - the Twitter application name
- **twitter.filter.keywords** - the keyword
- **twitter.stream.process.duration** - the application process execution duration
- **twitter.stream.statuses.count.threshold** - the maximum number of statuses to be handled

##### 3.2 Enter the project directory and run maven with the production profile:
**mvn clean install -Pprod**

This will build the project, run the unit tests and create a Docker image called **twitterapp/docker-twitterapp:1.0.0-SNAPSHOT**.

### 4: Run the application
In order to run the application execute the following commands:

- **run -it twitterapp/docker-twitterapp:1.0.0-SNAPSHOT** - to spawn the docker container
When in bash prompt of the container execute:
- **cd /usr/local/twitter/bin** - to change to the twitterapp script directory
- **./runTwitterApp.sh** - to run the application
- **cd ../log** - to change to the logger directory and observe the log file **twitterapp.log**

### 4: Notes

I recognise this is not a fully production ready approach, but at least it gives a good
idea about my knowledge in the area. Here I've used for a first time Java 9 and Twitter4J
as the idea is to show that I'm not afraid of using new language features and frameworks.
Unit tests code coverage could be a bit better, but the most critical parts are well covered.
I hope you're gonna like what I've created and see you soon.
