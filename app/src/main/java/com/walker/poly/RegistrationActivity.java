package com.walker.poly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    private CircleImageView profileimage;
    private EditText username , fullname,email,password;
    private Button  regButton;
    private TextView question;


    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ProgressDialog loader ;
    private String onlineUserID = "";
    private Uri resultUri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        profileimage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.regfullname);
        password = findViewById(R.id.regpassword);
        email=findViewById(R.id.regEmail);
        question=findViewById(R.id.regQustion);
        regButton=findViewById(R.id.regbutton);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });


        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity2.class);
                startActivity(intent);
            }
        });
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  userName = username.getText().toString();
                String fullName = fullname.getText().toString();
                String emailText = email.getText().toString();
                String passText = password.getText().toString();


                if (TextUtils.isEmpty(userName)){

                    username.setError("username is required");

                } if (TextUtils.isEmpty(fullName)){

                    fullname.setError("fullname is required");


                }if (TextUtils.isEmpty(emailText)){

                    email.setError("email is required");


                }if (TextUtils.isEmpty(passText)){

                    password.setError("password is required");

                }


                if (resultUri == null){

                    Toast.makeText(RegistrationActivity.this, "Profile Image is required", Toast.LENGTH_SHORT).show();
                }

                else{

                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(emailText,passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Registration faild" + task.getException().toString(), Toast.LENGTH_SHORT).show();


                            } else {
                                String onlineUserID = mAuth.getCurrentUser().getUid();
                                reference = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);
                                Map hashMap = new HashMap();
                                hashMap.put("username", userName);
                                hashMap.put("fullname", fullName);
                                hashMap.put("id", onlineUserID);
                                hashMap.put("email", emailText);
                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(RegistrationActivity.this, "Detailes set Successfully", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Faild to upload data" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                        finish();
                                        //loader.dismiss();
                                    }
                                });

                                if (resultUri != null) {

                                    final StorageReference filPath = FirebaseStorage.getInstance().getReference().child("profile images").child(onlineUserID);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);


                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filPath.putBytes(data);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                            if (taskSnapshot.getMetadata().getReference() != null) {

                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map hashMap = new HashMap();
                                                        hashMap.put("profileimageurl", imageUrl);
                                                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(RegistrationActivity.this, "Profile Image added successfully", Toast.LENGTH_SHORT).show();
                                                                } else {

                                                                    Toast.makeText(RegistrationActivity.this, "Process faild" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        finish();

                                                    }
                                                });

                                            }
                                        }
                                    });


                                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();


                                }

                            }
                        }
                    });





                    }

                }


        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode ==RESULT_OK && data !=null){

            resultUri = data.getData();
            profileimage.setImageURI(resultUri);
            


        }else{
            Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_SHORT).show();
        }

    }
}