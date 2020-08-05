package sarkar.kinboami.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import sarkar.kinboami.Buyer.LoadingDialog;
import sarkar.kinboami.R;
import sarkar.kinboami.model.ProductDetails;

public class AdminAddNewProduct extends AppCompatActivity {
    private ImageView inputProductImage,back_to_admin_home;
    private TextInputLayout inputProductName, inputProductDescription, inputProductPrice;
    private Button addNewProduct;

    private String pName,pDescription,pPrice;
    private String categoryName, saveCurrentDate, saveCurrentTime;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String productRandomKey,downLoadImageURL;

    private StorageReference storageReference;
    private DatabaseReference productReference;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        addNewProduct = findViewById(R.id.add_new_product);

        back_to_admin_home = findViewById(R.id.back_to_admin_home);

        //create loading bar object...
        loadingDialog = new LoadingDialog(AdminAddNewProduct.this);

        //Received Category Intent from AdminCategory Activity...
        categoryName = getIntent().getExtras().get("category").toString();

        //Create DB storage Reference...
        storageReference = FirebaseStorage.getInstance().getReference();
        //Create DB Product Child Reference...
        productReference = FirebaseDatabase.getInstance().getReference().child("Products");

        back_to_admin_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AdminAddNewProduct.this, AdminCategory.class);
                startActivity(intent);
            }
        });

    }

    public void SelectProductImage(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                inputProductImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void AddProduct(View view) {
        if (validateProductName() && validateProductDescription() && validateProductPrice()) {
            if (imageUri != null) {
                UploadPicture();
            } else {
                Toast.makeText(this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();
            }
        }
        else {return;}
    }

    private void UploadPicture() {
        productRandomKey = UUID.randomUUID().toString();
        final StorageReference storageImageReference = storageReference.child("Product_Images/"+productRandomKey);

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

                                SaveProductDetailsToDB(imageURL);
                            }
                        });
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AdminAddNewProduct.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void SaveProductDetailsToDB(String imageURL) {
        //Get Image URL from UploadPicture() Method...
        downLoadImageURL = imageURL;

        loadingDialog.start();

        pName = inputProductName.getEditText().getText().toString();
        pDescription = inputProductDescription.getEditText().getText().toString();
        pPrice = inputProductPrice.getEditText().getText().toString();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ProductDetails productDetails = new ProductDetails(productRandomKey,pName,pDescription,pPrice,categoryName,saveCurrentDate,saveCurrentTime,downLoadImageURL);
        productReference.child(productRandomKey).setValue(productDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingDialog.dismiss();
                            Toast.makeText(AdminAddNewProduct.this, "Product Successfully Added", Toast.LENGTH_LONG).show();

                            Intent intent =new Intent(AdminAddNewProduct.this,AdminCategory.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminAddNewProduct.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateProductName(){
        String val = inputProductName.getEditText().getText().toString();
        if (val.isEmpty()){
            inputProductName.setError("Field can't be empty");
            return false;
        }
        else{
            inputProductName.setError(null);
            inputProductName.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateProductDescription(){
        String val = inputProductDescription.getEditText().getText().toString();
        if (val.isEmpty()){
            inputProductDescription.setError("Field can't be empty");
            return false;
        }
        else{
            inputProductDescription.setError(null);
            inputProductDescription.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateProductPrice(){
        String val = inputProductPrice.getEditText().getText().toString();
        if (val.isEmpty()){
            inputProductPrice.setError("Field can't be empty");
            return false;
        }
        else{
            inputProductPrice.setError(null);
            inputProductPrice.setErrorEnabled(false);
            return true;
        }
    }
}