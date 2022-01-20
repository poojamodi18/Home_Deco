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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegister extends AppCompatActivity {

    private Button createAccountButton;
    private EditText inputName, inputPhoneNumber, inputPassword, inputConfirmPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputConfirmPassword = findViewById(R.id.register_confirm_password_input);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();

            }
        });
    }

    public boolean isValidPassword(final String pass){
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pass);

        return matcher.matches();
    }

    private void CreateAccount() {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password",Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 8){
            Toast.makeText(this, "Password should contain at least 8 characters", Toast.LENGTH_SHORT).show();
        }
        else if(!(isValidPassword(password))){
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        }
        else if(!(password.equals(confirmPassword))){
            Toast.makeText(this,"Password doesn't match",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name,phone,password);
        }
    }

    private void validatePhoneNumber(String name, String phone, String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();


        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists())){
                    HashMap<String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);
                    userDataMap.put("address","-");

                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ActivityRegister.this,"Your account has been created",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(ActivityRegister.this,ActivityLogin.class);
                                        startActivity(intent);

                                    }
                                    else {
                                        Toast.makeText(ActivityRegister.this,"Network Error: Please try again",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(ActivityRegister.this,"This "+phone+" already exists.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Intent intent = new Intent(ActivityRegister.this,MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}