package sarkar.kinboami;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CartActivity extends AppCompatActivity {
    private TextView total_price;
    private RecyclerView cart_list;
    private RecyclerView.LayoutManager layoutManager;
    private Button order_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        total_price = findViewById(R.id.total_price);
        order_button = findViewById(R.id.order_button);

        cart_list = findViewById(R.id.cart_list);
        cart_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cart_list.setLayoutManager(layoutManager);
    }
}