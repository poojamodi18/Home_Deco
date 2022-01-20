package com.home_deco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home_deco.model.CurrentUsers;

import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {
    private Button confirmOrder;
    private EditText txtConfirmName, txtConfirmPhone, txtConfirmAddress;
    private String totalAmount = "";
    private TextView totalAmountLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalAmount = getIntent().getStringExtra("totalPrice");
        totalAmountLabel = findViewById(R.id.confirm_order_amount_label);
        totalAmountLabel.setText("Order Amount = "+totalAmount);

        confirmOrder = findViewById(R.id.confirm_order_btn);
        txtConfirmName = findViewById(R.id.confirm_order_full_name);
        txtConfirmPhone = findViewById(R.id.confirm_order_phone_number);
        txtConfirmAddress = findViewById(R.id.confirm_order_address);

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUsers.currentUsers.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String password = snapshot.child("password").getValue().toString();
                    String address = snapshot.child("address").getValue().toString();

                    txtConfirmPhone.setText(phone);
                    txtConfirmName.setText(name);
                    txtConfirmAddress.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void check() {
        if(TextUtils.isEmpty(txtConfirmName.getText().toString())){
            Toast.makeText(ConfirmOrderActivity.this, "Please provide name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtConfirmPhone.getText().toString())){
            Toast.makeText(ConfirmOrderActivity.this, "Please provide phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtConfirmAddress.getText().toString())){
            Toast.makeText(ConfirmOrderActivity.this, "Please provide address", Toast.LENGTH_SHORT).show();
        }
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("name",txtConfirmName.getText().toString());
            userMap.put("address",txtConfirmAddress.getText().toString());
            userMap.put("phone",txtConfirmPhone.getText().toString());
            ref.child(CurrentUsers.currentUsers.getPhone()).updateChildren(userMap);
            confirmOrder();
        }
    }

    private void confirmOrder() {
        FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                .child(CurrentUsers.currentUsers.getPhone())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(ConfirmOrderActivity.this,OrderPlacedActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}