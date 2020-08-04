package sarkar.kinboami.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import sarkar.kinboami.R;
import sarkar.kinboami.ViewHolder.ProductViewHolder;
import sarkar.kinboami.model.ProductDetails;

public class AdminProductView extends AppCompatActivity {

    private ImageView back_to_admin_home;
    private RecyclerView admin_view_product_list;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_view);

        back_to_admin_home = findViewById(R.id.back_to_admin_home);

        admin_view_product_list = findViewById(R.id.admin_view_product_list);
        admin_view_product_list.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        admin_view_product_list.setLayoutManager(layoutManager);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        back_to_admin_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductView.this, AdminCategory.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProductDetails> options=
                new FirebaseRecyclerOptions.Builder<ProductDetails>()
                        .setQuery(productRef, ProductDetails.class)
                        .build();

        FirebaseRecyclerAdapter<ProductDetails, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductDetails, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final ProductDetails productDetails) {

                        holder.product_name.setText(productDetails.getName());
                        holder.product_price.setText(productDetails.getPrice()+ " ৳");
                        Picasso.get().load(productDetails.getImage()).into(holder.product_image);


                        //If user click on single item then go to the ProductDetailsActivity...
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                    Intent intent = new Intent(AdminProductView.this, AdminMaintainProduct.class);
                                    intent.putExtra("pid",productDetails.getPid());
                                    intent.putExtra("pname",productDetails.getName());
                                    intent.putExtra("pimage",productDetails.getImage());
                                    intent.putExtra("pcat",productDetails.getCategory());
                                    intent.putExtra("pprice",productDetails.getPrice());
                                    intent.putExtra("pdescription",productDetails.getDescription());
                                    startActivity(intent);
                                }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        admin_view_product_list.setAdapter(adapter);
        adapter.startListening();
    }
}