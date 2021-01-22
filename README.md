# Project Ozone
![Java CI](https://github.com/o7-Fire/Mindustry-Ozone/workflows/Java%20CI/badge.svg)
![CodeQL](https://github.com/o7-Fire/Mindustry-Ozone/workflows/CodeQL/badge.svg)
![](https://img.shields.io/jitpack/v/github/o7-Fire/Mindustry-Ozone?label=Mindustry-Ozone)
![](https://img.shields.io/github/v/release/Anuken/Mindustry?label=Mindustry-Latest)
![](https://img.shields.io/badge/java-14.0.2-orange)
![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fo7-Fire%2FMindustry-Ozone.svg?type=shield)



# Download
### Ozone
[Download](https://github.com/o7-Fire/Mindustry-Ozone/releases/tag/v122.13)
[0.14.0:0.12.24][Mindustry v122.1] \
[Download](https://jitpack.io/com/github/o7-Fire/Mindustry-Ozone/Desktop/v122/Desktop-v122.jar)
[0.10.0:0.11.8][Mindustry v122] \
[Download](https://jitpack.io/com/github/o7-Fire/Mindustry-Ozone/Desktop/v121.4/Desktop-v121.4.jar)
[0.9.0:0.11.3][Mindustry v121.4] \
[Download](https://jitpack.io/com/github/o7-Fire/Mindustry-Ozone/Desktop/a8805a30a5/Desktop-a8805a30a5.jar)
[0.9.0:0.11.0][Mindustry v121] \
[Download](https://github.com/o7-Fire/Mindustry-Ozone/releases/download/v120/Ozone-Desktop.jar)
[0.9.0:0.10.2][Mindustry v120] \
[Other Version](https://jitpack.io/#o7-Fire/Mindustry-Ozone) [Jitpack] \
[Other Version](https://github.com/o7-Fire/Mindustry-Ozone/actions) [Github]\
[Other Release](https://github.com/o7-Fire/Mindustry-Ozone/tags) [Github] \
[Snapshot Build](https://jitpack.io/com/github/o7-Fire/Mindustry-Ozone/Desktop/-SNAPSHOT/Desktop--SNAPSHOT.jar) [Jitpack][May take a while to build] \
[Snapshot Build](https://github.com/o7-Fire/Mindustry-Ozone/actions) [Github]

### Java
`because life never been any harder if you don't install java`\
[AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk14&jvmVariant=hotspot) [OpenJDK-OpenJRE 14]\
[AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot) [OpenJDK-OpenJRE 8]\
[Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html) [JDK 14 only require login]\
[Zulu JDK](https://www.azul.com/downloads/zulu-community/?version=java-8-lts&package=jdk) [OpenJDK-OpenJRE 8]
# Requirement

* Java 14+ and added to JAVA_HOME (mindustry desktop can run on java 14+) (For Ozone-Desktop)
* Java 8+ and added to JAVA_HOME (mindustry core can run on java 8+) (For Ozone-Core)
* Internet connection (required to download library at runtime and can impact loading speed) (For Ozone-Desktop)
* Year 2006+ Computer
* Android 5.0+ / sdk version 21+

# Build

* 0.Install JDK 14
* 1.run `./gradlew deploy`
* 2.you get the Desktop mods in `Desktop/build/libs/`
* 3.you get the Core mods in `build/libs/`
* 4.you get the Android mods in `Android/build/libs` if you have android sdk set to ANDROID_HOME

# Install

`Ozone destkop`

- install Java 14
- `java -jar Mindustry-Ozone.jar` or double click if on Windows
- click install
- optional: run to download mindustry and run it

`Ozone core`

- install java 8+
- `java -jar Mindustry-Ozone.jar` or double click if on Windows
- it's copied to mindustry mods folder

`Ozone core, desktop, android`

- install java 14 if using ozone-desktop, java 8+ if using ozone-core, don't install java if use android
- locate Mindustry mods folder

```css
Android: Android/data/io.anuken.mindustry/files/mods
Windows: %APPDATA%/Roaming/Mindustry/mods or C:/Users/USERNAME/.AppData/Roaming/Mindustry/mods
Linux: /home/USERNAME/.local/share/Mindustry/mods/
```

- copy the jar to the mods folder
- launch mindustry (for desktop ozone recommend to use *run* instead)
# Install android only
if you hasn't mindustry, install mindustry
* 0.Click actions(https://github.com/o7-Fire/Mindustry-Ozone/actions)
* 1.Find first succes run, and download Mindustry-Ozone-android.zip
* 2.Unzip archive
* 3.Start mindustry and go to mod menu
* 4.Add unziped jar archive as mod
* 5.Reopen mindustry
* 6.Profit

# Impossible things to do

- In-game Painter because a mere mortal can't draw with by typing code
- ~~Create mindustry class patcher~~
- Stop relying on third party library for bootstrap
- ~~Bootstrap in less than 10 second for first time setup (worst: 40 second)~~
- Improve download bar (its keep teleporting wtf)
- Remove useless commands and duplicate
- Add more commands
- ~~Use own custom settings framework~~
- Somehow create chat filter
- Stabilize bootstrapper
- Implement interactive mindustry-world API
- Implement information center for mindustry-world
- Somehow fix the horrible updater
- Create player watcher
- Add logic display toggle (goddamn those nsfw image)
- Stabilize Bot Launcher
- Auto start bot-server when launching bot(or remove that button ?)
- Make updater much more living(add GUI)
- Headless ?
- AI ?
- Make Bots actually useful
- Brainwash nexity to make actually useful code
- Add proxy ?
- Auto create efficient miner block pool using bruteforce
- Brainwash nexity to not mess with this README.md
- Create AIO script to launch ozone (non java user)
- Auto Java, switch java version when launched from another version
- World logger, VCS like system ?
- Unify library folder
- Steam support ?
- Event based key listener
- ~~Fix shutdown stall~~
- Fix javac commands
- Logic Code blacklisting
- Integrated png to draw logic tool
- Mobile support
- Schematic Pools
- Fix the goddamn buggy Pathfinding
- Ozone Extension Support
- Video player
# Disclaimer

It's totally for entertainment purpose, user is fully responsible for using this tools
<h4 align="center">Visitor's count :eyes:</h4>
<p align="center"><img src="https://profile-counter.glitch.me/%7Bsketchyirishman%7D/count.svg" alt="sketchyirishman :: Visitor's Count" /></p>
