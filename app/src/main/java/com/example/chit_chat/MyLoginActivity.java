package com.example.chit_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class MyLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail,editTextPassword;
    private Button buttonSignIn,buttonSignUp,forgot;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //full screen making code
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        setContentView(R.layout.activity_my_login);



        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);

        buttonSignIn=findViewById(R.id.SignUpButton);
        buttonSignUp=findViewById(R.id.buttonReset);
        forgot=findViewById(R.id.buttonForget);


        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        forgot.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.SignUpButton){
            String e=editTextEmail.getText().toString();
            String p=editTextPassword.getText().toString();

            if(!isValidEmail(e))
            {
                editTextEmail.setError("Enter a valid email.");
                editTextEmail.requestFocus();
                return;

            }
            if(!isValidPassword(p))
            {
                editTextPassword.setError("Enter a valid password.");
                editTextPassword.requestFocus();
                return;
            }

            signin(e,p);


        }

        if(view.getId()==R.id.buttonReset){

            Intent intent=new Intent(MyLoginActivity.this,SignUpActivity.class);
            startActivity(intent);


        }

        if(view.getId()==R.id.buttonForget){
            Log.d("Error checking","Reached");

            Intent i=new Intent(MyLoginActivity.this,forgetActivity.class);
            startActivity(i);


        }

    }

    public void signin(String email,String password)
    {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent i=new Intent(MyLoginActivity.this,MyLoginActivity.class);
                            Toast.makeText(MyLoginActivity.this, "Successfully Signed In!", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MyLoginActivity.this, "Error:"+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
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


    @Override
    protected void onStart() {
        super.onStart();

        if (user != null)
        {
            Intent i=new Intent(MyLoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}