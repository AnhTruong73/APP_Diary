package com.example.finalproject_diaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextView layoutSignUp;
    private TextView edtEmail,edtPassword;
    private Button btnSignIn;
    ProgressDialog dialog;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUi();
        initListener();
    }

    private void initUi(){
        layoutSignUp=findViewById(R.id.layout_sign_up);
        edtEmail=findViewById(R.id.edt_email);
        edtPassword=findViewById(R.id.edt_password);
        btnSignIn=findViewById(R.id.btn_sign_in);


    }
    private void initListener(){
        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignIn();
            }
        });
    }

    private void onClickSignIn() {
        auth= FirebaseAuth.getInstance();
        dialog= new ProgressDialog(SignInActivity.this);
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
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
        checkDataFromFirebase(email,password);
    }

    private void checkDataFromFirebase(String email, String password) {
        dialog.setMessage("Đang xử lý....");
        dialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }else{
                    Toast.makeText(SignInActivity.this,"Xảy ra lỗi",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
}