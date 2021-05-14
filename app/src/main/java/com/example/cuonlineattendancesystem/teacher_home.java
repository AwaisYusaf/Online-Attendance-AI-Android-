package com.example.cuonlineattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.TimeAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class teacher_home extends AppCompatActivity {
    TextView NAME;
    TextView EMAIL;
    TextView EMP_ID;
    Button LOGOUT_BTN;
    Button ADD_SUBJECT_BTN;
    Button GENERATE_QR_BTN;

    FirebaseAuth mAUTH;
    FirebaseFirestore DATABASE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        NAME = (TextView) findViewById(R.id.userName_tv_TeacherHome);
        EMAIL = (TextView) findViewById(R.id.email_tv_Teacherhome);
        EMP_ID = (TextView) findViewById(R.id.officialId_tv_teacherHome);

        mAUTH = FirebaseAuth.getInstance();
        DATABASE = FirebaseFirestore.getInstance();
        LOGOUT_BTN = (Button) findViewById(R.id.button_logout_TeacherHome);
        ADD_SUBJECT_BTN = (Button) findViewById(R.id.addSubjectBtn_teacherHome);
        GENERATE_QR_BTN = (Button) findViewById(R.id.generateQRBtn_teacherHome);



        loadData();

        LOGOUT_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        ADD_SUBJECT_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject();
            }
        });

        GENERATE_QR_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

    }

    private void loadData(){
        if (mAUTH!=null){
            DocumentReference dRef  = DATABASE.collection("teacher").document(mAUTH.getCurrentUser().getUid());
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()){
                            Map<String,Object> data= documentSnapshot.getData();
                            NAME.setText("Welcome, "+data.get("name").toString());
                            EMAIL.setText("Your Email: "+mAUTH.getCurrentUser().getEmail());
                            EMP_ID.setText("Employee ID:"+data.get("empId").toString());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Error: Unable to load teacher profile",Toast.LENGTH_LONG).show();
        }
    }

    private void addSubject(){
        Intent intent = new Intent(this,AddSubject.class);
        startActivity(intent);
    }
    private  void LogOut(){
        mAUTH.signOut();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void generateQRCode(){
        Intent intent = new Intent(this,GenerateQR.class);
        startActivity(intent);
    }
}