package sarkar.kinboami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sarkar.kinboami.ViewHolder.AdminOrdersViewHolder;
import sarkar.kinboami.model.Orders;

public class AdminNewOrder extends AppCompatActivity {
    private ImageView back_to_admin_home;

    private RecyclerView order_list;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        back_to_admin_home = findViewById(R.id.back_to_admin_home);

        order_list = findViewById(R.id.order_list);
        order_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        order_list.setLayoutManager(layoutManager);

        back_to_admin_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminNewOrder.this,AdminCategory.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference().child("Orders");

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(orderReference,Orders.class).build();
        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull Orders orders) {
                        holder.order_user_name.setText(orders.getName());
                        holder.order_user_phone.setText(orders.getPhone());
                        holder.order_time_date.setText(orders.getDate()+"; "+orders.getTime());
                        holder.total_price.setText(orders.getTotalAmount()+ " ৳");
                        holder.user_city.setText(orders.getCity());
                        holder.user_dist.setText(orders.getDistrict());
                        holder.user_region.setText(orders.getRegion());
                        holder.user_house.setText(orders.getHouse());
                        holder.user_postal.setText(orders.getPostal());
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
}