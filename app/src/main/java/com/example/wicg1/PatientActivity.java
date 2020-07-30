package com.example.wicg1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class PatientActivity extends AppCompatActivity {

    protected Button searchButton,checkinButton,openSearchButton;
    protected RadioGroup searchGroup;
    protected RadioButton addressRadioButton,workingHoursRadioButton,serviceProviderRadioButton;
    protected RadioButton doctorRadioButton,nurseRadioButton,staffRadioButton;
    protected TextView checkinAppointment;
    protected EditText addressSearchBar;

    protected CalendarView calendarView;

    protected ArrayList<Object> searchList=new ArrayList<>();
    protected ArrayList<Appointment> appointmentList=new ArrayList<>();
    protected ArrayList<Service> servicesList=new ArrayList<>();
    protected ArrayList<String> clinicsList=new ArrayList<>();

    protected ListView searchListView,appointmentListView,allServicesListView,clinicsProvidingListView;
    protected ArrayAdapter adapter,appAdapter,servicesAdapter,clinicAdapter;

    protected LinearLayout searchLayout,addressSearchLayout,WHSearchLayout,serviceSearchLayout,AppointmentLayout;

    protected Appointment savedAppointment;
    protected Shift.Date savedDate;
    protected Shift.Hour savedHour;

    DatabaseReference serviceRef= FirebaseDatabase.getInstance().getReference().child("Services");
    Service savedservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        searchButton=findViewById(R.id.searchButton);
        checkinButton=findViewById(R.id.checkinButton);
        openSearchButton=findViewById(R.id.openSearchButton);
        calendarView=findViewById(R.id.calendarView);

        searchGroup=findViewById(R.id.searchGroup);
        addressRadioButton=findViewById(R.id.addressRadioButton);
        workingHoursRadioButton=findViewById(R.id.workingHoursRadioButton);
        serviceProviderRadioButton=findViewById(R.id.serviceProviderRadioButton);

        allServicesListView=findViewById(R.id.allServicesList);
        servicesAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,servicesList );
        allServicesListView.setAdapter(servicesAdapter);

        clinicsProvidingListView=findViewById(R.id.clinicsProviding);
        clinicAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,clinicsList );
        clinicsProvidingListView.setAdapter(clinicAdapter);

        prepareServices();

        allServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                savedservice= (Service) adapter.getItemAtPosition(position);

                //clinicAdapter.notifyDataSetChanged();
            }
        });



        checkinAppointment=findViewById(R.id.checkinAppointment);
        addressSearchBar=findViewById(R.id.addressSearchBar);

        searchListView=findViewById(R.id.searchListView);
        appointmentListView=findViewById(R.id.appointmentListView);

        searchLayout=findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.GONE);
        addressSearchLayout=findViewById(R.id.addressSearchLayout);
        addressSearchLayout.setVisibility(View.GONE);
        WHSearchLayout=findViewById(R.id.WHSearchLayout);
        WHSearchLayout.setVisibility(View.GONE);
        serviceSearchLayout=findViewById(R.id.ServiceSearchLayout);
        serviceSearchLayout.setVisibility(View.GONE);
        AppointmentLayout=findViewById(R.id.AppointmentLayout);

        final Appointment appointment=new Appointment();
        appointment.status= Appointment.AppointmentStatus.waiting;
        appointment.clinicID="clinic 1";
        appointment.shift=new Shift(new Shift.Date(1,1,2019),new Shift.Hour(12,03),new Shift.Hour(12,50) );
        appointmentList.add(appointment);

        appAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,appointmentList);
        appointmentListView.setAdapter((appAdapter));

        checkinButton.setVisibility(View.GONE);

        openSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchLayout.getVisibility()==View.GONE){
                    searchLayout.setVisibility(View.VISIBLE);
                }else {
                    searchLayout.setVisibility(View.GONE);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressSearchLayout.getVisibility()==View.VISIBLE){
                    searchByAddress();
                }else if (serviceSearchLayout.getVisibility()==View.VISIBLE){
                    searchByService();
                }else if (WHSearchLayout.getVisibility()==View.VISIBLE){
                    searchByWH();
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                savedDate=new Shift.Date(dayOfMonth,month,year);
                Toast.makeText(getApplicationContext(), savedDate.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        searchGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (addressRadioButton.isChecked()){
                    serviceSearchLayout.setVisibility(View.GONE);
                    addressSearchLayout.setVisibility(View.VISIBLE);
                    WHSearchLayout.setVisibility(View.GONE);
                }else if (workingHoursRadioButton.isChecked()){
                    serviceSearchLayout.setVisibility(View.GONE);
                    addressSearchLayout.setVisibility(View.GONE);
                    WHSearchLayout.setVisibility(View.VISIBLE);
                }else if (serviceProviderRadioButton.isChecked()){
                    serviceSearchLayout.setVisibility(View.VISIBLE);
                    addressSearchLayout.setVisibility(View.GONE);
                    WHSearchLayout.setVisibility(View.GONE);
                }
            }
        });

        appointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkinButton.setVisibility(View.VISIBLE);
                savedAppointment=appointmentList.get(position);
                //make a dialog to change status and rate
                final Dialog dialog=new Dialog(PatientActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);

                dialog.setContentView(R.layout.appointment_layout);

                TextView dateView=dialog.findViewById(R.id.dateView);
                dateView.setText("Date : "+savedAppointment.shift.date.toString());
                TextView addressView=dialog.findViewById(R.id.addressView);
                addressView.setText("Date : "+getAddressFromClinicID(savedAppointment.clinicID));
                TextView statusView=dialog.findViewById(R.id.statusView);
                statusView.setText("Date : "+savedAppointment.status);

                final RatingBar bar=dialog.findViewById(R.id.ratingBar);
                Button submitComment=dialog.findViewById(R.id.submitCommentButton);
                final EditText comment=dialog.findViewById(R.id.comentText);

                submitComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (comment.getText().toString()==null && comment.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "please input a comment",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Rating rating=new Rating();
                            rating.rate=bar.getRating();
                            rating.comment=comment.getText().toString();

                            Singleton.getInstance().addRating(rating,savedAppointment.clinicID);
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedAppointment.status== Appointment.AppointmentStatus.waiting){
                    checkinButton.setText("check in");
                    savedAppointment.status= Appointment.AppointmentStatus.checkedin;
                }else if(savedAppointment.status== Appointment.AppointmentStatus.checkedin) {
                    checkinButton.setText("check out");
                    savedAppointment.status= Appointment.AppointmentStatus.checkedout;
                }else{
                    checkinButton.setText("nothing");
                }
                appAdapter.notifyDataSetChanged();
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog=new Dialog(PatientActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);

                dialog.setContentView(R.layout.make_appointmentlayout);
                savedDate=null;
                final CalendarView appCalendar=dialog.findViewById(R.id.appCalendar);
                final Button buttontime=dialog.findViewById(R.id.buttontime);

                appCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        savedDate =new Shift.Date(dayOfMonth,month,year);
                        appCalendar.setVisibility(View.GONE);
                        if (buttontime.getVisibility()==View.GONE){
                            dialog.dismiss();
                        }
                    }
                });

                buttontime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timepicker;
                        timepicker = new TimePickerDialog(PatientActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                savedHour=new Shift.Hour( selectedHour , selectedMinute);
                                buttontime.setVisibility(View.GONE);
                                if (appCalendar.getVisibility()==View.GONE){
                                    dialog.dismiss();
                                }
                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);//Yes 24 hour time
                        timepicker.setTitle("Select start Time");
                        timepicker.show();

                        dialog.show();
                    }
                });

            }
        });

        clinicsProvidingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog=new Dialog(PatientActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);

                dialog.setContentView(R.layout.make_appointmentlayout);
                savedDate=null;
                final CalendarView appCalendar=dialog.findViewById(R.id.appCalendar);
                final Button buttontime=dialog.findViewById(R.id.buttontime);

                appCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        savedDate =new Shift.Date(dayOfMonth,month,year);
                        appCalendar.setVisibility(View.GONE);
                        if (buttontime.getVisibility()==View.GONE){
                            dialog.dismiss();
                        }
                    }
                });

                buttontime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timepicker;
                        timepicker = new TimePickerDialog(PatientActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                savedHour=new Shift.Hour( selectedHour , selectedMinute);
                                buttontime.setVisibility(View.GONE);
                                if (appCalendar.getVisibility()==View.GONE){
                                    dialog.dismiss();
                                }
                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);//Yes 24 hour time
                        timepicker.setTitle("Select start Time");
                        timepicker.show();

                        dialog.show();
                    }
                });

            }
        });

        allServicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog=new Dialog(PatientActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);

                dialog.setContentView(R.layout.make_appointmentlayout);
                savedDate=null;

                final CalendarView appCalendar=dialog.findViewById(R.id.appCalendar);
                final Button buttontime=dialog.findViewById(R.id.buttontime);
                final Button buttonappointment=dialog.findViewById(R.id.buttonappointment);
                appCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        savedDate =new Shift.Date(dayOfMonth,month,year);
                        appCalendar.setVisibility(View.GONE);

                    }
                });
                buttonappointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Appointment app=new Appointment();
                        app.status= Appointment.AppointmentStatus.waiting;
                        app.clinicID="";
                        app.shift=new Shift(savedDate,savedHour,null);
                        makeAppointment(app);

                    }
                });

                buttontime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timepicker;
                        timepicker = new TimePickerDialog(PatientActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                savedHour=new Shift.Hour( selectedHour , selectedMinute);
                                buttontime.setVisibility(View.GONE);

                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);//Yes 24 hour time
                        timepicker.setTitle("Select start Time");
                        timepicker.show();


                    }
                });
                dialog.show();
            }
        });

    }

    private void makeAppointment(Appointment appointment) {
        Toast.makeText(getApplicationContext(), "appointment made",
                Toast.LENGTH_SHORT).show();
        appointmentList.add(appointment);
        appAdapter.notifyDataSetChanged();
    }

    public void searchByAddress(){
        Toast.makeText(getApplicationContext(), "searching by address",
                Toast.LENGTH_SHORT).show();
        searchList.clear();
        prepareSearchListByAddress();
        adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,searchList);
        searchListView.setAdapter((adapter));
    }
    public void searchByWH(){
        Toast.makeText(getApplicationContext(), "searching by working hours" ,
                Toast.LENGTH_SHORT).show();
        searchList.clear();
        prepareSearchListByWH();
        adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,searchList);
        searchListView.setAdapter((adapter));
    }
    public void searchByService(){
        Toast.makeText(getApplicationContext(), "searching by service type",
                Toast.LENGTH_SHORT).show();
        clinicsList.clear();
        prepareSearchListByService();
        adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,clinicsList);
        clinicsProvidingListView.setAdapter((adapter));
    }

    //adds the clinic that have the same address
    public void prepareSearchListByAddress(){}

    //adds the clinic that offer services on the same date
    public void prepareSearchListByWH(){}
    ArrayList<Service> temp = new ArrayList<>();
    //adds the clinics that offer services with the same provider selected (as radio button)
    public void prepareSearchListByService(){
        DatabaseReference accountRef=FirebaseDatabase.getInstance().getReference().child("Accounts");

        try {
            accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    clinicsList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Account account = ds.getValue(Account.class);
                        //ids.add(account.getAccountID());
                        if(account.getAccountID()!=null && account.getAccountType().equals("Employee")) {
                            //EmployeeAccount e = (EmployeeAccount) account;
                            Toast.makeText(getApplicationContext(), account.getUserName(),
                                    Toast.LENGTH_SHORT).show();
                            temp = Singleton.getAccountServicesFromDB(account.getAccountID());
                            for (Service s : temp) {
                                Toast.makeText(getApplicationContext(), savedservice.getName()+" "+s.name,
                                        Toast.LENGTH_SHORT).show();
                                if (savedservice.getName().equals(s.name)) {
                                    try {

                                        clinicsList.add(account.getAccountID());
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(), "Failed to update list",
                            Toast.LENGTH_SHORT).show();
                }
            });

            //accountadapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //gets appointments from database
    public boolean prepareAppointments(){
        //to implement

        return appointmentList.isEmpty();
    }
    //get clinic name from database using ID as employee id
    public String getAddressFromClinicID(String ID){

        return null;
    }


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

            servicesAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }

        //add them to the list in admin
        //use the method setServices(List<Service>)

    }

}
