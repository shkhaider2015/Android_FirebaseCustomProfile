package com.example.android_firebasecustomprofile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mProfilePic;
    private EditText mFullName, mEmail, mPassword, mPhoneNo;
    private Button mSignUp;
    private TextView mBackToSignIn;

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private Uri mURIOfProfilePic;
    private String mURLOfProfilePic;

    private static final int CHOOSE_IMAGE = 101;
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");


        mProfilePic = findViewById(R.id.image_profile);
        mFullName = findViewById(R.id.fullname_signup);
        mEmail = findViewById(R.id.email_signup);
        mPassword = findViewById(R.id.password_signup);
        mPhoneNo = findViewById(R.id.phoneno_signup);
        mSignUp = findViewById(R.id.button_signup);
        mBackToSignIn = findViewById(R.id.prob1_signup);

        mProfilePic.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mBackToSignIn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.image_profile:
                showImageChooser();
                break;
            case R.id.button_signup:
                saveUserInformation();
                break;
            case R.id.prob1_signup:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }

   private void showImageChooser()
   {
       Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), CHOOSE_IMAGE);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            mURIOfProfilePic = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mURIOfProfilePic);
                mProfilePic.setImageBitmap(bitmap);
                uploadImageToFirebase();


            }catch (IOException e)
            {
                Log.d(TAG, "onActivityResult: " +e.getMessage());
            }
        }
    }

    private void uploadImageToFirebase()
    {

        if(mURIOfProfilePic != null)
        {
            mStorageReference.putFile(mURIOfProfilePic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mStorageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {

                                            mURLOfProfilePic = uri.toString();
                                            Log.d(TAG, "Download URI onSuccess: " + mURLOfProfilePic);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d(TAG, "Download Uri onFailure: " + e.getMessage());
                                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "Storage Reference onFailure: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }

    private void saveUserInformation()
    {
        final String sFullName, sEmail, sPhoneNo, sPassword;

        sFullName = mFullName.getText().toString().trim();
        sEmail = mEmail.getText().toString().trim();
        sPhoneNo = mPhoneNo.getText().toString().trim();
        sPassword = mPassword.getText().toString().trim();

        if(sFullName.isEmpty())
        {
            mFullName.setError("Full Name is required");
            mFullName.requestFocus();
            return;
        }
        if(sEmail.isEmpty())
        {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }
        if(sPhoneNo.isEmpty())
        {
            mPhoneNo.setError("Phone No. is required");
            mPhoneNo.requestFocus();
            return;
        }
        if(sPassword.isEmpty())
        {
            mPassword.setError("Password. is required");
            mPassword.requestFocus();
            return;
        }

        if(sFullName.length() > 20)
        {
            mFullName.setError("Full Name must be less than 20");
            mFullName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches())
        {
            mEmail.setError("Email is not correct");
            mEmail.requestFocus();
            return;
        }

        if(sPhoneNo.length() < 10)
        {
            mPhoneNo.setError("Phone No is not correct");
            mPhoneNo.requestFocus();
            return;
        }
        if(sPassword.length() < 6 )
        {
            mPassword.setError("Password should more than 6");
            mPassword.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(sEmail, sPassword )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            User user = new User(
                              sFullName,
                              sEmail,
                              sPhoneNo
                            );

                           
                        }

                    }
                });




    }
}
