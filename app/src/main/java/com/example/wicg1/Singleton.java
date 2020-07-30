package com.example.wicg1;

import android.provider.ContactsContract;
import android.webkit.WebMessagePort;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.HashMap;
import java.util.Iterator;

public class Singleton {

    enum AccTypes{admin,employee,patient};

    protected static ArrayList<Service> empAccServList = new ArrayList<Service>();
    protected static ArrayList<Shift> empAccShiftList = new ArrayList<Shift>();
    final static String SERVICELIST="service";
    final static String SHIFTLIST="shift";
    final static String PAYMENTLIST="payment";
    final static String INSURANCELIST="insurance";

    protected DatabaseReference ref;
    protected String accessPath;

    protected HashMap<String,Object>  employeeMap=new HashMap<>();

    protected static Singleton singleton;
    protected Account account;
    protected EmployeeAccount empAcc;
    protected AdminAccount adm;
    protected PatientAccount patAcc;

    protected Shift.Hour start,finish;
    protected Shift.Date date;

    private Singleton(){}

    public static Singleton getInstance(){
        if (singleton == null)
            singleton = new Singleton();
        return singleton;
    }

    public void setAccount(Object account,AccTypes type){
        if (type==AccTypes.admin){
            this.adm=(AdminAccount) account;
        }else if (type==AccTypes.employee){
            this.empAcc=(EmployeeAccount) account;
        }else if(type==AccTypes.patient){
            this.patAcc=(PatientAccount) account;
        }
    }

    public void setStart(Shift.Hour start){this.start=start;}
    public void setFinish(Shift.Hour finish){this.finish=finish;}

    public void purseHours(){start=finish=null;}

    public Shift createShift(){
        Shift shift=null;
        if(start!=null && finish!=null){
            shift=new Shift(date,start,finish);
        }
        purseHours();
        return shift;
    }

    public boolean hasShift(Shift shift){
        if (empAcc==null ||shift==null) return false;
        int index=0;
        while(index<empAcc.shifts.size()){
            if (empAcc.shifts.get(index).equals(shift))
                return true;
        }
        return false;
    }

    public void removeShift(Shift shift){
        if (empAcc==null ||shift==null) return ;
        int index=0;
        while(index<empAcc.shifts.size()){
            if (empAcc.shifts.get(index).equals(shift)){
                empAcc.shifts.remove(index);
                break;
            }
        }
    }

    public static boolean contains(Service service, List<Service> list){
        Iterator<Service> it=list.iterator();
        while(it.hasNext()){
            if (it.next().equals(service)){
                return true;
            }
        }
        return false;
    }


    public void updateEmpAcc(){
        if (empAcc!=null){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Accounts").child(empAcc.getAccountID());
            //ref.removeValue();
            for(int i=0;i<empAcc.shifts.size();i++){
                ref.child(i+"").setValue(empAcc.shifts.get(i));
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void setRefToEmp(){
        ref=FirebaseDatabase.getInstance().getReference("Accounts").child(empAcc.getAccountID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeeMap=dataSnapshot.getValue(HashMap.class);
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    String snapKey=getRealAccess(snap.getKey());
                    if(snapKey.equals(SERVICELIST)){
                        employeeMap.put(snap.getKey() ,((Service) snap.getValue()));
                    }else if (snapKey.equals(SHIFTLIST)){
                        employeeMap.put(snap.getKey() ,(Shift) snap.getValue());
                    }else if (snapKey.equals(PAYMENTLIST)){
                        employeeMap.put(snap.getKey() ,(String) snap.getValue());
                    }else if (snapKey.equals(INSURANCELIST)){
                        employeeMap.put(snap.getKey() ,(String) snap.getValue());
                    }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateMapForEmp(){
        ref.updateChildren(employeeMap);
    }

    public void resetUpEmployeeMap(){
        employeeMap.clear();
        employeeMap.put("userName",empAcc.userName);
        employeeMap.put("firstName",empAcc.firstName);
        employeeMap.put("lastName",empAcc.lastName);
        employeeMap.put("dateOfBirth",empAcc.dateOfBirth);
        employeeMap.put("phoneNumber",empAcc.phoneNumber);
        employeeMap.put("email",empAcc.email);
        employeeMap.put("country",empAcc.country);
        employeeMap.put("province",empAcc.province);
        employeeMap.put("city",empAcc.city);
        employeeMap.put("street",empAcc.street);
        employeeMap.put("password",empAcc.password);
        employeeMap.put("postalCode",empAcc.postalCode);
        employeeMap.put("accountType",empAcc.accountType);
        employeeMap.put("accountID",empAcc.accountID);
        int index;
        //set services
        index=0;
        Iterator<Service> its=empAcc.services.iterator();
        while (its.hasNext()){
            employeeMap.put(SERVICELIST+index,its.next());
        }
        ;
        //set shifts
        index=0;
        Iterator<Shift> ith=empAcc.shifts.iterator();
        while (ith.hasNext()){
            employeeMap.put(SHIFTLIST+index,ith.next());
        }

        //set payments
        index=0;
        Iterator<String> itt=empAcc.paymentMethods.iterator();
        while (itt.hasNext()){
            employeeMap.put(PAYMENTLIST+index,itt.next());
        }
        //set insurance
        index=0;
        itt=empAcc.insuranceTypes.iterator();
        while (itt.hasNext()){
            employeeMap.put(INSURANCELIST+index,itt.next());
        }
    }

    private static String getRealAccess(String s){
        String result=""; int index=0;
        while ( index<s.length() ) {
            if (Character.isDigit(s.charAt(index))){
                break;
            }else{
                result+=s.charAt(index);
            }
        }
        return result;
    }

    public AdminAccount getAdmAccount(){
        return adm;
    }

    public EmployeeAccount getEmployeeAccount(){
        return empAcc;
    }



    //save arrayList to db

    public static void updateAccountServicesInDB(List<Service> list, String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accounts").child(id);
        ref.child("Services").removeValue();
        for(Service s:list){
            ref.child("Services").child(s.getName()).setValue(s);
        }
    }

    public static ArrayList<Service> getAccountServicesFromDB(String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accounts").child(id).child("Services");

        try {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    empAccServList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Service service = ds.getValue(Service.class);
                        empAccServList.add(service);
                    }

                    // Toast.makeText(getApplicationContext(), "List updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(EmployeeActivity.this, "Failed to update list from db",
                    //Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        } catch (Exception e) {
            //Toast.makeText(EmployeeActivity.this, "Something went wrong",
            //Toast.LENGTH_SHORT).show();
        }

        return  empAccServList;
    }


    public static void updateAccountShiftsInDB(List<Shift> list, String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accounts").child(id);
        ref.child("Shifts").removeValue();
        for(Shift s:list){
            ref.child("Shifts").child(s.toString()).setValue(s);
        }
    }

    public static ArrayList<Shift> getAccountShiftsFromDB(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accounts").child(id).child("Shifts");

        try {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    empAccShiftList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Shift shift = ds.getValue(Shift.class);
                        empAccShiftList.add(shift);
                    }

                    // Toast.makeText(getApplicationContext(), "List updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(EmployeeActivity.this, "Failed to update list from db",
                    //Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        } catch (Exception e) {
            //Toast.makeText(EmployeeActivity.this, "Something went wrong",
            //Toast.LENGTH_SHORT).show();
        }

        return empAccShiftList;
    }
    public void addRating(Rating rating,String clinicID){
        //to implement add rating to the employee who has the clinic name in database
    }

}
