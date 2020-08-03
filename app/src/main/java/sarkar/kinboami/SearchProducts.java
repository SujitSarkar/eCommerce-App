package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import sarkar.kinboami.ViewHolder.ProductViewHolder;
import sarkar.kinboami.model.ProductDetails;

public class SearchProducts extends AppCompatActivity {

    private ImageView back_to_home,search_btn;
    private TextInputEditText search_field;
    RecyclerView search_list;
    private String searchInput;

    RecyclerView.LayoutManager layoutManager;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        back_to_home = findViewById(R.id.back_to_home);
        search_btn = findViewById(R.id.search_btn);
        search_field = findViewById(R.id.search_field);

        search_list = findViewById(R.id.search_list);
        //search_list.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        search_list.setLayoutManager(layoutManager);

        loadingDialog = new LoadingDialog(SearchProducts.this);

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SearchProducts.this,Home.class);
                startActivity(intent);
                finish();
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = search_field.getText().toString();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<ProductDetails> options =
                new FirebaseRecyclerOptions.Builder<ProductDetails>()
                        .setQuery(productRef.orderByChild("name").startAt(searchInput),ProductDetails.class)
                        .build();
        FirebaseRecyclerAdapter<ProductDetails, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductDetails, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final ProductDetails productDetails) {

                        holder.product_name.setText(productDetails.getName());
                        holder.product_price.setText(productDetails.getPrice()+ " ৳");
                        Picasso.get().load(productDetails.getImage()).into(holder.product_image);

                        //If user click on single item then go to the ProductDetailsActivity...
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(SearchProducts.this,ProductDetailsActivity.class);
                                intent.putExtra("pid", productDetails.getPid());
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
        search_list.setAdapter(adapter);
        adapter.startListening();
    }
}