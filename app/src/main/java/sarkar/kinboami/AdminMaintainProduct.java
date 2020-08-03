package sarkar.kinboami;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class AdminMaintainProduct extends AppCompatActivity {

    private ImageView product_image_maintain,back_to_maintain_home;
    private TextInputEditText product_name_maintain,product_description_maintain,product_price_maintain;
    private Button apply_change_btn,delete_product_btn;
    private String pID,pName,pDescription,pPrice,pImage,pCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        back_to_maintain_home = findViewById(R.id.back_to_maintain_home);
        product_image_maintain = findViewById(R.id.product_image_maintain);
        product_name_maintain = findViewById(R.id.product_name_maintain);
        product_description_maintain = findViewById(R.id.product_description_maintain);
        product_price_maintain = findViewById(R.id.product_price_maintain);

        apply_change_btn = findViewById(R.id.apply_change_btn);
        delete_product_btn = findViewById(R.id.delete_product_btn);

        //Received Intent Data From Home Activity...
        pID = getIntent().getStringExtra("pid");
        pName = getIntent().getStringExtra("pname");
        pDescription = getIntent().getStringExtra("pdescription");
        pPrice = getIntent().getStringExtra("pprice");
        pImage = getIntent().getStringExtra("pimage");
        pCategory = getIntent().getStringExtra("pcat");

        //Set intent value to Input field...
        Picasso.get().load(pImage).into(product_image_maintain);
        product_name_maintain.setText(pName);
        product_description_maintain.setText(pDescription);
        product_price_maintain.setText(pPrice);

//        back_to_maintain_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AdminMaintainProduct.this,Home.class);
//                startActivity(intent);
//            }
//        });
    }
}