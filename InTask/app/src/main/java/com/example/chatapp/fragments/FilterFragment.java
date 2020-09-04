package com.example.chatapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapter.JobAdapter;
import com.example.chatapp.adapter.TimeAdapter;
import com.example.chatapp.model.Job;
import com.example.chatapp.model.Time;
import com.example.chatapp.start.RegisterActivity;
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

import static android.content.Context.LOCATION_SERVICE;

public class FilterFragment extends Fragment {

    CheckBox verified, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
            sixTen,tenTwelve, twelveFourteen, fourteenSixteen, sixteenTwenty, twentyMidnight,
            searchJob, searchTime, fiveKm, tenKm, twentyKm, onehundredKm;
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
    double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;


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



        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.wtf("Boh",getString(R.string.accendi_gps));
            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);

        }
        locationManager.requestLocationUpdates("gps", 0, 0, locationListener);


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
        searchJob = view.findViewById(R.id.job);
        searchTime = view.findViewById(R.id.time);
        fiveKm = view.findViewById(R.id.fiveKm);
        tenKm = view.findViewById(R.id.tenKm);
        twentyKm = view.findViewById(R.id.twentyKm);
        onehundredKm = view.findViewById(R.id.onehundredKm);
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

                if(!searchTime.isChecked() && !searchJob.isChecked())
                    Toast.makeText(getContext(), getString(R.string.scegli_quali_annunci_cercare), Toast.LENGTH_SHORT).show();
                else {
                    showAds();
                    if (searchJob.isChecked())
                        getJobs();
                    if (searchTime.isChecked())
                        getTimes();
                }
            }
        });
        return view;
    }

    private void getTimes() {

        final List<Time> timeMyAds = new ArrayList<>();
        final List<Time> timeAds = new ArrayList<>();

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
                                resultZero.setVisibility(View.GONE);
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


    }

    private void showAds() {

        scrollView.setVisibility(View.GONE);
        resultZero.setVisibility(View.VISIBLE);
        recyclerViewJob = view.findViewById(R.id.recycler_view_job);
        recyclerViewJob.setHasFixedSize(true);
        recyclerViewJob.setLayoutManager(new LinearLayoutManager(recyclerViewJob.getContext()));
        recyclerViewTime = view.findViewById(R.id.recycler_view_time);
        recyclerViewTime.setHasFixedSize(true);
        recyclerViewTime.setLayoutManager(new LinearLayoutManager(recyclerViewTime.getContext()));

    }

    private void getJobs() {

        final List<Job> jobMyAds = new ArrayList<>();
        final List<Job> jobAds = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
                                resultZero.setVisibility(View.GONE);
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

        return !time.getAchieved() && !time.getPending() && checkVerified(time.getVerified()) && checkType(time.getType()) && checkDistance(time.getLocation())
                && checkDate(time.getDay()) && checkHour(time.getTime()) && chechRating(average_ratings);

    }

    private boolean chechRating(float average_ratings) {
        return  ratingBar.getRating() == 0.0 || ratingBar.getRating() <= average_ratings;
    }

    private boolean checkHour(String time) {
        if (time.equals("")) return true;
        int a = ((int) time.charAt(0)) - 48;
        int b = ((int) time.charAt(1)) - 48;

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
        if(!sixTen.isChecked() && !tenTwelve.isChecked() &&
                !twelveFourteen.isChecked() && !fourteenSixteen.isChecked() &&
                !sixteenTwenty.isChecked() && !twentyMidnight.isChecked())
            return true;

        return false;
    }

    private boolean checkDate(String day) {
    if(day.equals("")) return true;
        String input_date=day;
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
        if(!monday.isChecked() && !tuesday.isChecked() &&
                !wednesday.isChecked() && !thursday.isChecked() &&
                !friday.isChecked() && !saturday.isChecked() &&
                !sunday.isChecked())
            return true;
        return false;
    }

    private boolean checkType(String type) {
        if(buttonFlorist.isChecked() && (type.equals("Giardinaggio")||type.equals("Gardening")))
            return true;
        if(buttonChild.isChecked() && type.equals("Babysitting"))
            return true;
        if(buttonPizza.isChecked() && (type.equals("Cucinare")||type.equals("Cooking")))
            return true;
        if(buttonLocal.isChecked() && (type.equals("Pulizie")||type.equals("Cleaning")))
            return true;
        if(buttonMenuBook.isChecked() && (type.equals("Ripetizioni")||type.equals("Private lessons")))
            return true;
        if(buttonShipping.isChecked() && (type.equals("Trasloco")||type.equals("Moving")))
            return true;
        if(buttonBuild.isChecked() && (type.equals("Riparazioni")||type.equals("Fixing stuff")))
            return true;
        if(buttonDog.isChecked() && type.equals("Dogsitting"))
            return true;
        if(buttonFitness.isChecked() && type.equals("Personal Training"))
            return true;
        if(buttonComputer.isChecked() && (type.equals("Supporto Informatico")||type.equals("It support")))
            return true;
        if(buttonCar.isChecked() && (type.equals("Trasporto")||type.equals("Transportation")))
            return true;
        if(buttonStore.isChecked() && (type.equals("Spesa")||type.equals("Grocery shopping")))
            return true;
        if(buttonSoccer.isChecked() && (type.equals("Decimo al Calcetto")||type.equals("Soccer player")))
            return true;
        if(buttonMore.isChecked() && (type.equals("Altro")||type.equals("Others")))
            return true;
        if(!buttonFlorist.isChecked() && !buttonChild.isChecked() &&
                !buttonPizza.isChecked() && !buttonLocal.isChecked() &&
                !buttonMenuBook.isChecked() && !buttonShipping.isChecked() &&
                !buttonBuild.isChecked() && !buttonDog.isChecked() &&
                !buttonFitness.isChecked() && !buttonComputer.isChecked() &&
                !buttonCar.isChecked() && !buttonStore.isChecked() &&
                !buttonSoccer.isChecked() && !buttonMore.isChecked())
            return true;
        return false;
    }

    private boolean checkVerified(Boolean bool) {
        return !verified.isChecked() || bool;
    }

    private boolean jobHasToBeShown(Job job, float average_ratings){

        return !job.getAchieved() && !job.getPending() && checkVerified(job.getVerified()) && checkType(job.getType()) && checkDistance(job.getLocation())
                && checkDate(job.getDay()) && checkHour(job.getTime()) && chechRating(average_ratings) &&
                checkReward(job.getReward());
    }

    private boolean checkReward(Float reward) {
        if (minReward.getProgress() == 0)
            if (maxReward.getProgress()==200)
                return true;
            else
                return reward <= maxReward.getProgress();
        else
            if (maxReward.getProgress()==200)
                return reward >= minReward.getProgress();
            else
                return reward >= minReward.getProgress() && reward <= maxReward.getProgress();
    }

    private boolean checkDistance(String otherAddress){

        final double otherLat;
        final double otherLong;
        float result[] = new float[3];


        otherLat = getLat(otherAddress);
        otherLong = getLong(otherAddress);

        Location.distanceBetween(latitude, longitude,otherLat,otherLong,result);
        if (onehundredKm.isChecked() && (result[0] / 1000) <= 100)
            return true;
        if (twentyKm.isChecked() && (result[0] / 1000) <= 20)
            return true;
        if (tenKm.isChecked() && (result[0] / 1000) <= 10)
            return true;
        if (fiveKm.isChecked() && (result[0] / 1000) <= 5)
            return true;
        if (!twentyKm.isChecked() && !tenKm.isChecked() &&!fiveKm.isChecked() &&!onehundredKm.isChecked())
                return true;

        return false;
    }

    private double getLat(String address){

        Geocoder geocoder;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
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
        geocoder = new Geocoder(getContext(), Locale.getDefault());
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