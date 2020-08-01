package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import sarkar.kinboami.ViewHolder.CartViewHolder;
import sarkar.kinboami.model.CartList;

public class AdminUserProducts extends AppCompatActivity {
    private RecyclerView products_list;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListReference;
    private ImageView back_to_admin_orders;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        back_to_admin_orders = findViewById(R.id.back_to_admin_orders);

        products_list = findViewById(R.id.products_list);
        products_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        products_list.setLayoutManager(layoutManager);

        userID = getIntent().getStringExtra("uid");

        cartListReference = FirebaseDatabase.getInstance().getReference().
                child("Cart List").child("Admin View").child(userID).child("Products");

        back_to_admin_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminUserProducts.this,AdminNewOrder.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<CartList> options =
                new FirebaseRecyclerOptions.Builder<CartList>()
                .setQuery(cartListReference,CartList.class)
                .build();

        FirebaseRecyclerAdapter<CartList, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartList, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartList cartModel) {
                        holder.cart_product_name.setText(cartModel.getName());
                        holder.cart_product_qtty.setText(cartModel.getQuantity());
                        holder.cart_product_price.setText(cartModel.getPrice()+" ৳");
                        Picasso.get().load(cartModel.getImage()).into(holder.cart_product_image);
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        products_list.setAdapter(adapter);
        adapter.startListening();
    }
}