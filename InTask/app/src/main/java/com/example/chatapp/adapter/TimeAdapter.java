package com.example.chatapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.fragments.AdsJobFragment;
import com.example.chatapp.fragments.AdsTimeFragment;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
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
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {

    private Context mContext;
    private List<Time> mAds;
    private Boolean myAds;

    public TimeAdapter(Context mContext, List<Time> mAds, Boolean myAds) {
        this.mContext = mContext;
        this.mAds = mAds;
        this.myAds = myAds;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.time_published_item,parent,false);
        return new TimeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Time time = mAds.get(position);


        holder.title.setText(time.getTitle());
        holder.day.setText(time.getDay());
        holder.time.setText(time.getTime());
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.ratingBar.setRating( Float.parseFloat(snapshot.child(time.getAuthor()).child("average_ratings").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(myAds) {
            holder.button.setText("Elimina");
            holder.button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    FirebaseDatabase.getInstance().getReference("Time").child(time.getKey()).removeValue();
                    if(time.getSetted_image())
                    FirebaseStorage.getInstance().getReference().child("/time_images/"+time.getKey()+".jpg").delete();


                }
            });
        }else{
            holder.button.setText("Contatta");
        }
        if(!time.getVerified())
            holder.verified.setVisibility(View.INVISIBLE);

        holder.linearLayoutAds.setClickable(true);
        holder.linearLayoutAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", time.getKey());
                Fragment selectedFragment = new AdsTimeFragment();
                selectedFragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(time.getAuthor()).child("surname").getValue().toString().concat(" ")
                    .concat(snapshot.child(time.getAuthor()).child("name").getValue().toString());
                holder.author.setText(name);
                holder.author.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", time.getAuthor());
                        Fragment selectedFragment = new ProfileFragment();
                        selectedFragment.setArguments(bundle);
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(time.getSetted_image()){

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final File finalLocalFile = localFile;
                storageReference.child("time_images/"+time.getKey()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

        switch (time.getType()) {
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
            case "Supporto Informatico":
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
            public Button button;
            public TextView author, title, day, time;
            public LinearLayout linearLayoutAds, verified;
            public RatingBar ratingBar;

            public ViewHolder(View itemView){

                super(itemView);
                title = itemView.findViewById(R.id.job_title);
                author = itemView.findViewById(R.id.job_publisher);
                day = itemView.findViewById(R.id.job_date);
                time = itemView.findViewById(R.id.job_hour);
                verified  = itemView.findViewById(R.id.job_verified);
                button = itemView.findViewById(R.id.button);
                linearLayoutAds = itemView.findViewById(R.id.layout_ads);
                ratingBar = itemView.findViewById(R.id.rating_user);

                image = itemView.findViewById(R.id.job_image);
                type = itemView.findViewById(R.id.job_symbol);



            }
        }
}