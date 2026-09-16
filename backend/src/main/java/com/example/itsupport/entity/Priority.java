package com.example.itsupport.entity;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Priority fromString(String text) {
        if (text == null) return MEDIUM;
        for (Priority p : Priority.values()) {
            if (p.name().equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return MEDIUM;
    }
}
