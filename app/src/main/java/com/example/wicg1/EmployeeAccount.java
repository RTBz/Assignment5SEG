package com.example.wicg1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeAccount extends com.example.wicg1.Account{

    ArrayList<String> insuranceTypes=new ArrayList<>();
    ArrayList<String> paymentMethods=new ArrayList<>();
    ArrayList<Rating> ratings=new ArrayList<>();

    protected String clinic;

    protected ArrayList<Service> services=new ArrayList<Service>();
    protected ArrayList<Shift> shifts=new ArrayList<Shift>();
    protected Map<Service,Waiting> waitingList;

    public EmployeeAccount(){}

    public EmployeeAccount(Account account){
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
    public EmployeeAccount(String clinic){
        this.clinic=clinic;
    }

    public EmployeeAccount(Account account,String clinic){
        userName=account.userName;
        firstName=account.firstName;
        lastName=account.lastName;
        dateOfBirth=account.dateOfBirth;
        phoneNumber=account.phoneNumber;
        email=account.email;
        country=account.country;
        province=account.province;
        city=account.city;
        street=account.street;
        postalCode=account.postalCode;
        password=account.password;
        accountType=account.accountType;
        accountID=account.accountID;
        this.clinic=clinic;
    }

    public String getClinic(){return clinic;}

    public void setWaitingList(Map<Service,Waiting> map) {
        this.waitingList = map;
    }
}
