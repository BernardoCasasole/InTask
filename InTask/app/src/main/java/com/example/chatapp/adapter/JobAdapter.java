package com.example.chatapp.adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;
import com.example.chatapp.fragments.AdsJobFragment;
import com.example.chatapp.fragments.ProfileFragment;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private Context mContext;
    private List<Job> mAds;
    private Boolean myAds;

    public JobAdapter(Context mContext, List<Job> mAds, Boolean myAds) {
        this.mContext = mContext;
        this.mAds = mAds;
        this.myAds = myAds;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.job_published_item,parent,false);
        return new JobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Job job = mAds.get(position);

        holder.button.setVisibility(View.GONE);
        holder.title.setText(job.getTitle());
        holder.day.setText(job.getDay());
        holder.time.setText(job.getTime());
        holder.reward.setText(String.valueOf(job.getReward()));
        holder.location.setClickable(true);
        getDistance(job.getLocation(), holder.location);
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + job.getLocation();
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(map)));
            }
        });
        if(job.getAchieved()) {
            holder.status.setText("Archiviato");
            holder.status.setTextColor(Color.RED);
        }else if(job.getPending())
            holder.status.setText(("In trattativa"));

        else {
            holder.status.setText(("Disponibile"));
            holder.status.setTextColor(Color.rgb(0, 153, 0));
        }
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.ratingBar.setRating( Float.parseFloat(snapshot.child(job.getAuthor()).child("average_ratings").getValue().toString()));
                holder.numOFReviews.setText(snapshot.child(job.getAuthor()).child("ratings").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(!job.getAchieved() && !job.getPending()) {
            holder.button.setVisibility(View.VISIBLE);
            if (myAds) {
                holder.button.setText("Elimina");
                holder.button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference("Job").child(job.getKey()).removeValue();
                        if (job.getSetted_image())
                            FirebaseStorage.getInstance().getReference().child("/time_images/" + job.getKey() + ".jpg").delete();


                    }
                });
            } else {
                holder.button.setText("Contatta");
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
                                    if (!job.getVerified() || Boolean.parseBoolean(snapshot.child("verified").getValue().toString())) {
                                        Intent intent = new Intent(mContext, MessagingActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString("sent", job.getAuthor());
                                        intent.putExtras(b);
                                        mContext.startActivity(intent);
                                    } else {
                                        Toast.makeText(mContext, "Devi verificare l'account prima di contattare questo utente!", Toast.LENGTH_SHORT).show();
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
        if(!job.getVerified())
            holder.verified.setVisibility(View.INVISIBLE);

        holder.linearLayoutAds.setClickable(true);
        holder.linearLayoutAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", job.getKey());
                Fragment selectedFragment = new AdsJobFragment();
                selectedFragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(job.getAuthor()).child("surname").getValue().toString().concat(" ")
                        .concat(snapshot.child(job.getAuthor()).child("name").getValue().toString());
                holder.author.setText(name);
                holder.author.setClickable(true);
                holder.author.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", job.getAuthor());
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

        if(job.getSetted_image()){

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final File finalLocalFile = localFile;
                storageReference.child("job_images/"+job.getKey()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
            public TextView author, title, day, time, reward, location, numOFReviews, status;
            public Button button;
            public RatingBar ratingBar;
            public LinearLayout linearLayoutAds, verified;

            public ViewHolder(View itemView){

                super(itemView);
                title = itemView.findViewById(R.id.job_title);
                author = itemView.findViewById(R.id.job_publisher);
                day = itemView.findViewById(R.id.job_date);
                time = itemView.findViewById(R.id.job_hour);
                reward = itemView.findViewById(R.id.job_proposed_price);
                location = itemView.findViewById(R.id.job_distance);
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

    private void getDistance(final String otherAddress, final TextView location){


        FirebaseUser firebaseUser;

        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getLocation().equals("")) {
                    SpannableString content = new SpannableString(otherAddress);
                    content.setSpan(new UnderlineSpan(), 0, otherAddress.length(), 0);
                    location.setText(content);
                }else {
                    double latitude, otherLat;
                    double longitude, otherLong;
                    float result[] = new float[3];
                    otherLat = getLat(otherAddress);
                    otherLong = getLong(otherAddress);
                    latitude = getLat(user.getLocation());
                    longitude = getLong(user.getLocation());
                    Location.distanceBetween(latitude, longitude, otherLat, otherLong, result);
                    result[0]/=1000;
                    String address = String.valueOf((double)Math.round(result[0] * 10d) / 10d).concat(" km");
                    SpannableString content = new SpannableString(address);
                    content.setSpan(new UnderlineSpan(), 0, address.length(), 0);
                    location.setText(content);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private double getLat(String address){

        Geocoder geocoder;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> geoResults = geocoder.getFromLocationName(address, 1);
           /* while (geoResults.size()==0) {
                geoResults = geocoder.getFromLocationName(address, 1);
            }*/
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                return  addr.getLatitude();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return 0;
    }

    private double getLong(String address){

        Geocoder geocoder;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> geoResults = geocoder.getFromLocationName(address, 1);
           /* while (geoResults.size()==0) {
                geoResults = geocoder.getFromLocationName(address, 1);
            }*/
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                return  addr.getLongitude();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return 0;
    }
}