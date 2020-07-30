package com.example.wicg1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    DatabaseReference serviceRef,accountRef;
    AdminAccount admin;

    Switch serviceaccountswitch;

    LinearLayout serviceLayout,accountLayout;
    EditText newservicetext;
    RadioButton doctorbutton, nursebutton, staffbutton;
    Button addbutton, removebutton, editbutton, syncbutton,deleteAccountButton;
    ListView listView,accountListView;

    ArrayAdapter servoceadapter;
    ArrayAdapter accountadapter;

    List<Service> servicesList = new ArrayList<Service>();
    List<Account> accountList = new ArrayList<Account>();

    Service savedservice;
    Account savedaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        serviceRef= FirebaseDatabase.getInstance().getReference().child("Services");
        accountRef=FirebaseDatabase.getInstance().getReference().child("Accounts");

        admin= new AdminAccount(); // we don't need the data so leave it like this

        OnClickSyncServices(); //prepare the list

        serviceLayout=findViewById(R.id.serviceLayout);
        accountLayout=findViewById(R.id.accountLayout);
        accountLayout.setVisibility(View.GONE);

        deleteAccountButton=findViewById(R.id.deleteAccountButton);
        serviceaccountswitch=findViewById(R.id.serviceAccountSwitch);
        newservicetext = findViewById(R.id.newServiceText);
        doctorbutton = findViewById(R.id.doctorButton);
        nursebutton = findViewById(R.id.nurseButton);
        staffbutton = findViewById(R.id.staffButton);
        addbutton = findViewById(R.id.addButton);
        removebutton = findViewById(R.id.removeButton);
        editbutton = findViewById(R.id.editButton);
        listView = findViewById(R.id.ServiceList);
        syncbutton= findViewById(R.id.syncbutton);
        accountListView=findViewById(R.id.accountList);

        servoceadapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,servicesList );
        listView.setAdapter((servoceadapter));

        accountadapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,accountList );
        accountListView.setAdapter((accountadapter));

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeleteAccount();
                syncbutton.performClick();
            }
        });

        serviceaccountswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceaccountswitch.isChecked()){
                    accountLayout.setVisibility(View.VISIBLE);
                    serviceLayout.setVisibility(View.GONE);
                    OnClickSyncAccount();
                } else{
                    accountLayout.setVisibility(View.GONE);
                    serviceLayout.setVisibility(View.VISIBLE);
                    OnClickSyncServices();
                }
            }
        });
        syncbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceaccountswitch.isChecked()){
                    OnClickSyncAccount();
                }else {
                    OnClickSyncServices();
                }
            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickCreateService();
                syncbutton.performClick();
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickEditService();
                syncbutton.performClick();
            }
        });

        removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickDeleteService();
                syncbutton.performClick();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                savedservice= (Service) adapter.getItemAtPosition(position);
            }
        });

        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                savedaccount = (Account) adapter.getItemAtPosition(position);
            }
        });

    }
    protected void OnClickCreateService(){
        if ((doctorbutton.isChecked()||nursebutton.isChecked()||staffbutton.isChecked())&&
                !newservicetext.getText().toString().equals("")){
            String name=newservicetext.getText().toString();
            String provider=
                    (doctorbutton.isChecked()?Service.DOCTOR:
                            ((nursebutton.isChecked())?Service.NURSE:Service.STAFF));
            //add service to the database
            Service tocreate=new Service(name,provider);
            if (!admin.exists(tocreate)){
                createServiceInDataBase(name,provider);
                admin.createService(tocreate);
                Toast.makeText(getApplicationContext(),"service added",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(),"service exists",Toast.LENGTH_LONG).show();
            }
        }else if (newservicetext.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"name must not be empty",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"select a provider please",Toast.LENGTH_LONG).show();
        }
    }

    protected void createServiceInDataBase(String name, String provider){
        Service newService = new Service(name,provider);
        serviceRef.child(name).setValue(newService);
    }

    protected void OnClickDeleteService(){
        if (savedservice != null){
            //delete service from the database
            String id = savedservice.getName();//TODO we just need id for this
            deleteServiceInDataBase(id);
            admin.deleteService(savedservice);
            savedservice=null;
            Toast.makeText(getApplicationContext(),"service deleted",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"select a service",Toast.LENGTH_LONG).show();
        }
    }

    protected void deleteServiceInDataBase(String id){
        try {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Services").child(id);
            dr.removeValue();
            Toast.makeText(getApplicationContext(), "Service removed",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

    }

    protected void OnClickEditService(){
        if (newservicetext.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"name must not be empty",Toast.LENGTH_SHORT).show();
        } else if (savedservice==null){
            Toast.makeText(getApplicationContext(),"select service please "
                    ,Toast.LENGTH_SHORT).show();
        }else if ((doctorbutton.isChecked()||nursebutton.isChecked()||staffbutton.isChecked())
                && !newservicetext.getText().toString().equals("") && savedservice != null){
            //getting the data
            String name=newservicetext.getText().toString();
            String provider=
                    (doctorbutton.isChecked()?Service.DOCTOR:
                            ((nursebutton.isChecked())?Service.NURSE:Service.STAFF));
            //edit service in the database;
            String id = savedservice.getName();//TODO
            editServiceInDataBase(id, new Service(name,provider));
            //delete service from the list in admin
            //use the method deleteServices(name,provider)
            admin.editService(savedservice,new Service(name,provider)); //to change
            savedservice=null;
            OnClickSyncServices();
            Toast.makeText(getApplicationContext(),"service deleted",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"select a provider please",Toast.LENGTH_SHORT).show();
        }
    }

    protected void editServiceInDataBase(String id, Service service){
        try {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Services").child(id);
            dr.removeValue();
            serviceRef.child(service.getName()).setValue(service);
            Toast.makeText(getApplicationContext(), "Service edited",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this method fetches all services from database and show them
     */
    protected void prepareServices(){
        //fetch services from
        // Read from the database
        try {
            serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    servicesList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Service service = ds.getValue(Service.class);
                        servicesList.add(service);
                    }

                   // Toast.makeText(getApplicationContext(), "List updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(), "Failed to update list",
                            Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            servoceadapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

        //add them to the list in admin
        //use the method setServices(List<Service>)

    }

    /**
     *get account from database
     * forget about this for now
     */
    protected AdminAccount getAccount(){
        return null;
    }

    /**
     * update the services list, basically recreating the services list
     */
    protected void OnClickSyncServices(){
        prepareServices();
        admin.setServices(servicesList);
        //later we add the accounts
    }

    protected void OnClickSyncAccount(){
        prepareAccounts();
    }

    protected void onClickDeleteAccount(){
        if (savedaccount != null){
            //delete service from the database
            String id = savedaccount.getAccountID();//TODO we just need id for this
            deleteAccountInDataBase(id);
            //delete service from the list in admin
            //use the method deleteServices(name,provider)
            savedaccount=null;
            Toast.makeText(getApplicationContext(),"account deleted",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"select an account",Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAccountInDataBase(String id) {
        try {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Accounts").child(id);
            dr.removeValue();
            Toast.makeText(getApplicationContext(), "account removed",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void prepareAccounts(){
        try {
            accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    accountList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Account account = ds.getValue(Account.class);
                        accountList.add(account);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(), "Failed to update list",
                            Toast.LENGTH_SHORT).show();
                }
            });

            accountadapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
