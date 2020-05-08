package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class NoCoronaActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nocorona);

        String apiKey;
        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance
        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        //        autocompleteFragment.getView().se;

        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setTextColor(Color.WHITE);
        etPlace.setHintTextColor(Color.WHITE);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = db.getReference();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull final Place place) {

                myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("OVER HERE", "1");
                        int coronaCount0 = 0;

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Boolean bool = (Boolean) snap.child("corona").getValue(true);
                            Log.d("OVER HERE", "2");
                            if(bool) {
                                Log.d("OVER HERE", "3");
                                if (snap.child("places").exists()) {
                                    Log.d("OVER HERE", "4");
                                    Log.d("OVER HERE", snap.child("places").getChildrenCount() + "");

                                    for (DataSnapshot mPlace : snap.child("places").getChildren()) {
                                        Log.d("OVER HERE", "5");
                                        Long thenTime = (Long) mPlace.child("time").getValue(true);
                                        String thenPlace = (String) mPlace.child("place_id").getValue(true);
                                        Log.d("OVER HERE", thenPlace);
                                        float current = System.currentTimeMillis();
                                        if (current - 604800000 < thenTime && thenPlace.equals(place.getId())) {
                                            Log.d("OVER HERE", "made it");
                                            coronaCount0 += 1;
                                        }
                                    }

                                }
                            }

                        }

                        final int coronaCount = coronaCount0;

                        // Define a Place ID.
                        String placeId = place.getId();

                        // Specify the fields to return.
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL);

                        // Construct a request object, passing the place ID and fields array.
                        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                        PlacesClient placesClient = Places.createClient(getApplicationContext());

                        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                              @Override
                              public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                  Place placeDeets = fetchPlaceResponse.getPlace();
                                  int priceLevel = 2;
                                  if (placeDeets.getPriceLevel() != null) {
                                      priceLevel = placeDeets.getPriceLevel();
                                  }
                                  Double rating = placeDeets.getRating();
                                  int temp = (int)(rating*100.0);
                                  double shortDouble = ((double)temp)/100.0;
                                  int userRatings = 23;
                                  if (placeDeets.getUserRatingsTotal() != null) {
                                      userRatings = placeDeets.getUserRatingsTotal();
                                  }

                                  TextView moneyView = (TextView) findViewById(R.id.money);
                                  TextView ratingView = (TextView) findViewById(R.id.rating);
                                  TextView numView = (TextView) findViewById(R.id.numRatings);

                                  moneyView.setText(priceLevel + "");
                                  ratingView.setText(shortDouble + "");
                                  numView.setText(userRatings + "");


                                  double coronaRisk = 0;

                                  double dPLevel = (double) priceLevel;
                                  dPLevel = ((1/(dPLevel/4))/4) * .15;

                                  double uRatings = 0;
                                  if (userRatings < 5) {
                                      uRatings = .1;
                                  } if (userRatings > 5 && userRatings < 100) {
                                      uRatings = .2;
                                  } else if (userRatings < 300) {
                                      uRatings = .5;
                                  } else if (userRatings < 500) {
                                      uRatings = .8;
                                  } else {
                                      uRatings = 1;
                                  }
                                  uRatings = uRatings * .25;

                                  rating = (rating/5) * .1;

                                  if (coronaCount == 0) {
                                      coronaRisk = 0.28;
                                  } else if (coronaCount < 4) {
                                      coronaRisk = .8;
                                  } else {
                                      coronaRisk = 1;
                                  }

                                  coronaRisk = coronaRisk * .5;

                                  Double finalCalc = dPLevel + uRatings + rating + coronaRisk;
                                  Log.d("Vals!!" , dPLevel + " " + uRatings + " " + rating + " " + coronaRisk + " " + finalCalc);
                                  String riskLevel = "";

                                  TextView riskView = (TextView) findViewById(R.id.risk);

                                  if(finalCalc > .70) {
                                      riskLevel = "High";
                                      riskView.setTextColor(Color.parseColor("#ff0000"));
                                  } else if (finalCalc > .38) {
                                      riskLevel = "Medium";
                                      riskView.setTextColor(Color.parseColor("#ffff00"));
                                  } else {
                                      riskLevel = "Low";
                                      riskView.setTextColor(Color.parseColor("#7CFC00"));
                                  }
                                  riskView.setText(riskLevel);


                              }
                          }
                        );

                        Log.d("DEETS", place.getRating() + "");


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("My Tag", "onCancelled", databaseError.toException());
                    }
                });


