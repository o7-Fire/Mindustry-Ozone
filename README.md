Logo
Build Status Discord

A sandbox tower defense game written in Java.

Trello Board
Wiki

Building u self
Bleeding-edge live builds are generated automatically for every commit. You can see them here. Old builds might still be on jenkins.

If you'd rather compile on your own, follow these instructions. First, make sure you have Java 8 and JDK 8 installed. Open a terminal in the root directory, cd to the Mindustry folder and run the following commands: and then download the repo and unzip it after unzip u must run gradlew.bat

Important you will need
JDK 8
JRE 8
Windows
Building: click me to building desktop.bat

Linux/Mac OS
Running: ./gradlew desktop:run
Building: ./gradlew desktop:dist

Server
Server builds are bundled with each released build (in Releases). If you'd rather compile on your own, replace 'desktop' with 'server', e.g. gradlew server:dist.

Android
Building: click me to building android.bat

Troubleshooting
If the terminal returns Permission denied or Command not found on Mac/Linux, run chmod +x ./gradlew before running ./gradlew. This is a one-time procedure.

Android:Mindustry-o7\android\build\outputs\apk\debug Gradle may take up to several minutes to download files. Be patient.
After building, the output .JAR file should be in /desktop/build/libs/Mindustry.jar for desktop builds, and in /server/build/libs/server-release.jar for server builds.

Downloads
Get it on Itch.io

Get it on Google Play

Get it on F-Droid

Mindustry-o7
