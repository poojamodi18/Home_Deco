package com.home_deco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OrderPlacedActivity extends AppCompatActivity {
    private Button homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        homeBtn = findViewById(R.id.placed_order_btn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderPlacedActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}