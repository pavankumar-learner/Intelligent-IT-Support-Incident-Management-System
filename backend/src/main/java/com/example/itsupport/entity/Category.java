package com.example.itsupport.entity;

public enum Category {
    Hardware,
    Software,
    Network,
    Email,
    Access_Login,
    Security,
    Database,
    Printer,
    Other;

    public static Category fromString(String text) {
        if (text == null) return Other;
        String clean = text.trim().replace("/", "_").replace(" ", "_");
        for (Category c : Category.values()) {
            if (c.name().equalsIgnoreCase(clean) || c.name().equalsIgnoreCase(text)) {
                return c;
            }
        }
        if (clean.equalsIgnoreCase("Access_Login") || clean.equalsIgnoreCase("Access")) return Access_Login;
        return Other;
    }

    public String toDisplayString() {
        if (this == Access_Login) return "Access/Login";
        return this.name();
    }
}
