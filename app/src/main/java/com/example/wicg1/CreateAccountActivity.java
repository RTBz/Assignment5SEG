package com.example.wicg1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;


public class CreateAccountActivity extends AppCompatActivity {

    EditText usernametext,passwordtext,firstnametext,lastnametext
            ,dateofbirthtext,phonenumbertext,emailaddresstext,confirmpasswordtext
            ,clinicName,address,paymentMethod,insurance;


    Button signupbutton,returntologinbutton;
    RadioButton employeebutton,patientbutton;
    RadioGroup radioGroupAccountType;

    final int VALUESAREOK=1;
    final int VALUESNOTOK=0;

    //firebase


    DatabaseReference reff;

    boolean userNameUsed = false;

    Account currentUserAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //firebase

        reff = FirebaseDatabase.getInstance().getReference().child("Accounts");

        currentUserAccount = new Account();

        // here you get the information

        returntologinbutton=findViewById(R.id.returntologinbutton);

        returntologinbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(VALUESNOTOK);
                CreateAccountActivity.this.finish();
            }
        });
        clinicName=findViewById(R.id.createClinicName);
        clinicName.setVisibility(View.GONE);
        usernametext=findViewById(R.id.usernametext);
        passwordtext=findViewById(R.id.passwordtext);
        firstnametext=findViewById(R.id.firstNameText);
        lastnametext=findViewById(R.id.lastNameText);
        dateofbirthtext=findViewById(R.id.dateOfBirthText);
        phonenumbertext=findViewById(R.id.phoneNumberText);
        emailaddresstext=findViewById(R.id.emailAddressText);
        confirmpasswordtext=findViewById(R.id.confirmPasswordText);
        signupbutton=findViewById(R.id.signupbutton);
        employeebutton=findViewById(R.id.employeeButton);
        patientbutton=findViewById(R.id.patientButton);
        radioGroupAccountType = findViewById(R.id.roleProvider);
        address=findViewById(R.id.address);
        insurance=findViewById(R.id.insuranceText);
        insurance.setVisibility(View.GONE);
        paymentMethod=findViewById(R.id.paymentText);
        paymentMethod.setVisibility(View.GONE);

        signupbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check all values


                //if everything is ok  // implement valuesAreOk
                if (valuesAreOk()){

                    //call method to create account
                    createAccount();


                    //CreateAccountActivity.this,com.example.wicg1.MainActivity.class
                    Intent intent=new Intent();
                    //put all the info in database

                    //put the values to be return to mainactivity : username, password
                    /*
                    intent.putExtra("username",usernametext.getText().toString());
                    intent.putExtra("password",passwordtext.getText().toString());
                    setResult((valuesAreOk()?VALUESAREOK:VALUESNOTOK),intent);
                    CreateAccountActivity.this.finish();

                     */
                } else {
                    //print something or do whaterver you want to ask the user to input valid values

                }
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.employeeButton:
                if (checked)
                    // Employee
                    clinicName.setVisibility(View.VISIBLE);
                    insurance.setVisibility(View.GONE);
                    paymentMethod.setVisibility(View.GONE);
                    break;
            case R.id.patientButton:
                if (checked)
                    clinicName.setVisibility(View.GONE);
                    insurance.setVisibility(View.VISIBLE);
                    paymentMethod.setVisibility(View.VISIBLE);
                // Patient
                    break;
        }
    }

    // access all the values and check if something is wrong
    //for now it retuns true
    protected boolean valuesAreOk(){
        return true;
    }

    private boolean validateFields(){
        if (usernametext.getText().toString()== null ||usernametext.getText().toString().equals("")  ){
            return false;
        }

        if (address.getText().toString()== null ||address.getText().toString().equals("")  ){
            return false;
        }

        if (firstnametext.getText().toString() == null || firstnametext.getText().toString().equals("")){
            return false;
        }

        if (lastnametext.getText().toString()== null || lastnametext.getText().toString().equals("")){
            return false;
        }

        if (dateofbirthtext.getText().toString() == null || dateofbirthtext.getText().toString().equals("")){
            return false;
        }

        if (phonenumbertext.getText().toString() == null || phonenumbertext.getText().toString().equals("")){
            return false;
        }

        if (emailaddresstext.getText().toString()==null || emailaddresstext.getText().toString().equals("")){
            return false;
        }

        if (passwordtext.getText().toString() == null || passwordtext.getText().toString().equals("")){
            return false;
        }

        if (((RadioButton)findViewById(radioGroupAccountType.getCheckedRadioButtonId())).getText().toString()== null || ((RadioButton)findViewById(radioGroupAccountType.getCheckedRadioButtonId())).getText().toString().equals("")){
            return false;
        }

        else{
            return true;
        }


    }


    //create account method
    private void createAccount(){

        if(!validateFields()){
            Toast.makeText(getApplicationContext(), "Please fill out all the fields required!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //check if username already used
        reff.child(usernametext.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Account account = dataSnapshot.getValue(Account.class);
                if(account != null){
                    Toast.makeText(getApplicationContext(), "Account exists!",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {

                        saveAccountToDatabase();
/*
                    if (currentUserAccount.getAccountType().equals("Administrator")){
                        AdminAccount adm= dataSnapshot.getValue(AdminAccount.class);
                        Singleton.getInstance().setAccount(adm, Singleton.AccTypes.admin);
                    }
                    if (currentUserAccount.getAccountType().equals("Employee")){
                        EmployeeAccount emp= dataSnapshot.getValue(EmployeeAccount.class);
                        Singleton.getInstance().setAccount(emp, Singleton.AccTypes.employee);
                    }
                    if (currentUserAccount.getAccountType().equals("Patient")){
                        PatientAccount emp= dataSnapshot.getValue(PatientAccount.class);
                        Singleton.getInstance().setAccount(emp, Singleton.AccTypes.patient);
                    }
                        // String value = dataSnapshot.getValue(String.class);
                        //Log.d(TAG, "Value is: " + value);
                        Toast.makeText(getApplicationContext(), "New account created",
                                Toast.LENGTH_SHORT).show();
*/
                        Intent myIntent;
                        myIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        myIntent.putExtra("ACCOUNT_USERNAME", currentUserAccount.getUserName());
                        myIntent.putExtra("ACCOUNT_TYPE", currentUserAccount.getAccountType());
                        startActivity(myIntent);
                    }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to read value",
                        Toast.LENGTH_SHORT).show();
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
    //method to save account data to real time database
    public void saveAccountToDatabase(){

        if (employeebutton.isChecked()){
            currentUserAccount=new EmployeeAccount(clinicName.getText().toString());
        }else if(patientbutton.isChecked()){
            currentUserAccount=new PatientAccount(insurance.getText().toString(),paymentMethod.getText().toString());
        }

        String userName = usernametext.getText().toString();
        String firstName = firstnametext.getText().toString();
        String lastName = lastnametext.getText().toString();
        String dateOfBirth = dateofbirthtext.getText().toString();
        String phoneNumber = phonenumbertext.getText().toString();
        String email = emailaddresstext.getText().toString();
        String addressS = address.getText().toString();
        String country = "fj";
        String province = "fj";
        String city = "fj";
        String street = "fj";
        String postalCode = "fj";
        String password = passwordtext.getText().toString();
        String accountType = ((RadioButton)findViewById(radioGroupAccountType.getCheckedRadioButtonId())).getText().toString();
        String accountID = userName;

        currentUserAccount.setUserName(userName);
        currentUserAccount.setFirstName(firstName);
        currentUserAccount.setLastName(lastName);
        currentUserAccount.setAccountID(accountID);
        currentUserAccount.setAccountType(accountType);
        currentUserAccount.setAddress(addressS);
        currentUserAccount.setCity(city);
        currentUserAccount.setCountry(country);
        currentUserAccount.setDateOfBirth(dateOfBirth);
        currentUserAccount.setEmail(email);
        currentUserAccount.setPassword(sha256(password).toString());
        currentUserAccount.setPhoneNumber(phoneNumber);
        currentUserAccount.setPostalCode(postalCode);
        currentUserAccount.setProvince(province);

        //to add patient
        if (currentUserAccount.getAccountType().equals("Employee")){
            EmployeeAccount emp=new EmployeeAccount(currentUserAccount);
            emp.clinic=clinicName.getText().toString();
            Singleton.getInstance().setAccount(emp, Singleton.AccTypes.employee);
            reff.child(userName).setValue(emp);
        }else if(currentUserAccount.getAccountType().equals("Patient")){
            PatientAccount pat=new PatientAccount(currentUserAccount);
            pat.insurance=insurance.getText().toString();
            pat.paymentMethod=paymentMethod.getText().toString();
            Singleton.getInstance().setAccount(pat, Singleton.AccTypes.patient);
            reff.child(userName).setValue(pat);
        }//adad patient


    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    //clinic section

}
