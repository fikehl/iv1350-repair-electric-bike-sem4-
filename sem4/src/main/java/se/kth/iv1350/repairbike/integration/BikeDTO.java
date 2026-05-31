package se.kth.iv1350.repairbike.integration;

/**
 * Contains information about one specific bike. Instances are immutable.
 */
public final class BikeDTO {
    private final String brand;
    private final String model;
    private final String serialNumber;

    /**
     * Creates a new instance representing a particular bike.
     *
     * @param brand        The brand of the bike.
     * @param model        The model of the bike.
     * @param serialNumber The bike's serial number, uniquely identifying the bike.
     */
    public BikeDTO(String brand, String model, String serialNumber) {
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
    }

    /**
     * @return The brand of the bike.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return The model of the bike.
     */
    public String getModel() {
        return model;
    }

    /**
     * @return The bike's serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @return A human-readable description of the bike.
     */
    @Override
    public String toString() {
        return "Bike[brand=" + brand
                + ", model=" + model
                + ", serialNumber=" + serialNumber + "]";
    }
}
