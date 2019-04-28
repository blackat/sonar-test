# Coverage Report

Some concepts.

- **reactor:**
- **maven module:**
- **coverage:**
- **jacoco:**
- **bytecode instrumentation:**
- **jvm agent:**
- **sonar:**
- **sonar jacoco plugin:**
- **application code:**
- **test code:**
- **static code analysis:**
- **software metric:**
- **sensors:** are used to scan projects and calculate metric values (measures, in SonarQube terminology).
    - A Sensor is executed once per analysis. It typically scans the project folder structure recursively, 
    calculating metric values, i.e. with the help of an external tool, and stores the information in SonarQube database.
- **JaCoCo session:** a coverage session is the code coverage information of particular program run. It contains the 
list of considered Java classes along with the recorded coverage details. Store in `jacoco-session.xml`.
- **SonarJava:** [SonarJava](https://github.com/SonarSource/sonar-java) is a code analyzer for Java projects.
- **SonarJavaSquid:** [Squid](https://dzone.com/articles/cache-java-webapps-squid) is a free proxy server for 
HTTP, HTTPS and FTP which saves bandwidth and increases response time by caching frequently requested web pages.

## Project convention and beyond

A maven project has a specific structure to organise files based on convention over configuration practice.

So `src\main\java` and `src\main\test`, classe in the former and tests in the latter w.r.t. package names.

What happens to coverage and analysis if the tests are not in the right package or module?
In the case of integration tests for instance, developers create a separate maven module in the same reactor.

This is a typical case, how the coverage should work?

## JaCoCo maven plugin

JaCoCo is a free software used to gather the test coverage. **Test coverage** is a technique which determines if
*test cases* cover the *application code* and how much application code is exercised, that is how much application 
code is used by tests.

In a simple way how much application code is tested.

Maven comes with two plugins: `maven-surefire-plugin` and `maven-failsafe-plugin`.
https://confluence.atlassian.com/clover/using-with-surefire-and-failsafe-plugins-294489218.html

### Maven Surefire Plugin

`maven-surefire-plugin`: unit tests runner, used during the `test` phase of the build lifecycle to execute unit 
tests. It generates reports in two different formats: plain text `*.txt` and XML `*.xml`. These files are generated 
in the folder `${basedir}/target/surefire-reports/TEST-*.xml`.

#### Text report example

A report show how many tests have been run and the time spent.

```bash
/-------------------------------------------------------------------------------
Test set: com.wuzz.common.CommonReaderTest
-------------------------------------------------------------------------------
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.084 sec
```

#### Disable the plugin

To disable the *Surefire* plugin it is enough to set `<skip>true</skip>` 

#### TeamCity

It a continuous integration and build management server to automate tasks in a continuous way. When TC run some Maven
 tasks such as `mvn clean install` at the end it uses what *Surefire* produces to show statistics about the latest run.

TC uses *XML Report Processing* (it is a TC plugin) build feature to use report files produced by an external tool such 
as, for instance,*Surefire*. The *XML Report Processing* supports different testing runners such as Maven *Surefire/Failsafe* plugins
  and some code inspections tools (FindBugs, PMD, Checkstyle, etc). The plugin just imports the XML reports of the 
  test runner into TC internal storage and then show them in the associated build.

### Maven Failsafe Plugin

`maven-failsafe-plugin`: integration tests runner. Maven lifecycle has four phases for integartion tests
    - `pre-integration-test` to setup the integration test environment.
    - `integration-test` to run the integration tests.
    - `post-integration-test` to tear down the integration test environment.
    - `verify` to check the results of the integration tests.

This plugin is not included in the super `pom.xml` like *surefire*, the plugin *must be explicitly specified* 
along with the `integration-test` and `verify` *goals*. It runs and verifies tests using different goals. A test 
failure in the `integration-test` phase doesn't fail the build straight away allowing the phase 
`post-integration-test` to execute and perform clean up operations. During the `verify` phase, after the 
environment has been torn down properly, *failed tests are reported*.

### Static Code Analysis

It is technique to analyze source code of program without running them, some simple scenario can be discovered such 
as null pointer dereferencing.

JaCoCo is a software metric to measure how many lines of the application code are executed during the automated tests.

Running a unit test using JUnit will automatically activate JaCoCo agent which will create a report in *binary 
format* in the target folder `target/jacoco.exec`. This report can be interpreted by  other tools such as SonarQube 
and IntelliJ.

The goal `report` of the plugin can generate reports in different readable formats such as `HTML`, `CSV` and `XML`.
https://www.baeldung.com/jacoco

Since test running automatically activate JaCoCo agent, running the maven `test` phase will automatically perform the
 static code analysis.
 
```bash
mvn clean install
```

Being a Maven multi-module project `install` phase is required to provide `modules` dependencies to other module of 
the reactor. Running a Maven `phase` activates all the *plugin goals* registered in that phase.

`argLine` shouldn't be set explicitly unless there is already some usage in the `pom.xml`.

Coverage has been run, *binary report* `jacoco.exec` (default name) has been generated and a readable version produced 
for each sub module under the `target` folder.

#### Recap

*Surefire* plugin is the test runner, when unit tests start they activate JaCoCo that, via *bytecode instrumentation*,
 registers the application code lines executed or exerciced during the test phase.

The result of Maven `test` phase are two reports:

- *Surefire* report: number of executed tests, failures, skipped tests and the time elapsed.
- *JaCoCo* report: binary files containing the application code coverage by the test methods.

#### Effective pom

Run `mvn help:effective-pom` and then see that the plugin definition in the *root pom* has been propagated to all the
 child modules, hence the coverage is run for all of them.
 

### Java Agent

> Java agent is a powerful mechanism providing the ability to instrument and change classes at runtime. 
Java agent library is passed as a JVM parameter when running given application with -javaagent:{path_to_jar}.

More on https://docs.oracle.com/javase/7/docs/api/java/lang/instrument/package-summary.html

https://automationrhapsody.com/code-coverage-of-manual-or-automated-tests-with-jacoco/

Instrumentation con be on-the-fly or offline
https://www.eclemma.org/jacoco/trunk/doc/instrument-mojo.html
https://www.jacoco.org/jacoco/trunk/doc/offline.html
https://github.com/igorstojanovski/jacoco-offline-instrumentation
https://automationrhapsody.com/code-coverage-with-jacoco-offline-instrumentation-with-maven/

JaCoCo agent can be started even in `tcpserver` output mode.

## SonarQube

Once all the reports (test and coverage executions) have been generated, they must be aggregated and analyzed to 
better display statics about the code to the user.

JaCoCo executions gives us a binary file, using the `report` goal a `site` folder is generated related to the Maven 
module, but locally. It is one of many code analysis can be conducted on the project.

Sonar helps to integrate test reports and many other analysis reports produced by other tools to evaluate the quality
 of the code.

### SonarQube Docker container

Instantiating a SonarQube Docker container is the fastest way to have SonarQube up and running and experiment with it
. Type the following command:

```bash
docker run  -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube
```

Connect to the SonarQube server interface via a browser and the url `localhost:9000`, user the user `admin` and the 
password `admin` to log into the application.

SonarQube version 7.7.0 has been used to write this post.


### SonarQube analysis

Some facts about SonarQube:

- it doesn't have any knowledge about the application code organization, it doesn't access to any code 
repository;
- it just know the tools have been used to collect reports and the reports themselves;
- some Maven project properties have be set in order to allow SonarQube to process data such as
    - where the reports are
    - which module have to be excluded/included
    - etc
- data are sent to SonarQube, hence they must be as much complete as possible to have a good report analysis. 

### Maven Sonar Plugin

It is a plugin to analyse a Maven project. 
Some *analysis parameters* have to be specified using Maven project `properties` as described [here](https://docs
.sonarqube.org/latest/analysis/analysis-parameters/).

#### Exclude a module from the analysis

According to [SonarQube docs](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Maven) there 
are 3 ways:

1. Define the property `<skip>true</skip>` in the `pom.xml` of the module to be excluded.
2. Via build profiles exclude some modules like for the integration tests case.
3. Use [Advanced Reactor Options](https://blog.sonatype.com/2009/10/maven-tips-and-tricks-advanced-reactor-options/) 
to exclude modules from the executions such as `mvn sonar:sonar -pl !wuzz-common`.

### Configuration

Add the plugin to the Maven project (take the latest version and considering the compatibility with the SonarQube 
server):

```xml
<plugins>
    <plugin>
        <groupId>org.sonarsource.scanner.maven</groupId>
        <artifactId>sonar-maven-plugin</artifactId>
        <version>3.6.0.1398</version>
    </plugin>
</plugins>
```

#### Maven properties

Add some Maven properties to configure SonarQube Maven Scanner.

From the [list](https://docs.sonarqube.org/latest/analysis/analysis-parameters/) some are **mandatory**.

- `sonar.host.url`: the url of the SonarQube server, its default value is `http://localhost:9000`
- `sonar.projectKey`: the project unique key to identify the Maven project among other projects in the SonarQube web 
application its default value in a Maven project is `<groupId>:<artifactId>`.

Then other optional properties can be defined in order to better describe a project such as the version number, the 
project name
 etc. 

#### SonarScanner logs

It is possible to control the log during the analysis to have more details about the `SonarScanner` work. The default
 level is `INFO`.

#### Test coverage and test execution

Analysis parameters list related to test coverage and execution reports can be found [here](https://docs.sonarqube
.org/latest/analysis/coverage/).

SonarSource does not run any test or generate reports, it just *import* reports produced by specific tools. So it is 
required to give some information to SonarSource via `properties` to better import and interpret the reports.

For *coverage* it is possible to specify either the `XML` reports produced by JaCoCo `report` Maven goal 
(`sonar.coverage.jacoco.xmlReportPaths`) or the binary report directly produced by the JaCoCo Java agent (`sonar
.jacoco.reportPaths`). The latest property is supported on for Java.

[image] 00-sonar to display the initial screen without any scan.

[image] 01-sonar to display the initial screen after the first scan.

Before a new scan is run, the previous scan analysis is deleted, just to start with something fresh because SonarQube
 keep an history to show the evolution of the project.
 
 ```bash
[INFO] Sensor JaCoCoSensor [java]
[INFO] Sensor JaCoCo XML Report Importer [jacoco]
```

#### Indexing files

```bash
[INFO] Indexing files...
[INFO] Project configuration:
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-alone-unit-tests
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-common
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-database
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-database-integration-tests
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-submodules/wuzz-submodule-unit-test
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-submodules
[INFO]   Source paths: pom.xml
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test
[INFO]   Source paths: pom.xml
[INFO] 14 files indexed
[INFO] Quality profile for java: Sonar way
[INFO] Quality profile for xml: Sonar way
```

#### Sensors

Different sensors (`Sensor JavaSquidSensor [java]`, `Sensor JaCoCo XML Report Importer [jacoco]`, ``) will be run 
against each Maven module, for instance:

```bash
[INFO] ------------- Run sensors on module Sonar Test
[INFO] Sensor JavaSquidSensor [java]
[INFO] Configured Java source version (sonar.java.source): 8
[INFO] JavaClasspath initialization
[INFO] JavaClasspath initialization (done) | time=0ms
[INFO] JavaTestClasspath initialization
[INFO] JavaTestClasspath initialization (done) | time=1ms
[INFO] Java Main Files AST scan
[INFO] 1 source files to be analyzed
[INFO] Java Main Files AST scan (done) | time=51ms
[INFO] Java Test Files AST scan
[INFO] 1/1 source files have been analyzed
[INFO] 1 source files to be analyzed
[WARNING] Unable to create a corresponding matcher for custom assertion method, please check the format of the following symbol: ''
[INFO] Java Test Files AST scan (done) | time=36ms
[INFO] 1/1 source files have been analyzed
[INFO] Sensor JavaSquidSensor [java] (done) | time=99ms
[INFO] Sensor JaCoCo XML Report Importer [jacoco]
[INFO] Sensor JaCoCo XML Report Importer [jacoco] (done) | time=11ms
[INFO] Sensor SurefireSensor [java]
[INFO] parsing [/Users/eugenio/Projects/Github/sonar-test/wuzz-common/target/surefire-reports]
[INFO] Sensor SurefireSensor [java] (done) | time=27ms
[INFO] Sensor JaCoCoSensor [java]
[INFO] Analysing /Users/eugenio/Projects/Github/sonar-test/wuzz-common/target/jacoco.exec
[INFO] No information about coverage per test.
[INFO] Sensor JaCoCoSensor [java] (done) | time=66ms
[INFO] Sensor JavaXmlSensor [java]
[INFO] 1 source files to be analyzed
[INFO] Sensor JavaXmlSensor [java] (done) | time=7ms
[INFO] Sensor HTML [web]
[INFO] 1/1 source files have been analyzed
[INFO] Sensor HTML [web] (done) | time=0ms
[INFO] Sensor XML Sensor [xml]
[INFO] 1 source files to be analyzed
[INFO] Sensor XML Sensor [xml] (done) | time=7ms
[INFO] 1/1 source files have been analyzed
```

#### Experiment 1

Remember, two kinds of reports have to be imported in SonarJava: 
[JUnit tests results and JaCoCo reports](https://docs.sonarqube.org/display/PLUG/Java+Unit+Tests+and+Coverage+Results+Import).

SonarJava has embedded a JaCoCo analyzer that analyze the report produced by the JaCoCo agent. **Attention** the 
binary reports produced by the JaCoCo agent must be compatible with the JaCoCo analyzer.

Once an analysis parameter `property` is modified type `mvn sonar:sonar` to start the SonarScanner analysis and upload 
the different reportsto SonarQube. This Maven `goal` will then produce a report in `target/sonar` folder with som ebasic info of the latest analysis.

###### JaCoCo reports

Let's do some tries with different kind of report formats

###### `sonar.coverage.jacoco.xmlReportPaths`

```xml
<sonar.coverage.jacoco.xmlReportPaths>wuzz-common/target/site/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

[image] 02-sonar to display only coverage reports, no info on number of tests run will be displayed.

###### `sonar.javascript.lcov.reportPaths`

```xml
<sonar.javascript.lcov.reportPaths>wuzz-common/target/site/jacoco.xml</sonar.javascript.lcov.reportPaths>
```

[images] 03-sonar and 04-sonar

###### `sonar.junit.reportPaths` + `sonar.javascript.lcov.reportPaths`

Let's add the JUnit XML reports and see how the analysis changes.

Pay attention to how the paths are specified

```bash
[INFO] parsing [/Users/eugenio/Projects/Github/sonar-test/wuzz-database/wuzz-common/target/surefire-reports]
[ERROR] Reports path not found or is not a directory: /Users/eugenio/Projects/Github/sonar-test/wuzz-database/wuzz-common/target/surefire-reports
```

Set it to

```xml
<sonar.junit.reportPaths>target/surefire-reports</sonar.junit.reportPaths>
```

this property will be propagated to all the submodules, the path of the module will be concatenated with what 
reported in the property. Even the full path is not correct for this property.

An interesting data is

```bash
[INFO] Analysing /Users/eugenio/Projects/Github/sonar-test/wuzz-common/target/jacoco.exec
[INFO] No information about coverage per test.
```

So it seems that without the `surefire` report some analysis will be incomplete.

[image] 05-sonar no test number information is available

[image] 06-sonar correct information are displayed

Even without that property, at the beginning, all the info were correct this because, probably, conventions over 
configurations are taken into account. Some properties are optionals and already have a `default` value, no need to 
specify any value if the project adheres 100% to the Maven and report conventions.

#### Experiment 2

##### New test in a separate module

Sometimes in legacy projects Maven conventions are not totally respected, for instance unit tests could part in the 
good place `src/test` of the corresponding module and part in another module.

Will be the reports generated correctly? Let's add a new module `wuzz-alone-unit-tests` with just 1 unit test, the new 
module will be added at the same level as other modules. Then add dependecy on `wuzz-common` module.

Rerun the unit test to generate the reports and then the scanner.

```bash
mvn clean install sonar:sonar
```

[image] 07-sonar the new tests is added, now 2 tests are present.

##### New test in a submodule

Let create a submodule called `wuzz-submodule-unit-test` in the new module `wuzz-submodules`, add dependency and 
`wuzz-common` and a unit test.

**Attention:** the tests are always on the same Java class `CommonReader.java` but in different modules and 
packages, with class names that do not respect any convention. With this heterogeneous configuration we would like to 
test the ability of the scanner to correctly collect reports and project information, so how starting from the 
`default` configuration the Maven plugin is able to collect data correctly.

Erase the project from SonarQube and rerun the scanning on new reports

```bash
mvn clean install sonar:sonar
```  

[image] 08-sonar tests once again have been found and reported.

Let's add a new method `writeMe()` to the class under test (`SUT`), rerun the execution and have a look at the 
coverage, it is not correct.

[image] 09-sonar the coverage show that the line is not covered, the meaning is that no unit test has exercised the 
method hence line not covered.

Looking at the scanner log of the submodule

```bash
[INFO] Sensor JaCoCoSensor [java]
[INFO] No JaCoCo analysis of project coverage can be done since there is no class files.
```

So the scanner has found, w.r.t. the conventions, the `surefire` reports but not the correct coverage.

###### Logs

Let's analyze better the logs.

So logs can be split is x sections

**1st section:** `Reactor Build Order` gives the list of modules that will be scanned.

```bash
[INFO] Reactor Build Order:
[INFO] 
[INFO] sonar-test
[INFO] wuzz-common
[INFO] wuzz-database
[INFO] wuzz-database-integration-tests
[INFO] wuzz-alone-unit-tests
[INFO] wuzz-submodules
[INFO] wuzz-submodule-unit-test
```

**2nd section:** `Building sonar-test 1.0-SNAPSHOT` is the start of the building process, immediately followed by 
an initialization phase such as `Process project properties` etc. 

```bash
[INFO] ------------------------------------------------------------------------
[INFO] Building sonar-test 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- sonar-maven-plugin:3.6.0.1398:sonar (default-cli) @ sonar-test ---
[INFO] User cache: /Users/eugenio/.sonar/cache
[INFO] SonarQube version: 7.7.0
[INFO] Default locale: "en_US", source code encoding: "UTF-8"
[INFO] Load global settings
[INFO] Load global settings (done) | time=438ms
[INFO] Server id: BF41A1F2-AWpeMEGY3gB064P2-y0f
[INFO] User cache: /Users/eugenio/.sonar/cache
[INFO] Load/download plugins
[INFO] Load plugins index
[INFO] Load plugins index (done) | time=372ms
[INFO] Load/download plugins (done) | time=426ms
[INFO] Process project properties
[INFO] Execute project builders
[INFO] Execute project builders (done) | time=4ms
[INFO] Project key: com.wuzz:sonar-test
[INFO] Base dir: /Users/eugenio/Projects/Github/sonar-test
[INFO] Working dir: /Users/eugenio/Projects/Github/sonar-test/target/sonar
[INFO] Load project settings for component key: 'com.wuzz:sonar-test'
[INFO] Load project repositories
[INFO] Load project repositories (done) | time=313ms
[INFO] Load quality profiles
[INFO] Load quality profiles (done) | time=356ms
[INFO] Load active rules
[INFO] Load active rules (done) | time=7852ms
[WARNING] SCM provider autodetection failed. Please use "sonar.scm.provider" to define SCM of your project, or disable the SCM Sensor in the project settings.
```

**3rd section:** `[INFO] Indexing files...` where files are indexed, here it is possible to check if all the modules 
and submodules are processed, if the source and test paths are have been correctly found and, of course, the base dir.

```bash
[INFO] Indexing files...
[INFO] Project configuration:
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-alone-unit-tests
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-common
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-database
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-database-integration-tests
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-submodules/wuzz-submodule-unit-test
[INFO]   Source paths: pom.xml, src/main/java
[INFO]   Test paths: src/test/java
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test/wuzz-submodules
[INFO]   Source paths: pom.xml
[INFO] Indexing files of module 'Sonar Test'
[INFO]   Base dir: /Users/eugenio/Projects/Github/sonar-test
[INFO]   Source paths: pom.xml
[INFO] 14 files indexed
[INFO] Quality profile for java: Sonar way
[INFO] Quality profile for xml: Sonar way
```

**4th section:** `[INFO] ------------- Run sensors on module Sonar Test` where the scanner starts to run the sensors 
to scan the projects and calculate metric values `[INFO] Sensor JavaSquidSensor [java]`.

```bash
[INFO] Java Main Files AST scan
[INFO] 1 source files to be analyzed
[INFO] 1/1 source files have been analyzed
[INFO] Java Main Files AST scan (done) | time=445ms
[INFO] Java Test Files AST scan
[INFO] 1 source files to be analyzed
[WARNING] Unable to create a corresponding matcher for custom assertion method, please check the format of the following symbol: ''
[INFO] 1/1 source files have been analyzed
[INFO] Java Test Files AST scan (done) | time=50ms
[INFO] Sensor JavaSquidSensor [java] (done) | time=1075ms
[INFO] Sensor JaCoCo XML Report Importer [jacoco]
[INFO] Sensor JaCoCo XML Report Importer [jacoco] (done) | time=3ms
[INFO] Sensor SurefireSensor [java]
[INFO] parsing [/Users/eugenio/Projects/Github/sonar-test/wuzz-database-integration-tests/target/surefire-reports]
[INFO] Sensor SurefireSensor [java] (done) | time=1ms
[INFO] Sensor JaCoCoSensor [java]
[INFO] Sensor JaCoCoSensor [java] (done) | time=1ms
```

It is possible to check if all the paths are correct, if the `surefire` reports have been correctly found, if the 
JaCoCo sensor points to the right folder etc.

Let's see some issues
```bash
[INFO] No JaCoCo analysis of project coverage can be done since there is no class files.
```
[double check] The sensor needs the class files in order to give some coverage metrics. Tests run against bytecode, 
JaCoCo agents works instrumenting the bytecode and the its dump produce the binary file, everything is one on `
.class` file, on the bytecode.

In the module `wuzz-alone-unit-tests` there is one test but no source code classes, so according to Maven convention 
there is not source code in the right position `src/main/java`, hence the error. 
**Attention** the scanner works based on the Maven convention unless differently specified via analysis parameter 
properties.

And looking a bit before the message happens

```bash
[INFO] Java Main Files AST scan
[INFO] 0 source files to be analyzed
[INFO] Java Main Files AST scan (done) | time=1ms
[INFO] 0/0 source files have been analyzed
[INFO] Java Test Files AST scan
[INFO] 1 source files to be analyzed
[WARNING] Unable to create a corresponding matcher for custom assertion method, please check the format of the following symbol: ''
[INFO] 1/1 source files have been analyzed
[INFO] Java Test Files AST scan (done) | time=29ms
```

[double check] check why the warning message

In the module `wuzz-database` there is just one source class but no tests, hence no JaCoCo execution data file
```bash
[INFO] Sensor JaCoCoSensor [java]
[INFO] Sensor JaCoCoSensor [java] (done) | time=0ms
```

The same is for module `wuzz-submodule-unit-test`
```bash
[INFO] Sensor JaCoCoSensor [java]
[INFO] No JaCoCo analysis of project coverage can be done since there is no class files.
[INFO] Sensor JaCoCoSensor [java] (done) | time=3ms
```
The module has 1 unit test but not source code, so `jacoco,exec` is present in `target` folder but cannot be used to 
analyse any source code.

Another one is
```bash
[INFO] Sensor JaCoCoSensor [java]
[INFO] Analysing /Users/eugenio/Projects/Github/sonar-test/wuzz-common/target/jacoco.exec
[INFO] No information about coverage per test.
[INFO] Sensor JaCoCoSensor [java] (done) | time=51ms
```
Coverage per test is the information about which test covered which file, so logs tells that no information about 
which test covered which lines.
[double check] logs debug to better see what happens

### Some observations from experiments

- Even without the `surefire` reports the number of tests displayed is correct if either `sonar.coverage.jacoco
.xmlReportPaths` or `sonar.javascript.lcov.reportPaths` properties are used.
- Select the coverage, the only two classes in the project are shown, so it means the scanner has even sent the fully
 list of application code along with the code to SonarQube server. A kind of full scan based on Maven convention has 
 been done.
- For each Maven module different Sonar sensors will be executed in order to collect reports and information.
- SonarQub analysis is strictly tailored to the project structure (Maven conventions), language used, unit tests and 
coverage reporters. If the projects follows 100% the conventions many *analysis parameter properties* are optional 
because already configured.
- SonarQube scanner runs the source code analysis, from the [documentation](https://docs.sonarqube.org/display/DEV/API+Basics)
>In the scanner stack, properties are checked in the following order, and the first non-blank value is the one that 
is used:

>System property
Scanner command-line (-Dsonar.property=foo for instance)
Scanner tool (<properties> of scanner for Maven for instance) 
Project configuration defined in the web UI 
Global configuration defined in the web UI 
Default value
- [SonarQube Scanner](https://docs.sonarqube.org/display/DEV/Adding+an+SCM+Provider) uses information from the project's SCM, if available to
    - assign a new issue to the person who introduced it. The last committer on the related line of code is considered to be the author of the issue.
    - estimate the coverage on new code, including added and changed code, on the new code period.
    - display last commits in sources.
    - [SCM integration](https://docs.sonarqube.org/latest/analysis/scm-integration/) and the [analysis parameters]
    (https://docs.sonarqube.org/latest/analysis/analysis-parameters/).




## ToDo

- having integration tests and unit tests in different places than what is expected by the convention can alter the 
real number of unit tests and the outcome of the testing phase: unit tests cannot be easily mapped to the application
 code  and coverage not precise.
    - solution: use the report-merge?
- SonarQube vs SonarScanner vs SonarJava vs SonarSource.
- Sonar takes some properties from Maven `pom.xml`, if they are correctly set, such as the `scm` they will be taken 
from it, [see](https://stackoverflow.com/questions/47158546/sonarqube-links-are-outdated-how-do-i-update-them).
- Sonar sensor [plugin api](https://github.com/SonarSource/sonarqube/blob/6.2
.1/sonar-plugin-api/src/main/java/org/sonar/api/batch/sensor/SensorDescriptor.java#L37-L42) and [JaCoCo sensor]
(https://github.com/SonarSource/sonar-java/blob/4.5.0.8398/java-jacoco/src/main/java/org/sonar/plugins/jacoco/JaCoCoSensor.java#L65).
- Write [SonarQube plugins and sensors](https://deors.wordpress.com/2014/04/01/sonarqube-plugins-2/).
- Important and first class **analysis parameters** to set
```xml
<!-- Sonar server address -->
<sonar.host.url>http://localhost:9000</sonar.host.url>

<!-- Sonar credentials -->
<sonar.login>admin</sonar.login>
<sonar.password>admin</sonar.password>

<!-- Project information and description -->
<sonar.projectKey>${project.artifactId}</sonar.projectKey>
<sonar.projectName>${project.artifactId}</sonar.projectName>
<sonar.projectVersion>${project.version}</sonar.projectVersion>
<sonar.language>java</sonar.language>
<sonar.java.source>1.8</sonar.java.source>
<sonar.sourceEncoding>UTF-8</sonar.sourceEncoding>
<sonar.projectDescription>A simple toy to play with reports and SonarQube</sonar.projectDescription>

<!--SCM, CI and Issue addresses -->
<sonar.scm.provider>git</sonar.scm.provider>
<sonar.links.scm></sonar.links.scm>https://github.com/example/scm<sonar.links.homepage>
<sonar.links.ci>https://github.com/example/ci</sonar.links.ci>
<sonar.links.issue>https://github.com/example/issue</sonar.links.issue>

<!-- Scanner log level -->
<sonar.log.level>INFO</sonar.log.level>

<!-- JaCoCo and Surefire reports -->
<sonar.jacoco.reportPaths>${project.basedir}/../target/jacoco.exec</sonar.jacoco.reportPaths>
```
- `<argLine>` are arbitrary JVM options to set on the command line.
    - [surefire argLine](http://maven.apache.org/surefire/maven-surefire-plugin/faq.html#late-property-evaluation)
    - If your project uses the argLine property to configure the surefire-maven-plugin, be sure that argLine defined 
    as a property, rather than as part of the plugin configuration. Doing so will allow JaCoCo to set its agent properly. Otherwise the JVM may crash while tests are running. 
- [JUnit listeners](https://howtodoinjava.com/junit/how-to-add-listner-in-junit-testcases/), in general, help in 
listening the events on which we are interested. [SonarJava listener](https://github.com/SonarSource/sonar-java/tree/master/sonar-jacoco-listeners/src/main/java/org/sonar/java/jacoco)

## References

[JaCoCo concepts](https://www.baeldung.com/jacoco)
[SonarJava](https://docs.sonarqube.org/display/PLUG/SonarJava)
[Java test and coverage report](https://docs.sonarqube.org/display/PLUG/Java+Unit+Tests+and+Coverage+Results+Import)
[JaCoCo with SonarJava](https://docs.sonarqube.org/display/PLUG/Usage+of+JaCoCo+with+SonarJava)
[SonarQube Maven sample project](https://github.com/SonarSource/sonar-scanning-examples/tree/master/sonarqube-scanner-maven)
[Example 1](https://stackoverflow.com/questions/38094676/jacoco-coverage-per-test-setup)
[Example 2](https://dzone.com/articles/integration-jenkins-jacoco-and-sonarqube)