//                final int coronaCount = mCount.count;
//
//                // Define a Place ID.
//                String placeId = place.getId();
//
//                // Specify the fields to return.
//                List<Place.Field> placeFields = Arrays.asList(Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL);
//
//                // Construct a request object, passing the place ID and fields array.
//                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//
//                PlacesClient placesClient = Places.createClient(getApplicationContext());
//
//                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//                      @Override
//                      public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
//                          Place placeDeets = fetchPlaceResponse.getPlace();
//                          int priceLevel = 2;
//                          if (placeDeets.getPriceLevel() != null) {
//                              priceLevel = placeDeets.getPriceLevel();
//                          }
//                          Double rating = placeDeets.getRating();
//                          int temp = (int)(rating*100.0);
//                          double shortDouble = ((double)temp)/100.0;
//                          int userRatings = 23;
//                          if (placeDeets.getUserRatingsTotal() != null) {
//                              userRatings = placeDeets.getUserRatingsTotal();
//                          }
//
//                          TextView moneyView = (TextView) findViewById(R.id.money);
//                          TextView ratingView = (TextView) findViewById(R.id.rating);
//                          TextView numView = (TextView) findViewById(R.id.numRatings);
//
//                          moneyView.setText(priceLevel + "");
//                          ratingView.setText(shortDouble + "");
//                          numView.setText(userRatings + "");
//
//
//                          double coronaRisk = 0;
//
//                          double dPLevel = (double) priceLevel;
//                          dPLevel = ((1/(dPLevel/4))/4) * .15;
//
//                          double uRatings = 0;
//                          if (userRatings < 5) {
//                              uRatings = .1;
//                          } if (userRatings > 5 && userRatings < 100) {
//                              uRatings = .2;
//                          } else if (userRatings < 300) {
//                              uRatings = .5;
//                          } else if (userRatings < 500) {
//                              uRatings = .8;
//                          } else {
//                              uRatings = 1;
//                          }
//                          uRatings = uRatings * .25;
//
//                          rating = (rating/5) * .1;
//
//                          if (coronaCount == 0) {
//                              coronaRisk = 0.28;
//                          } else if (coronaCount < 4) {
//                              coronaRisk = .8;
//                          } else {
//                              coronaRisk = 1;
//                          }
//
//                          coronaRisk = coronaRisk * .5;
//
//                          Double finalCalc = dPLevel + uRatings + rating + coronaRisk;
//                          Log.d("Vals!!" , dPLevel + " " + uRatings + " " + rating + " " + coronaRisk + " " + finalCalc);
//                          String riskLevel = "";
//
//                          TextView riskView = (TextView) findViewById(R.id.risk);
//
//                          if(finalCalc > .70) {
//                              riskLevel = "High";
//                              riskView.setTextColor(Color.parseColor("#ff0000"));
//                          } else if (finalCalc > .38) {
//                              riskLevel = "Medium";
//                              riskView.setTextColor(Color.parseColor("#ffff00"));
//                          } else {
//                              riskLevel = "Low";
//                              riskView.setTextColor(Color.parseColor("#7CFC00"));
//                          }
//                          riskView.setText(riskLevel);
//
//
//                      }
//                  }
//                );
//
//                Log.d("DEETS", place.getRating() + "");


            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("STATUSMSG", status.getStatusMessage());

            }
        });


    }

    public class MyCount {

        public int count;

        public MyCount() {
            this.count = 0;
        }

        public void add1() {
            this.count += 1;
        }

    }
}
