package com.example.doanmobile.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.example.doanmobile.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private TextView tvDisplayName;
    private EditText edtFullName, edtPhone, edtDob, edtEmail;
    private RadioGroup rgGender;
    private Button btnUpdate;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        tvDisplayName = findViewById(R.id.tvDisplayName);
        edtFullName   = findViewById(R.id.edtFullName);
        edtPhone      = findViewById(R.id.edtPhone);
        edtDob        = findViewById(R.id.edtDob);
        edtEmail      = findViewById(R.id.edtEmail);
        rgGender      = findViewById(R.id.rgGender);
        btnUpdate     = findViewById(R.id.btnUpdate);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        findViewById(R.id.topBar).setOnClickListener(v -> finish());

        loadUserInfo();
        setListeners();
    }

    private void loadUserInfo() {
        String uid = auth.getUid();
        if (uid == null) { finish(); return; }

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    currentUser = doc.toObject(UserModel.class);
                    if (currentUser == null) return;

                    tvDisplayName.setText(currentUser.getMail());
                    edtEmail.setText(currentUser.getMail());
                    edtEmail.setEnabled(false);

                    edtFullName.setText(currentUser.getName());
                    edtPhone.setText(currentUser.getSdt());
                    edtDob.setText(currentUser.getNgaySinh());

                    if ("Nam".equals(currentUser.getGioiTinh())) {
                        rgGender.check(R.id.rbMale);
                    } else if ("Nữ".equals(currentUser.getGioiTinh())) {
                        rgGender.check(R.id.rbFemale);
                    }

                    btnUpdate.setEnabled(false);
                });
    }

    private void setListeners() {
        edtDob.setOnClickListener(v -> showDatePicker());

        TextWatcher watcher = new SimpleTextWatcher(() -> btnUpdate.setEnabled(true));
        edtFullName.addTextChangedListener(watcher);
        edtPhone.addTextChangedListener(watcher);
        edtDob.addTextChangedListener(watcher);
        rgGender.setOnCheckedChangeListener((g, id) -> btnUpdate.setEnabled(true));

        btnUpdate.setOnClickListener(v -> saveChanges());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    String dob = String.format("%02d/%02d/%04d", d, m + 1, y);
                    edtDob.setText(dob);
                    btnUpdate.setEnabled(true);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    private void saveChanges() {
        String uid = auth.getUid();
        if (uid == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", edtFullName.getText().toString().trim());
        updates.put("sdt", edtPhone.getText().toString().trim());
        updates.put("ngaySinh", edtDob.getText().toString().trim());

        String gender = (rgGender.getCheckedRadioButtonId() == R.id.rbMale) ? "Nam" : "Nữ";
        updates.put("gioiTinh", gender);

        firestore.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(false);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleTextWatcher(Runnable r) { onChange = r; }
        public void afterTextChanged(Editable s) { onChange.run(); }
        public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        public void onTextChanged(CharSequence s, int st, int b, int c) {}
    }
}
