package com.home_deco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home_deco.model.CurrentUsers;
import com.home_deco.model.Products;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button addToCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName,productPrice,productDescription;
    private String productId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

        addToCart = findViewById(R.id.add_product_to_cart);
        numberButton = findViewById(R.id.product_number_btn);
        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_detail);
        productPrice = findViewById(R.id.product_price_detail);
        productDescription = findViewById(R.id.product_description_detail);

        getProductDetails(productId);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart();
            }
        });
    }

    private void addProductToCart() {
        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("quantity",numberButton.getNumber());

        cartRef.child("User View").child(CurrentUsers.currentUsers.getPhone())
                .child("Products").child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this , "Added to the cart", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                            startActivity(intent);
                            ProductDetailsActivity.this.finish();
                        }
                    }
                });
    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products product = snapshot.getValue(Products.class);
                    productName.setText(product.getPname().toString());
                    productPrice.setText(product.getPrice().toString());
                    productDescription.setText(product.getDescription().toString());
                    Picasso.get().load(product.getImage().toString()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}