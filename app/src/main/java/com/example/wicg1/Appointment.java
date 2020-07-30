package com.example.wicg1;

public class Appointment {

    enum AppointmentStatus{checkedin,checkedout,waiting};

    protected Shift shift;

    protected String clinicID;

    protected AppointmentStatus status;

    public String toString(){
        return "on the "+shift.toString()+" with "+ clinicID+" is "+status;
    }
}
