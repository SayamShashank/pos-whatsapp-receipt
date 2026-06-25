package com.ina.pos.receipt.constants;

import java.time.format.DateTimeFormatter;

public class AppConstants {

    private AppConstants() {
    }

    public static final String TEMPLATE_PATH = "templates/receipt-template.html";

    public static final DateTimeFormatter INPUT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormatter OUTPUT_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
}
