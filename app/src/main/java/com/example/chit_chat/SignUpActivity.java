package com.example.chit_chat;

import androidx.annotation.NonNull;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    CircleImageView imageView;
    private EditText editTextEmailSignUp,editTextPasswordSignUp,editTextuserNameSignUp;
    private Button buttonRegister;
    boolean imageControl=false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen making code
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }



        setContentView(R.layout.activity_sign_up);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        imageView=findViewById(R.id.SignUpprofilePic);
        editTextEmailSignUp=findViewById(R.id.signupEMail);
        editTextPasswordSignUp=findViewById(R.id.signUpPassword);
        editTextuserNameSignUp=findViewById(R.id.signUpUserName);
        buttonRegister=findViewById(R.id.SignUpButton);

        imageView.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.SignUpprofilePic)
        {

            imageChooser();

        }

        if(view.getId()==R.id.SignUpButton)
        {
            String email=editTextEmailSignUp.getText().toString();
            String pass=editTextPasswordSignUp.getText().toString();
            String userName=editTextuserNameSignUp.getText().toString();

            if(!isValidEmail(email))
            {
                editTextEmailSignUp.setError("Enter a valid email");
                editTextEmailSignUp.requestFocus();
                return;
            }
            if(!isValidPassword(pass))
            {
                editTextPasswordSignUp.setError("Enter a valid password.");
                editTextPasswordSignUp.requestFocus();
                return;
            }

            if(userName.equals(""))
            {
                editTextuserNameSignUp.setError("Enter a User name.");
                editTextuserNameSignUp.requestFocus();
                return;
            }



            signup(email,pass,userName);



        }

    }

    private void signup(String email, String pass, String userName) {

        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            reference.child("Users").child(auth.getUid())
                                    .child("userName").setValue(userName);

                            if(imageControl)
                            {
                                //For diffrent pictures ,we need to have different location
                                //to identify. That's why we will create UUID to get unique ID of image
                                //for uploading image everytime.

                                UUID randomID=UUID.randomUUID();

                                //In this location the photo will be uploaded.
                                String imageName="image/"+randomID+".jpg";

                                //Putting the file in imageName location name.
                                storageReference.child(imageName).putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                //getting the reference of the uploaded image
                                                StorageReference myStorageRef= firebaseStorage.getReference(imageName);

                                                //Getting the URI of the image.
                                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        //This the URl of the saved image
                                                        //We will convert it to String so that
                                                        //We can store it in the real-time database to retrieve
                                                        //and show the image when needed

                                                        String filePath=uri.toString();
                                                        //Keeping the link in the real-time databse so that we can retrieve
                                                        //it whenever needed

                                                        reference.child("Users").child(auth.getUid())
                                                                .child("image").setValue(filePath)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(SignUpActivity.this, "Write to the database is successful.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(SignUpActivity.this, "Write to the database is successful. Some error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                    }
                                                });

                                            }
                                        });





                            }
                            else {
                                reference.child("Users").child(auth.getUid())
                                        .child("image").setValue("null");
                            }

                            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                            finish();



                        }
                        else {

                            Toast.makeText(SignUpActivity.this, "Error:"+
                                    task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    public void imageChooser()
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(imageView);
            imageControl=true;


        }
        else
        {
            imageControl=false;
        }
    }

    public boolean isValidEmail(String email) {
        if (email=="")
        {
            return false;
        }
        // Use the android.util.Patterns.EMAIL_ADDRESS pattern for email validation
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        if(password=="")
        {
            return false;
        }
        // Define your password criteria
        int minLength = 6; // Minimum password length

        // Check if the password meets the criteria
        return password.length() >= minLength;
    }




}