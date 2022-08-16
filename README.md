# dealership

welcome to our brand new dealrship!<br/>
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


