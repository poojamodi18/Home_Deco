package com.home_deco.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home_deco.ItemClickListener;
import com.home_deco.ProductDetailsActivity;
import com.home_deco.R;
import com.home_deco.databinding.FragmentHomeBinding;
import com.home_deco.model.Products;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;
    private RecyclerView recyclerView;

    private DatabaseReference productRef;
//    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        recyclerView = root.findViewById(R.id.home_recycler_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productRef, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                productRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productViewHolder.txtProductName.setText(products.getPname().toString());
                        productViewHolder.txtProductPrice.setText("Price: "+products.getPrice().toString()+" Rs.");
                        productViewHolder.txtProductDescription.setText(products.getDescription().toString());
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                                intent.putExtra("pid",products.getPid().toString());
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                ProductViewHolder viewHolder = new ProductViewHolder(view);

                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductDescription, txtProductPrice;
        public ImageView imageView;
        public ItemClickListener listener;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            txtProductName = itemView.findViewById(R.id.product_name);
            txtProductPrice = itemView.findViewById(R.id.product_price);
            txtProductDescription = itemView.findViewById(R.id.product_description);
        }

        public void setItemClickListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}