package vehicle;

import java.math.BigDecimal;

public class Vehicle {
    private final int year;
    private final String make;
    private final String model;
    private final BigDecimal msrp;

    private BigDecimal listPrice;

    public Vehicle(final int year,
                   final String make,
                   final String model,
                   final BigDecimal msrp) {

        this.year = year;
        this.make = make;
        this.model = model;
        this.msrp = msrp;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getMsrp() {
        return msrp;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    @Override
    public String toString() {
        return "vehicle.Vehicle{" +
                "year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", msrp=" + msrp +
                ", listPrice=" + listPrice +
                '}';
    }
}
