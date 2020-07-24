package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import sarkar.kinboami.model.Users;

public class Signup extends AppCompatActivity {
    private TextInputLayout regName, regPhoneNo,regPassword;
    FirebaseDatabase database;
    DatabaseReference rootReference;
    DatabaseReference userReference;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Hooks...
        regName = findViewById(R.id.regName);
        regPhoneNo = findViewById(R.id.regPhoneNo);
        regPassword = findViewById(R.id.regPassword);
        //create loading bar object...
        loadingDialog = new LoadingDialog(Signup.this);

    }

    public void SignUpToLogin(View view) {
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void UserRegistration(View view) {
        //Firebase Database create Root reference...
        database = FirebaseDatabase.getInstance();
        rootReference = database.getReference();
        userReference = rootReference.child("Users");

        //get input from user and put them to String variable...
        final String userName = regName.getEditText().getText().toString();
        final String userPhone = regPhoneNo.getEditText().getText().toString();
        final String userPassword = regPassword.getEditText().getText().toString();

        if (validateName() && validatePhoneNo() && validatePassword()){
            loadingDialog.start();
            //check duplicate username with DB...
            if (!userPhone.isEmpty()){
                Query checkUser = userReference.orderByChild("phone").equalTo(userPhone);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            loadingDialog.dismiss();
                            regPhoneNo.setError("This phone number is already taken");
                        }
                        else {
                            //Set value into firebase database...
                            Users user = new Users(userName,userPhone,userPassword);
                            userReference.child(userPhone).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){

                                           loadingDialog.dismiss();
                                           Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_LONG).show();
                                           Intent intent = new Intent(Signup.this, Login.class);
                                           intent.putExtra("phn", userPhone);
                                           intent.putExtra("pass", userPassword);
                                           startActivity(intent);
                                           finish();

                                       } else {
                                           loadingDialog.dismiss();
                                           Toast.makeText(Signup.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                       }
                                    }
                                });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(Signup.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }else {
                return;
            }
        }
        else {return;}
    }

    private boolean validateName(){
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()){
            regName.setError("Field can't be empty");
            return false;
        }
        else{
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePhoneNo(){
        String val = regPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()){
            regPhoneNo.setError("Field can't be empty");
            return false;
        }
        else if (val.length() < 11){
            regPhoneNo.setError("Phone must be 11 digit");
            return false;
        }else if (val.length() > 11){
            regPhoneNo.setError("Phone must be 11 digit");
            return false;
        }
        else{
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePassword(){
        String val = regPassword.getEditText().getText().toString();

        String passwordVal = "^" +
                //"(?=.*[0-9])" +        //at least 1 digit
                "(?=.*[a-z])" +        //at least 1 lower case letter
                //"(?=.*[A-Z])" +        //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +       //any letter
                //"(?=.*[@#$%^&+=])" +     //at least 1 special character
                "(?=\\S+$)" +            //no white spaces
                ".{4,}" +                //at least 4 characters
                "$";

        if (val.isEmpty()) {
            regPassword.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("At least 4 characters, 1 lower case letter, no white space,");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }
}