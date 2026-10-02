package hotel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import hotel.decorators.AddOnCatalog;
import hotel.model.RoomBooking;
import hotel.model.StandardRoom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Acts as the Decorator pattern's Client by stacking decorators at runtime
 * according to the add-ons selected by the user.
 */
public final class HotelServer {
    private static final int PORT = 8080;

    private HotelServer() {
    }

    /**
     * Starts the HTTP server on port 8080 and serves the API and frontend assets.
     *
     * @param args command-line arguments (unused)
     * @throws IOException if the server cannot bind to its port
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", HotelServer::handleRequest);
        server.start();
        System.out.println("Hotel booking server running at http://localhost:" + PORT);
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Only GET requests are supported.\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        switch (path) {
            case "/" -> serveFrontendFile(exchange, "index.html", "text/html; charset=UTF-8");
            case "/styles.css" -> serveFrontendFile(exchange, "styles.css", "text/css; charset=UTF-8");
            case "/app.js" -> serveFrontendFile(exchange, "app.js", "text/javascript; charset=UTF-8");
            case "/api/addons" -> sendJson(exchange, 200, AddOnCatalog.toJson());
            case "/api/booking" -> handleBooking(exchange);
            default -> sendJson(exchange, 404, "{\"error\":\"Not found.\"}");
        }
    }

    private static void serveFrontendFile(HttpExchange exchange, String fileName, String contentType)
            throws IOException {
        Path file = Path.of("frontend", fileName);
        if (!Files.isRegularFile(file)) {
            sendJson(exchange, 500, "{\"error\":\"The requested frontend file is unavailable.\"}");
            return;
        }

        byte[] content = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(content);
        }
    }

    private static void handleBooking(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> parameters = parseQuery(exchange.getRequestURI().getRawQuery());
            String nightsValue = parameters.get("nights");
            if (nightsValue == null || nightsValue.isBlank()) {
                throw new IllegalArgumentException("The nights parameter is required.");
            }

            int nights;
            try {
                nights = Integer.parseInt(nightsValue);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Nights must be a whole number from 1 to 30.");
            }
            if (nights < 1 || nights > 30) {
                throw new IllegalArgumentException("Nights must be between 1 and 30.");
            }

            String addOnsValue = parameters.getOrDefault("addOns", "");
            List<String> addOnIds = new ArrayList<>();
            Set<String> seenAddOnIds = new HashSet<>();
            if (!addOnsValue.isEmpty()) {
                for (String addOnId : addOnsValue.split(",", -1)) {
                    if (addOnId.isBlank()) {
                        throw new IllegalArgumentException("Add-on IDs cannot be empty.");
                    }
                    if (!seenAddOnIds.add(addOnId)) {
                        throw new IllegalArgumentException("Duplicate add-on: " + addOnId);
                    }
                    addOnIds.add(addOnId);
                }
            }

            RoomBooking booking = new StandardRoom();
            List<String> layers = new ArrayList<>();
            layers.add(booking.getClass().getSimpleName());
            for (String addOnId : addOnIds) {
                booking = AddOnCatalog.apply(addOnId, booking);
                layers.add(booking.getClass().getSimpleName());
            }

            sendJson(exchange, 200, bookingToJson(booking, nights, layers));
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(exception.getMessage()) + "\"}");
        }
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        java.util.HashMap<String, String> parameters = new java.util.HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return parameters;
        }

        for (String pair : rawQuery.split("&")) {
            int separator = pair.indexOf('=');
            String rawName = separator < 0 ? pair : pair.substring(0, separator);
            String rawValue = separator < 0 ? "" : pair.substring(separator + 1);
            String name = URLDecoder.decode(rawName, StandardCharsets.UTF_8);
            String value = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
            if (parameters.putIfAbsent(name, value) != null) {
                throw new IllegalArgumentException("Duplicate query parameter: " + name);
            }
        }
        return parameters;
    }

    private static String bookingToJson(RoomBooking booking, int nights, List<String> layers) {
        StringBuilder json = new StringBuilder();
        json.append("{\"description\":\"").append(escapeJson(booking.getDescription()))
                .append("\",\"nights\":").append(nights)
                .append(",\"total\":").append(booking.calculateTotal(nights))
                .append(",\"amenities\":");
        appendStringArray(json, booking.getAmenities());
        json.append(",\"layers\":");
        appendStringArray(json, layers);
        return json.append('}').toString();
    }

    private static void appendStringArray(StringBuilder json, List<String> values) {
        json.append('[');
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append('"').append(escapeJson(values.get(index))).append('"');
        }
        json.append(']');
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] content = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, content.length);
        try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(content);
        }
    }
}
