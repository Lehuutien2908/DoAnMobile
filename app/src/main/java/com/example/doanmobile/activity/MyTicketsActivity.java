package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.TicketAdapter;
import com.example.doanmobile.model.TicketModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView recyclerTickets;
    private TicketAdapter adapter;
    private List<TicketModel> ticketList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        recyclerTickets = findViewById(R.id.recyclerTickets);
        recyclerTickets.setLayoutManager(new LinearLayoutManager(this));
        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(ticketList, ticket -> {
            Intent intent = new Intent(MyTicketsActivity.this, TicketDetailActivity.class);
            intent.putExtra("ticket", ticket);
            startActivity(intent);
        });
        recyclerTickets.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUserTickets();
    }

    private void loadUserTickets() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để xem vé!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestore.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    ticketList.clear();
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải vé: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            TicketModel ticket = doc.toObject(TicketModel.class);
                            if (ticket != null) {
                                ticket.setTicketId(doc.getId()); // Gán ID Firestore
                                ticketList.add(ticket);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
