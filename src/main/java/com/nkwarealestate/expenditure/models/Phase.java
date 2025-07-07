package com.nkwarealestate.expenditure.models;

/**
 * Enumeration representing the different phases of expenditure
 */
public enum Phase {
    CONSTRUCTION("Construction"),
    MARKETING("Marketing"),
    SALES("Sales"),
    ADMINISTRATION("Administration"),
    MAINTENANCE("Maintenance"),
    OTHER("Other");
    
    private final String displayName;
    
    Phase(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Get Phase enum from string value
     */
    public static Phase fromString(String value) {
        for (Phase phase : Phase.values()) {
            if (phase.displayName.equalsIgnoreCase(value) || 
                phase.name().equalsIgnoreCase(value)) {
                return phase;
            }
        }
        return OTHER; // Default fallback
    }
}
