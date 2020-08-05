package sarkar.kinboami.Buyer;

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
import android.widget.TextView;
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

import sarkar.kinboami.R;
import sarkar.kinboami.model.Users;
import sarkar.kinboami.prevalent.Prevalent;

public class Settings extends AppCompatActivity {
    private TextView close_settings_btn,update_account_settings_btn;
    private ImageView settings_profile_image;
    private Button profile_image_change_btn,security_question_btn;
    private TextInputEditText settings_full_name,settings_address;

    private Uri imageUri;
    private String checker = "";
    private StorageReference storageReference;
    private DatabaseReference userReference;
    LoadingDialog loadingDialog;

    private String onPhone = Prevalent.currentOnlineUser.getPhone();
    private String onName = Prevalent.currentOnlineUser.getName();
    private String onAddress = Prevalent.currentOnlineUser.getAddress();
    private String onImage = Prevalent.currentOnlineUser.getImage();
    private String onPassword = Prevalent.currentOnlineUser.getPassword();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        close_settings_btn = findViewById(R.id.close_settings_btn);
        update_account_settings_btn = findViewById(R.id.update_account_settings_btn);
        settings_profile_image = findViewById(R.id.settings_profile_image);
        profile_image_change_btn = findViewById(R.id.profile_image_change_btn);
        security_question_btn = findViewById(R.id.security_question_btn);

        settings_full_name = findViewById(R.id.settings_full_name);
        settings_address = findViewById(R.id.settings_address);
        
        //Create Database Reference;
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        //create loading bar object...
        loadingDialog = new LoadingDialog(Settings.this);

        ShowUserInfoFromDB();

        close_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Settings.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        update_account_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateName() && validateAddress()){
                    if (checker == "checked"){
                        UploadUserPhoto();
                    }else {
                        UploadUserDetails(onImage);
                    }
                }
                else { return; }
            }
        });

        profile_image_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "checked";
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
            }
        });

        security_question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ResetPassword.class);
                intent.putExtra("check","settings");
                startActivity(intent);
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
                settings_profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ShowUserInfoFromDB() {
        Picasso.get().load(onImage).into(settings_profile_image);
        settings_full_name.setText(onName);
        settings_address.setText(onAddress);
    }

    private void UploadUserDetails(String imageUrl) {
        loadingDialog.start();

        String nameET= settings_full_name.getText().toString();
        String addressET= settings_address.getText().toString();

        final Users users = new Users(nameET,onPhone,onPassword,imageUrl,addressET);
        userReference.child(onPhone).setValue(users)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingDialog.dismiss();
                            //Update Prevalent data....
                            Prevalent.currentOnlineUser = users;
                            Toast.makeText(Settings.this, "Profile Successfully Updated", Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(Settings.this,Home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(Settings.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void UploadUserPhoto() {
        String imageName = onPhone;
        final StorageReference storageImageReference = storageReference.child("User_Profile_Photo/"+imageName);

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

                                UploadUserDetails(imageURL);
                            }
                        });
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Settings.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }




    private boolean validateName(){
        String val = settings_full_name.getText().toString();
        if (val.isEmpty()){
            settings_full_name.setError("Field can't be empty");
            return false;
        }
        else{
            settings_full_name.setError(null);
            return true;
        }
    }
    private boolean validateAddress(){
        String val = settings_address.getText().toString();
        if (val.isEmpty()){
            settings_address.setError("Field can't be empty");
            return false;
        }
        else{
            settings_address.setError(null);
            return true;
        }
    }
}