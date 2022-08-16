package report;

import env.Config;
import ingester.CsvFileIngester;
import vehicle.VehicleCsvParser;
import vehicle.VehicleValidator;

import java.io.IOException;

public class VehicleReporter {

    public static void main(final String[] args) throws IOException {

        /**
         * config or its content can be passed via:
         *
         * - main args ^^^
         * - system properties: -Dfoo=bar
         * - environmental variables: export FOO=bar
         * - classpath
         *
         * here this sample solution assumes config from the classpath
         * that can be overridden with system properties (i.e. -Dtax.rate=4.2)
         */
        var config = Config.loadConfig().withSystemProperties();

        System.out.println("working with config: " + config);

        final var ingester = new CsvFileIngester<>(new VehicleCsvParser(),
                                                   new VehicleValidator());

        var ingested = ingester.ingest(config.getInputFilePath());
        var report = new VehicleTextReport(ingested);

        report.generate(config);
    }
}
