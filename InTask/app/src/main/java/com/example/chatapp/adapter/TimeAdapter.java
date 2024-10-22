package com.example.chatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;
import com.example.chatapp.fragments.AdsJobFragment;
import com.example.chatapp.fragments.AdsTimeFragment;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.example.chatapp.start.RegisterActivity;
import com.example.chatapp.start.StartActivity;
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

import static com.facebook.FacebookSdk.getApplicationContext;

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

        holder.button.setVisibility(View.GONE);
        holder.title.setText(time.getTitle());
        holder.day.setText(time.getDay());
        holder.time.setText(time.getTime());
        if(time.getAchieved()) {
            holder.status.setText(getApplicationContext().getResources().getString(R.string.archiviato));
            holder.status.setTextColor(Color.RED);
        }else if(time.getPending())
            holder.status.setText((getApplicationContext().getResources().getString(R.string.in_trattativa)));

        else {
            holder.status.setText((getApplicationContext().getResources().getString(R.string.disponibile)));
            holder.status.setTextColor(Color.rgb(0, 153, 0));
        }
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.ratingBar.setRating( Float.parseFloat(snapshot.child(time.getAuthor()).child("average_ratings").getValue().toString()));
                holder.numOFReviews.setText(snapshot.child(time.getAuthor()).child("ratings").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    if(!time.getAchieved() && !time.getPending()) {
        holder.button.setVisibility(View.VISIBLE);
        if (myAds) {
            holder.button.setText(getApplicationContext().getResources().getString(R.string.elimina));
            holder.button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    FirebaseDatabase.getInstance().getReference("Time").child(time.getKey()).removeValue();
                    if (time.getSetted_image())
                        FirebaseStorage.getInstance().getReference().child("/time_images/" + time.getKey() + ".jpg").delete();


                }
            });
        } else {
            holder.button.setText(getApplicationContext().getResources().getString(R.string.contatta_button));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null)
                        mContext.startActivity(new Intent(mContext, StartActivity.class));
                    else {
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!time.getVerified() || Boolean.parseBoolean(snapshot.child("verified").getValue().toString())) {
                                    Intent intent = new Intent(mContext, MessagingActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("sent", time.getAuthor());
                                    intent.putExtras(b);
                                    mContext.startActivity(intent);
                                } else {
                                    Toast.makeText(mContext, getApplicationContext().getResources().getString(R.string.devi_verificare_l_account_prima_di_contattare_questo_utente), Toast.LENGTH_SHORT).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", snapshot.child("id").getValue().toString());
                                    Fragment selectedFragment = new ProfileFragment();
                                    selectedFragment.setArguments(bundle);
                                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }
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
                        Activity activity = (Activity)mContext;
                        if (activity.isFinishing()) {
                            return;
                        }
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

        switch (time.getType())  {
            case "Giardinaggio":
            case "Gardening":
                holder.type.setImageResource(R.drawable.ic_baseline_local_florist_24);
                break;
            case "Babysitting":
                holder.type.setImageResource(R.drawable.ic_outline_child_friendly_24);
                break;
            case "Cucinare":
            case "Cooking":
                holder.type.setImageResource(R.drawable.ic_baseline_local_pizza_24);
                break;
            case "Pulizie":
            case "Cleaning":
                holder.type.setImageResource(R.drawable.ic_outline_local_laundry_service_24);
                break;
            case "Ripetizioni":
            case "Private lessons":
                holder.type.setImageResource(R.drawable.ic_baseline_menu_book_24);
                break;
            case "Trasloco":
            case "Moving":
                holder.type.setImageResource(R.drawable.ic_baseline_local_shipping_24);
                break;
            case "Riparazioni":
            case "Fixing stuff":
                holder.type.setImageResource(R.drawable.ic_baseline_build_24);
                break;
            case "Dogsitting":
                holder.type.setImageResource(R.drawable.ic_dog);
                break;
            case "Personal Training":
                holder.type.setImageResource(R.drawable.ic_baseline_fitness_center_24);
                break;
            case "Supporto Informatico":
            case "It support":
                holder.type.setImageResource(R.drawable.ic_baseline_computer_24);
                break;
            case "Trasporto":
            case "Transportation":
                holder.type.setImageResource(R.drawable.ic_baseline_directions_car_24);
                break;
            case "Spesa":
            case "Grocery shopping":
                holder.type.setImageResource(R.drawable.ic_baseline_local_grocery_store_24);
                break;
            case "Decimo al Calcetto":
            case "Soccer player":
                holder.type.setImageResource(R.drawable.ic_baseline_sports_soccer_24);
                break;
            case "Altro":
            case "Others":
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
            public TextView author, title, day, time, numOFReviews, status;
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
                numOFReviews = itemView.findViewById(R.id.number_of_reviews);
                status = itemView.findViewById(R.id.status);

                image = itemView.findViewById(R.id.job_image);
                type = itemView.findViewById(R.id.job_symbol);



            }
        }
}