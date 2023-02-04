package com.walker.poly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AskActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner spinner;
    private EditText questionBox;
    private ImageView imageView;
    private Button cancelbtn,postbtn;
    private  String  askedbyName ="" ;
    private DatabaseReference askedbyRef;
    private ProgressDialog loader ;
    private  String myurl = "";
    StorageTask  uploadTask ;
    StorageReference storageReference ;
    private Uri imageUri ;
    private FirebaseAuth mAauth ;
    private FirebaseUser  mUser;
    private  String onlinUserID = "" ;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ask);
        toolbar = findViewById(R.id.qs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ask Question");

        spinner = findViewById(R.id.spinner);
        questionBox = findViewById(R.id.question_text);
        imageView = findViewById(R.id.question_image);
        cancelbtn = findViewById(R.id.cancel);
        postbtn = findViewById(R.id.save_post);

        loader = new ProgressDialog(this);
        mAauth = FirebaseAuth.getInstance();
        mUser = mAauth.getCurrentUser();
        onlinUserID  = mUser.getUid();
        askedbyRef = FirebaseDatabase.getInstance().getReference("users").child(onlinUserID);
        askedbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                askedbyName= snapshot.child("fullname").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("questions");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.topics));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem().equals("Select topic")){

                    Toast.makeText(AskActivity.this, "Please Select a valid topic", Toast.LENGTH_SHORT).show();
                    
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,1);

        }
    });
    cancelbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();

        }
    });
    postbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PerformDirectAction();

        }
    });





    }
    String getQuestionText(){
    return  questionBox.getText().toString().trim();

    }
    String getTopic(){
        return  spinner.getSelectedItem().toString();
    }
    String mDate = DateFormat.getDateInstance().format(new Date());
    DatabaseReference  ref = FirebaseDatabase.getInstance().getReference("questions posts");



    private void PerformDirectAction() {
        if (getQuestionText().isEmpty()){
            questionBox.setError("Question required");


        }else if (getTopic().equals("Select topic")){

            Toast.makeText(this, "Select Valid Topic", Toast.LENGTH_SHORT).show();
        }
        else if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageUri == null ){
            uploadQuestionwithoutimage();


        }else  if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageUri != null){
            uploadQuestionwithimage();

         }
    }

    private  void startLoader(){
        loader.setMessage("posting your question");
        loader.setCanceledOnTouchOutside(false);
        loader.show();


    }
    private String getFileExtension(Uri uri ){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private  void  uploadQuestionwithoutimage(){
        startLoader();
        String postId = ref.push().getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("postid",postId);
        hashMap.put("question",getQuestionText());
        hashMap.put("publisher",onlinUserID);
        hashMap.put("topic",getTopic());
        hashMap.put("askedby",askedbyName);
        hashMap.put("date",mDate);

        ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AskActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                    startActivity(new Intent(AskActivity.this,HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(AskActivity.this, "could not upload image "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                   loader.dismiss();

                }
            }
        });

    }
    private void uploadQuestionwithimage(){
        startLoader();
        final StorageReference fileref;
        fileref =  storageReference.child(System.currentTimeMillis() + '.' + getFileExtension(imageUri));
        uploadTask = fileref.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isCanceled()){
                    throw   task.getException();

                }
                return fileref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){

                    Uri download = (Uri) task.getResult();
                    myurl = download.toString();
                    String postId = ref.push().getKey();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("postid",postId);
                    hashMap.put("question",getQuestionText());
                    hashMap.put("publisher",onlinUserID);
                    hashMap.put("topic",getTopic());
                    hashMap.put("askedby",askedbyName);
                    hashMap.put("questionimage",myurl);
                    hashMap.put("date",mDate);

                    ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(AskActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                startActivity(new Intent(AskActivity.this,HomeActivity.class));
                                finish();


                            }else {
                                Toast.makeText(AskActivity.this, "could not upload image "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                loader.dismiss();

                            }
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AskActivity.this, "Failed to upload the question", Toast.LENGTH_SHORT).show();

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== -1 && resultCode ==RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);


        }
    }
}