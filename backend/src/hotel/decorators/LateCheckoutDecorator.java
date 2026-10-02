package hotel.decorators;

import hotel.model.RoomBooking;

import java.util.List;

/**
 * ConcreteDecorator that delegates to the wrapped booking before adding a
 * late-checkout description, one-time charge, and amenity.
 */
public class LateCheckoutDecorator extends RoomBookingDecorator {
    public static final String ID = "late-checkout";
    public static final String NAME = "Late Checkout";
    public static final String CHARGE_TYPE = "ONE_TIME";
    public static final long PRICE = 40_000;

    public LateCheckoutDecorator(RoomBooking wrappedBooking) {
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
        amenities.add("Checkout until 3:00 PM");
        return amenities;
    }
}
