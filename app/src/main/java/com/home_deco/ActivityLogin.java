package com.home_deco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home_deco.model.CurrentUsers;
import com.home_deco.model.Users;

public class ActivityLogin extends AppCompatActivity {

    private Button loginButton;
    private EditText inputNumber, inputPassword;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_btn);
        inputNumber = findViewById(R.id.login_phone_number_input);
        inputPassword = findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        String phone = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone,password);
        }
    }

    private void allowAccessToAccount(String phone, String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(phone).exists()){
                    Users userData = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){
                            Toast.makeText(ActivityLogin.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(ActivityLogin.this,HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            CurrentUsers.currentUsers = userData;
                            startActivity(intent);
                            ActivityLogin.this.finish();
                        }
                        else{
                            Toast.makeText(ActivityLogin.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else{
                    Toast.makeText(ActivityLogin.this,"Account with this "+phone+" do not exists",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}