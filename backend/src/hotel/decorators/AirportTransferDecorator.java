package hotel.decorators;

import hotel.model.RoomBooking;

import java.util.List;

/**
 * ConcreteDecorator that delegates to the wrapped booking before adding an
 * airport-transfer description, one-time charge, and amenity.
 */
public class AirportTransferDecorator extends RoomBookingDecorator {
    public static final String ID = "airport-transfer";
    public static final String NAME = "Airport Transfer";
    public static final String CHARGE_TYPE = "ONE_TIME";
    public static final long PRICE = 60_000;

    public AirportTransferDecorator(RoomBooking wrappedBooking) {
        super(wrappedBooking);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + NAME;
    }

    @Override
    public long calculateTotal(int nights) {
        // ONE_TIME charges are added once for the entire booking.
        return super.calculateTotal(nights) + PRICE;
    }

    @Override
    public List<String> getAmenities() {
        List<String> amenities = super.getAmenities();
        amenities.add("Round-trip airport transfer");
        return amenities;
    }
}
