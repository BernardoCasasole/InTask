package com.example.chatapp.adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private Context mContext;
    private List<Job> mAds;

    public JobAdapter(Context mContext, List<Job> mAds) {
        this.mContext = mContext;
        this.mAds = mAds;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.job_published_item,parent,false);
        return new JobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Job job = mAds.get(position);


        holder.title.setText(job.getTitle());
        holder.day.setText(job.getDay());
        holder.time.setText(job.getTime());
        holder.reward.setText(job.getReward());
        holder.location.setText(job.getLocation());
        if(!job.getVerified())
            holder.verified.setVisibility(View.INVISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(job.getAuthor()).child("surname").getValue().toString().concat(" ")
                        .concat(snapshot.child(job.getAuthor()).child("name").getValue().toString());
                holder.author.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(job.getSetted_image()){

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final File finalLocalFile = localFile;
                storageReference.child("job_images/"+job.getKey()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Glide.with(mContext).load(finalLocalFile).into(holder.image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        switch (job.getType()) {
            case "Giardinaggio":
                holder.type.setImageResource(R.drawable.ic_baseline_local_florist_24);
                break;
            case "Babysitting":
                holder.type.setImageResource(R.drawable.ic_outline_child_friendly_24);
                break;
            case "Cucinare":
                holder.type.setImageResource(R.drawable.ic_baseline_local_pizza_24);
                break;
            case "Pulizie":
                holder.type.setImageResource(R.drawable.ic_outline_local_laundry_service_24);
                break;
            case "Ripetizioni":
                holder.type.setImageResource(R.drawable.ic_baseline_menu_book_24);
                break;
            case "Trasloco":
                holder.type.setImageResource(R.drawable.ic_baseline_local_shipping_24);
                break;
            case "Riparazioni":
                holder.type.setImageResource(R.drawable.ic_baseline_build_24);
                break;
            case "Dogsitting":
                holder.type.setImageResource(R.drawable.ic_dog);
                break;
            case "Personal Training":
                holder.type.setImageResource(R.drawable.ic_baseline_fitness_center_24);
                break;
            case "Supporto informatico":
                holder.type.setImageResource(R.drawable.ic_baseline_computer_24);
                break;
            case "Trasporto":
                holder.type.setImageResource(R.drawable.ic_baseline_directions_car_24);
                break;
            case "Spesa":
                holder.type.setImageResource(R.drawable.ic_baseline_local_grocery_store_24);
                break;
            case "Decimo al Calcetto":
                holder.type.setImageResource(R.drawable.ic_baseline_sports_soccer_24);
                break;
            case "Altro":
                holder.type.setImageResource(R.drawable.ic_baseline_more_horiz_24);
                break;
        }
    }


    @Override
    public int getItemCount() {
            return mAds.size();
    }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView image,type;
            public TextView author, title, day, time, reward, location, verified;

            public ViewHolder(View itemView){

                super(itemView);
                title = itemView.findViewById(R.id.job_title);
                author = itemView.findViewById(R.id.job_publisher);
                day = itemView.findViewById(R.id.job_date);
                time = itemView.findViewById(R.id.job_hour);
                reward = itemView.findViewById(R.id.job_proposed_price);
                location = itemView.findViewById(R.id.job_distance);
                verified  = itemView.findViewById(R.id.job_verified);

                image = itemView.findViewById(R.id.job_image);
                type = itemView.findViewById(R.id.job_symbol);



            }
        }
}