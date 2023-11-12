package com.example.chit_chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView imageViewCircle;
    private EditText editTextUserName;
    private Button buttonUpdate;
    String image;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Uri imageUri;
    boolean imageControl=false;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        imageViewCircle=findViewById(R.id.SignUpprofilePicProfile);
        editTextUserName=findViewById(R.id.UserName);
        buttonUpdate=findViewById(R.id.UpdateButton);


        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        auth=FirebaseAuth.getInstance();
        firebaseUser= auth.getCurrentUser();

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        getUserInfo();

        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();

            }
        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextUserName.getText().toString().equals(""))
                {
                    editTextUserName.setError("Enter a User Name");
                    editTextUserName.requestFocus();
                    return;
                }
                updateProfile();

            }
        });
    }

    public void getUserInfo()
    {
        reference.child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name=snapshot.child("userName").getValue().toString();
                        image=snapshot.child("image").getValue().toString();

                        editTextUserName.setText(name);

                        if(image.equals("null"))
                        {
                            imageViewCircle.setImageResource(R.drawable.profile_logo1);
                        }
                        else
                        {
                            Picasso.get().load(image).into(imageViewCircle);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void updateProfile()
    {
        String userName=editTextUserName.getText().toString();
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
                                                    Toast.makeText(ProfileActivity.this, "Write to the database is successful.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProfileActivity.this, "Write to the database is successful. Some error:"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }
                            });

                        }
                    });





        }
        else {
            reference.child("Users").child(auth.getUid())
                    .child("image").setValue(image);
        }

        Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
        intent.putExtra("userName",userName);
        startActivity(intent);
        finish();



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
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl=true;


        }
        else
        {
            imageControl=false;
        }
    }
}