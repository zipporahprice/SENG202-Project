# SafeTrip - SENG 202 Project Team 10
SafeTrip, designed by second-year software engineering students from the University of Canterbury, is a unique, New Zealand-based safety routing app, providing visual displays of crash data and enabling users to plan routes based on comprehensive safety ratings using public data from Waka Kotahi. Targeting New Zealand road users with a keen focus on safety, SafeTrip distinguishes itself by combining features of potential competitors, offering unique functionality to prioritize your safety on the roads. Below are details on how to set up and run the application.

## Authors
- Angelica Silva
- Christopher Wareing
- Neil Alombro
- Todd Vermeir
- William Thompson
- Zipporah Price

## Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Maven [Download](https://gradle.org/releases/) and [Install](https://gradle.org/install/)


## What's Included
This project includes some of the following:
- JavaFX
- Logging (with Log4J)
- Junit 5
- Mockito (mocking unit tests)
- Cucumber (for acceptance testing)

We have also included a basic setup of the Gradle project and Tasks required for the course including:
- Required dependencies for the functionality above
- Build plugins:
    - JavaFX Gradle plugin for working with (and packaging) JavaFX applications easily


## Importing Project (Using IntelliJ)
IntelliJ has built-in support for Gradle. To import your project:

- Launch IntelliJ and choose `Open` from the start up window.
- Select the project and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If so, click load

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't appear then open the Gradle sidebar and click the Refresh icon.*

## Build Project 
1. Open a command line interface inside the project directory and run `./gradlew jar` to build a .jar file. The file is located at `build/libs/safetrip-2.0.jar

## Run App (Linux users)
- open a terminal and move to the directory with the jar file
- run the command java -jar safetrip-2.0.jar
- enjoy

