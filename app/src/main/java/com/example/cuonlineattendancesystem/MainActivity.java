package com.example.cuonlineattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;

    TextView signupLink;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    RadioButton teacher_radioButton;
    RadioButton student_radioButton;
    RadioButton selectedRadioBtn;
    RadioGroup radioButtonGroup;

    boolean isTeacher = true;
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
        database = FirebaseFirestore.getInstance();
        mAuth.signOut();
        progressBar.setVisibility(View.INVISIBLE);
        radioButtonGroup = (RadioGroup) findViewById(R.id.radio_btn_group);
        teacher_radioButton = (RadioButton) findViewById(R.id.teacher_Radio_btn);
        student_radioButton = (RadioButton) findViewById(R.id.student_Radio_btn);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        progressBar.setVisibility(View.INVISIBLE);
        enablePage();
        clearPage();
    }

    private void checkAndLogin(){
        final String EMAIL = email.getText().toString();
        String PASSWORD = password.getText().toString();
        if (EMAIL.equals("")){
            email.setError("Email is required");
        }
        else if (PASSWORD.equals("")){
            password.setError("Password is required");
        }


        int selectedId = radioButtonGroup.getCheckedRadioButtonId();
        if (selectedId==-1){
            Toast.makeText(getApplicationContext(),"Please select a category",Toast.LENGTH_LONG).show();
            return;
        }

        selectedRadioBtn = (RadioButton) findViewById(selectedId);
        progressBar.setVisibility(View.VISIBLE);
        disablePage();

        if (selectedRadioBtn.getTag().toString().equals("teacher")){
            logInAsTeacher(EMAIL,PASSWORD);
        }
        else if(selectedRadioBtn.getTag().toString().equals("student")){
            loginStudent(EMAIL,PASSWORD);
        }
    }

    private void disablePage(){
        email.setEnabled(false);
        password.setEnabled(false);
        radioButtonGroup.setEnabled(false);
        login.setEnabled(false);
        signupLink.setEnabled(false);
        teacher_radioButton.setEnabled(false);
        student_radioButton.setEnabled(false);
    }

    private void enablePage(){
        email.setEnabled(true);
        password.setEnabled(true);
        radioButtonGroup.setEnabled(true);
        login.setEnabled(true);
        signupLink.setEnabled(true);
        teacher_radioButton.setEnabled(true);
        student_radioButton.setEnabled(true);
    }
    private void clearPage(){
        email.setText("");
        password.setText("");
    }


    private void logInAsTeacher(final String EMAIL,String PASSWORD){
            mAuth.signInWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (mAuth.getCurrentUser()!=null){
                    DocumentReference dRef = database.collection("teacher").document(mAuth.getCurrentUser().getUid());
                    dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), teacher_home.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            enablePage();
                            Toast.makeText(getApplicationContext(),"Error Signing In...",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    enablePage();
                    Toast.makeText(getApplicationContext(),"Error Signing In...",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
    }


    private void loginStudent(final String EMAIL,String PASSWORD){

            mAuth.signInWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (mAuth.getCurrentUser()!=null){
                    DocumentReference dRef = database.collection("student").document(mAuth.getCurrentUser().getUid());
                    dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(), student_home.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            enablePage();
                            Toast.makeText(getApplicationContext(),"Error Signing In...",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    enablePage();
                    Toast.makeText(getApplicationContext(),"Error Signing In...",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
    }


    private void signUp(){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }
}