package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sarkar.kinboami.model.Orders;
import sarkar.kinboami.prevalent.Prevalent;

public class ConfirmFinalOrder extends AppCompatActivity {
    private TextInputLayout order_user_name,order_user_city,order_user_dist,
            order_user_region,order_user_houseNo,order_user_postalCode;
    private Button confirm_order_btn;
    private ImageView back_to_CartList;
    private String totalAmount ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        back_to_CartList = findViewById(R.id.back_to_CartList);
        order_user_name = findViewById(R.id.order_user_name);
        order_user_city = findViewById(R.id.order_user_city);
        order_user_dist = findViewById(R.id.order_user_dist);
        order_user_region = findViewById(R.id.order_user_region);
        order_user_houseNo = findViewById(R.id.order_user_houseNo);
        order_user_postalCode = findViewById(R.id.order_user_postalCode);

        confirm_order_btn = findViewById(R.id.confirm_order_btn);

        //Receive total Orderd Product Price from CartActivity...
        totalAmount = getIntent().getStringExtra("total price");

        SetPreviousEnteredUserInfo();

        back_to_CartList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmFinalOrder.this,CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        confirm_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateName() && validateCity() && validateDist() && validateRegion() && validateHouse() && validatePostal()){
                    ConfirmOrder();
                }
                else {return;}
            }
        });
    }

    private void SetPreviousEnteredUserInfo() {
        order_user_name.getEditText().setText(Prevalent.currentOnlineUser.getName());

        final DatabaseReference shippedRef = FirebaseDatabase.getInstance().getReference().child("Shipped Orders").child("Recent Order")
                .child(Prevalent.currentOnlineUser.getPhone());
        shippedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Orders orders = snapshot.getValue(Orders.class);

                    order_user_city.getEditText().setText(orders.getCity());
                    order_user_dist.getEditText().setText(orders.getDistrict());
                    order_user_region.getEditText().setText(orders.getRegion());
                    order_user_houseNo.getEditText().setText(orders.getHouse());
                    order_user_postalCode.getEditText().setText(orders.getPostal());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ConfirmOrder() {
        String date,time;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        time = currentTime.format(calendar.getTime());

        String name= order_user_name.getEditText().getText().toString();
        String city= order_user_city.getEditText().getText().toString();
        String district= order_user_dist.getEditText().getText().toString();
        String region= order_user_region.getEditText().getText().toString();
        String house= order_user_houseNo.getEditText().getText().toString();
        String postal= order_user_postalCode.getEditText().getText().toString();

        final DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        Orders orders = new Orders(name,Prevalent.currentOnlineUser.getPhone(),totalAmount,city,district,region,house,postal,date,time,"not shipped");
        orderReference.setValue(orders)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Remove "cart list" data...
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrder.this, "Your order has been placed successfully", Toast.LENGTH_LONG).show();
                                        Intent intent =new Intent(ConfirmFinalOrder.this,Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else { Toast.makeText(ConfirmFinalOrder.this, task.getException().getMessage(), Toast.LENGTH_LONG).show(); }
                                }
                            });
                }
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