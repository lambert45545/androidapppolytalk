package com.walker.poly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import  android.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity2 extends AppCompatActivity {
    private TextView question;
    private EditText emailEd,passworded;
    private Button login;
    private ProgressDialog loader;
    private FirebaseAuth  mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        question=findViewById(R.id.logQustion);
        emailEd=findViewById(R.id.logemail);
        passworded=findViewById(R.id.logpassword);
        login=findViewById(R.id.logbutton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity2.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEd.getText().toString();
                String password = passworded.getText().toString();
                if(TextUtils.isEmpty(email)){
                    emailEd.setError("Email is required");


                } if(TextUtils.isEmpty(password)){
                    passworded.setError("Password is required");

                }else {

                loader.setMessage("Login in progress");
                loader.setCanceledOnTouchOutside(false);
                //loader.show();


                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity2.this, "Login is successful. loged  in as"+ mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity2.this,HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Toast.makeText(LoginActivity2.this, "Login faild" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                }


            }
        });

    }

}