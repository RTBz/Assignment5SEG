package com.example.wicg1;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PatientAccount extends Account {

    protected ArrayList<Appointment> appointments;

    protected String insurance;
    protected String paymentMethod;

    PatientAccount(){}

    public PatientAccount(Account account){
        this.userName=account.userName;
        this.accountID=account.accountID;
        this.accountType=account.accountType;
        this.address=account.address;
        this.city=account.city;
        this.country=account.country;
        this.dateOfBirth=account.dateOfBirth;
        this.email=account.email;
        this.firstName=account.firstName;
        this.lastName=account.lastName;
        this.password=account.password;
        this.phoneNumber=account.phoneNumber;
        this.postalCode=account.postalCode;
        this.province=account.province;
        this.street=account.street;
    }

    PatientAccount(String isurance,String paymentMethod){
        this.insurance=insurance;
        this.paymentMethod=paymentMethod;
    }

    void setIsurance(String in){insurance=in;}
    String getIsurance(){return insurance;}

    void setPaymentMethod(String in){paymentMethod=in;}
    String getPaymentMethod(){return paymentMethod;}
}
