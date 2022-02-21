package com.example.finalproject_diaryapp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.finalproject_diaryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordFragment extends Fragment {

    private View mView;
    private EditText edtNewPassword, edtConfirmPassword, edtOldPassword;
    private Button btnChangePassword;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         mView = inflater.inflate(R.layout.activity_change_password_fragment,container,false);

        initUi();
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reAuthenticate();
            }
        });

         return mView;
    }

    private void initUi() {
        dialog = new ProgressDialog(getActivity());
        edtNewPassword = mView.findViewById(R.id.edt_new_password);
        edtOldPassword = mView.findViewById(R.id.edt_old_password);
        edtConfirmPassword = mView.findViewById(R.id.edt_confirm_password);
        btnChangePassword = mView.findViewById(R.id.btn_change_password);
    }

    private void onClickChangePassword(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String strNewPassword = edtNewPassword.getText().toString().trim();
        String strConfirmPassword = edtConfirmPassword.getText().toString().trim();
        dialog.show();
        if (strConfirmPassword.equals(strNewPassword)) {
            user.updatePassword(strNewPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),"ĐÃ ĐỔI MẬT KHẨU THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

        }
        else{
            Toast.makeText(getActivity(),strConfirmPassword, Toast.LENGTH_SHORT).show();
            edtConfirmPassword.setError("MẬT KHẨU KHÔNG GIỐNG NHAU");
            dialog.dismiss();
        }
    }
    private void reAuthenticate(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String strOldPassword = edtOldPassword.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),strOldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    onClickChangePassword();
                }
                else{
                    Toast.makeText(getActivity(),"Sai mật khẩu cữ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
}