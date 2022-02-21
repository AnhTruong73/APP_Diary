package com.example.finalproject_diaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignUp;
    FirebaseAuth auth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUi();
        initListener();
    }
    private void initUi(){
        edtEmail=findViewById(R.id.etEmail);
        edtPassword=findViewById(R.id.etPassword);
        btnSignUp=findViewById(R.id.btnSignup);
    }
    private void initListener(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });
    }

    private void onClickSignUp() {
        auth= FirebaseAuth.getInstance();
        dialog= new ProgressDialog(SignUpActivity.this);
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            edtEmail.setError("Bắt buộc nhập Email");
            return;
        }
        if (TextUtils.isEmpty(password)){
            edtPassword.setError("Bắt buộc nhập Mật Khẩu");
            return;
        }
        if (password.length()<6){
            edtPassword.setError("Mật Khẩu phải có ít nhất 6 chữ số");
            return;
        }
        storeDataToFirebase(email,password);
    }
    private void storeDataToFirebase(String email, String password){
        dialog.setMessage("Xin hãy đợi 1 chút....");
        dialog.show();

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }else{
                    Toast.makeText(SignUpActivity.this,"Xảy ra lỗi",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}