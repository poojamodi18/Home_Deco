package com.home_deco.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home_deco.R;
import com.home_deco.databinding.FragmentSettingsBinding;
import com.home_deco.model.CurrentUsers;

import java.util.HashMap;

public class SettingsFragment extends Fragment {
    private EditText txtPhoneNumber, txtFullName, txtAddress;
    private Button updateBtn;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        txtPhoneNumber = root.findViewById(R.id.settings_phone_number);
        txtFullName = root.findViewById(R.id.settings_full_name);
        txtAddress = root.findViewById(R.id.settings_address);
        updateBtn = root.findViewById(R.id.settings_update_account_btn);

        userInfoDisplay(txtPhoneNumber,txtFullName,txtAddress);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                HashMap<String,Object> userMap = new HashMap<>();
                userMap.put("name",txtFullName.getText().toString());
                userMap.put("address",txtAddress.getText().toString());
                userMap.put("phone",txtPhoneNumber.getText().toString());
                ref.child(CurrentUsers.currentUsers.getPhone()).updateChildren(userMap);

                userInfoDisplay(txtPhoneNumber,txtFullName,txtAddress);

                Toast.makeText(getContext(), "Profile has been updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }



    private void userInfoDisplay(EditText txtPhoneNumber, EditText txtFullName, EditText txtAddress) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUsers.currentUsers.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String password = snapshot.child("password").getValue().toString();
                    String address = snapshot.child("address").getValue().toString();

                    txtPhoneNumber.setText(phone);
                    txtFullName.setText(name);
                    txtAddress.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
//        getuserdata and display it and allow user to edit it
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}