package com.example.chatapp.fragments.ads;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


public class JobSvFormFragment extends Fragment {

    EditText title, description, reward, day, time, duration, location;
    RadioGroup type;
    ImageView imageView;
    RadioButton radioButton;
    CheckBox verified;
    Button btn_publish;
    View rootView;

    Calendar myCalendar;

    Uri mImageUri;
    Boolean uploaded = false;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    double latitude, longitude;
    Button btn_get_position;
    LocationManager locationManager;
    LocationListener locationListener;
    GoogleMap googleMap;
    int requestCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_job_sv_form, container, false);


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.wtf("Amico", "accendi GPS");
            }
        };


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);

        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Job");
        storageReference = FirebaseStorage.getInstance().getReference();


        title = rootView.findViewById(R.id.proposal_title);
        description = rootView.findViewById(R.id.proposal_description);
        reward = rootView.findViewById(R.id.proposal_reward);
        location = rootView.findViewById(R.id.proposal_location);
        day = rootView.findViewById(R.id.proposal_day);
        time = rootView.findViewById(R.id.proposal_time);
        duration = rootView.findViewById(R.id.proposal_duration);
        type = rootView.findViewById(R.id.proposal_type);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        day.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =

                        new DatePickerDialog(getContext(),date , myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        imageView = rootView.findViewById(R.id.image);
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);

            }
        });
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location.setText(snapshot.child(firebaseUser.getUid()).child("location").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_get_position = rootView.findViewById(R.id.position_button);

        btn_get_position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());


                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    location.setText(address);
                } catch (IOException | IndexOutOfBoundsException e) {
                    Toast.makeText(getContext(), "Indirizzo non trovato", Toast.LENGTH_SHORT).show();
                }


            }
        });

        /*getMaps = rootView.findViewById(R.id.get_maps);
        getMaps.setClickable(true);
        getMaps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setTargetFragment( new MapFragment(), requestCode);
                getTargetFragment();


                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MapFragment(),

                        "JOB_FRAGMENT").addToBackStack("JOB_FRAGMENT").commit();


            }
        });
*/

        btn_publish = rootView.findViewById(R.id.proposal_btn_publish);
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioButton = rootView.findViewById(type.getCheckedRadioButtonId());
                verified = rootView.findViewById(R.id.proposal_verified);

                String title_text, description_text, reward_text, day_text, time_text, duration_text, location_text, key;

                title_text = title.getText().toString();
                description_text = description.getText().toString();
                reward_text = reward.getText().toString();
                location_text = location.getText().toString();
                day_text = day.getText().toString();
                time_text = time.getText().toString();
                duration_text = duration.getText().toString();

                if (!title_text.equals("") &&
                        !description_text.equals("") &&
                        !reward_text.equals("") &&
                        !location_text.equals("") &&
                        !day_text.equals("") &&
                        !time_text.equals("") &&
                        !duration_text.equals("") &&
                        checkHour(time_text) &&
                        radioButton != null) {
                    databaseReference.push();
                    key = databaseReference.push().getKey();
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", key);
                    map.put("author", firebaseUser.getUid());
                    map.put("title", title_text);
                    map.put("description", description_text);
                    map.put("reward",Float.parseFloat(reward_text));
                    map.put("location", location_text);
                    map.put("day", day_text);
                    map.put("time", time_text);
                    map.put("duration", duration_text);
                    map.put("type", radioButton.getText().toString());
                    map.put("verified", verified.isChecked());
                    map.put("setted_image", uploaded);
                    map.put("pending", false);
                    map.put("achieved", false);

                    Toast.makeText(getContext(), "Annuncio pubblicato con successo", Toast.LENGTH_SHORT).show();
                    if (uploaded) {

                        StorageReference childRef = storageReference.child("/job_images/" + key + ".jpg");
                        childRef.putFile(mImageUri);
                    }
                    databaseReference.child(key).setValue(map);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PublicationChoiceFragment()).commit();

                } else
                    Toast.makeText(getContext(), "Devi riempire tutti i campi correttamente", Toast.LENGTH_SHORT).show();


            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode != 0) {
            mImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
                imageView.setImageBitmap(bitmap);
                uploaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   /*@Override
    public void onStart() {
        super.onStart();
        Bundle b = getActivity().getIntent().getExtras();

        if (b != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());


            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(b.getString("Lat")), Double.parseDouble(b.getString("Long")), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                location.setText(address);
            } catch (IOException | IndexOutOfBoundsException e) {
                Toast.makeText(getContext(), "Indirizzo non trovato", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        day.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkHour(String result){

        int a = ((int) result.charAt(0)) - 48;
        int b = ((int) result.charAt(1)) - 48;
        int c = ((int) result.charAt(3)) - 48;
        if(a < 0 || b < 0 || c < 0) {
            Toast.makeText(getContext(), "Inserire orario corretto", Toast.LENGTH_SHORT).show();
            return false;
        }

        if((a > 2 || (a == 2 && b > 3)) || c > 59) {
            Toast.makeText(getContext(), "Inserire orario corretto", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
}