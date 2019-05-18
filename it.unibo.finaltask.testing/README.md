# Test

Testing is limited the core problem, the QActor `cleaner`. The testing is executed by isolating this component in a dedicated project cotained in this folder, with some changes to ease the testing process. See [documentation](https://github.com/piscoTech/uniboIss2018/blob/master/Docs/Report.pdf), section 2.4, for the choices that have been made.

## Initial Setup
After importing the project in this folder in Eclipse (the workspace for the main project is fine), open a terminal in this folder and execute, by following [the same advices](https://github.com/piscoTech/uniboIss2018#initial-setup-macos) given for the main project, the command

```bash
gradle -b build_ctxFinalSysTesting.gradle eclipse
```

## Testing
At this point it's possible to execute the tests by using JUnit and selecting all classes (just one) inside the `test` folder. If reports about code coverage or Gradle is preferred, the following command is available

```bash
gradle -b build_ctxFinalSysTesting.gradle test
```

**Note:** The tests takes ~4 minutes to execute as the robot is moved with the same timings as if it were in a real (or virtual) room.

**Note:** It is not required to setup and environment to launch the tests, they will do so on their own.
