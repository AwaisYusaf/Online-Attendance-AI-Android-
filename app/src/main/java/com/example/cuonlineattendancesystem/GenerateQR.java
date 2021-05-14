package com.example.cuonlineattendancesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerateQR extends AppCompatActivity {

    EditText LECTURE_ROOM;
    EditText LECTURE_TIME;
    EditText SUBJECT_CODE;
    Button GENERATE_QR_BTN;

    FirebaseFirestore DATABASE;
    FirebaseAuth AUTH;

    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_q_r);

        LECTURE_ROOM = (EditText) findViewById(R.id.room_no_generateQR);
        LECTURE_TIME = (EditText) findViewById(R.id.lecture_time_generateQR);
        SUBJECT_CODE = (EditText) findViewById(R.id.subjectId_generateQR);
        GENERATE_QR_BTN = (Button) findViewById(R.id.btn_generateQR);

        DATABASE = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();

        GENERATE_QR_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLecture();
            }
        });


    }

    private void addLecture(){
        final String mLectureRoom = LECTURE_ROOM.getText().toString();
        final String mLectureTime = LECTURE_TIME.getText().toString();
        final String mSubjectCode = SUBJECT_CODE.getText().toString();

        final Map<String,Object> lecture = new HashMap<>();
        lecture.put("lectureRoom",mLectureRoom);
        lecture.put("lectureTime",mLectureTime);
        lecture.put("subjectCode",mSubjectCode);


        pg = new ProgressDialog(this);
        pg.show();

        //Validating that the subject is added by the current user..

        DocumentReference dRef = DATABASE.collection("subject").document(mSubjectCode);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"task.isSuccessful()",Toast.LENGTH_LONG).show();
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        Map<String,Object> subject = snapshot.getData();
                        String uId = subject.get("userId").toString();
                        if (uId.equals(AUTH.getCurrentUser().getUid())){
                            Toast.makeText(getApplicationContext(),"Adding...",Toast.LENGTH_LONG).show();
                            //Adding lecture in lecture collection , Document Identity will be subjectCode+lecture room.
                            //Subject code : C123
                            //Lecture Room : LT3
                            //Document Reference : C123LT3
                            DocumentReference documentReference = DATABASE.collection("lecture").document(mSubjectCode+mLectureRoom);
                            documentReference.set(lecture).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Lecture Added Successfully",Toast.LENGTH_LONG).show();
                                    pg.dismiss();
                                    generateQR();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error: Lecture can't be added",Toast.LENGTH_LONG).show();
                                    pg.dismiss();
                                    return;
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"This subject is not added by current user",Toast.LENGTH_LONG).show();
                            pg.dismiss();
                            return;
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Error: Subject with this code does not exists",Toast.LENGTH_LONG).show();
                        pg.dismiss();
                        return;
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error: Session expired",Toast.LENGTH_LONG).show();
                    pg.dismiss();
            }
        });
    }

    private void generateQR(){
        String mLectureRoom = LECTURE_ROOM.getText().toString();
        String mLectureTime = LECTURE_TIME.getText().toString();
        String mSubjectCode = SUBJECT_CODE.getText().toString();

        String mQR_Data = mSubjectCode+mLectureRoom;

        // Convert this data (mQR_Data) into QR Code


        //....


        //After successful QR Code generation, sending QR Code to student.. Let's get back to teacher console
        pg.dismiss();
        Intent intent = new Intent(this,teacher_home.class);
        startActivity(intent);
    }

}