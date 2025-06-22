package com.example.doanmobile.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.ListUserAdminAdapter;
import com.example.doanmobile.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListAdminActivity extends AppCompatActivity {
    private RecyclerView recyclerUser;
    private ListUserAdminAdapter userAdapter;
    private List<UserModel> userList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_admin);

        recyclerUser = findViewById(R.id.recyclerUser);
        recyclerUser.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new ListUserAdminAdapter(userList);
        recyclerUser.setAdapter(userAdapter);

        // Nút quay lại
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        // Lấy user role = "user"
        db.collection("users")
                .whereEqualTo("role", "user")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserModel user = doc.toObject(UserModel.class);
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
