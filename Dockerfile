# Base Alpine Linux based image with OpenJDK JRE only
FROM ubuntu:16.04

RUN apt-get update
RUN apt-get install -y openjdk-9-jdk-headless
RUN apt-get install -y dos2unix && apt-get clean
CMD /bin/bash

RUN mkdir /usr/local/twitterapp
RUN mkdir /usr/local/twitterapp/bin
RUN mkdir /usr/local/twitterapp/lib
RUN mkdir /usr/local/twitterapp/log
RUN mkdir /usr/local/twitterapp/etc

# copy application WAR (with libraries inside)
COPY target/classes/twitterapp.properties /usr/local/twitterapp/etc
COPY target/classes/log4j.xml /usr/local/twitterapp/bin
COPY target/classes/runTwitterApp.sh /usr/local/twitterapp/bin
RUN chmod +x /usr/local/twitterapp/bin/runTwitterApp.sh
RUN dos2unix /usr/local/twitterapp/bin/runTwitterApp.sh
COPY target/bieber-tweets-1.0.0-SNAPSHOT.jar /usr/local/twitterapp/lib/bieber-tweets.jar
# specify default command