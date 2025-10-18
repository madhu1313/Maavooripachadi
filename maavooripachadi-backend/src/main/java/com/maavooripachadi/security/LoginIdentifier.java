package com.maavooripachadi.security;

import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility helper to normalise and classify login identifiers.
 * Supports email addresses and international phone numbers (digits only after normalisation).
 */
public final class LoginIdentifier {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private LoginIdentifier() {
    }

    public enum Type {
        EMAIL,
        PHONE
    }

    public record Parsed(Type type, String value) {
    }

    /**
     * Parse and normalise a raw identifier, returning its detected type and canonical value.
     *
     * @param raw user supplied email or phone number
     * @return parsed identifier
     * @throws IllegalArgumentException when the identifier cannot be classified
     */
    public static Parsed parse(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new IllegalArgumentException("Identifier must not be empty.");
        }

        String trimmed = raw.trim();
        if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            return new Parsed(Type.EMAIL, trimmed.toLowerCase(Locale.ROOT));
        }

        String phone = normalizePhone(trimmed);
        if (phone != null) {
            return new Parsed(Type.PHONE, phone);
        }

        throw new IllegalArgumentException("Enter a valid email address or mobile number.");
    }

    /**
     * Normalise a phone number by stripping non-digits and validating length.
     * Returns {@code null} when the value cannot be normalised.
     */
    public static String normalizePhone(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }

        StringBuilder digits = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            }
        }

        int length = digits.length();
        if (length < 10 || length > 15) {
            return null;
        }

        return digits.toString();
    }
}
