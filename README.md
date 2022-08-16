# dealership

- [what and how](#what-and-how)
  - [what](#what)
  - [how](#how)
- [show me](#show-me)
  - [what is inside](#what-is-inside)
  - [how to run it](#how-to-run-it)
  - [run it](#run-it)
- [overriding configuration](#overriding-configuration)
- [dealing with errors](#dealing-with-errors)
- [where are my libraries?](#where-are-my-libraries)

welcome to our brand new dealership!<br/>
you have come to the right place where we make vehicle price list reports.

## what and how

### what

* for a list of vehicles with year, make, model and msrp
* given a current tax rate
* we generate a detailed price report that details each vehicle grouped by year and ordered by make
* the report will display the vehicle’s information along with its list price (the list price is the MSRP x Tax Rate)
* we then save this report with additional report stats (it would come handy in case something fails)

### how

dealership takes a configuration properties file in a form of:

```properties
vehicles.file.path=/tmp/vehicles.csv
vehicle.price.report.dir=/tmp
report.stats.dir=/tmp
tax.rate=1.07
```

`/tmp/vehicles.csv` will be an input file that would look something like this:

```csv
year,make,model,msrp
2012,honda,civic,30000
2018,acura,mdx,45000
2017,ford,focus,27500
2017,chevrolet,impala,33000
2018,chrysler,pacifica,40000
2018,jeep,wrangler,55000
2017,honda,accord,28000
2019,infinity,g35,50000
2019,chevrolet,avalanche,60000
2017,nissan,altima,30000
```

dealership then:

* parses the configuration file into [env.Config](src/main/java/env/Config.java)
* ingests the input (csv) file into a list of [Result](src/main/java/either/Result.java)s
  - in order to do that it parses each line with a [VehicleCsvParser](src/main/java/vehicle/VehicleCsvParser.java)
  - and validates it with a [VehicleValidator](src/main/java/vehicle/VehicleValidator.java)
  - in case of an [Error](src/main/java/error/Errors.java#L10-L51), it records it as Result's [Errors](src/main/java/error/Errors.java)
* creates a [VehicleTextReport](src/main/java/report/VehicleTextReport.java)
  - that is constructed from a list of Results
* calls a [generate](src/main/java/report/VehicleReporter.java#L33) method of VehicleTextReport
  - that [makes](src/main/java/report/VehicleTextReport.java#L120) a [VehiclePriceList](src/main/java/report/VehiclePriceList.java)
  - creates [text reports](src/main/java/report/VehicleTextReport.java#L122)
  - creates [statistics](src/main/java/report/VehicleTextReport.java#L124)
  - and [writes both](src/main/java/report/VehicleTextReport.java#L128-L134) report and statistics into text files

## show me

dealership is written in Java and can be executed in multiple ways: from the editor, with maven, with `java -jar`, etc.<br/>
this particular example uses maven (a.k.a. `mvn`) to compile and run this program.

### what is inside

before running it, let's look at the program structure:

```bash
[dealership]$ tree
.
├── README.md
├── pom.xml
└── src
    ├── main
    │   └── java
    │       ├── either
    │       │   └── Result.java
    │       ├── env
    │       │   └── Config.java
    │       ├── error
    │       │   └── Errors.java
    │       ├── ingester
    │       │   └── CsvFileIngester.java
    │       ├── parser
    │       │   └── CsvParser.java
    │       ├── report
    │       │   ├── VehiclePriceList.java
    │       │   ├── VehicleReporter.java
    │       │   └── VehicleTextReport.java
    │       ├── validator
    │       │   └── Validator.java
    │       └── vehicle
    │           ├── Vehicle.java
    │           ├── VehicleCsvParser.java
    │           └── VehicleValidator.java
    └── test
        └── resources
            ├── config.properties
            ├── vehicles-invalid.csv
            └── vehicles.csv
```

### how to run it

the entry point (a class with a `main` method) is `VehicleReporter` that constructs and runs it all.

a `pom.xml` has an [exec plugin](https://www.mojohaus.org/exec-maven-plugin/) which allows to execute it with `mvn exec:java`:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <mainClass>report.VehicleReporter</mainClass>
    </configuration>
</plugin>
```

so, given that maven is installed, and this repository is cloned, this is how to run the program:

```bash
$ mvn clean compile exec:java
```

### run it

following the section above, we can run dealership as:

```bash
$ mvn clean compile exec:java
```

but.. it would fail :) with an exception:

```java
java.lang.RuntimeException: could not find a configuration file in path provided (or not): [path = null]. please pass a system property "config.path" that points to a configuration file.
    at env.Config.loadConfig (Config.java:104)
    at report.VehicleReporter.main (VehicleReporter.java:25)
    at org.codehaus.mojo.exec.ExecJavaMojo$1.run (ExecJavaMojo.java:254)
    at java.lang.Thread.run (Thread.java:830)
```

since dealership needs a configuration file, which can be provided with a `config.path` system property.

feel free to copy all the files from [src/test/resources](src/test/resources) to any directory, say `/tmp`:

```bash
[dealership]$ cp src/test/resources/* /tmp

[dealership]$ ls -l /tmp/
total 48
-rw-r--r--  1 user  group   150 Aug 15 23:45 config.properties
-rw-r--r--  1 user  group   262 Aug 15 23:45 vehicles-invalid.csv
-rw-r--r--  1 user  group   273 Aug 15 23:45 vehicles.csv
```

now we are ready to give it a try:

```bash
$ mvn clean compile exec:java -Dconfig.path=/tmp/config.properties

[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ dealership ---

working with config: Config{inputFilePath='/tmp/vehicles.csv', priceReportDir='/tmp', statsDir='/tmp', taxRate=1.07}
created vehicle price report in /tmp/vehicles081522.txt, with report's stats in /tmp/report-stats.081522.txt
```

dealership generated a report that looks like this:

```
$ cat /tmp/vehicles081522.txt

--- Vehicle Report ---                                                  Date:  08/15/2022
2012
	honda civic                    MSRP:$30000.00       List Price:$32100.00

2017
	chevrolet impala               MSRP:$33000.00       List Price:$35310.00
	ford focus                     MSRP:$27500.00       List Price:$29425.00
	honda accord                   MSRP:$28000.00       List Price:$29960.00
	nissan altima                  MSRP:$30000.00       List Price:$32100.00

2018
	acura mdx                      MSRP:$45000.00       List Price:$48150.00
	chrysler pacifica              MSRP:$40000.00       List Price:$42800.00
	jeep wrangler                  MSRP:$55000.00       List Price:$58850.00

2019
	chevrolet avalanche            MSRP:$60000.00       List Price:$64200.00
	infinity g35                   MSRP:$50000.00       List Price:$53500.00


--- Grand Total ---
	MSRP:$398500.00
	List Price:$426395.00
```

and statistics that looks like this:

```
$ cat /tmp/report-stats.081522.txt

--- Report Statistics ---

Total lines read: 10
Failed lines count: 0
```

## overriding configuration

configuration file is a one stop place to provide properties to dealership.<br/>
however all these properties can be overridden with system properties.

for example, a default tax rate is `1.07%`, but let's say it went up or we need different report to show a customer with a different tax rate.<br/>
this can be done with providing tax rate as a system property:

```bash
$ mvn clean compile exec:java -Dconfig.path=/tmp/config.properties -Dtax.rate=1.42

[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ dealership ---

working with config: Config{inputFilePath='/tmp/vehicles.csv', priceReportDir='/tmp', statsDir='/tmp', taxRate=1.42}
created vehicle price report in /tmp/vehicles081522.txt, with report's stats in /tmp/report-stats.081522.txt
```

notice that the dealership picked it up and now works with:

> working with config:  ... taxRate=1.42

if we look at the report, it will take a new tax rate as its input:

```
$ cat /tmp/vehicles081522.txt

--- Vehicle Report ---                                                  Date:  08/15/2022
2012
	honda civic                    MSRP:$30000.00       List Price:$42600.00

2017
	chevrolet impala               MSRP:$33000.00       List Price:$46860.00
	ford focus                     MSRP:$27500.00       List Price:$39050.00
	honda accord                   MSRP:$28000.00       List Price:$39760.00
	nissan altima                  MSRP:$30000.00       List Price:$42600.00

2018
	acura mdx                      MSRP:$45000.00       List Price:$63900.00
	chrysler pacifica              MSRP:$40000.00       List Price:$56800.00
	jeep wrangler                  MSRP:$55000.00       List Price:$78100.00

2019
	chevrolet avalanche            MSRP:$60000.00       List Price:$85200.00
	infinity g35                   MSRP:$50000.00       List Price:$71000.00


--- Grand Total ---
	MSRP:$398500.00
	List Price:$565870.00
```

all other properties can be [customized and overridden](https://github.com/amoroza/dealership/blob/master/src/main/java/env/Config.java#L126-L129).

## dealing with errors

let's look at the invalid input file with some "bad" lines:

```
$ cat /tmp/vehicles-invalid.csv

year,make,model,msrp
2012,honda,,30000                  << missing model
2018,acura,mdx,-1                  << price is less than $0
1082,ford,focus,27500              << 1082.. too old for ford focus :)
2017,chevrolet,impala,33000
2018,chrysler,pacifica,40000
1582,jeep,wrangler,55000           << 1582.. too old for jeep wrangler :)
2017,honda,accord,28000
2019,,g35,50000                    << missing make
2019,chevrolet,avalanche,60000
2017,nissan,altima,30000
```

let's generate the report:

```
$ mvn clean compile exec:java -Dconfig.path=/tmp/config.properties -Dvehicles.file.path=/tmp/vehicles-invalid.csv

[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ dealership ---

working with config: Config{inputFilePath='/tmp/vehicles-invalid.csv', priceReportDir='/tmp', statsDir='/tmp', taxRate=1.07}
created vehicle price report in /tmp/vehicles081622.txt, with report's stats in /tmp/report-stats.081622.txt
```

notice `-Dvehicles.file.path=/tmp/vehicles-invalid.csv`: i.e. we overridden the input file with an invalid one.

let's look at the report:

```
$ cat /tmp/vehicles081622.txt

--- Vehicle Report ---                                                  Date:  08/16/2022
2017
	chevrolet impala               MSRP:$33000.00       List Price:$35310.00
	honda accord                   MSRP:$28000.00       List Price:$29960.00
	nissan altima                  MSRP:$30000.00       List Price:$32100.00

2018
	chrysler pacifica              MSRP:$40000.00       List Price:$42800.00

2019
	chevrolet avalanche            MSRP:$60000.00       List Price:$64200.00

--- Grand Total ---
	MSRP:$191000.00
	List Price:$204370.00
```

not that many vehicles. this is due the errors we introduce.

and we can see them in stats that dealership also generated:

```
$ cat /tmp/report-stats.081622.txt

--- Report Statistics ---

Total lines read: 10
Failed lines count: 5

Lines that failed:

line number: 0, type: vehicle.Vehicle, attribute: model, description: vehicle model cannot be empty.
line number: 1, type: vehicle.Vehicle, attribute: msrp, description: vehicle msrp should be positive, but was [-1]
line number: 2, type: vehicle.Vehicle, attribute: year, description: invalid vehicle year. expected a value larger than 1769, but received: [1082]
line number: 5, type: vehicle.Vehicle, attribute: year, description: invalid vehicle year. expected a value larger than 1769, but received: [1582]
line number: 7, type: vehicle.Vehicle, attribute: make, description: vehicle make cannot be empty.
```

> _"invalid vehicle year. expected a value larger than 1769"_

is an interesting one, but dealership adds it based on the [research](src/main/java/vehicle/VehicleValidator.java#L13-L17).

## where are my libraries?

dealership uses Java core with no libraries.<br/>
the reason for that is personal challenge to learn as well as to create.

also simplicity is great.

once the dealership goes IPO, we'll definitely add a logging library :)
