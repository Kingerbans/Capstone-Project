package com.example.webrtcdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.webrtcdemo.Model.Users;
import com.example.webrtcdemo.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<Users> lUsers;

    public UserAdapter(Context context, List<Users> lUsers) {
        this.context = context;
        this.lUsers = lUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = lUsers.get(position);
        holder.txtUsername.setText(user.getFullName());
        holder.profileImage.setImageResource(R.drawable.usericon);
    }

    @Override
    public int getItemCount() {
        return lUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtUsername;
        public ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
