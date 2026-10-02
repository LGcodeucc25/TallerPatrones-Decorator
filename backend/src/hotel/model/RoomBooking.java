package hotel.model;

import java.util.List;

/**
 * Component role of the Decorator pattern.
 *
 * <p>This is the common abstraction shared by the object being decorated and by
 * every decorator that wraps it. Because {@code StandardRoom} (the concrete
 * component) and {@code RoomBookingDecorator} (the base decorator) both
 * implement this interface, a client can treat a bare room and a room wrapped in
 * five add-ons exactly the same way, and decorators can be stacked in any order
 * at runtime.</p>
 *
 * <p>All prices are expressed in Colombian pesos (COP) and handled as
 * {@code long} values, so no rounding error is introduced.</p>
 */
public interface RoomBooking {

    /**
     * Returns the human readable description of this booking, accumulated
     * through the decorator chain.
     *
     * @return for example {@code "Standard Room + Breakfast Buffet + Spa Access"}
     */
    String getDescription();

    /**
     * Returns the total price of the whole stay in COP.
     *
     * <p>Each decorator adds its own charge on top of the price returned by the
     * booking it wraps: a per-night add-on multiplies its price by
     * {@code nights}, while a one-time add-on adds its price only once.</p>
     *
     * @param nights number of nights of the stay
     * @return the accumulated total of the stay in COP
     */
    long calculateTotal(int nights);

    /**
     * Returns every amenity included in this booking.
     *
     * <p>The list grows as decorators are applied: each decorator copies the
     * amenities of the booking it wraps and appends its own.</p>
     *
     * @return the accumulated list of included amenities
     */
    List<String> getAmenities();
}
