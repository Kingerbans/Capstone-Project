package com.example.webrtcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    EditText edtTxtEmail, edtTxtPassword, edtTxtRePassword, edtTxtFullName;
    Button btnRegister, btnReturnLogin, btnBack;
    TextView txtError;
    Dialog myDialog;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtTxtFullName = findViewById(R.id.edtTxtFullName);
        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        edtTxtRePassword = findViewById(R.id.edtTxtRePassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnReturnLogin = findViewById(R.id.btnReturnLogin);

        myDialog = new Dialog(this);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(edtTxtFullName.getText().toString(), edtTxtEmail.getText().toString(), edtTxtPassword.getText().toString(), edtTxtRePassword.getText().toString());
            }
        });

        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void register(String fullName, String email, String password, String rePassword) {
        progressDialog.show();

        if (fullName.equals("") || email.equals("") || password.equals("") || rePassword.equals("")){
            progressDialog.dismiss();
            popUp("ERROR: Full Name, Email And Password Can Not Be Empty!");
        } else if (password.equals(rePassword)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        popUp("Account Is Created!");

                        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String userId = firebaseUser.getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userId);
                                hashMap.put("email", email);
                                hashMap.put("fullName", fullName);

                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    } else {
                        popUp(task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            popUp("ERROR: Password Must Be The Same!");
        }
    }

    private void popUp(String text){
        myDialog.setContentView(R.layout.error_popup);
        txtError = myDialog.findViewById(R.id.txtError);
        txtError.setText(text);
        btnBack = myDialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }
}