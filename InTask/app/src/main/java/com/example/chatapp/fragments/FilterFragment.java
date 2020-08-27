package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.adapter.JobAdapter;
import com.example.chatapp.adapter.TimeAdapter;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FilterFragment extends Fragment {

    CheckBox verified, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
            sixTen,tenTwelve, twelveFourteen, fourteenSixteen, sixteenTwenty, twentyMidnight;
    Button filterButton;
    View view;
    RatingBar ratingBar;
    CheckBox buttonFlorist, buttonChild, buttonPizza, buttonLocal, buttonMenuBook, buttonShipping,
            buttonBuild, buttonDog, buttonFitness, buttonComputer, buttonCar, buttonStore, buttonSoccer, buttonMore;
    RecyclerView recyclerViewJob, recyclerViewTime;
    LinearLayout resultZero, resultNotZero;
    SeekBar minReward, maxReward;
    TextView min, max;
    Boolean found;
    ScrollView scrollView;
    String myAds;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filter, container, false);
        final Bundle b = getArguments();
        myAds = "true";
        if(b != null) {
            myAds = b.getString("myAds");
        }
        found = false;
        verified = view.findViewById(R.id.verified);
        filterButton = view.findViewById(R.id.filter_btn);
        scrollView = view.findViewById(R.id.scrollView);
        buttonFlorist = view.findViewById(R.id.button_florist);
        buttonChild = view.findViewById(R.id.button_child);
        buttonPizza = view.findViewById(R.id.button_pizza);
        buttonLocal = view.findViewById(R.id.button_local);
        buttonMenuBook = view.findViewById(R.id.button_menu_book);
        buttonShipping = view.findViewById(R.id.button_shipping);
        buttonBuild = view.findViewById(R.id.button_build);
        buttonDog = view.findViewById(R.id.button_dog);
        buttonFitness = view.findViewById(R.id.button_fitness);
        buttonComputer = view.findViewById(R.id.button_computer);
        buttonCar = view.findViewById(R.id.button_car);
        buttonStore = view.findViewById(R.id.button_store);
        buttonSoccer = view.findViewById(R.id.button_soccer);
        buttonMore = view.findViewById(R.id.button_more);
        ratingBar = view.findViewById(R.id.rating_user);
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);
        sixTen= view.findViewById(R.id.SixAm_TenAm);
        tenTwelve= view.findViewById(R.id.TenAm_TwelvePm);
        twelveFourteen= view.findViewById(R.id.TwelvePm_TwoPm);
        fourteenSixteen= view.findViewById(R.id.TwoPm_FourPm);
        sixteenTwenty= view.findViewById(R.id.FourPm_EightPm);
        twentyMidnight= view.findViewById(R.id.EightPm_TwelveAm);
        resultZero = view.findViewById(R.id.results_list_size_zero);
        resultNotZero = view.findViewById(R.id.results_list_size_not_zero);
        minReward = view.findViewById(R.id.minimum_retribution);
        maxReward = view.findViewById(R.id.maximum_retribution);
        min = view.findViewById(R.id.min_curentValue);
        max = view.findViewById(R.id.max_curentValue);
        min.setText(String.valueOf(minReward.getProgress()));
        minReward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                min.setText(String.valueOf(minReward.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                min.setText(String.valueOf(minReward.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                min.setText(String.valueOf(minReward.getProgress()));
            }
        });
        max.setText(String.valueOf(maxReward.getProgress()));
        maxReward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                max.setText(String.valueOf(maxReward.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                max.setText(String.valueOf(maxReward.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                max.setText(String.valueOf(maxReward.getProgress()));
            }
        });

        resultZero.setVisibility(View.GONE);
        resultNotZero.setVisibility(View.GONE);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds();
                getAds();
            }
        });
        return view;
    }

    private void showAds() {

        scrollView.setVisibility(View.GONE);
        recyclerViewJob = view.findViewById(R.id.recycler_view_job);
        recyclerViewJob.setHasFixedSize(true);
        recyclerViewJob.setLayoutManager(new LinearLayoutManager(recyclerViewJob.getContext()));
        recyclerViewTime = view.findViewById(R.id.recycler_view_time);
        recyclerViewTime.setHasFixedSize(true);
        recyclerViewTime.setLayoutManager(new LinearLayoutManager(recyclerViewTime.getContext()));

    }

    private void getAds() {
        final List<Time> timeMyAds = new ArrayList<>();
        final List<Job> jobMyAds = new ArrayList<>();
        final List<Time> timeAds = new ArrayList<>();
        final List<Job> jobAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Time");


        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                timeAds.clear();
                timeMyAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final Time time = snapshot.getValue(Time.class);
                    FirebaseDatabase.getInstance().getReference("Users").child(time.getAuthor()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (timeHasToBeShown(time,Float.parseFloat(snapshot.child("average_ratings").getValue().toString()))){
                                found = true;
                                resultNotZero.setVisibility(View.VISIBLE);
                                if (firebaseUser == null || (!time.getAuthor().equals(firebaseUser.getUid()))) {
                                    timeAds.add(time);
                                } else
                                    timeMyAds.add(time);
                            }

                            if (Boolean.parseBoolean(myAds))
                                recyclerViewTime.setAdapter(new TimeAdapter(recyclerViewTime.getContext(), timeMyAds, Boolean.parseBoolean(myAds)));
                            else
                                recyclerViewTime.setAdapter(new TimeAdapter(recyclerViewTime.getContext(), timeAds, Boolean.parseBoolean(myAds)));

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Job");
        reference1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                jobAds.clear();
                jobMyAds.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final Job job = snapshot.getValue(Job.class);
                    FirebaseDatabase.getInstance().getReference("Users").child(job.getAuthor()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (jobHasToBeShown(job,Float.parseFloat(snapshot.child("average_ratings").getValue().toString()))){
                                found = true;
                                resultNotZero.setVisibility(View.VISIBLE);
                                if (firebaseUser == null || (!job.getAuthor().equals(firebaseUser.getUid()))) {
                                    jobAds.add(job);
                                } else
                                    jobMyAds.add(job);
                            }

                            if (Boolean.parseBoolean(myAds))
                                recyclerViewJob.setAdapter(new JobAdapter(recyclerViewJob.getContext(), jobMyAds, Boolean.parseBoolean(myAds)));
                            else
                                recyclerViewJob.setAdapter(new JobAdapter(recyclerViewJob.getContext(), jobAds, Boolean.parseBoolean(myAds)));

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean timeHasToBeShown(Time time, float average_ratings){
        if(verified.isChecked() && time.getVerified())
            return true;
        if(buttonFlorist.isChecked() && time.getType().equals("Giardinaggio"))
            return true;
        if(buttonChild.isChecked() && time.getType().equals("Babysitting"))
            return true;
        if(buttonPizza.isChecked() && time.getType().equals("Cucinare"))
            return true;
        if(buttonLocal.isChecked() && time.getType().equals("Pulizie"))
            return true;
        if(buttonMenuBook.isChecked() && time.getType().equals("Ripetizioni"))
            return true;
        if(buttonShipping.isChecked() && time.getType().equals("Trasloco"))
            return true;
        if(buttonBuild.isChecked() && time.getType().equals("Riparazioni"))
            return true;
        if(buttonDog.isChecked() && time.getType().equals("Dogsitting"))
            return true;
        if(buttonFitness.isChecked() && time.getType().equals("Personal Training"))
            return true;
        if(buttonComputer.isChecked() && time.getType().equals("Supporto Informatico"))
            return true;
        if(buttonCar.isChecked() && time.getType().equals("Trasporto"))
            return true;
        if(buttonStore.isChecked() && time.getType().equals("Spesa"))
            return true;
        if(buttonSoccer.isChecked() && time.getType().equals("Decimo al Calcetto"))
            return true;
        if(buttonMore.isChecked() && time.getType().equals("Altro"))
            return true;
        if(ratingBar.getRating() > 0.0 && ratingBar.getRating() <= average_ratings)
            return true;
        String input_date=time.getDay();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Date myDate = null;
        try {
            myDate = sdf.parse(input_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern("EEEE");
        String finalDay = sdf.format(myDate);
        if(wednesday.isChecked() && finalDay.equals("lunedì"))
           return true;
        if(thursday.isChecked() && finalDay.equals("martedì"))
            return true;
        if(friday.isChecked() && finalDay.equals("mercoledì"))
            return true;
        if(saturday.isChecked() && finalDay.equals("giovedì"))
            return true;
        if(sunday.isChecked() && finalDay.equals("venerdì"))
            return true;
        if(monday.isChecked() && finalDay.equals("sabato"))
            return true;
        if(tuesday.isChecked() && finalDay.equals("domenica"))
            return true;

        int a = ((int) time.getTime().charAt(0)) - 48;
        int b = ((int) time.getTime().charAt(1)) - 48;

        if(sixTen.isChecked() && a < 1)
            return true;
        if(tenTwelve.isChecked() && a == 1 && b < 2)
            return true;
        if(twelveFourteen.isChecked() && a == 1 && b >= 2 && b < 4)
            return true;
        if(fourteenSixteen.isChecked() && a == 1 && b >= 4 && b < 6)
            return true;
        if(sixteenTwenty.isChecked() && a == 1 && b >= 6)
            return true;
        if(twentyMidnight.isChecked() && a == 2)
            return true;

        return false;
    }

    private boolean jobHasToBeShown(Job job, float average_ratings){
        if(verified.isChecked() && job.getVerified())
            return true;
        if(buttonFlorist.isChecked() && job.getType().equals("Giardinaggio"))
            return true;
        if(buttonChild.isChecked() && job.getType().equals("Babysitting"))
            return true;
        if(buttonPizza.isChecked() && job.getType().equals("Cucinare"))
            return true;
        if(buttonLocal.isChecked() && job.getType().equals("Pulizie"))
            return true;
        if(buttonMenuBook.isChecked() && job.getType().equals("Ripetizioni"))
            return true;
        if(buttonShipping.isChecked() && job.getType().equals("Trasloco"))
            return true;
        if(buttonBuild.isChecked() && job.getType().equals("Riparazioni"))
            return true;
        if(buttonDog.isChecked() && job.getType().equals("Dogsitting"))
            return true;
        if(buttonFitness.isChecked() && job.getType().equals("Personal Training"))
            return true;
        if(buttonComputer.isChecked() && job.getType().equals("Supporto Informatico"))
            return true;
        if(buttonCar.isChecked() && job.getType().equals("Trasporto"))
            return true;
        if(buttonStore.isChecked() && job.getType().equals("Spesa"))
            return true;
        if(buttonSoccer.isChecked() && job.getType().equals("Decimo al Calcetto"))
            return true;
        if(buttonMore.isChecked() && job.getType().equals("Altro"))
            return true;
        if(ratingBar.getRating() > 0.0 && ratingBar.getRating() <= average_ratings) {
            return true;
        }

        String input_date=job.getDay();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Date myDate = null;
        try {
            myDate = sdf.parse(input_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern("EEEE");
        String finalDay = sdf.format(myDate);
        if(wednesday.isChecked() && finalDay.equals("lunedì"))
            return true;
        if(thursday.isChecked() && finalDay.equals("martedì"))
            return true;
        if(friday.isChecked() && finalDay.equals("mercoledì"))
            return true;
        if(saturday.isChecked() && finalDay.equals("giovedì"))
            return true;
        if(sunday.isChecked() && finalDay.equals("venerdì"))
            return true;
        if(monday.isChecked() && finalDay.equals("sabato"))
            return true;
        if(tuesday.isChecked() && finalDay.equals("domenica"))
            return true;
        int a = ((int) job.getTime().charAt(0)) - 48;
        int b = ((int) job.getTime().charAt(1)) - 48;

        if(sixTen.isChecked() && a < 1)
            return true;
        if(tenTwelve.isChecked() && a == 1 && b < 2)
            return true;
        if(twelveFourteen.isChecked() && a == 1 && b >= 2 && b < 4)
            return true;
        if(fourteenSixteen.isChecked() && a == 1 && b >= 4 && b < 6)
            return true;
        if(sixteenTwenty.isChecked() && a == 1 && b >= 6)
            return true;
        if(twentyMidnight.isChecked() && a == 2)
            return true;
        if(job.getReward() >= minReward.getProgress())
            return true;
        if(job.getReward()<= maxReward.getProgress())
            return true;

        return false;
    }

}