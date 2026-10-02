package hotel.decorators;

import hotel.model.RoomBooking;

import java.util.List;

/**
 * ConcreteDecorator that delegates to the wrapped booking before adding a
 * spa description, one-time charge, and amenity.
 */
public class SpaDecorator extends RoomBookingDecorator {
    public static final String ID = "spa";
    public static final String NAME = "Spa Access";
    public static final String CHARGE_TYPE = "ONE_TIME";
    public static final long PRICE = 90_000;

    public SpaDecorator(RoomBooking wrappedBooking) {
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
        amenities.add("Spa circuit access");
        return amenities;
    }
}
