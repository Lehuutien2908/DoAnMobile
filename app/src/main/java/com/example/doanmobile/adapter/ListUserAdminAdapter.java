package com.example.doanmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanmobile.R;
import com.example.doanmobile.model.UserModel;
import java.util.List;

public class ListUserAdminAdapter extends RecyclerView.Adapter<ListUserAdminAdapter.UserViewHolder> {
    private List<UserModel> userList;
    public ListUserAdminAdapter(List<UserModel> userList) {
        this.userList = userList;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.txtUserName.setText(user.getName() == null ? "Không tên" : user.getName());
        holder.txtUserEmail.setText(user.getMail());
        holder.txtUserRole.setText(user.getRole());
        // Avatar: nếu muốn hiển thị ảnh, dùng Glide. Không thì giữ nguyên ảnh tĩnh.
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtUserName, txtUserEmail, txtUserRole;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtUserRole = itemView.findViewById(R.id.txtUserRole);
        }
    }
}
