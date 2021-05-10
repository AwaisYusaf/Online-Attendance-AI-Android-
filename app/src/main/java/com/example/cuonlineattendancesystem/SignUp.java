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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SignUp extends AppCompatActivity {
    EditText email;
    EditText pass;
    EditText confirmPass;
    TextView to_login;
    Button signUp;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        FileInputStream serviceAccount = null;
//        try {
//            serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleAuthCredential.fromStream(serviceAccount))
//                .build();
//
//        FirebaseApp.initializeApp(options);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.SignUpRegNo);
        pass = (EditText) findViewById(R.id.signUpPassword);
        confirmPass = (EditText) findViewById(R.id.signUpConfirmPassword);
        signUp = (Button) findViewById(R.id.button_SignUp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpClicked();
            }
        });
        to_login = (TextView) findViewById(R.id.login_link);
        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage();
            }
        });


    }
    private void startLoginPage(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void SignUpClicked(){
        String EMAIL = email.getText().toString();

        String PASSWORD = pass.getText().toString();

        String CONFIRM_PASS = confirmPass.getText().toString();

        if (PASSWORD.equals(CONFIRM_PASS)){
            if (PASSWORD.length()<8){
                pass.setError("Your password must be at least 8 characters.");
                return;
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                createAccount(EMAIL,PASSWORD);
            }
        }
        else {
            confirmPass.setError("Password must be same");
            return;
        }

    }
    private void createAccount(String EMAIL,String Password){
        mAuth.createUserWithEmailAndPassword(EMAIL,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Succeed",Toast.LENGTH_LONG).show();;
            }
        });
    }
}