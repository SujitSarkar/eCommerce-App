package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sarkar.kinboami.model.CartList;
import sarkar.kinboami.model.ProductDetails;
import sarkar.kinboami.prevalent.Prevalent;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView product_image_details,back_to_home,add_to_cart_btn;
    private TextView product_name_details,product_desc_details,product_price_details;
    private ElegantNumberButton product_quantity_btn;
    private String productId = "";
    private String productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        back_to_home = findViewById(R.id.back_to_home);
        product_image_details = findViewById(R.id.product_image_details);
        add_to_cart_btn = findViewById(R.id.add_to_cart_btn);

        product_name_details = findViewById(R.id.product_name_details);
        product_desc_details = findViewById(R.id.product_desc_details);
        product_price_details = findViewById(R.id.product_price_details);

        product_quantity_btn = findViewById(R.id.product_quantity_btn);

        //Receive intent data from Home Activity...
        productId = getIntent().getStringExtra("pid");
        GetProductDetailsFromDB(productId);

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,Home.class);
                startActivity(intent);
            }
        });
        add_to_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddingToCartList();
            }
        });
    }

    private void AddingToCartList() {
        String saveCurrentDate,saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final CartList cartList = new CartList(productId,product_name_details.getText().toString(),product_price_details.getText().toString(),saveCurrentDate,saveCurrentTime,product_quantity_btn.getNumber(),"",productImage);

        cartReference.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId)
                .setValue(cartList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //This is for Admin Panel...
                            cartReference.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productId)
                                    .setValue(cartList)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Product Added To Cart List", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this,Home.class);
                                                startActivity(intent);
                                                finish();
                                            }else { Toast.makeText(ProductDetailsActivity.this, "Product Added Failed", Toast.LENGTH_LONG).show(); }
                                        }
                                    });
                        }else{ Toast.makeText(ProductDetailsActivity.this, "Product Added Failed", Toast.LENGTH_LONG).show(); }
                    }
                });
    }

    private void GetProductDetailsFromDB(String productId) {
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products");

        productReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ProductDetails products = snapshot.getValue(ProductDetails.class);

                    Picasso.get().load(products.getImage()).into(product_image_details);
                    product_name_details.setText(products.getName());
                    product_desc_details.setText(products.getDescription());
                    product_price_details.setText(products.getPrice());

                    productImage = products.getImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}