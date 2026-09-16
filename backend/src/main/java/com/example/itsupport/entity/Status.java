package com.example.itsupport.entity;

public enum Status {
    NEW,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    REOPENED;

    public static Status fromString(String text) {
        if (text == null) return NEW;
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return NEW;
    }
}
