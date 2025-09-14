package com.dmdm.ecommerce_backend.helpers;

import java.util.Map;
import java.util.Set;

public class ProductHelper {

    public static void validatePatchFields(Map<String, Object> updates) {
        Set<String> allowedFields = Set.of("name", "description", "price", "stockQuantity");

        for (String key : updates.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new IllegalArgumentException("Invalid field: " + key);
            }

            Object value = updates.get(key);

            switch (key) {
                case "name", "description" -> {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException(key + " must be a string");
                    }
                }
                case "price" -> {
                    try {
                        Double.parseDouble(value.toString());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("price must be a valid number");
                    }
                }
                case "stockQuantity" -> {
                    if (!(value instanceof Integer)) {
                        throw new IllegalArgumentException("stockQuantity must be an integer");
                    }
                }
            }
        }
    }
}
