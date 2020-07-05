package com.android.viba;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeComAdapter extends RecyclerView.Adapter<HomeComAdapter.HomeComViewHolder> {
    Context context;
    ArrayList<GetSet> getsets;
    private List<GetSet> homeComList;
    private AdapterView.OnItemClickListener listener;

    HomeComAdapter() {
    }

    public HomeComAdapter(List<GetSet> hList, AdapterView.OnItemClickListener listener) {
        this.homeComList = hList;
        this.listener = listener;
    }

    public HomeComAdapter(Context c, ArrayList<GetSet> h) {
        context = c;
        getsets = h;
    }

    @NonNull
    @Override
    public HomeComAdapter.HomeComViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeComAdapter.HomeComViewHolder(LayoutInflater.from(context).inflate(R.layout.homecomments_layout,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeComAdapter.HomeComViewHolder holder, int position) {

        holder.bind(getsets.get(position), listener);
        holder.username.setText(getsets.get(position).getUsername());
        holder.homeComments.setText(getsets.get(position).getCommments());
        holder.homeComdate.setText(getsets.get(position).getDate());
        holder.comuid = getsets.get(position).getComuid();
        Picasso.get().load(getsets.get(position).getUserimage()).placeholder(R.drawable.profilepic).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return getsets.size();
    }
    public class HomeComViewHolder extends RecyclerView.ViewHolder {

        TextView username, homeComments, homeComdate;
        CircleImageView circleImageView;
        String comuid;

        public HomeComViewHolder(View itemView) {

            super(itemView);

            circleImageView = itemView.findViewById(R.id.homeComProUserimage);
            username = itemView.findViewById(R.id.homeComUsername);
            homeComments = itemView.findViewById(R.id.shareHomeComments);
            homeComdate = itemView.findViewById(R.id.dateHomeComments);
        }

        public void bind(GetSet item,AdapterView.OnItemClickListener listener) {
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UserInfoActivity.class).putExtra("UserInfo", comuid));
                }
            });
        }
    }
}
