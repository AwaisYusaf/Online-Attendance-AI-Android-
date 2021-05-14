package com.example.cuonlineattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSubject extends AppCompatActivity {
    EditText SUBJECT_NAME;
    EditText SUBJECT_CODE;
    Button ADD_SUBJECT_BTN;
    TextView CANCEL_LINK;
    ProgressBar P_Bar;


    FirebaseAuth mAUTH_FIREBASE;
    FirebaseFirestore DATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        SUBJECT_NAME = (EditText) findViewById(R.id.add_subject_name);
        SUBJECT_CODE = (EditText) findViewById(R.id.add_subject_code);
        ADD_SUBJECT_BTN = (Button) findViewById(R.id.add_subject_btn);
        CANCEL_LINK = (TextView) findViewById(R.id.backToTeacherConsole);

        mAUTH_FIREBASE = FirebaseAuth.getInstance();
        DATABASE = FirebaseFirestore.getInstance();
        P_Bar = (ProgressBar) findViewById(R.id.addSubjectProgressBar);
        P_Bar.setVisibility(View.INVISIBLE);

        ADD_SUBJECT_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject();
            }
        });


        CANCEL_LINK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });



    }
    private void addSubject(){
        final String mSubjectName = SUBJECT_NAME.getText().toString();
        final String mSubjectCode = SUBJECT_CODE.getText().toString();

        if (mSubjectCode.equals("")){
            SUBJECT_CODE.setError("Subject code is required");
            return;
        }
        if (mSubjectName.equals("")){
            SUBJECT_NAME.setError("Subject name is required");
            return;
        }
        P_Bar.setVisibility(View.VISIBLE);


        final DocumentReference teacher = DATABASE.collection("teacher").document(mAUTH_FIREBASE.getCurrentUser().getUid());
        teacher.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String teacherId = document.getData().get("empId").toString();
                    Map<String,Object> subject = new HashMap<>();
                    subject.put("subjectCode",mSubjectCode);
                    subject.put("subjectName",mSubjectName);
                    subject.put("userId",mAUTH_FIREBASE.getCurrentUser().getUid());
                    subject.put("empId",teacherId);

                    //Creating new collection , Adding a document with name = subject id in Subject collection.
                    DocumentReference dRef = DATABASE.collection("subject").document(mSubjectCode);
                    dRef.set(subject).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Subject Added Successfully",Toast.LENGTH_LONG).show();
                            returnToTeacher();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error adding subject",Toast.LENGTH_LONG).show();
                        }
                    });
                    P_Bar.setVisibility(View.INVISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error adding subject",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void returnToTeacher(){
        P_Bar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this,teacher_home.class);
        startActivity(intent);
    }

    private void cancel(){
        Intent intent = new Intent(this,teacher_home.class);
        startActivity(intent);
    }
}