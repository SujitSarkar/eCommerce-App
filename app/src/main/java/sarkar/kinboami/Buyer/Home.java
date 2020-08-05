package sarkar.kinboami.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.paperdb.Paper;
import sarkar.kinboami.R;
import sarkar.kinboami.ViewHolder.ProductViewHolder;
import sarkar.kinboami.model.ProductDetails;
import sarkar.kinboami.prevalent.Prevalent;

public class Home extends AppCompatActivity {

    private DatabaseReference productRef;

    private SwipeRefreshLayout swipe_refresh_layout;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private AppBarConfiguration mAppBarConfiguration;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");


        Paper.init(this);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipe_refresh_layout.setRefreshing(false);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, CartActivity.class);
                startActivity(intent);
            }
        });


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        //Hooks...
        View headerView = navigationView.getHeaderView(0);
        TextView profileNameTextView = headerView.findViewById(R.id.user_profile_name);
        ImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        //Set profile picture and Name...
        profileNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {



            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id== R.id.nav_cart){
                    Intent intent = new Intent(Home.this,CartActivity.class);
                    startActivity(intent);
                }
                if (id== R.id.nav_search){
                    Intent intent =new Intent(Home.this, SearchProducts.class);
                    startActivity(intent);

                }
                if (id== R.id.nav_categories){

                }
                if (id == R.id.nav_setting){
                    Intent intent = new Intent(Home.this, Settings.class);
                    startActivity(intent);
                    finish();
                }
                if (id== R.id.nav_logout){
                    Paper.book().destroy();
                    Intent intent = new Intent(Home.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                drawer.closeDrawers();
                return false;
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        ShowProduct();
    }


    public void ShowProduct(){
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

                                    Intent intent = new Intent(Home.this, ProductDetailsActivity.class);
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

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}