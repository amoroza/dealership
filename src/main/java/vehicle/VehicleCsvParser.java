package vehicle;

import either.Result;
import error.Errors;
import parser.CsvParser;

import java.math.BigDecimal;
import java.util.Arrays;

public class VehicleCsvParser implements CsvParser<Vehicle> {
    final static String[] VEHICLE_HEADER = new String[]{"year", "make", "model", "msrp"};
    final static int NUMBER_OF_FIELDS = VEHICLE_HEADER.length;
    final static String COMMA = ",";

    private Integer parseYear(final String year) {
        try {
            return Integer.parseInt(year);
        }
        catch (final Exception ex) {
            throw new RuntimeException("could not parse vehicle year due to: " + ex);
        }
    }

    private BigDecimal parseManufacturerSuggestedRetailPrice(final String msrp) {
        try {
            return new BigDecimal(msrp);
        }
        catch (final Exception ex) {
            throw new RuntimeException("could not parse vehicle MSRP due to: " + ex);
        }
    }

    @Override
    public Boolean isHeader(final String line) {
        if (line == null) { return false; }

        var header = line.toLowerCase().split(COMMA);
        return Arrays.equals(header, VEHICLE_HEADER) ;
    }

    @Override
    public Result<Vehicle> parse(final String line) {

        // TODO: check line for null
        final String[] vehicle = line.split(COMMA);

        try {

            if (vehicle.length != NUMBER_OF_FIELDS) {
                throw new RuntimeException("expecting " + NUMBER_OF_FIELDS +
                                           " number of fields, but received [" + vehicle.length + "]");
            }

            final var year = parseYear(vehicle[0]);
            final var make = vehicle[1];
            final var model = vehicle[2];
            final var msrp = parseManufacturerSuggestedRetailPrice(vehicle[3]);

            return new Result<>(
                    new Vehicle(year, make, model, msrp));
        }
        catch (Exception ex) {
            return Result.error(new Errors.FieldError(Vehicle.class.getTypeName(), ex.getMessage()));
        }
    }
}
