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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

//import com.example.chatapp.ProfileActivity;
import com.example.chatapp.PopupActivity;
import com.example.chatapp.R;
import com.example.chatapp.fragments.ads.PublicationChoiceFragment;
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

public class PublicationTimeFragment extends Fragment {

    EditText title, description, day, time, distance, location;
    ImageView imageView;
    RadioGroup type;
    RadioButton radioButton;
    CheckBox verified;
    Button btn_publish;
    Uri mImageUri;
    Boolean uploaded = false;
    Calendar myCalendar;

    View rootView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    private static Bitmap Image = null;
    private static final int GALLERY = 1;

    double latitude, longitude;
    Button getPosition;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_publication_time, container, false);

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
           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Time");
        storageReference = FirebaseStorage.getInstance().getReference();

        title = rootView.findViewById(R.id.title);
        description = rootView.findViewById(R.id.description);
        location = rootView.findViewById(R.id.location);
        time = rootView.findViewById(R.id.time);
        day = rootView.findViewById(R.id.day);
        distance = rootView.findViewById(R.id.distance);
        type = rootView.findViewById(R.id.type);
        getPosition = rootView.findViewById(R.id.position_button);
        getPosition.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(getContext(), getString(R.string.indirizzo_non_trovato), Toast.LENGTH_SHORT).show();
                }


            }
        });
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

        btn_publish = rootView.findViewById(R.id.btn_publish);

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioButton = rootView.findViewById(type.getCheckedRadioButtonId());
                verified = rootView.findViewById(R.id.verified);

                String title_text, description_text, day_text, time_text, distance_text, location_text, key;


                title_text = title.getText().toString();
                description_text = description.getText().toString();
                location_text = location.getText().toString();
                day_text = day.getText().toString();
                time_text = time.getText().toString();
                distance_text = distance.getText().toString();

                if (!title_text.equals("") &&
                        !description_text.equals("") &&
                        !location_text.equals("") &&
                        !distance_text.equals("") &&
                        checkHour(time_text) &&
                        radioButton != null) {
                    databaseReference.push();
                    key = databaseReference.push().getKey();
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", key);
                    map.put("author", firebaseUser.getUid());
                    map.put("title", title_text);
                    map.put("description", description_text);
                    map.put("location", location_text);
                    map.put("day", day_text);
                    map.put("time", time_text);
                    map.put("distance", distance_text);
                    map.put("type", radioButton.getText().toString());
                    map.put("verified", verified.isChecked());
                    map.put("setted_image", uploaded);
                    map.put("pending", false);
                    map.put("achieved", false);
                    Toast.makeText(getContext(), getString(R.string.annuncio_pubblicato_con_successo), Toast.LENGTH_SHORT).show();

                    databaseReference.child(key).setValue(map);
                    if (uploaded) {

                        StorageReference childRef = storageReference.child("/time_images/" + key + ".jpg");
                        childRef.putFile(mImageUri);
                    }
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PublicationChoiceFragment()).commit();

                } else
                    Toast.makeText(getContext(), getString(R.string.riempi_tutti_i_campi_correttamente), Toast.LENGTH_SHORT).show();


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
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        day.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkHour(String result){
        if(result.equals("")) return true;
        int a = ((int) result.charAt(0)) - 48;
        int b = ((int) result.charAt(1)) - 48;
        int c = ((int) result.charAt(3)) - 48;
        if(a < 0 || b < 0 || c < 0) {
            Toast.makeText(getContext(), R.string.inserire_orario_corretto, Toast.LENGTH_SHORT).show();
            return false;
        }

        if((a > 2 || (a == 2 && b > 3)) || c > 59) {
            Toast.makeText(getContext(), R.string.inserire_orario_corretto, Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

}