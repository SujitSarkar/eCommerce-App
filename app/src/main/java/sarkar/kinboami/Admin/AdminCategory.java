package sarkar.kinboami.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import sarkar.kinboami.Buyer.Login;
import sarkar.kinboami.R;

public class AdminCategory extends AppCompatActivity {
    private ImageView t_shirt_IV,jersey_IV,female_dress_IV,sweater_IV;
    private ImageView laptop_IV,mobile_IV,watch_IV,headphone_IV;
    private ImageView sun_glass_IV,bag_IV,hat_IV,shoes_IV;
    private Button check_order_btn,logout_btn,maintain_product_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        t_shirt_IV = findViewById(R.id.t_shirt_IV);
        jersey_IV = findViewById(R.id.jersey_IV);
        female_dress_IV = findViewById(R.id.female_dress_IV);
        sweater_IV = findViewById(R.id.sweater_IV);

        laptop_IV = findViewById(R.id.laptop_IV);
        mobile_IV = findViewById(R.id.mobile_IV);
        watch_IV = findViewById(R.id.watch_IV);
        headphone_IV = findViewById(R.id.headphone_IV);

        sun_glass_IV = findViewById(R.id.sun_glass_IV);
        bag_IV = findViewById(R.id.bag_IV);
        hat_IV = findViewById(R.id.hat_IV);
        shoes_IV = findViewById(R.id.shoes_IV);

        maintain_product_btn = findViewById(R.id.maintain_product_btn);
        check_order_btn = findViewById(R.id.check_order_btn);
        logout_btn = findViewById(R.id.logout_btn);

        maintain_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminProductView.class);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        check_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminNewOrder.class);
                startActivity(intent);
            }
        });

        t_shirt_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","tShirts");
                startActivity(intent);
                finish();
            }
        });
        jersey_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","jerseys");
                startActivity(intent);
                finish();
            }
        });
        female_dress_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","femaleDresses");
                startActivity(intent);
                finish();
            }
        });
        sweater_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","sweaters");
                startActivity(intent);
                finish();
            }
        });
        laptop_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","laptops");
                startActivity(intent);
                finish();
            }
        });
        mobile_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","mobiles");
                startActivity(intent);
                finish();
            }
        });
        watch_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","watches");
                startActivity(intent);
                finish();
            }
        });
        headphone_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","headphones");
                startActivity(intent);
                finish();
            }
        });
        sun_glass_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","sunGlasses");
                startActivity(intent);
                finish();
            }
        });
        bag_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","bags");
                startActivity(intent);
                finish();
            }
        });
        hat_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","hats");
                startActivity(intent);
                finish();
            }
        });
        shoes_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this, AdminAddNewProduct.class);
                intent.putExtra("category","shoes");
                startActivity(intent);
                finish();
            }
        });
    }
}