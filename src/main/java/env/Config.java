package env;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static java.util.Objects.nonNull;

public class Config {
    public static final String VEHICLES_FILE_PATH = "vehicles.file.path";
    public static final String VEHICLE_PRICE_REPORT_DIR = "vehicle.price.report.dir";
    public static final String REPORT_STATS_DIR = "report.stats.dir";
    public static final String TAX_RATE = "tax.rate";
    private final String inputFilePath;
    private final String priceReportDir;
    private final String statsDir;
    private final BigDecimal taxRate;


    public Config(final Builder builder) {
        this.inputFilePath = builder.inputFilePath;
        this.priceReportDir = builder.priceReportDir;
        this.statsDir = builder.statsDir;
        this.taxRate = builder.taxRate;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getPriceReportDir() {
        return priceReportDir;
    }

    public String getStatsDir() {
        return statsDir;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public static class Builder {
        private String inputFilePath;
        private String priceReportDir;
        private String statsDir;
        private BigDecimal taxRate;


        private BigDecimal parseTaxRate(String rawTaxRate) {

            BigDecimal taxRate;

            try {
                taxRate = new BigDecimal(rawTaxRate);
            }
            catch (Exception ex) {
                throw new RuntimeException("could not parse the tax rate [" + rawTaxRate + "]. expecting a ");
            }
            if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("tax rate can't be less than zero, but was [" + taxRate + "]");
            }
            return  taxRate;
        }

        private Builder(){}

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder inputFilePath(final String inputFilePath) {
            this.inputFilePath = inputFilePath;
            return this;
        }

        public Builder priceReportDir(final String priceReportDir) {
            this.priceReportDir = priceReportDir;
            return this;
        }

        public Builder statsDir(final String statsDir) {
            this.statsDir = statsDir;
            return this;
        }

        public Builder taxRate(final String taxRate) {
            this.taxRate = parseTaxRate(taxRate);
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    public static Config loadConfig() {
        var path = System.getProperty("config.path");

        if (path == null || ! new File(path).exists()) {
            throw new RuntimeException("could not find a configuration file in path provided (or not): " +
                                       "[path = " + path + "]. " +
                                       "please pass a system property \"config.path\" " +
                                       "that points to a configuration file.");
        }

        try {
            var configIn = new FileInputStream(path);
            var props = new Properties();
            props.load(configIn);

            System.out.println("config: " + props);

            return Config.Builder.newInstance()
                         .inputFilePath(props.getProperty(VEHICLES_FILE_PATH))
                         .priceReportDir(props.getProperty(VEHICLE_PRICE_REPORT_DIR))
                         .statsDir(props.getProperty(REPORT_STATS_DIR))
                         .taxRate(props.getProperty(TAX_RATE))
                         .build();
        }
        catch (Exception ex) {
            throw new RuntimeException(
                    "could not read properties from a configuration file [" + path + "] due to:", ex);
        }
    }

    public Config withSystemProperties () {
        var inputFilePath = System.getProperty(VEHICLES_FILE_PATH);
        var reportDir = System.getProperty(VEHICLE_PRICE_REPORT_DIR);
        var reportStatsDir = System.getProperty(REPORT_STATS_DIR);
        var taxRate =  System.getProperty(TAX_RATE);

        return Config.Builder.newInstance()
                     .inputFilePath(inputFilePath == null ? this.inputFilePath : inputFilePath)
                     .priceReportDir(reportDir == null ? this.priceReportDir : reportDir)
                     .statsDir(reportStatsDir == null ? this.statsDir : reportStatsDir)
                     .taxRate(taxRate == null ? this.taxRate.toString() : taxRate)
                     .build();
    }

    @Override
    public String toString() {
        return "Config{" +
                "inputFilePath='" + inputFilePath + '\'' +
                ", priceReportDir='" + priceReportDir + '\'' +
                ", statsDir='" + statsDir + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}
