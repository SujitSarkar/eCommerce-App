package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import sarkar.kinboami.ViewHolder.CartViewHolder;
import sarkar.kinboami.model.CartList;
import sarkar.kinboami.model.ProductDetails;
import sarkar.kinboami.prevalent.Prevalent;

public class CartActivity extends AppCompatActivity {
    private TextView total_price,mgs1;
    private RecyclerView cart_list;
    private RecyclerView.LayoutManager layoutManager;
    private Button order_button;
    private ImageView back_to_home;
    LoadingDialog loadingDialog;
    private int totalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        total_price = findViewById(R.id.total_price);
        order_button = findViewById(R.id.order_button);
        back_to_home = findViewById(R.id.back_to_home);
        mgs1 = findViewById(R.id.mgs1);

        loadingDialog = new LoadingDialog(this);

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


        order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_price.setText("Total Price: "+String.valueOf(totalPrice)+" ৳");

                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrder.class);
                intent.putExtra("total price",String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<CartList> options =
                new FirebaseRecyclerOptions.Builder<CartList>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"),CartList.class).build();

        FirebaseRecyclerAdapter<CartList, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<CartList, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final CartList cartModel) {
                        holder.cart_product_name.setText(cartModel.getName());
                        holder.cart_product_qtty.setText(cartModel.getQuantity());
                        holder.cart_product_price.setText(cartModel.getPrice());
                        Picasso.get().load(cartModel.getImage()).into(holder.cart_product_image);

                        //Calculate Total Product Price...
                        int eachProductPriceWithQtty = Integer.valueOf(cartModel.getPrice()) * Integer.valueOf(cartModel.getQuantity());
                        totalPrice = totalPrice + eachProductPriceWithQtty;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{
                                        "Edit",
                                        "Remove from Cart"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        if (index==0){
                                            Intent intent= new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", cartModel.getPid());
                                            startActivity(intent);
                                        }
                                        if (index==1){
                                            loadingDialog.start();
                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(cartModel.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                loadingDialog.dismiss();
                                                                Toast.makeText(CartActivity.this, "Item Deleted", Toast.LENGTH_LONG).show();

                                                            } else {
                                                                loadingDialog.dismiss();
                                                                Toast.makeText(CartActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                        total_price.setText("Total Price: "+String.valueOf(totalPrice)+" ৳");
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

    private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        total_price.setText("Dear "+ userName+"\nOrder is shipped successfully");
                        cart_list.setVisibility(View.GONE);
                        mgs1.setVisibility(View.VISIBLE);
                        mgs1.setText("Congratulations, your final order has been shipped successfully. Soon you will receive your order at your door step.\n You can purchase more product after shipping the last order.");
                        order_button.setVisibility(View.GONE);
                    }
                    else if (shippingState.equals("not shipped")){
                        total_price.setText("Not shipped yet");
                        cart_list.setVisibility(View.GONE);
                        mgs1.setVisibility(View.VISIBLE);
                        mgs1.setText("Congratulations, your final order has been placed successfully. Soon it will be verified.\n You can purchase more product after shipping the last order.");
                        order_button.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}