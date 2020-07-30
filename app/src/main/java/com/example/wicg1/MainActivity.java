package com.example.wicg1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginbutton,forgotpasswordbutton,createaccountbutton;
    TextView forgotpasswordnotice;

    final int CREATEACCOUNTACTIVITY=1;


    //firebase

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Accounts");

    Account currentUserAccount;

    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username=findViewById(R.id.usernametext);
        password=findViewById(R.id.passwordtext);
        loginbutton=findViewById(R.id.loginbutton);
        forgotpasswordbutton=findViewById(R.id.forgotpasswordbutton);
        createaccountbutton=findViewById(R.id.createaccountbutton);
        forgotpasswordnotice=findViewById(R.id.forgotpasswordnotice);
        forgotpasswordnotice.setVisibility(View.GONE);

        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,CreateAccountActivity.class);

                startActivityForResult(intent,CREATEACCOUNTACTIVITY);

            }
        });

        forgotpasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (forgotpasswordnotice.getVisibility()==View.GONE){

                    forgotpasswordnotice.setVisibility(View.VISIBLE);

                } else {

                    forgotpasswordnotice.setVisibility(View.GONE);

                }


            }
        });


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validate


                //call signin method
                signIn(username.getText().toString(),password.getText().toString());

                //if everything is fine (change true)


                //TODO
                /*
                if (true){
                    Intent intent=new Intent(MainActivity.this, com.example.wicg1.WelcomeActivity.class);
                    intent.putExtra("username",username.getText().toString());
                    startActivity(intent);
                }
                 */

            }
        });
    }
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);

        if (requestcode==CREATEACCOUNTACTIVITY){

            username.setText(getIntent().getStringExtra("username"));
            password.setText(getIntent().getStringExtra("password"));

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUserAccount!=null)
            Toast.makeText(this, "Already logged in as: "+currentUserAccount.getUserName(),Toast.LENGTH_LONG).show();

        //updateUI(currentUser);
    }

    public void signIn(String userName, String password){

        // Read from the database
        try {
            pass = sha256(password).toString();
            myRef.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Account account = dataSnapshot.getValue(Account.class);
                    if(account == null){
                        Toast.makeText(MainActivity.this, "Inexistant account!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(account.getPassword().equals(pass)){
                        currentUserAccount = account;
                        // String value = dataSnapshot.getValue(String.class);
                        //Log.d(TAG, "Value is: " + value);
                        Toast.makeText(MainActivity.this, "Login successful",
                                Toast.LENGTH_SHORT).show();
                        if (currentUserAccount.getAccountType().equals("Administrator")){
                            AdminAccount adm= dataSnapshot.getValue(AdminAccount.class);
                            Singleton.getInstance().setAccount(adm, Singleton.AccTypes.admin);
                        }
                        if (currentUserAccount.getAccountType().equals("Employee")){
                            EmployeeAccount emp= dataSnapshot.getValue(EmployeeAccount.class);
                            Singleton.getInstance().setAccount(emp, Singleton.AccTypes.employee);

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Accounts").child(account.getAccountID());
                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                    if (Singleton.isNumeric(snapshot.getKey()))
                                        emp.shifts.add((Shift) snapshot.getValue(Shift.class));//to play with
                                }
                                ref.child("insuranceTypes").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snap: dataSnapshot.getChildren()){
                                            Singleton.getInstance().empAcc.insuranceTypes.add((String) snap.getValue());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            ref.child("paymentMethods").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snap: dataSnapshot.getChildren()){
                                        Singleton.getInstance().empAcc.paymentMethods.add((String) snap.getValue());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }else if (currentUserAccount.getAccountType().equals("Patient")){
                            PatientAccount pat= dataSnapshot.getValue(PatientAccount.class);
                            Singleton.getInstance().setAccount(pat, Singleton.AccTypes.patient);
                        }

                        Intent myIntent;
                        myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                        myIntent.putExtra("ACCOUNT_USERNAME", currentUserAccount.getUserName());
                        myIntent.putExtra("ACCOUNT_TYPE", currentUserAccount.getAccountType());
                        startActivity(myIntent);

                    } else {
                        Toast.makeText(MainActivity.this, "wrong username or password",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(MainActivity.this, "Failed to read value",
                            Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Something went wrong",
                    Toast.LENGTH_SHORT).show();
        }


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

}
