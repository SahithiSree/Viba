package com.android.viba;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomepostAdapter extends RecyclerView.Adapter<HomepostAdapter.PostsViewHolder>  {

    private Context context;
    private ArrayList<GetSet> getSets;
    private List<GetSet> getSetList;
    private AdapterView.OnItemClickListener listener;

    public HomepostAdapter(ArrayList<GetSet> list) {
        this.getSets = list;
    }

    public HomepostAdapter(List<GetSet> gsList, AdapterView.OnItemClickListener listener) {
        this.getSetList = gsList;
        this.listener = listener;
    }

    public HomepostAdapter(Context c, ArrayList<GetSet> gs) {
        context = c;
        getSets = gs;
    }

    @NonNull
    @Override
    public HomepostAdapter.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepost_layout, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomepostAdapter.PostsViewHolder holder, int position) {

        holder.bind(getSets.get(position), listener);
        holder.homepostkey = getSets.get(position).getHomeuid();
        holder.aduid = getSets.get(position).getAduid();
        holder.postImage = getSets.get(position).getHomepi();
        holder.postTime.setText(getSets.get(position).getTime());
        holder.adname.setText(getSets.get(position).getAdname());
        holder.adrole.setText(getSets.get(position).getAdrole());
        holder.date.setText(getSets.get(position).getDate());
        holder.description.setText(getSets.get(position).getHomepd());

        Picasso.get().load(getSets.get(position).getAdimage()).placeholder(R.drawable.profilepic).into(holder.circleImageView);

        holder.progressImageBar.setVisibility(View.VISIBLE);
        Picasso.get().load(getSets.get(position).getHomepi())
                .into(holder.inImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.progressImageBar != null) {
                            holder.progressImageBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressImageBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return getSets.size();
    }
    
    public class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView date, description, adname, adrole, postTime;
        CircleImageView circleImageView;
        ProgressBar progressImageBar;
        ZoomInImageView inImageView;
        ImageView imageView, homeComments, homeShare;
        String homepostkey, aduid, postImage;
        DatabaseReference databaseReference;

        public PostsViewHolder(View itemView) {

            super(itemView);

            circleImageView = itemView.findViewById(R.id.homeAdProImage);
            date = itemView.findViewById(R.id.homeAdDate);
            description = itemView.findViewById(R.id.homeAdDescp);
            adname = itemView.findViewById(R.id.homeAdname);
            adrole = itemView.findViewById(R.id.homeAdRole);
            postTime = itemView.findViewById(R.id.homeAdTimepost);
            progressImageBar = itemView.findViewById(R.id.progress_homepost);
            inImageView = itemView.findViewById(R.id.homeAdPostimage);
            homeComments = itemView.findViewById(R.id.homeComment);
            homeShare = itemView.findViewById(R.id.homeShare);
            imageView = itemView.findViewById(R.id.homeEdit);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Homepost Info");
        }

        public void bind(final GetSet item, final AdapterView.OnItemClickListener listener) {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence[] options = new CharSequence[] {
                      "Edit", "Remove"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose to edit");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                context.startActivity(new Intent(context,
                                        HomeEditActivity.class).putExtra("homepostkey", homepostkey));
                                break;
                                case 1:
                                    databaseReference.child(homepostkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                                                context.startActivity(new Intent(context, HomeActivity.class));
                                            }
                                            else {
                                                Toast.makeText(context, "Error Ocurred!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });

            homeComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homepostView = new Intent(context, HomeCommentsActivity.class);
                    homepostView.putExtra("homeuid", homepostkey);
                    context.startActivity(homepostView);
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent userinfo = new Intent(context, UserInfoActivity.class);
                    userinfo.putExtra("aduid", aduid);
                    context.startActivity(userinfo);
                }
            });
            inImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewImage = new Intent(context, PostImageActivity.class);
                    viewImage.putExtra("homeimage", postImage);
                    context.startActivity(viewImage);
                }
            });

            homeShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) inImageView.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Share via", null);
                        Uri uri = Uri.parse(bitmapPath);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/jpeg");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    } catch (Exception e) {
                        Snackbar.make(v, "Image not exist! Can't share", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
