package com.example.cuonlineattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    TextView signupLink;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText)  findViewById(R.id.LoginRegNo);
        password = (EditText) findViewById(R.id.loginPassword);
        login = (Button) findViewById(R.id.button_login);
        signupLink = (TextView) findViewById(R.id.sign_up_link);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndLogin();
            }
        });
    }


    private void checkAndLogin(){
        final String EMAIL = email.getText().toString();
        String PASSWORD = password.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent = new Intent(getApplicationContext(),Home.class);
                intent.putExtra("email",EMAIL);
                startActivity(intent);
            }
        });
    }

    private void signUp(){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }

}