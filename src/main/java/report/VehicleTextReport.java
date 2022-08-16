package report;

import either.Result;
import env.Config;
import error.Errors;
import vehicle.Vehicle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VehicleTextReport {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter FILE_NAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyy");
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    private final List<Result<Vehicle>> vehicles;
    private static final List<String> reportLines =  new ArrayList<>();


    public VehicleTextReport(final List<Result<Vehicle>> vehicles) {
        this.vehicles = vehicles;
    }

    private List<Vehicle> findVehicles() {
        return this.vehicles.stream()
                            .filter(Result::isSuccess)
                            .map(Result::getRecord)
                            .collect(Collectors.toList());
    }

    private List<String> makeReport(final VehiclePriceList vehiclePriceList) {
        makeHeader();
        makeVehicleList(vehiclePriceList.getVehicles());
        makeGrandTotal(vehiclePriceList.getTotalMsrp(),
                       vehiclePriceList.getTotalListPrice());
        return reportLines;
    }

    private static void makeHeader() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        String header =  String.format("%s %55s %s", "--- Vehicle Report ---","Date: ", date);

         reportLines.add(header);
    }

    private static void makeVehicleList(final Map<Integer, List<Vehicle>> vehicles) {
        for (var entry : vehicles.entrySet()) {
            reportLines.add(entry.getKey().toString());
            for (Vehicle vehicle: entry.getValue()) {
                String line = String.format("\t%-30s %-20s %s", vehicle.getMake() + SPACE
                                            + vehicle.getModel(), "MSRP:$"
                                            + vehicle.getMsrp().setScale(2, RoundingMode.DOWN),
                        "List Price:$" + vehicle.getListPrice().setScale(2, RoundingMode.DOWN));
                reportLines.add(line);
            }
            reportLines.add(EMPTY_STRING);
        }
    }

    private static void makeGrandTotal(final BigDecimal totalMsrp, final BigDecimal totalListPrice) {
        reportLines.add("--- Grand Total ---");
        reportLines.add(String.format("\t%s", "MSRP:$" + totalMsrp.setScale(2, RoundingMode.DOWN)));
        reportLines.add(String.format("\t%s","List Price:$" + totalListPrice.setScale(2, RoundingMode.DOWN)));
    }


    private static String makeReportFilePath(Config config) {
        return config.getPriceReportDir() + "/vehicles" + LocalDate.now().format(FILE_NAME_DATE_FORMATTER) + ".txt";
    }

    private static String makeStatsReportFilePath(Config config) {
        return config.getStatsDir() + "/report-stats." + LocalDate.now().format(FILE_NAME_DATE_FORMATTER) + ".txt";
    }

    private Long countFailedLines() {
        return this.vehicles.stream()
                            .filter(r -> ! r.isSuccess())
                            .count();

    }

    private List<String> makeStats() {

        final var lines = new ArrayList<String>();
        final var failedLineCount = countFailedLines();

        lines.add("--- Report Statistics ---\n");
        lines.add("Total lines read: " + this.vehicles.size());
        lines.add("Failed lines count: " + failedLineCount);

        if (failedLineCount > 0) {
            lines.add("\nLines that failed: \n");
        }

        for(int i = 0; i < this.vehicles.size(); i++) {
            var errors = this.vehicles.get(i).getErrors().allErrors();
            for(Errors.FieldError e: errors) {
                lines.add("line number: " + i + ", type: "
                          + e.getType() + ", attribute: "
                          + e.getAttribute() + ", description: "
                          + e.getDescription());
            };
        }

        return lines;
    }

    public void generate(final Config config) {

        var priceList = VehiclePriceList.makePriceList(findVehicles(), config.getTaxRate());
        var stats = makeStats();
        var report = makeReport(priceList);
        var reportPath = makeReportFilePath(config);
        var statsPath = makeStatsReportFilePath(config);

        try {

            Files.write(Path.of(statsPath),
                        stats,
                        StandardCharsets.UTF_8);

            Files.write(Path.of(reportPath),
                        report,
                        StandardCharsets.UTF_8);

            //TODO: this is meant for logging not standard out
            System.out.println("created vehicle price report in " + reportPath +
                               ", with report's stats in " + statsPath);
        }
        catch (Exception ex) {
            throw new RuntimeException("could not write report to a file (config: [" + config + "]) due to: ", ex);
        }
    }
}
