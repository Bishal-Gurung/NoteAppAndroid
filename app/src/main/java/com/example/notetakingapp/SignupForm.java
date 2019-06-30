package com.example.notetakingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupForm extends AppCompatActivity {

    private TextView btn_login;
    EditText etUname, etEmail, etPass, etRePass;
    Button btnSignUp;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignupForm.this, LoginForm.class);
                startActivity(intent);

            }
        });

        etUname = findViewById(R.id.etUname);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);
        btnSignUp = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = etUname.getText().toString();

                String txt_password = etPass.getText().toString();

                String txt_cpassword = etRePass.getText().toString();

                String txt_email = etEmail.getText().toString();



                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_cpassword)

                        || TextUtils.isEmpty(txt_email))

                {

                    Toast.makeText(SignupForm.this, "Fill All Fields", Toast.LENGTH_SHORT).show();

                }

                else if (txt_password.length() < 6)

                {

                    Toast.makeText(SignupForm.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();

                }

                else if (!txt_password.equals(txt_cpassword))

                {

                    Toast.makeText(SignupForm.this, "Password doesnt match", Toast.LENGTH_SHORT).show();

                }else {

                    register(txt_username, txt_password, txt_email);

                }
            }
        });
    }

    private void  register (final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);

                            hashMap.put("username", username);

                            hashMap.put("imageURL", "default");



                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Intent intent = new Intent(SignupForm.this, LoginForm.class);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);

                                        finish();
                                    }

                                }
                            });
                        } else {

                            Toast.makeText(SignupForm.this, "You cant register with this email or password", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
