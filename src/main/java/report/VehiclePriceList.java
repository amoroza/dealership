package report;

import vehicle.Vehicle;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class VehiclePriceList {
    final private Map<Integer, List<Vehicle>> vehicles;
    final private BigDecimal totalMsrp;
    final private BigDecimal totalListPrice;

    private VehiclePriceList(Map<Integer, List<Vehicle>> vehicles, BigDecimal totalMsrp, BigDecimal totalListPrice) {
        this.vehicles = vehicles;
        this.totalMsrp = totalMsrp;
        this.totalListPrice = totalListPrice;
    }

    public Map<Integer, List<Vehicle>> getVehicles() {
        return vehicles;
    }

    public BigDecimal getTotalMsrp() {
        return totalMsrp;
    }

    public BigDecimal getTotalListPrice() {
        return totalListPrice;
    }

    private static List<Vehicle> addListPrices(final List<Vehicle> vehicles, final BigDecimal taxRate){
       return vehicles.stream().peek(vehicle -> {
            BigDecimal listPrice = vehicle.getMsrp().multiply(taxRate);
            vehicle.setListPrice(listPrice);
       }).collect(Collectors.toList());
    }

    public static Map<Integer, List<Vehicle>> groupByYear(final List<Vehicle> vehicles){
        return vehicles.stream().collect(Collectors.groupingBy(Vehicle::getYear, TreeMap::new, Collectors.toList()));
    }

    public static BigDecimal calculateTotalManufacturerSuggestedRetailPrice(final List<Vehicle> vehicles){
        return BigDecimal.valueOf(vehicles.stream().mapToDouble(vehicle -> vehicle.getMsrp().doubleValue()).sum());
    }
    public static BigDecimal calculateTotalListPrice(final List<Vehicle> vehicles) {
        return BigDecimal.valueOf(vehicles.stream().mapToDouble(vehicle -> vehicle.getListPrice().doubleValue()).sum());
    }

    public static VehiclePriceList makePriceList(final List<Vehicle> vehicles, final BigDecimal taxRate) {
        List<Vehicle> vehicleWithPriceList = addListPrices(vehicles, taxRate);
        BigDecimal totalMsrp = calculateTotalManufacturerSuggestedRetailPrice(vehicleWithPriceList);
        BigDecimal totalListPrice= calculateTotalListPrice(vehicleWithPriceList);
        Map<Integer, List<Vehicle>> groupByYearVehicles = groupByYear(vehicleWithPriceList);
        return new VehiclePriceList(groupByYearVehicles, totalMsrp, totalListPrice);
    }
}
