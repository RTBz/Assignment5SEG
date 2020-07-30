package com.example.wicg1;

class Service {

    static final String DOCTOR = "doctor";
    static final String NURSE = "nurse";
    static final String STAFF = "staff";

    protected String name;
    protected String provider;
    protected double price;

    public Service(){
        //this is to solve a db exception 'does not define a no-argument constructor'
    }

    public Service(String name, String provider){
        this.name=name;
        this.provider=provider;
    }

    public boolean equals(Service other){
        return this.name.equals(other.name) && this.provider.equals(other.provider);
    }

    //setters
    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }
    //getters
    public void setName(String name) {
        this.name = name;
    }

    public void setProvider(String provider) {
        if (provider.equals(DOCTOR) || provider.equals(STAFF) || provider.equals(NURSE))
            this.provider = provider;
    }

    public double getPrice(){return price;}

    public void setPrice(double price){if (price>=0) this.price=price;}

    public String toString(){
        return "Service name: "+getName()+" Provider: "+getProvider();
    }
}
