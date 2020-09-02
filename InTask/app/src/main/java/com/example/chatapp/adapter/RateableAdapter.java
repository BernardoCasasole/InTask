package com.example.chatapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Rateable;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RateableAdapter extends RecyclerView.Adapter<RateableAdapter.ViewHolder> {

    private Context mContext;
    private List<Rateable> mRateable;

    public RateableAdapter(Context mContext, List<Rateable> mAds) {
        this.mContext = mContext;
        this.mRateable = mAds;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pending_review_item,parent,false);
        return new RateableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Rateable rateable = mRateable.get(position);
        FirebaseDatabase.getInstance().getReference("Users").child(rateable.getUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                String title = getApplicationContext().getResources().getString(R.string.valuta_da_1_a_5_la_tua_esperienza_con) + user.getName() + getApplicationContext().getResources().getString(R.string.relativa_al_lavoro) + rateable.getTitle() + ".";

                holder.title.setText(title);
                holder.buttonRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float rate = holder.ratingBar.getRating();
                        float newRate = (user.getAverage_ratings()*user.getRatings() + rate)/(user.getRatings() + 1);
                        FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("ratings"). setValue(user.getRatings() + 1);
                        FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("average_ratings"). setValue(newRate);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rateable").child(rateable.getKey()).removeValue();
                        Toast.makeText(mContext, getApplicationContext().getResources().getString(R.string.utente_valutato_correttamente), Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return mRateable.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView title;
        public Button buttonRate;
        public RatingBar ratingBar;

        public ViewHolder(View itemView){

            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_user);
            title = itemView.findViewById(R.id.pending_reviews_text);
            buttonRate = itemView.findViewById(R.id.user_data_update_button);

        }
    }
}