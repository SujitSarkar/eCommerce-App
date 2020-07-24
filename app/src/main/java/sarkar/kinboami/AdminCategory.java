package sarkar.kinboami;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategory extends AppCompatActivity {
    private ImageView t_shirt_IV,jersey_IV,female_dress_IV,sweater_IV;
    private ImageView laptop_IV,mobile_IV,watch_IV,headphone_IV;
    private ImageView sun_glass_IV,bag_IV,hat_IV,shoes_IV;

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