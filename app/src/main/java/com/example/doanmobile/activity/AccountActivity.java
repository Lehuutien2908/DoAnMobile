    package com.example.doanmobile.activity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageButton;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.doanmobile.R;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    public class AccountActivity extends AppCompatActivity {
        private FirebaseAuth auth;
        private FrameLayout contentFrame;
        private ImageButton btnBack;
        private TextView txtName,txtInfo,txtMyTicket; // For logged-in layout
        private Button btnLogout; // For logged-in layout

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_account_base); // Set the base layout

            auth = FirebaseAuth.getInstance();
            contentFrame = findViewById(R.id.content_frame);
            btnBack = findViewById(R.id.btnBack);

            btnBack.setOnClickListener(v -> finish());
            //thanh điều hướng
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            bottomNav.setSelectedItemId(R.id.nav_account);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this,MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (id == R.id.nav_theater) {
                    startActivity(new Intent(this, TheaterActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_movie) {
                    startActivity(new Intent(this, MovieListActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_account) {
                    return true;
                }
                return false;
            });


            loadAppropriateLayout();

        }

        private void loadAppropriateLayout() {
            FirebaseUser currentUser = auth.getCurrentUser();
            LayoutInflater inflater = LayoutInflater.from(this);

            if (currentUser == null) {
                // User is not logged in, load guest layout
                contentFrame.removeAllViews();
                View guestLayout = inflater.inflate(R.layout.activity_profile_guest, contentFrame, false);
                contentFrame.addView(guestLayout);

                // Initialize guest layout elements
                Button btnRegister = guestLayout.findViewById(R.id.btnRegister);
                Button btnLogin = guestLayout.findViewById(R.id.btnLogin);

                btnRegister.setOnClickListener(v -> {
                    startActivity(new Intent(AccountActivity.this, RegisterActivity.class));
                });

                btnLogin.setOnClickListener(v -> {
                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                });
            } else {
                // User is logged in, load logged-in layout
                contentFrame.removeAllViews();
                View loggedInLayout = inflater.inflate(R.layout.activity_profile_logged_in, contentFrame, false);
                contentFrame.addView(loggedInLayout);

                // Initialize logged-in layout elements
                txtName = loggedInLayout.findViewById(R.id.txtName);
                txtMyTicket  = loggedInLayout.findViewById(R.id.txtMyTicket);
                btnLogout = loggedInLayout.findViewById(R.id.btnLogout);

                txtMyTicket.setOnClickListener(v->{
                    startActivity(new Intent(this, MyTicketsActivity.class));

                });

                txtInfo = findViewById(R.id.infoUser);
                txtInfo.setOnClickListener(v -> {

                    startActivity( new Intent(this,InfoActivity.class));
                });

                // Set user name
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    txtName.setText(currentUser.getDisplayName());
                } else if (currentUser.getEmail() != null) {
                    txtName.setText(currentUser.getEmail());
                } else {
                    txtName.setText("Người dùng");
                }

                // Setup logout button
                btnLogout.setOnClickListener(v -> {
                    auth.signOut();
                    loadAppropriateLayout(); // Reload to show guest layout after logout
                });
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            loadAppropriateLayout(); // Re-check status when activity resumes (e.g., after login/register/logout)
        }
    }
