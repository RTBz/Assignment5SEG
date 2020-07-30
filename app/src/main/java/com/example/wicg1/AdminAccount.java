package com.example.wicg1;

import com.example.wicg1.Account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdminAccount extends Account {

    protected void createService(Service service){
        int index = getIndex(service);
        if (index>=services.size()){
            services.add(service);
        }
    }

    protected void deleteService(Service service){
        int index = getIndex(service);
        if (index<services.size()){
            services.remove(index);
        }
    }

    protected void editService(Service service, Service otherservice){
        int index=getIndex(service);
        if (index<services.size()) {
            services.set(index, otherservice);
        }
    }

    protected void setServices(List<Service> list){
        services=list;
    }

    protected List<Service> getServices(){
        return services;
    }

    private int getIndex(Service service){
        Iterator<Service> iterator=services.iterator();
        int index=0;
        while(iterator.hasNext() ){
            Service otherservice=iterator.next();
            if (service.equals(otherservice)){
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * this method deletes the account with the username provided
     * @param username
     */
    protected void deleteAccount(String username){

    }

    protected boolean exists(Service service){
        return getIndex(service)<services.size();
    }
}
