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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import sarkar.kinboami.ViewHolder.CartViewHolder;
import sarkar.kinboami.model.CartList;
import sarkar.kinboami.prevalent.Prevalent;

public class CartActivity extends AppCompatActivity {
    private TextView total_price;
    private RecyclerView cart_list;
    private RecyclerView.LayoutManager layoutManager;
    private Button order_button;
    private ImageView back_to_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        total_price = findViewById(R.id.total_price);
        order_button = findViewById(R.id.order_button);
        back_to_home = findViewById(R.id.back_to_home);

        cart_list = findViewById(R.id.cart_list);
        cart_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cart_list.setLayoutManager(layoutManager);

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,Home.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<CartList> options =
                new FirebaseRecyclerOptions.Builder<CartList>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"),CartList.class).build();

        FirebaseRecyclerAdapter<CartList, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<CartList, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartList cartModel) {
                        holder.cart_product_name.setText(cartModel.getName());
                        holder.cart_product_qtty.setText(cartModel.getQuantity());
                        holder.cart_product_price.setText(cartModel.getPrice());
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
        cart_list.setAdapter(adapter);
        adapter.startListening();

    }

}