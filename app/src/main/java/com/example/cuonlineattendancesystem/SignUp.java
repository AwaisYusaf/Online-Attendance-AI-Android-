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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText FULL_NAME;
    EditText OFFICIAL_ID;
    EditText EMAIL;
    EditText PASSWORD;
    EditText CONFIRM_PASSWORD;
    RadioGroup RADIO_BTN_GROUP;
    RadioButton STUDENT_RADIO_BTN;
    RadioButton TEACHER_RADIO_BTN;
    RadioButton SELECTED_RADIO_BTN;
    TextView SEND_TO_LOGIN;
    Button SIGNUP_BTN;
    ProgressBar PROGRESS_BAR;
    FirebaseAuth M_AUTH_FIREBASE;
    FirebaseFirestore DATABASE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        M_AUTH_FIREBASE = FirebaseAuth.getInstance();
        DATABASE = FirebaseFirestore.getInstance();

        FULL_NAME = (EditText) findViewById(R.id.signUpFullName);
        OFFICIAL_ID = (EditText) findViewById(R.id.officialId);
        OFFICIAL_ID.setEnabled(false);
        EMAIL = (EditText) findViewById(R.id.signUpEmail);
        PASSWORD = (EditText) findViewById(R.id.signUpPassword);
        CONFIRM_PASSWORD = (EditText) findViewById(R.id.signUpConfirmPassword);

        RADIO_BTN_GROUP = (RadioGroup) findViewById(R.id.sign_up_radioBtnGroup);
        STUDENT_RADIO_BTN = (RadioButton) findViewById(R.id.signUp_student_radioButton);
        TEACHER_RADIO_BTN = (RadioButton) findViewById(R.id.signUp_teacher_radioButton);

        SIGNUP_BTN = (Button) findViewById(R.id.button_SignUp);
        SEND_TO_LOGIN = (TextView) findViewById(R.id.login_link);

        PROGRESS_BAR = (ProgressBar) findViewById(R.id.progressBar2);
        PROGRESS_BAR.setVisibility(View.INVISIBLE);

        STUDENT_RADIO_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        TEACHER_RADIO_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });


        SIGNUP_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });


        SEND_TO_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage();
            }
        });


    }

    private void update() {
        int selectedId = RADIO_BTN_GROUP.getCheckedRadioButtonId();

        SELECTED_RADIO_BTN = (RadioButton) findViewById(selectedId);
        OFFICIAL_ID.setEnabled(true);
        if (SELECTED_RADIO_BTN.getTag().equals("teacher")) {
            OFFICIAL_ID.setHint("Employee ID");
        } else if (SELECTED_RADIO_BTN.getTag().equals("student")) {
            OFFICIAL_ID.setHint("Registration Number");
        }
    }


    private void startLoginPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void SignUp() {

        //GETTING DATA FROM FIELDS

        String mFullName = FULL_NAME.getText().toString();
        String mCategory = SELECTED_RADIO_BTN.getTag().toString();
        String mOfficial_ID = OFFICIAL_ID.getText().toString();
        String mEmail = EMAIL.getText().toString();
        String mPassword = PASSWORD.getText().toString();
        String mConfirmPassword = CONFIRM_PASSWORD.getText().toString();

        // VALIDATING DATA

        if (mFullName.equals("")) {
            FULL_NAME.setError("Username is required");
            return;
        }
        if (mOfficial_ID.equals("")) {
            OFFICIAL_ID.setError(mCategory.equals("teacher") ? "Employee ID is required" : "Registration Number is required");
            return;
        }
        if (mEmail.equals("")) {
            EMAIL.setError("Email is required");
            return;
        }
        if (mPassword.length() < 8) {
            PASSWORD.setError("Password must be equal of greater than 8 characters");
            return;
        }
        if (!mPassword.equals(mConfirmPassword)) {
            CONFIRM_PASSWORD.setError("Passwords must be same");
            return;
        }


        // CHECKING AND CREATING ACCOUNT
        if (mCategory.equals("teacher")) {
            CreateTeacherAccount(mFullName, mOfficial_ID, mEmail, mPassword);
        } else if (mCategory.equals("student")) {
            CreateStudentAccount(mFullName, mOfficial_ID, mEmail, mPassword);
        }
    }

    private void CreateTeacherAccount(final String fullName, final String empId, String email, String password) {
        disablePage();
        PROGRESS_BAR.setVisibility(View.VISIBLE);
        createAccount(email, password);
        M_AUTH_FIREBASE.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Map<String, Object> user = new HashMap<>();
                user.put("name", fullName);
                user.put("category", "teacher");
                user.put("empId", empId);

                DocumentReference documentReference = DATABASE.collection("teacher").document(M_AUTH_FIREBASE.getCurrentUser().getUid());

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        PROGRESS_BAR.setVisibility(View.INVISIBLE);
                        enablePage();
                        LogIn("teacher");
                        Toast.makeText(getApplicationContext(), "Account Successfully Created", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PROGRESS_BAR.setVisibility(View.INVISIBLE);
                        enablePage();
                        Toast.makeText(getApplicationContext(), "Error: Teacher data saving error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                PROGRESS_BAR.setVisibility(View.INVISIBLE);
                enablePage();
                Toast.makeText(getApplicationContext(), "Session Expired", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void CreateStudentAccount(final String fullName, final String regNo, String email, String password) {
        disablePage();
        PROGRESS_BAR.setVisibility(View.VISIBLE);
        createAccount(email, password);
        M_AUTH_FIREBASE.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Map<String, Object> user = new HashMap<>();
                user.put("name", fullName);
                user.put("category", "student");
                user.put("regNo", regNo);

                DocumentReference documentReference = DATABASE.collection("student").document(M_AUTH_FIREBASE.getCurrentUser().getUid());

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        PROGRESS_BAR.setVisibility(View.INVISIBLE);
                        enablePage();
                        LogIn("student");
                        Toast.makeText(getApplicationContext(), "Account Successfully created", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enablePage();
                        Toast.makeText(getApplicationContext(), "Error: Student data saving error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enablePage();
                Toast.makeText(getApplicationContext(), "Session Expired", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void disablePage() {
        FULL_NAME.setEnabled(false);
        OFFICIAL_ID.setEnabled(false);
        EMAIL.setEnabled(false);
        PASSWORD.setEnabled(false);
        CONFIRM_PASSWORD.setEnabled(false);
        RADIO_BTN_GROUP.setEnabled(false);
        STUDENT_RADIO_BTN.setEnabled(false);
        TEACHER_RADIO_BTN.setEnabled(false);
        SIGNUP_BTN.setEnabled(false);
        SEND_TO_LOGIN.setEnabled(false);
    }

    private void enablePage() {
        FULL_NAME.setEnabled(true);
        OFFICIAL_ID.setEnabled(true);
        EMAIL.setEnabled(true);
        PASSWORD.setEnabled(true);
        CONFIRM_PASSWORD.setEnabled(true);
        RADIO_BTN_GROUP.setEnabled(true);
        STUDENT_RADIO_BTN.setEnabled(true);
        TEACHER_RADIO_BTN.setEnabled(true);
        SIGNUP_BTN.setEnabled(true);
        SEND_TO_LOGIN.setEnabled(true);
    }


    private void createAccount(String EMAIL, String Password) {
        M_AUTH_FIREBASE.createUserWithEmailAndPassword(EMAIL, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                M_AUTH_FIREBASE.signOut();
                return;
            }
        });
    }

    private void LogIn(String category) {
        if (M_AUTH_FIREBASE.getCurrentUser() != null) {
            if (category.equals("student")) {
                Intent intent = new Intent(this, student_home.class);
                startActivity(intent);
            }
            else if (category.equals("teacher")){
                Intent intent = new Intent(this, teacher_home.class);
                startActivity(intent);
            }
        }
    }
}