package com.example.notetakingapp;

import android.app.ProgressDialog;
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

public class LoginForm extends AppCompatActivity {

    private TextView btn_signup;
    private EditText etEmailLogin, etPassword;
    private Button btnLogin;
    FirebaseAuth auth;
    private ProgressDialog progressDialogLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        auth = FirebaseAuth.getInstance();

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);



        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginForm.this, SignupForm.class);
                startActivity(intent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = etEmailLogin.getText().toString();

                String txt_password = etPassword.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {

                    Toast.makeText(LoginForm.this, "All fields are required", Toast.LENGTH_SHORT).show();

                } else {

                    login(txt_email, txt_password);

                }

            }
        });

    }


               private  void login(final String txt_email, String txt_password){


                   progressDialogLogin = new ProgressDialog(this);
                   progressDialogLogin.setMessage("Logging in, please wait...");

                   progressDialogLogin.show();


                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(LoginForm.this, MainActivity.class);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);
                                        progressDialogLogin.dismiss();
                                        finish();

                                        Toast.makeText(LoginForm.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    } else {

                                        {
                                            progressDialogLogin.dismiss();

                                            Toast.makeText(LoginForm.this, "Email or Password incorrect", Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                }
                            });



            }

}
