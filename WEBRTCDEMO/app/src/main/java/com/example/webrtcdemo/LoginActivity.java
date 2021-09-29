package com.example.webrtcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.webrtcdemo.Handler.SocketHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN = "login";
    private static final String Users = "Users";
    private static final String FULLNAME = "fullName";
    EditText edtTxtEmail, edtTxtPassword;
    TextView txtError;
    Button btnLogin, btnRegister, btnBack;
    Dialog myDialog;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    SocketHandler socketHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);

        btnLogin = findViewById(R.id.btnReturnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        myDialog = new Dialog(this);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(edtTxtEmail.getText().toString(), edtTxtPassword.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login(String email, String password) {
        progressDialog.show();
        if (email.equals("") || password.equals("")){
            progressDialog.dismiss();
            popUp("ERROR: Email And Password Can Not Be Empty");
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        socketHandler = new SocketHandler();
                        socketHandler.getSocket().connect();

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference(Users).child(userId).child(FULLNAME);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                SocketHandler.getSocket().emit(LOGIN, snapshot.getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        socketHandler.getSocket().on(LOGIN, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                socketHandler.setSocketId((String)args[0]);
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                finish();

                            }
                        });
                    } else {
                        popUp(task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    public void popUp(String text){
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