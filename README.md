# Robot Cleaner


Final task for course "Software Systems Engineering M" during my master degree at University of Bologna, carried out together with my colleague Marco Boschi.

(A.Y. 2017-2018)

## Initial Setup (macOS)

Install the two [QActor plug-ins](https://github.com/anatali/iss2018/tree/master/it.unibo.issMaterial/plugins) for Eclipse provided by the professor, Gradle and Node.js, then install MongoDB and Mosquitto via HomeBrew by using

```bash
brew install mosquitto
brew install mongodb
```

Create an Eclipse workspace in the root of this repository and import the projects `it.unibo.finaltask` and `it.unibo.frontend`. Open a terminal in the `it.unibo.finaltask` folder and execute

```bash
gradle -b build_ctxFinalSysAnalysis.gradle eclipse
```

More than one execution of this command may be necessary, in that case open the _Build Path_ configuration and make sure no folders are duplicated. Open the _Build Path_ configuration of `it.unibo.frontend` and delete all folders from the _Source_ tab. At this point Eclipse should not display any error (some warnings only).

To easily start the many servers, create aliases by appending these lines to the the `~/.bash_profile` file

```bahs
alias mosquitto='/usr/local/opt/mosquitto/sbin/mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf'
alias mongodb='mongod --dbpath "/my/custom/path/to/mongodb"'
```

For the second one, make sure to create a folder in the given path.

Install all packages required by Node.js in the folders `it.unibo.frontend/nodeCode/frontend`, `VirtualRobotJS/WebGLScene` and `VirtualRobotJS/server` by using, in each of these, the following command

```bash
npm install
```

### Xcode configuration
Frameworks required to work with MQTT are available via CocoaPods, after install CocoaPods itself, download them by executing in the `iss2018` folder the following command

```bash
pod install
```

### Test
Testing is limited to a single part of the project, see [`it.unibo.finaltask.testing`](https://github.com/bobcorn/robot-cleaner/tree/master/it.unibo.finaltask.testing) for setup details.

## System Startup

1. Start (in any order)
	- Mosquitto with the alis `mosquitto`
	- The virtual robot with `startRobot.sh`
	- MongoDB with the alias `mongodb`
	- The frontend server with `startFrontEnd.sh`
2. Start hardware mocks with `ISS 2018 – Mock.app`
3. Start the QActor application by launching the class `it.unibo.ctxfinalSysAnalysis` in `src-gen` in the `it.unibo.finaltask` project

It is now possible to view the virtual robot at http://localhost:8081 and the frontend at http://localhost:3000. By reloading the virtual robot page, the initial scene will be reset (file `VirtualRobotJS/WebGLScene/sceneConfig.js`). If the application was **not** in the auto-cleaning phase when the the scene has been reloaded, the QActors have automatically reconfigured themselves the restart at the sending of a new *Start* command.

## Commit

Make sure Eclipse doesn't change the  `.gitignore` files by deleting the line `.classpath`: the `.classpath` files must _**NOT**_ be committed since they contain the _Build Path_ configuration, so they have to be configured as specified above. If Eclipse suggests this change in `.gitignore`, restore the file to the previous version.

## Demo

[![Download demo video](https://github.com/bobcorn/robot-cleaner/blob/master/Docs/DemoThumb.png)](https://github.com/bobcorn/robot-cleaner/blob/master/Docs/Demo.mp4)
