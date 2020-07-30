package sarkar.kinboami;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import sarkar.kinboami.prevalent.Prevalent;

public class ConfirmFinalOrder extends AppCompatActivity {
    private TextInputLayout order_user_name,order_user_phone,order_user_city,order_user_dist,
            order_user_region,order_user_houseNo,order_user_postalCode;
    private Button confirm_order_btn;
    private String totalAmount ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        order_user_name = findViewById(R.id.order_user_name);
        order_user_phone = findViewById(R.id.order_user_phone);
        order_user_city = findViewById(R.id.order_user_city);
        order_user_dist = findViewById(R.id.order_user_dist);
        order_user_region = findViewById(R.id.order_user_region);
        order_user_houseNo = findViewById(R.id.order_user_houseNo);
        order_user_postalCode = findViewById(R.id.order_user_postalCode);

        confirm_order_btn = findViewById(R.id.confirm_order_btn);

        //Receive total Orderd Product Price from CartActivity...
        totalAmount = getIntent().getStringExtra("total price");
        Toast.makeText(this, totalAmount+" ৳", Toast.LENGTH_LONG).show();

        order_user_name.getEditText().setText(Prevalent.currentOnlineUser.getName());
        order_user_phone.getEditText().setText(Prevalent.currentOnlineUser.getPhone());

        confirm_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateName() && validatePhone() && validateCity() && validateDist() && validateRegion() && validateHouse() && validatePostal()){

                }
                else {return;}
            }
        });
    }

    private boolean validateName(){
        String val = order_user_name.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_name.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_name.setError(null);
            order_user_name.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePhone(){
        String val = order_user_phone.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_phone.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_phone.setError(null);
            order_user_phone.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateCity(){
        String val = order_user_city.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_city.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_city.setError(null);
            order_user_city.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateDist(){
        String val = order_user_dist.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_dist.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_dist.setError(null);
            order_user_dist.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateRegion(){
        String val = order_user_region.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_region.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_region.setError(null);
            order_user_region.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateHouse(){
        String val = order_user_houseNo.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_houseNo.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_houseNo.setError(null);
            order_user_houseNo.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePostal(){
        String val = order_user_postalCode.getEditText().getText().toString();
        if (val.isEmpty()){
            order_user_postalCode.setError("Field can't be empty");
            return false;
        }
        else{
            order_user_postalCode.setError(null);
            order_user_postalCode.setErrorEnabled(false);
            return true;
        }
    }
}