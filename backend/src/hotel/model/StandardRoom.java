package hotel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ConcreteComponent role of the Decorator pattern.
 *
 * <p>This is the base object that decorators wrap: a plain hotel room with no
 * extras. It is the innermost layer of every decorator chain and the only class
 * in the model that knows the nightly rate of the room itself.</p>
 *
 * <p>A {@code StandardRoom} is fully usable on its own, which is the point of
 * the pattern: add-ons are optional layers, not a requirement.</p>
 */
public class StandardRoom implements RoomBooking {

    /** Nightly rate of a standard room in Colombian pesos (COP). */
    public static final long PRICE_PER_NIGHT = 180_000;

    /** Description shown as the first segment of every booking description. */
    private static final String DESCRIPTION = "Standard Room";

    /** Amenities always included with the room, before any add-on is applied. */
    private static final List<String> BASE_AMENITIES =
            Arrays.asList("Double bed", "Private bathroom", "Wi-Fi");

    /**
     * {@inheritDoc}
     *
     * @return {@code "Standard Room"}, the root of the accumulated description
     */
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Base case of the chain: the room rate multiplied by the number of
     * nights, with no add-on charges.</p>
     */
    @Override
    public long calculateTotal(int nights) {
        return PRICE_PER_NIGHT * nights;
    }

    /**
     * {@inheritDoc}
     *
     * <p>A new list is returned on every call so that a decorator appending its
     * own amenity can never corrupt the amenities of this room.</p>
     */
    @Override
    public List<String> getAmenities() {
        return new ArrayList<>(BASE_AMENITIES);
    }
}
