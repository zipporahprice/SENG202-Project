# SafeTrip - SENG 202 Project Team 10
SafeTrip is a New Zealand based safety routing app with public data from the Waka Kotahi. This project is developed
by a group of second-year software engineering students. The following details how to run the application.

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
This project comes with some basic examples of the following (including dependencies in the build.gradle file):
- JavaFX
- Logging (with Log4J)
- Junit 5
- Mockito (mocking unit tests)
- Cucumber (for acceptance testing)

We have also included a basic setup of the Gradle project and Tasks required for the course including:
- Required dependencies for the functionality above
- Build plugins:
    - JavaFX Gradle plugin for working with (and packaging) JavaFX applications easily

You are expected to understand the content provided and build your application on top of it. If there is anything you
would like more information about please reach out to the tutors.

## Importing Project (Using IntelliJ)
IntelliJ has built-in support for Gradle. To import your project:

- Launch IntelliJ and choose `Open` from the start up window.
- Select the project and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If so, click load

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't appear then open the Gradle sidebar and click the Refresh icon.*

## Running the project
You can run the project through your IDE from the main class (App.java) or use the `./gradlew run` command from your terminal

## Build Project 
1. Open a command line interface inside the project directory and run `./gradlew jar` to build a .jar file. The file is located at `build/libs/seng202_teamX-1.0-SNAPSHOT.jar

## Run App
- If you haven't already, Build the project.
- Open a command line interface inside the project directory and run `cd target` to change into the target directory.
- Run the command `java -jar SafeTrip-1.0_10k` to open the application.
