package com.example.prm392_final_prj.utils;

public class BookingStatus {
    public static String getStatusString(int status){
        switch (status) {
            case 1:
                return "Pending";
            case 2:
                return "Confirmed";
            case 3:
                return "Cancelled";
            case 4:
                return "Completed";
            default:
                return "Unknown";
        }
    }
}
