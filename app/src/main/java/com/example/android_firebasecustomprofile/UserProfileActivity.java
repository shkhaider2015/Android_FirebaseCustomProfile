package com.example.android_firebasecustomprofile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class UserProfileActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPassword, mPhoneNo;
    private Button mUpdate;
    private ImageView mProfilePic;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mFullName = findViewById(R.id.fullname_profile);
        mEmail = findViewById(R.id.email_profile);
        mPassword = findViewById(R.id.password_profile);
        mPhoneNo = findViewById(R.id.phoneno_profile);
        mUpdate = findViewById(R.id.update_profile);
        mProfilePic = findViewById(R.id.profile_pic_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


    }

    private void loadProfile()
    {
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Users");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                User user = dataSnapshot.getValue(User.class);

                mFullName.setText(user.mFullName);
                mEmail.setText(user.mEmail);
                mPhoneNo.setText(user.mPhoneNo);
                mPassword.setText("********");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
