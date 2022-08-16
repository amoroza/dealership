package vehicle;

import either.Result;
import error.Errors;
import validator.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class VehicleValidator implements Validator<Vehicle> {

    /**
     * Development of the automobile started in 1672 with the invention of the first steam-powered vehicle,
     * which led to the creation of the first steam-powered automobile capable of human transportation,
     * built by Nicolas-Joseph Cugnot in 1769
     */
    public static final int FIRST_VEHICLE_PRODUCTION_YEAR = 1769;

    private static final String MAKE = "make";
    private static final String MODEL = "model";
    private static final String YEAR = "year";

    /**
     * TODO: In case MSRP can't be X years into the future, need to also validate "year < 'current date + X years'"
     */
    public Result validateYear(final Integer year) {
        if (year > FIRST_VEHICLE_PRODUCTION_YEAR) {
            return Result.success();
        }
        else {
            return Result.error(new Errors.FieldError(Vehicle.class.getTypeName(),
                                             YEAR,
                                           "invalid vehicle year. expected a value larger than 1769, " +
                                                     "but received: [" + year + "]"));
        }
    }

    public Result validateNotEmpty(final String field, final String fieldName){
        if (Objects.isNull(field) ||
                !field.trim().isEmpty()){
            return Result.success();
        } else {
            return Result.error(new Errors.FieldError(Vehicle.class.getTypeName(),
                    fieldName,
                    "vehicle " + fieldName + " cannot be empty."));
        }
    }

    public Result validateManufacturerSuggestedRetailPrice(final BigDecimal msrp){ // raspishi msrp
        if (msrp.compareTo(BigDecimal.ZERO) > 0) {
            return Result.success();
        } else {
            return Result.error(new Errors.FieldError(Vehicle.class.getTypeName(),
                    "msrp",
                    "vehicle msrp should be positive, but was [" + msrp + "]"));
        }
    }

    @Override
    public Result<Vehicle> validate(final Vehicle vehicle) {

        var results = List.of(validateYear(vehicle.getYear()),
                                           validateNotEmpty(vehicle.getMake(), MAKE),
                                           validateNotEmpty(vehicle.getModel(), MODEL),
                                           validateManufacturerSuggestedRetailPrice(vehicle.getMsrp()));

        return Result.withErrorsFromResults(results);
    }
}
