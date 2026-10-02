const currencyFormatter = new Intl.NumberFormat("es-CO", {
  style: "currency",
  currency: "COP",
  maximumFractionDigits: 0
});

let latestBookingRequest = 0;

/**
 * Loads available add-ons, renders their selection controls, and requests the initial booking.
 */
async function loadAddOns() {
  const addOnContainer = document.getElementById("addons");
  try {
    const response = await fetch("/api/addons");
    if (!response.ok) {
      throw new Error(`Could not load add-ons (HTTP ${response.status}).`);
    }
    const addOns = await response.json();
    renderAddOns(addOns);
    await recalculateBooking();
  } catch (error) {
    showError(error.message || "Could not load available add-ons.");
    addOnContainer.replaceChildren(createTextElement("p", "muted", "Add-ons are unavailable."));
  }
}

/**
 * Renders catalog add-ons using DOM nodes and attaches the booking update event.
 */
function renderAddOns(addOns) {
  const addOnContainer = document.getElementById("addons");
  addOnContainer.replaceChildren();

  addOns.forEach((addOn) => {
    const label = document.createElement("label");
    label.className = "addon-option";

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.name = "addOn";
    checkbox.value = addOn.id;
    checkbox.addEventListener("change", recalculateBooking);

    const info = document.createElement("span");
    info.className = "addon-info";
    info.append(
      createTextElement("span", "addon-name", addOn.name),
      createTextElement(
        "span",
        "addon-charge",
        addOn.chargeType === "PER_NIGHT" ? "Charged per night" : "One-time charge"
      )
    );

    const price = currencyFormatter.format(addOn.price);
    const priceLabel = addOn.chargeType === "PER_NIGHT" ? `${price} / night` : price;
    label.append(
      checkbox,
      info,
      createTextElement("span", "addon-price", priceLabel)
    );
    addOnContainer.append(label);
  });
}

/**
 * Requests the current booking and ignores stale responses from earlier selections.
 */
async function recalculateBooking() {
  const requestId = ++latestBookingRequest;
  const nightsInput = document.getElementById("nights");
  const nights = Number(nightsInput.value);

  if (!Number.isInteger(nights) || nights < 1 || nights > 30) {
    showError("Choose a whole number of nights from 1 to 30.");
    return;
  }

  clearError();
  const addOns = Array.from(document.querySelectorAll('input[name="addOn"]:checked'))
    .map((checkbox) => checkbox.value);
  const query = new URLSearchParams({
    nights: String(nights),
    addOns: addOns.join(",")
  });

  try {
    const response = await fetch(`/api/booking?${query.toString()}`);
    const booking = await response.json();
    if (!response.ok) {
      throw new Error(booking.error || `Booking request failed (HTTP ${response.status}).`);
    }
    if (requestId === latestBookingRequest) {
      renderBooking(booking);
    }
  } catch (error) {
    if (requestId === latestBookingRequest) {
      showError(error.message || "Could not calculate your booking.");
    }
  }
}

/**
 * Updates the booking summary and draws decorator layers with the room at the center.
 */
function renderBooking(booking) {
  document.getElementById("description").textContent = booking.description;
  document.getElementById("stay-detail").textContent =
    `${booking.nights} ${booking.nights === 1 ? "night" : "nights"}`;
  document.getElementById("total").textContent = currencyFormatter.format(booking.total);

  const amenities = document.getElementById("amenities");
  amenities.replaceChildren(
    ...booking.amenities.map((amenity) => createTextElement("li", "", amenity))
  );
  renderLayers(booking.layers);
}

/**
 * Creates nested layer boxes in outer-to-inner order from the API's inner-to-outer list.
 */
function renderLayers(layers) {
  const layerContainer = document.getElementById("layers");
  let nestedLayer = null;

  layers.slice().reverse().forEach((layerName) => {
    const layer = document.createElement("div");
    const isRoom = layerName === "StandardRoom";
    layer.className = `layer-box${isRoom ? " room-layer" : ""}`;
    layer.append(
      createTextElement(
        "span",
        "layer-type",
        isRoom ? "BASE COMPONENT" : "DECORATOR"
      ),
      createTextElement("strong", "", layerName)
    );
    if (nestedLayer) {
      layer.append(nestedLayer);
    }
    nestedLayer = layer;
  });

  layerContainer.replaceChildren(...(nestedLayer ? [nestedLayer] : []));
}

/**
 * Creates a text-only element with the requested tag and CSS class.
 */
function createTextElement(tagName, className, text) {
  const element = document.createElement(tagName);
  if (className) {
    element.className = className;
  }
  element.textContent = text;
  return element;
}

/**
 * Displays an API or validation error in the booking form.
 */
function showError(message) {
  const errorMessage = document.getElementById("error-message");
  errorMessage.textContent = message;
  errorMessage.hidden = false;
}

/**
 * Hides the current booking error.
 */
function clearError() {
  const errorMessage = document.getElementById("error-message");
  errorMessage.textContent = "";
  errorMessage.hidden = true;
}

document.getElementById("nights").addEventListener("input", recalculateBooking);
loadAddOns();
