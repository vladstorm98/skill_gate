package com.skill_gate.util;

import com.skill_gate.exception.BusinessException;

/**
 * Safe parsing of Telegram callback_data payloads like {@code prefix:id} or {@code prefix_a_b}.
 */
public final class CallbackIds {

    private CallbackIds() {}

    /**
     * Split by {@code :} and parse the segment at {@code index} as long.
     * Example: {@code view_lesson:42} → colonId(data, 1) == 42
     */
    public static long colonId(String data, int index) {
        return parse(data, ":", index);
    }

    /**
     * Split by {@code _} and parse the segment at {@code index} as long.
     * Example: {@code lessons_page_5_1} → underscoreId(data, 2) == 5
     */
    public static long underscoreId(String data, int index) {
        return parse(data, "_", index);
    }

    /**
     * Split by {@code _} and parse the segment at {@code index} as int.
     */
    public static int underscoreInt(String data, int index) {
        long value = parse(data, "_", index);
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new BusinessException("Invalid int in callback data: " + data);
        }
        return (int) value;
    }

    /**
     * Parse trailing number after a fixed prefix (e.g. {@code positions_page_} + {@code 2}).
     */
    public static int pageAfterPrefix(String data, String prefix) {
        if (data == null || prefix == null || !data.startsWith(prefix)) {
            throw new BusinessException("Invalid callback data: " + data);
        }
        var tail = data.substring(prefix.length());
        try {
            return Integer.parseInt(tail);
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid page in callback data: " + data);
        }
    }

    /**
     * Split by {@code :} and return the segment at {@code index} as String.
     * Example: {@code set_employee_role:12:ADMIN} → colonSegment(data, 2) == "ADMIN"
     */
    public static String colonSegment(String data, int index) {
        if (data == null || data.isBlank()) {
            throw new BusinessException("Empty callback data");
        }
        var parts = data.split(":");
        if (parts.length <= index || parts[index].isBlank()) {
            throw new BusinessException("Invalid callback data: " + data);
        }
        return parts[index];
    }

    private static long parse(String data, String delimiter, int index) {
        if (data == null || data.isBlank()) {
            throw new BusinessException("Empty callback data");
        }
        var parts = data.split(delimiter);
        if (parts.length <= index) {
            throw new BusinessException("Invalid callback data: " + data);
        }
        try {
            return Long.parseLong(parts[index]);
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid id in callback data: " + data);
        }
    }
}
