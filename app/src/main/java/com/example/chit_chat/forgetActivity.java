package com.example.chit_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class forgetActivity extends AppCompatActivity {
    private EditText editTextForget;
    private Button buttonForget;

    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        editTextForget=findViewById(R.id.editTextEmail);
        buttonForget=findViewById(R.id.buttonReset);
        auth=FirebaseAuth.getInstance();



        buttonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e=editTextForget.getText().toString();
                if(!isValidEmail(e))
                {
                    editTextForget.setError("Enter a valid email address.");
                    editTextForget.requestFocus();
                    return;

                }
                passwordreset(e);


            }
        });


    }

    private void passwordreset(String email)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(forgetActivity.this, "An email send to your email.", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(forgetActivity.this, "There is a problem.", Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    public boolean isValidEmail(String email) {
        if (email.equals(""))
        {
            return false;
        }
        // Use the android.util.Patterns.EMAIL_ADDRESS pattern for email validation
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(email).matches();
    }
}