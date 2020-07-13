package com.example.showloc;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    public FirebaseFirestore db;
    String snippet;
    LatLng tempLatLng;
    AlertDialog.Builder builder;
    ArrayList<String> ids;
    String tempID;
    Marker tempMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db =FirebaseFirestore.getInstance(); //Ετοίμασα την Firebase

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Εδώ ετοιμάζω ένα Popup στην περίπτωση που ο χρήστης θέλει να διαγράψει κάποιο marker.
        builder = new AlertDialog.Builder(this).setCancelable(true).setTitle("Διαγραφή Marker").setMessage("Θέλεις να διαγραφεί αυτό το Marker;");
        builder.setPositiveButton("Ναί", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Διαγράφουμε το collection ΠΟυ αντιστοιχεί στο marker
                db.collection("Locations").document(tempID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Επιτυχής διαγραφή",Toast.LENGTH_SHORT).show();
                        tempMarker.remove(); //Αφαίρεση του marker απ το χάρτη
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Αποτυχία διαγραφής",Toast.LENGTH_SHORT).show();
                        Log.e("OnDelete: ",e+"");
                    }
                });

                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Συνδέω το πρόγραμμα με τη βάζη δεδομένων και πέρνω όλα τα documents του collection ¨Locations¨
        final CollectionReference colRef = db.collection("Locations");
        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ids = new ArrayList<>(); //Αποθηκεύω το id του κάθε document
               for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){ //Δημιουργώ τόσα Markers, ώσα και οι εγγραφές μου στη Firestore
                   //Αφού έχω πάρει όλες τις πληροφορίες, τις τοποθετώ στα ανάλογα πεδία
                   ids.add(documentSnapshot.getId()); //Το id του document
                   Location location = documentSnapshot.toObject(Location.class);
                   String comment =location.getComment(); //Σχόλιο χρήστη
                   String gravityH =location.getGravH(); //Βαρύτητα Height
                   String gravityW =location.getGravW(); //Βαρύτητα Weight
                   String tempR=location.getTempR(); //Θερμοκρασία
                   String humidR=location.getHumidR();//Υγρασία
                   float markerColor =location.getMarkerColor(); //Χρώμμα marker
                   GeoPoint geoPoint =location.getGeopoint(); //GeoPoint της τοποθεσίας

                   //Αναλύω το GeoPoint σε Latitude και Longitude
                   double latitude = geoPoint.getLatitude();
                   double longitude = geoPoint.getLongitude();

                   //Δημιουργώ την τοποθεσία
                   final LatLng latLng = new LatLng(latitude,longitude);
                   Geocoder geocoder = new Geocoder(getApplicationContext()); //Πέρνω πληροφορίες για την τοποθεσία που δημιούργησα

                   //Ξεκινάω να δημιουργώ το Marker
                   try {
                       List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                        //Ελέγχω αν υπάρχει η πόλη του Marker(Σε περίπτωση που είναι null δεν το αφήνω να φανεί.)
                       String locality =addressList.get(0).getLocality();
                       if(locality==null) locality="Άγνωστο";

                       String countryName = addressList.get(0).getCountryName();
                       if(countryName==null) countryName="Άγνωστο";

                       String markertxt =locality +" , "+ countryName;
                       snippet="";
                        //Ελέγχω αν οι Sensor έχουν τιμή για να τους εμφανίσω.
                       if(!gravityH.equals("-"))
                           snippet+="\nGravity Height: "+gravityH;
                       if(!gravityW.equals("-"))
                           snippet+="\nGravity Weight: "+gravityW;
                       if(!tempR.equals("-"))
                           snippet+="\nΘερμοκρασία: "+tempR;
                       if(!humidR.equals("-"))
                           snippet="\nΥγρασία: "+humidR;

                       snippet+="\nΠεριγραφή: "+comment;
                       //Δημιουργώ το Marker για το σημείο
                       mMap.addMarker(new MarkerOptions().draggable(true).position(latLng).title(markertxt).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.2f));
                       //Επειδή δεν υπάρχει τρόπος για OnLongMarkerClick ή κάτι τέτοιο, κάθε φορά που
                       //ο χρήστης πάει να κάνει "Drag" ένα marker πριν προλάβει να κουνίσει το Marker
                       //εμφανίζεται μήνυμα που τον ρωτάει αν θέλει να διαγράψει το marker.
                       mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                           @Override
                           public void onMarkerDragStart(Marker marker) {
                               tempLatLng = marker.getPosition();
                               marker.setDraggable(false);
                               tempMarker = marker; //Αποθήκευση του marker
                               tempID= ids.get(Integer.parseInt(marker.getId().substring(1))); //Αποθήκευση του collection του Marker
                               builder.create().show(); //Εμφανίζω το μήνυμα για διαγραφή
                           }

                           @Override
                           public void onMarkerDrag(Marker marker) {
                                marker.setDraggable(false);
                           }

                           @Override
                           public void onMarkerDragEnd(Marker marker) {
                                marker.setPosition(tempLatLng);
                                marker.setDraggable(true);
                                //Επαναφέρω το marker στην αρχική του θέση
                           }
                       });
                       //Επειδή το snippet είναι μεγάλο, φτιάχνω custom τρόπο εμφάνισης με το InfoWindowAdapter
                       mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                           @Override
                           public View getInfoWindow(Marker marker) {
                               return null;
                           }
                           @Override
                           public View getInfoContents(Marker marker) {
                               //Συνδέω το custom_snippet με το πρόγραμμα
                               View mWindow = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_snippet, null);
                               TextView tvtitle = mWindow.findViewById(R.id.title);
                               TextView tvsnippet = mWindow.findViewById(R.id.snippet);
                               tvtitle.setText(marker.getTitle());
                               tvsnippet.setText(marker.getSnippet());

                               return mWindow;
                           }
                       });
                       System.out.print(snippet);
                   } catch (Exception e) {
                      //Σε περίπτωση που κάτι πάει λάθος
                       e.printStackTrace();
                   }
               }
                //Σε περίπτωση που η βάση δεν έχει κάποια εγγραφή
               try{
                   ids.get(0);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα Markers", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Στην περίπτωση που δεν δουλεύει το Query
                Toast.makeText(getApplicationContext(),"Υπάρχει κάποιο σφάλμα στη σύνδεση με τη βάση",Toast.LENGTH_SHORT).show();
                Log.e("ShowLocations: ",e+"");
            }
        });

    }
}
