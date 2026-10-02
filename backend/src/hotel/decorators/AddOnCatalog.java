package hotel.decorators;

import hotel.model.RoomBooking;

/**
 * Centralizes decorator creation by add-on id so clients such as the server
 * can use add-ons without depending on concrete decorator classes.
 */
public final class AddOnCatalog {
    private AddOnCatalog() {
    }

    public static RoomBooking apply(String addOnId, RoomBooking booking) {
        if (addOnId == null) {
            throw new IllegalArgumentException("Unknown add-on id: null");
        }

        return switch (addOnId) {
            case BreakfastDecorator.ID -> new BreakfastDecorator(booking);
            case SeaViewDecorator.ID -> new SeaViewDecorator(booking);
            case SpaDecorator.ID -> new SpaDecorator(booking);
            case AirportTransferDecorator.ID -> new AirportTransferDecorator(booking);
            case LateCheckoutDecorator.ID -> new LateCheckoutDecorator(booking);
            default -> throw new IllegalArgumentException("Unknown add-on id: " + addOnId);
        };
    }

    public static String toJson() {
        StringBuilder json = new StringBuilder("[");
        appendEntry(json, BreakfastDecorator.ID, BreakfastDecorator.NAME,
                BreakfastDecorator.PRICE, BreakfastDecorator.CHARGE_TYPE);
        appendEntry(json, SeaViewDecorator.ID, SeaViewDecorator.NAME,
                SeaViewDecorator.PRICE, SeaViewDecorator.CHARGE_TYPE);
        appendEntry(json, SpaDecorator.ID, SpaDecorator.NAME,
                SpaDecorator.PRICE, SpaDecorator.CHARGE_TYPE);
        appendEntry(json, AirportTransferDecorator.ID, AirportTransferDecorator.NAME,
                AirportTransferDecorator.PRICE, AirportTransferDecorator.CHARGE_TYPE);
        appendEntry(json, LateCheckoutDecorator.ID, LateCheckoutDecorator.NAME,
                LateCheckoutDecorator.PRICE, LateCheckoutDecorator.CHARGE_TYPE);
        return json.append(']').toString();
    }

    private static void appendEntry(StringBuilder json, String id, String name,
                                    long price, String chargeType) {
        if (json.length() > 1) {
            json.append(',');
        }
        json.append("{\"id\":\"").append(id)
                .append("\",\"name\":\"").append(name)
                .append("\",\"price\":").append(price)
                .append(",\"chargeType\":\"").append(chargeType)
                .append("\"}");
    }
}
