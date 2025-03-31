package com.example.demo;

public class HotelAssignmentChangeResponse {
    private boolean requiresHotelUpdate;

    public HotelAssignmentChangeResponse(boolean requiresHotelUpdate) {
        this.requiresHotelUpdate = requiresHotelUpdate;
    }

    public boolean isRequiresHotelUpdate() {
        return requiresHotelUpdate;
    }

    public void setRequiresHotelUpdate(boolean requiresHotelUpdate) {
        this.requiresHotelUpdate = requiresHotelUpdate;
    }
}
