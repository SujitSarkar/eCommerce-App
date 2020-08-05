package sarkar.kinboami.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sarkar.kinboami.Buyer.LoadingDialog;
import sarkar.kinboami.R;
import sarkar.kinboami.ViewHolder.AdminOrdersViewHolder;
import sarkar.kinboami.model.CartList;
import sarkar.kinboami.model.Orders;

public class AdminNewOrder extends AppCompatActivity {
    private ImageView back_to_admin_home;

    private RecyclerView order_list;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference orderReference,cartListAdminViewRef,shippedRef,cartListReference;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        back_to_admin_home = findViewById(R.id.back_to_admin_home);

        order_list = findViewById(R.id.order_list);
        order_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        order_list.setLayoutManager(layoutManager);

        orderReference = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartListAdminViewRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View");

        loadingDialog= new LoadingDialog(AdminNewOrder.this);

        back_to_admin_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminNewOrder.this, AdminCategory.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(orderReference,Orders.class).build();
        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final Orders orders) {
                        holder.order_user_name.setText(orders.getName());
                        holder.order_user_phone.setText(orders.getPhone());
                        holder.order_time_date.setText(orders.getDate()+"; "+orders.getTime());
                        holder.total_price.setText(orders.getTotalAmount()+ " ৳");
                        holder.user_city.setText(orders.getCity());
                        holder.user_dist.setText(orders.getDistrict());
                        holder.user_region.setText(orders.getRegion());
                        holder.user_house.setText(orders.getHouse());
                        holder.user_postal.setText(orders.getPostal());

                        holder.show_product_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();
                                Intent intent = new Intent(AdminNewOrder.this, AdminUserProducts.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });

                        holder.confirm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{
                                  "Yes",
                                  "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrder.this);
                                builder.setTitle("Have You Shipped this order products?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i==0){
                                            String uid = getRef(position).getKey();
                                            RemoveOrder(uid,orders.getName(),orders.getPhone(),orders.getCity(),orders.getDistrict(), orders.getRegion(),
                                                    orders.getPostal(),orders.getTotalAmount(),orders.getHouse());
                                        } else{finish();}
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        AdminOrdersViewHolder holder = new AdminOrdersViewHolder(view);
                        return holder;
                    }
                };
        order_list.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(final String uID, String name, String phone, String city, String district, String region, String postal, String totalAmount, String house) {

        String crntDate,crntTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        crntDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        crntTime = currentTime.format(calendar.getTime());

        loadingDialog.start();
        final DatabaseReference shippedRef = FirebaseDatabase.getInstance().getReference().child("Shipped Orders").child("Recent Order");
        Orders orders= new Orders(name,phone,totalAmount,city,district,region,house,postal,crntDate,crntTime,"shipped");
        shippedRef.child(uID).setValue(orders)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            RecordCustomerOrder(uID);
                            orderReference.child(uID).removeValue(); //Remove Customer Order from Orders DB.
                            cartListAdminViewRef.child(uID).removeValue(); //Remove Product From "Cart list">"Admin View".
                            loadingDialog.dismiss();
                            Toast.makeText(AdminNewOrder.this, "Order Shipped", Toast.LENGTH_LONG).show();
                        }
                        else {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminNewOrder.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();}
                    }
                });
    }

    private void RecordCustomerOrder(String uID) {
        //Current Data and Time...
        final String crntDate,crntTime;
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        crntDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        crntTime = currentTime.format(calendar.getTime());

        shippedRef = FirebaseDatabase.getInstance().getReference().child("Shipped Orders").child("All Order Records").child(uID);

        cartListReference = FirebaseDatabase.getInstance().getReference().
                child("Cart List").child("Admin View").child(uID).child("Products");

        cartListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ss: snapshot.getChildren()){
                    CartList cartList = ss.getValue(CartList.class);

                    shippedRef.child(cartList.getPid()).child(crntDate+";"+crntTime).setValue(cartList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}