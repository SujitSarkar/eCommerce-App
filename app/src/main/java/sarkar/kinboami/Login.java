package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import sarkar.kinboami.model.Users;
import sarkar.kinboami.prevalent.Prevalent;

public class Login extends AppCompatActivity {

    private TextInputLayout loginPhone,loginPassword;
    private AppCompatCheckBox remember_me_chk;
    private Button forget_password_link, login_btn;
    private TextView admin_panel_link, not_admin_panel;
    private String parentDB = "Users";
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create loading bar object...
        loadingDialog = new LoadingDialog(Login.this);

        //hooks...
        loginPhone = findViewById(R.id.loginPhone);
        loginPassword = findViewById(R.id.loginPassword);
        remember_me_chk = (AppCompatCheckBox)findViewById(R.id.remember_me_chk);
        Paper.init(this);
        forget_password_link = findViewById(R.id.forget_password_link);
        admin_panel_link = findViewById(R.id.admin_panel_link);
        not_admin_panel = findViewById(R.id.not_admin_panel);
        login_btn = findViewById(R.id.login_btn);

        admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setText("Log in as Admin");
                admin_panel_link.setVisibility(View.INVISIBLE);
                not_admin_panel.setVisibility(View.VISIBLE);
                parentDB = "Admins";
            }
        });
        not_admin_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setText("Log in");
                not_admin_panel.setVisibility(View.INVISIBLE);
                admin_panel_link.setVisibility(View.VISIBLE);
                parentDB = "Users";
            }
        });

        PhoneAndPasswordReceivedFromSignup();

        RememberUserPhoneAndPassword();
    }

    private boolean validatePhoneNo(){
        String val = loginPhone.getEditText().getText().toString();

        if (val.isEmpty()){
            loginPhone.setError("Field can't be empty");
            return false;
        }
        else if (val.length() < 11){
            loginPhone.setError("Phone must be 11 digit");
            return false;
        }else if (val.length() > 11){
            loginPhone.setError("Phone must be 11 digit");
            return false;
        }
        else{
            loginPhone.setError(null);
            loginPhone.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePassword(){
        String val = loginPassword.getEditText().getText().toString();

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
            loginPassword.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            loginPassword.setError("At least 4 characters including lowercase letter & number, no white space,");
            return false;
        } else {
            loginPassword.setError(null);
            loginPassword.setErrorEnabled(false);
            return true;
        }
    }

    private void PhoneAndPasswordReceivedFromSignup(){
        //Received Intent data from SignUp activity...
        String phnFromSignup= getIntent().getStringExtra("phn");
        String passFromSignup = getIntent().getStringExtra("pass");

        loginPhone.getEditText().setText(phnFromSignup);
        loginPassword.getEditText().setText(passFromSignup);
    }

    public void loginToSignUp(View view) {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
        finish();
    }

    public void UserSignIn(View view) {
        if (validatePhoneNo() && validatePassword()){
            ///Start Loading Bar...
            loadingDialog.start();

            final String phoneFromUser = loginPhone.getEditText().getText().toString();
            final String passwordFromUser = loginPassword.getEditText().getText().toString();

            //Remember User Phone and Password...
            if (remember_me_chk.isChecked()){
                Paper.book().write(Prevalent.UserPhoneKey, phoneFromUser);
                Paper.book().write(Prevalent.UserPasswordKey, passwordFromUser);
            }

            DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

            rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(parentDB).child(phoneFromUser).exists()){

                        //fetch data from DB by User model class...
                        Users userData = snapshot.child(parentDB).child(phoneFromUser).getValue(Users.class);

                        //Compare phone between DB phone and user entered phone...
                            if (userData.getPassword().equals(passwordFromUser)){
                                if (parentDB.equals("Admins")){
                                    loadingDialog.dismiss();
                                    Toast.makeText(Login.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, AdminCategory.class);
                                    startActivity(intent);
                                }
                                else if (parentDB.equals("Users")){
                                    loadingDialog.dismiss();
                                    Toast.makeText(Login.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Home.class);
                                    //Save UserData into Prevalent class...
                                    Prevalent.currentOnlineUser = userData;
                                    startActivity(intent);
                                }
                            } else{
                                loadingDialog.dismiss();
                                loginPassword.setError("Incorrect Password");
                            }
                    }
                    else {
                        loadingDialog.dismiss();
                        loginPhone.setError("Incorrect Phone Number");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {return;}
    }

    private void RememberUserPhoneAndPassword(){
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                AllowAccess(UserPhoneKey,UserPasswordKey);
                loadingDialog.start();
            }
        }
    }

    private void AllowAccess(final String phone, final String password) {

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDB).child(phone).exists()){

                    //fetch data from DB by User model class...
                    Users userData = snapshot.child(parentDB).child(phone).getValue(Users.class);

                    //Compare phone between DB-phone and user-entered-phone...
                    if (userData.getPassword().equals(password)){
                            loadingDialog.dismiss();
                            Toast.makeText(Login.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            //Save UserData into Prevalent class...
                            Prevalent.currentOnlineUser = userData;
                            startActivity(intent);
                            finish();
                    } else{
                        loadingDialog.dismiss();
                        loginPassword.setError("Incorrect Password");
                    }
                }
                else {
                    loadingDialog.dismiss();
                    loginPhone.setError("Incorrect Phone Number");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(Login.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}