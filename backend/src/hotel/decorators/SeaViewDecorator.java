package hotel.decorators;

import hotel.model.RoomBooking;

import java.util.List;

/**
 * ConcreteDecorator that delegates to the wrapped booking before adding a
 * sea-view description, per-night charge, and amenity.
 */
public class SeaViewDecorator extends RoomBookingDecorator {
    public static final String ID = "sea-view";
    public static final String NAME = "Sea View Upgrade";
    public static final String CHARGE_TYPE = "PER_NIGHT";
    public static final long PRICE = 50_000;

    public SeaViewDecorator(RoomBooking wrappedBooking) {
        super(wrappedBooking);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + NAME;
    }

    @Override
    public long calculateTotal(int nights) {
        // PER_NIGHT charges are added once for each night of the stay.
        return super.calculateTotal(nights) + PRICE * nights;
    }

    @Override
    public List<String> getAmenities() {
        List<String> amenities = super.getAmenities();
        amenities.add("Sea view balcony");
        return amenities;
    }
}
