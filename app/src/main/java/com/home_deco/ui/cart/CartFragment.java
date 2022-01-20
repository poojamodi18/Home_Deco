package com.home_deco.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.home_deco.ConfirmOrderActivity;
import com.home_deco.ItemClickListener;
import com.home_deco.MainActivity;
import com.home_deco.R;
import com.home_deco.databinding.FragmentCartBinding;
import com.home_deco.model.Cart;
import com.home_deco.model.CurrentUsers;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcess;
    private TextView txtTotalAmount;
    private int totalPrice = 0;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    private FragmentCartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nextProcess = root.findViewById(R.id.next_process_btn);
        txtTotalAmount = root.findViewById(R.id.text_cart_total);

        nextProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ConfirmOrderActivity.class);
                intent.putExtra("totalPrice",String.valueOf(totalPrice));
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(CurrentUsers.currentUsers.getPhone()).child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                cartViewHolder.txtProductQuantity.setText("Qty: " + cart.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price: " + cart.getPrice() + " Rs");
                cartViewHolder.txtProductName.setText(cart.getPname());

                int oneTotalPrice = ((Integer.valueOf(cart.getPrice()))*(Integer.valueOf(cart.getQuantity())));
                totalPrice = totalPrice+oneTotalPrice;
                txtTotalAmount.setText("Total Price = "+ String.valueOf(totalPrice)+" Rs.");

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductQuantity;
        private ItemClickListener itemClickListener;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.cart_product_name);
            txtProductPrice = itemView.findViewById(R.id.cart_product_price);
            txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}