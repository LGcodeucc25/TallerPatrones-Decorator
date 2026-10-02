package hotel.decorators;

import hotel.model.RoomBooking;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator role of the Decorator pattern.
 *
 * <p>This abstract class is the shared parent of every add-on. It implements
 * {@link RoomBooking} and at the same time holds a reference to another
 * {@code RoomBooking}, which is what makes the layers stackable: the wrapped
 * object may be a {@code StandardRoom} or another decorator, and the wrapper is
 * indistinguishable from the wrapped object to any client.</p>
 *
 * <p>By default every method simply delegates to the wrapped booking, so a
 * concrete decorator only has to override what it actually changes: typically it
 * appends its name to the description, adds its charge to the total and appends
 * its amenity to the list.</p>
 */
public abstract class RoomBookingDecorator implements RoomBooking {

    /** The booking being decorated: the next layer towards the standard room. */
    protected final RoomBooking wrappedBooking;

    /**
     * Wraps an existing booking in a new layer.
     *
     * @param wrappedBooking the booking to decorate, never {@code null}
     * @throws IllegalArgumentException if {@code wrappedBooking} is {@code null}
     */
    protected RoomBookingDecorator(RoomBooking wrappedBooking) {
        if (wrappedBooking == null) {
            throw new IllegalArgumentException("wrappedBooking must not be null");
        }
        this.wrappedBooking = wrappedBooking;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to the wrapped booking. Concrete decorators override this to
     * append {@code " + " + NAME}.</p>
     */
    @Override
    public String getDescription() {
        return wrappedBooking.getDescription();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to the wrapped booking. Concrete decorators override this to
     * add {@code PRICE * nights} for a per-night add-on, or {@code PRICE} once
     * for a one-time add-on.</p>
     */
    @Override
    public long calculateTotal(int nights) {
        return wrappedBooking.calculateTotal(nights);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns a new {@link ArrayList} copy of the wrapped booking's amenities
     * so that a subclass can append its own amenity without mutating the inner
     * layers of the chain.</p>
     */
    @Override
    public List<String> getAmenities() {
        return new ArrayList<>(wrappedBooking.getAmenities());
    }
}
