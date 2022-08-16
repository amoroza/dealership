## What do I do?

Read file and provide vehicle report.

## Can you show me?

Sure.

Here is a [sample file](src/main/resources/vehicles.csv) with vehicle information:

```bash
[vehiclereport/src/main/java]$ cat ../resources/vehicles.csv

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

### compiling the program

```bash
[vehiclereport/src/main/java]$ javac VehicleReporter.java
```

### running it

#### missed arguments

```bash
[vehiclereport/src/main/java]$ java VehicleReporter foo

[PROBLEM]: I need exactly 2 arguments, but was given 1 arguments

this program reads a file with vehicle information,
creates file vehiclesMMDDYY.txt with report which groups vehicles by year, 
calculates list price based on provided tax rate,
shows Grand Total for manufacturer suggested retail price and lists price.
it also provides statistics report file (vehicle-statsMMDDYY.txt) which includes total lines and failed lines(line number, type, attribute, description)


usage:       java VehicleReporter <path to a file with tax rate>
for example: java VehicleReporter ../resources/vehicles.csv 1.07
```

#### running it for real

```bash
[vehiclereport/src/main/java]$ java VehicleReporter ../resources/vehicles.csv 1.07
result:
--- Vehicle Report ---                                                  Date:  08/13/2022
2012
	honda civic                    MSRP:$30000.00       List Price:$32100.00

2017
	ford focus                     MSRP:$27500.00       List Price:$29425.00
	chevrolet impala               MSRP:$33000.00       List Price:$35310.00
	honda accord                   MSRP:$28000.00       List Price:$29960.00
	nissan altima                  MSRP:$30000.00       List Price:$32100.00

2018
	acura mdx                      MSRP:$45000.00       List Price:$48150.00
	chrysler pacifica              MSRP:$40000.00       List Price:$42800.00
	jeep wrangler                  MSRP:$55000.00       List Price:$58850.00

2019
	infinity g35                   MSRP:$50000.00       List Price:$53500.00
	chevrolet avalanche            MSRP:$60000.00       List Price:$64200.00

--- Grand Total ---
	MSRP:$398500.00
	List Price:$426395.00

```

```bash
[vehiclereport/src/main/java]$ java VehicleReporter ../resources/vehiclesWithInvalidData.csv 1.07

--- Stats report ---
Lines count: 10
Failed lines: 

line: 0, type: vehicle.Vehicle, atribute: model, description: vehicle model cannot be empty.
line: 1, type: vehicle.Vehicle, atribute: msrp, description: vehicle msrp should be positive, but was [-1]
line: 2, type: vehicle.Vehicle, atribute: year, description: invalid vehicle year. expected a value larger than 1769, but received: [1082]
line: 7, type: vehicle.Vehicle, atribute: model, description: vehicle model cannot be empty.


```


