package sarkar.kinboami.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import sarkar.kinboami.R;
import sarkar.kinboami.model.Users;
import sarkar.kinboami.prevalent.Prevalent;

public class ResetPassword extends AppCompatActivity {
    private String check;
    private TextInputLayout on_user_current_password,on_user_new_password,off_user_phone,off_user_otp,off_user_new_password;
    private Button get_otp_btn,reset_password_btn,resend_code_btn,verify_otp_btn;
    CountryCodePicker ccp;
    DatabaseReference userRef;
    LoadingDialog loadingDialog;

    FirebaseAuth fAuth;
    String verificationId,phnWithoutCountryCode;
    PhoneAuthProvider.ForceResendingToken token;
    boolean verificationInProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        fAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(ResetPassword.this);
        Paper.init(this);

        check = getIntent().getStringExtra("check");

        ccp = findViewById(R.id.ccp);
        on_user_current_password = findViewById(R.id.on_user_current_password);
        on_user_new_password = findViewById(R.id.on_user_new_password);
        off_user_phone = findViewById(R.id.off_user_phone);
        off_user_otp = findViewById(R.id.off_user_otp);
        off_user_new_password = findViewById(R.id.off_user_new_password);
        get_otp_btn = findViewById(R.id.get_otp_btn);
        reset_password_btn = findViewById(R.id.reset_password_btn);
        resend_code_btn = findViewById(R.id.resend_code_btn);
        verify_otp_btn = findViewById(R.id.verify_otp_btn);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        reset_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check.equals("settings")){
                    CurrentOnlineUserPasswordReset();
                }
                if (check.equals("login")){
                    ResetOfflineUserPassword();
                }
            }
        });

        verify_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyOtpManually();
            }
        });

        get_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phnWithoutCountryCode = "0"+off_user_phone.getEditText().getText().toString();
                final String phoneWithCountryCode = "+"+ccp.getSelectedCountryCode()+off_user_phone.getEditText().getText().toString();

                if (!verificationInProgress){
                    if (validatePhoneNo()){
                        loadingDialog.start();
                        userRef.child(phnWithoutCountryCode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    off_user_phone.setError(null);
                                    off_user_phone.setErrorEnabled(false);
                                    RequestForOTP(phoneWithCountryCode);
                                }
                                else {
                                    loadingDialog.dismiss();
                                    off_user_phone.setError("Phone number doesn't exist"); }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ResetPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {return;}
                }else{
                    VerifyOtpManually();
                }
            }
        });
    }

    private void VerifyOtpManually() {
        String userEnteredOTP = off_user_otp.getEditText().getText().toString();
        if (!userEnteredOTP.isEmpty() && userEnteredOTP.length()==6){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userEnteredOTP);
            VerifyAuth(credential);
        }
        else {
            off_user_otp.setError("Invalid OTP");
        }
    }

    private void VerifyAuth(PhoneAuthCredential credential) {
        loadingDialog.start();
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loadingDialog.dismiss();
                    Toast.makeText(ResetPassword.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                    off_user_otp.setVisibility(View.GONE);
                    verify_otp_btn.setVisibility(View.GONE);
                    off_user_new_password.setVisibility(View.VISIBLE);
                    reset_password_btn.setVisibility(View.VISIBLE);
                    ResetOfflineUserPassword();
                }
                else {
                    loadingDialog.dismiss();
                    Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void RequestForOTP(String phoneWithCountryCode) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneWithCountryCode, 120L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                off_user_phone.setVisibility(View.GONE);
                ccp.setVisibility(View.GONE);
                get_otp_btn.setVisibility(View.GONE);
                off_user_otp.setVisibility(View.VISIBLE);
                verify_otp_btn.setVisibility(View.VISIBLE);
                loadingDialog.dismiss();

                verificationId = s;
                token = forceResendingToken;
                verificationInProgress = true ;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(ResetPassword.this, "Request timeout, Try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                VerifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (check.equals("settings")){
            on_user_current_password.setVisibility(View.VISIBLE);
            on_user_new_password.setVisibility(View.VISIBLE);
            reset_password_btn.setVisibility(View.VISIBLE);

        }
        else if (check.equals("login")){
            off_user_phone.setVisibility(View.VISIBLE);
            get_otp_btn.setVisibility(View.VISIBLE);
            ccp.setVisibility(View.VISIBLE);
        }
    }

    private void ResetOfflineUserPassword() {
        final String offlineUserNewPassword = off_user_new_password.getEditText().getText().toString();
        userRef.child(phnWithoutCountryCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    loadingDialog.start();
                    Users users = snapshot.getValue(Users.class);
                    if (validateOffUserPassword()){
                        Users users1 = new Users(users.getName(),users.getPhone(),offlineUserNewPassword,users.getImage(),users.getAddress());
                        userRef.child(users.getPhone()).setValue(users1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    loadingDialog.dismiss();
                                    Toast.makeText(ResetPassword.this, "Password reset successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ResetPassword.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else {
                        loadingDialog.dismiss();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResetPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void CurrentOnlineUserPasswordReset() {
        final String userEnteredCurrentPass = on_user_current_password.getEditText().getText().toString();
        final String newPassword = on_user_new_password.getEditText().getText().toString();

        userRef.child(Prevalent.currentOnlineUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users users = snapshot.getValue(Users.class);
                    if (users.getPassword().equals(userEnteredCurrentPass)){
                        on_user_current_password.setError(null);
                        on_user_current_password.setErrorEnabled(false);
                        if (validatePassword()){
                            loadingDialog.start();
                            final Users users1 = new Users(users.getName(),users.getPhone(),newPassword,users.getImage(),users.getAddress());
                            userRef.child(users.getPhone()).setValue(users1);
                            Toast.makeText(ResetPassword.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();

                            CharSequence options[] = new CharSequence[]{
                                    "Go to Home",
                                    "Login Again"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                            builder.setTitle("Password Reset Successfully");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0){
                                        //Paper.book().write(Prevalent.UserPhoneKey, users1.getPhone());
                                        //Paper.book().write(Prevalent.UserPasswordKey, users1.getPassword());
                                        Prevalent.currentOnlineUser = users1;
                                        Intent intent = new Intent(ResetPassword.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    if (i==1){
                                        Paper.book().destroy();
                                        Intent intent = new Intent(ResetPassword.this,Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();

                        }else {return;}
                    }
                    else {
                        on_user_current_password.setError("Wrong Password");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResetPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean validatePassword(){
        String val = on_user_new_password.getEditText().getText().toString();

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
            on_user_new_password.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            on_user_new_password.setError("At least 4 characters, 1 lower case letter, no white space,");
            return false;
        } else {
            on_user_new_password.setError(null);
            on_user_new_password.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePhoneNo(){
        String val = off_user_phone.getEditText().getText().toString();

        if (val.isEmpty()){
            off_user_phone.setError("Field can't be empty");
            return false;
        }
        else if (val.length() < 10){
            off_user_phone.setError("Phone must be 10 digit");
            return false;
        }else if (val.length() > 10){
            off_user_phone.setError("Phone must be 10 digit");
            return false;
        }
        else{
            off_user_phone.setError(null);
            off_user_phone.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateOffUserPassword(){
        String val = off_user_new_password.getEditText().getText().toString();

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
            off_user_new_password.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            off_user_new_password.setError("At least 4 characters, 1 lower case letter, no white space,");
            return false;
        } else {
            off_user_new_password.setError(null);
            off_user_new_password.setErrorEnabled(false);
            return true;
        }
    }

}