package sarkar.kinboami.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sarkar.kinboami.Buyer.LoadingDialog;
import sarkar.kinboami.R;
import sarkar.kinboami.model.ProductDetails;

public class AdminMaintainProduct extends AppCompatActivity {

    private ImageView product_image_maintain,back_to_maintain_home;
    private TextInputEditText product_name_maintain,product_description_maintain,product_price_maintain;
    private Button apply_change_btn,delete_product_btn;
    private String pID,pName,pDescription,pPrice,pImage,pCategory;

    private DatabaseReference productRef;
    private StorageReference storageReference;
    private Uri imageUri;
    LoadingDialog loadingDialog;

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

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        storageReference = FirebaseStorage.getInstance().getReference();

        loadingDialog = new LoadingDialog(AdminMaintainProduct.this);

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

        back_to_maintain_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMaintainProduct.this, AdminProductView.class);
                startActivity(intent);
                finish();
            }
        });

        apply_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri!=null){
                    UploadPicture();
                }
                else {
                    UpdateProductDetails(pImage);
                }
            }
        });

        delete_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{
                        "Yes"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProduct.this);
                builder.setTitle("You want to delete this product?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0){
                            DeleteProduct();
                        }
                    }
                });
                builder.show();
            }
        });

        product_image_maintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                product_image_maintain.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void DeleteProduct() {

        loadingDialog.start();
        productRef.child(pID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        storageReference.child("Product_Images").child(pID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(AdminMaintainProduct.this, "Product Deleted Successfully", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(AdminMaintainProduct.this, AdminProductView.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(AdminMaintainProduct.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                        loadingDialog.dismiss();
                        Toast.makeText(AdminMaintainProduct.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        });

    }

    private void UploadPicture() {

        final StorageReference storageImageReference = storageReference.child("Product_Images/"+pID);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        storageImageReference.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading... " + (int)progress+"%");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = String.valueOf(uri);

                                UpdateProductDetails(imageURL);
                            }
                        });
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AdminMaintainProduct.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void UpdateProductDetails(String imageUrl) {
        String editedName = product_name_maintain.getText().toString();
        String editedDes = product_description_maintain.getText().toString();
        String editedPrice = product_price_maintain.getText().toString();

        String updatedDate,updatedTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        updatedDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        updatedTime = currentTime.format(calendar.getTime());

        ProductDetails productDetails = new ProductDetails(pID,editedName,editedDes,editedPrice,pCategory,updatedDate,updatedTime,imageUrl);
        loadingDialog.start();
        productRef.child(pID).setValue(productDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingDialog.dismiss();
                            Toast.makeText(AdminMaintainProduct.this, "Applied Changes", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminMaintainProduct.this,AdminProductView.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminMaintainProduct.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}