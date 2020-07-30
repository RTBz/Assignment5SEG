package com.example.wicg1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class EmployeeActivity extends AppCompatActivity {

    DatabaseReference ref,shiftRef;


    protected Switch employeeInfoChip,AffiliatedClinicChip,calendarSwitch;
    protected Button employeeSaveButton,Sync;
    protected EditText username,email,phoneNumber,ClinicName,newpayment,newinsurance;
    protected LinearLayout employeeInfoLayout,AffiliatedClinicLayout;
    protected CalendarView calendar;


    ListView shiftListView,paymentListView,insuranceListView;

    protected ArrayList<Shift> shiftstoshow=new ArrayList<Shift>();
    List<Shift> shiftsFromDB = Singleton.getAccountShiftsFromDB(Singleton.getInstance().empAcc.getAccountID());

    ArrayAdapter adapter,paymentadapter,insuranceadapter;

    Shift savedShift;

    Service savedservice;
    List<Service> servicesList = new ArrayList<Service>();
    List<Service> clinicServicesList = Singleton.getAccountServicesFromDB(Singleton.getInstance().empAcc.getAccountID());
    DatabaseReference serviceRef= FirebaseDatabase.getInstance().getReference().child("Services");


    ArrayAdapter serviceAdapter;
    ListView serviceListView;

    ArrayAdapter clinicServiceAdapter;
    ListView clinicServiceListView;

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

            serviceAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

        //add them to the list in admin
        //use the method setServices(List<Service>)

    }

    protected void prepareShifts(){
        //fetch services from
        // Read from the database
        try {
            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    shiftsFromDB.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Shift shift = ds.getValue(Shift.class);
                        shiftsFromDB.add(shift);
                    }
                    if (adapter!=null){
                        adapter.notifyDataSetChanged();

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


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

        //add them to the list in admin
        //use the method setServices(List<Service>)

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        ref=FirebaseDatabase.getInstance().getReference().child("Accounts");
        shiftRef=FirebaseDatabase.getInstance().getReference().child("Accounts").child(Singleton.getInstance().empAcc.accountID).child("Shifts");

        try {
            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    shiftsFromDB.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Shift shift = ds.getValue(Shift.class);
                        shiftsFromDB.add(shift);
                    }
                    if(adapter!=null)
                         adapter.notifyDataSetChanged();

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


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

        ref.child(Singleton.getInstance().empAcc.getAccountID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Singleton.getInstance().getEmployeeAccount()!=null){
                    Singleton.getInstance().getEmployeeAccount().shifts.clear();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Accounts").child(Singleton.getInstance().getEmployeeAccount().getAccountID());
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        if (Singleton.isNumeric(snapshot.getKey())) {
                            Singleton.getInstance().getEmployeeAccount().shifts.add((Shift) snapshot.getValue(Shift.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Singleton.getInstance();

        Sync=findViewById(R.id.Sync);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        ClinicName = findViewById(R.id.ClinicName);
        employeeInfoLayout = findViewById(R.id.emplyeeInfolayout);
        AffiliatedClinicLayout = findViewById(R.id.AffiliatedClinicLayout);
        employeeInfoChip = findViewById(R.id.employeeInfoChip);
        AffiliatedClinicChip = findViewById(R.id.AffiliatedClinicChip);
        employeeSaveButton = findViewById(R.id.emplyeeInfoSaveButton);
        calendar = findViewById(R.id.calendarView);
        calendarSwitch = findViewById(R.id.calendarSwitch);
        paymentListView=findViewById(R.id.payment);
        insuranceListView=findViewById(R.id.insurance);
        newinsurance=findViewById(R.id.insuranceToAdd);
        newpayment=findViewById(R.id.paymentToAdd);

        insuranceadapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,Singleton.getInstance().empAcc.insuranceTypes );
        insuranceListView.setAdapter((insuranceadapter));

        paymentadapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,Singleton.getInstance().empAcc.paymentMethods );
        paymentListView.setAdapter((paymentadapter));

        employeeInfoLayout.setVisibility(View.GONE);
        AffiliatedClinicLayout.setVisibility(View.GONE);
        calendar.setVisibility(View.GONE);

        Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        calendarSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarSwitch.isChecked()) {
                    calendar.setVisibility(View.VISIBLE);
                } else {
                    calendar.setVisibility(View.GONE);
                }
            }
        });

        employeeInfoChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (employeeInfoChip.isChecked()) {
                    employeeInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    employeeInfoLayout.setVisibility(View.GONE);
                }
            }
        });

        AffiliatedClinicChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AffiliatedClinicChip.isChecked()) {
                    AffiliatedClinicLayout.setVisibility(View.VISIBLE);
                } else {
                    AffiliatedClinicLayout.setVisibility(View.GONE);
                }
            }
        });

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //to implement
                 showShiftLayout(view,dayOfMonth,month,year);

            }
        });


        employeeSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verify(username.getText().toString()) && verify(email.getText().toString()) &&
                        verify(phoneNumber.getText().toString()) && verify(ClinicName.getText().toString())
                && verify(ClinicName.getText().toString()) && (verify(newpayment.getText().toString()) ||
                        !Singleton.getInstance().getEmployeeAccount().paymentMethods.isEmpty()) &&
                        (verify(newinsurance.getText().toString())) || !Singleton.getInstance().getEmployeeAccount().insuranceTypes.isEmpty()){
                    Singleton.getInstance().getEmployeeAccount().setUserName(username.getText().toString());
                    Singleton.getInstance().getEmployeeAccount().setEmail(email.getText().toString());
                    Singleton.getInstance().getEmployeeAccount().setPhoneNumber(phoneNumber.getText().toString());
                    Singleton.getInstance().getEmployeeAccount().clinic=ClinicName.getText().toString();
                    Singleton.getInstance().getEmployeeAccount().insuranceTypes.add(newinsurance.getText().toString());
                    Singleton.getInstance().getEmployeeAccount().paymentMethods.add(newpayment.getText().toString());
                    insuranceadapter.notifyDataSetChanged();
                    paymentadapter.notifyDataSetChanged();
                    ref.child(Singleton.getInstance().getEmployeeAccount().getAccountID()).setValue(Singleton.getInstance().getEmployeeAccount());
                    update();
                }else{
                    Toast.makeText(getApplicationContext(),"please validate all fields",Toast.LENGTH_SHORT).show();
                }

            }});

            //fj
        serviceListView = findViewById(R.id.ServiceList);
        serviceAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,servicesList );
        serviceListView.setAdapter(serviceAdapter);

        clinicServiceListView = findViewById(R.id.ClinicServiceList);
        clinicServiceAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,clinicServicesList );
        clinicServiceListView.setAdapter(clinicServiceAdapter);

        prepareServices();
        prepareShifts();

        //clinicServicesList = Singleton.getAccountServicesFromDB(Singleton.getInstance().empAcc.getAccountID());
        //clinicServiceAdapter.notifyDataSetChanged();


        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                savedservice= (Service) adapter.getItemAtPosition(position);
                if (!Singleton.contains(savedservice,clinicServicesList)){
                    final Dialog dialog=new Dialog(EmployeeActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
                    dialog.setContentView(R.layout.addservicelayout);

                    TextView priceText=dialog.findViewById(R.id.serviceDetailText);
                    final EditText price=dialog.findViewById(R.id.servicePriceText);
                    Button priceButton=dialog.findViewById(R.id.setServicePriceButton);

                    priceText.setText(savedservice.toString());
                    priceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (validPrice(price.getText().toString())){
                                savedservice.price=Double.parseDouble(price.getText().toString());
                                Singleton.getInstance().empAcc.services.add(savedservice);
                                clinicServicesList.add(savedservice);
                                clinicServiceAdapter.notifyDataSetChanged();
                                Singleton.updateAccountServicesInDB(clinicServicesList,Singleton.getInstance().empAcc.getAccountID());
                                dialog.dismiss();
                            }else{
                                Toast.makeText(EmployeeActivity.this,"price not valid",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }else{
                    Toast.makeText(null,"service alreadt offered by clinic",Toast.LENGTH_SHORT);
                }
            }
        });

        clinicServiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                savedservice= (Service) adapter.getItemAtPosition(position);
                clinicServicesList.remove(savedservice);
                clinicServiceAdapter.notifyDataSetChanged();
                Singleton.updateAccountServicesInDB(clinicServicesList,Singleton.getInstance().empAcc.getAccountID());
            }
        });

        update();
    }
    //to improve
    public boolean verify(String str){
        if (str==null) return false;
        return !str.isEmpty();
    }

    public boolean validPrice(String str){
        try{
            Double.parseDouble(str);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public void update(){

        username.setText(Singleton.getInstance().getEmployeeAccount().getUserName());
        email.setText(Singleton.getInstance().getEmployeeAccount().getEmail());
        phoneNumber.setText(Singleton.getInstance().getEmployeeAccount().getPhoneNumber());
        ClinicName.setText(Singleton.getInstance().getEmployeeAccount().getClinic());

    }

    protected void showShiftLayout(View v,int day, int month, int year){
        //try{

            Singleton.getInstance().date=new Shift.Date(day,month,year);
            prepareShifts();
            shiftstoshow.clear();
            for(int i=0;i<Singleton.getInstance().empAcc.shifts.size();i++){
                Shift.Date date=Singleton.getInstance().empAcc.shifts.get(i).date;
                if(date.month==month && date.day==day && date.year==year){
                    shiftstoshow.add(Singleton.getInstance().empAcc.shifts.get(i));
                }
            }

             final Dialog dialog=new Dialog(EmployeeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);

            dialog.setContentView(R.layout.shift_layout);

            shiftListView=dialog.findViewById(R.id.shiftListView);
            TextView dateText=dialog.findViewById(R.id.datetext);
            dateText.setText("Date "+day+"/"+month+"/"+year);

            Button createShift=dialog.findViewById(R.id.createShiftButton);
            final Button removeShift=dialog.findViewById(R.id.removeShiftButton);
            removeShift.setVisibility(View.GONE);

            adapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,shiftstoshow );
            shiftListView.setAdapter((adapter));
            shiftListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    savedShift=shiftstoshow.get(position);
                    Toast.makeText(getApplicationContext(),"shift "+savedShift+" selected",Toast.LENGTH_SHORT).show();
                    removeShift.setVisibility(View.VISIBLE);
                    return false;
                }
            });

            final LinearLayout createShiftLayout=dialog.findViewById(R.id.createShiftLayout);
            createShiftLayout.setVisibility(View.GONE);
            Button shiftStart=dialog.findViewById(R.id.shiftstart);
            Button shiftFinish=dialog.findViewById(R.id.shiftfinish);

            createShift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (createShiftLayout.getVisibility()==View.VISIBLE){
                        Shift shift=Singleton.getInstance().createShift();
                        boolean allgood=false;
                        if (shift!=null){
                            boolean hasshift=Singleton.getInstance().hasShift(shift);
                            if (!hasshift){
                                adapter.notifyDataSetChanged();
                                Singleton.getInstance().empAcc.shifts.add(shift);
                                shiftRef.child(shift.date.toString()).setValue(shift);
                                prepareShifts();
                            }

                        }

                        createShiftLayout.setVisibility(View.GONE);
                        if(allgood)
                            Toast.makeText(getApplicationContext(),""+shift.toString(),Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"please select start and finishing hour",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        createShiftLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            shiftStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(EmployeeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Singleton.getInstance().setStart(new Shift.Hour( selectedHour , selectedMinute));
                        }
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);//Yes 24 hour time
                    mTimePicker.setTitle("Select start Time");
                    mTimePicker.show();
                }
            });

            shiftFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(EmployeeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Singleton.getInstance().setFinish(new Shift.Hour( selectedHour , selectedMinute));
                        }
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);//Yes 24 hour time
                    mTimePicker.setTitle("Select finish Time");
                    mTimePicker.show();
                }
            });

            removeShift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    shiftstoshow.remove(savedShift);
                    Singleton.getInstance().removeShift(savedShift);
                    prepareShifts();
                      Singleton.getInstance().updateEmpAcc();
                    removeShift.setVisibility(View.GONE);
                }
            });

            adapter.notifyDataSetChanged();
            dialog.show();
        /*} catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "we are experiencing technical difficulties", Toast.LENGTH_SHORT).show();

        }*/

    }
}
