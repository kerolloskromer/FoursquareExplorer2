package com.kromer.foursquareexplorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.CheckInListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends FragmentActivity implements AccessTokenRequestListener, ImageRequestListener {
    private Marker currentPositionMarker;
    private GoogleMap mMap;
    private boolean successVenues = false;
    private boolean successVenueID = false;
    private EasyFoursquareAsync async;
    Location currentLocation = new Location("");
    GPSTracker gps;
    SharedPrefVenues sharedPrefVenues;
    CheckInternetConnection checkNetConn;
    AsyncHttpClient clientGetAllVenues = new AsyncHttpClient();
    AsyncHttpClient clientGetVenue = new AsyncHttpClient();
    TextView tvUserName;
    ProgressBar progressBar;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    LinearLayout llGallery;
    AlertDialog.Builder builder;
    VenuesModel venuesData = new VenuesModel();
    VenueModel venueData = new VenueModel();
    private RecyclerView recyclerViewGallery;
    GalleryAdapter galleryAdapter;
    int checkInPos;
    ArrayList<String> imagesUrl = new ArrayList<String>();
    Button btnMorInfo;
    ImageView imvUserImage;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void initUI() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        builder = new AlertDialog.Builder(this);

        ImageLoader.getInstance().init(config);
        btnMorInfo = (Button) findViewById(R.id.btnMoreInfo);
        imvUserImage = (ImageView) findViewById(R.id.imvUserImage);
        recyclerViewGallery = (RecyclerView) findViewById(R.id.recycleViewGallery);
        llGallery = (LinearLayout) findViewById(R.id.llGallery);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewGallery.setLayoutManager(layoutManager);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        sharedPrefVenues = new SharedPrefVenues(MainActivity.this);
        gps = new GPSTracker(MainActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        checkNetConn = new CheckInternetConnection(MainActivity.this);

//check if can get Location
        if (gps.canGetLocation()) {
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
        } else {
            gps.showSettingsAlert();
        }

        btnMorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = builder.create();
                dialog.show();
            }
        });


        builder.setPositiveButton("Check In", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                CheckInCriteria criteria = new CheckInCriteria();
                criteria.setBroadcast(CheckInCriteria.BroadCastType.PUBLIC);
                criteria.setVenueId(venuesData.getResponse().getVenues().get(checkInPos).getId());

                async.checkIn(new CheckInListener() {
                    @Override
                    public void onCheckInDone(Checkin checkin) {
                        Toast.makeText(MainActivity.this, "Check In Success.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
                    }
                }, criteria);

            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        setUpMapIfNeeded();

        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
//Get Token and user Profile

    @Override
    public void onAccessGrant(String accessToken) {

        sharedPrefVenues.setCachedVenues(SharedPrefVenues.KEY_OAUTH_TOKEN, accessToken);
        async.getUserInfo(new UserInfoRequestListener() {

            @Override
            public void onError(String errorMsg) {
                // Some error getting user info
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onUserInfoFetched(User user) {
                if (user != null) {
                    if (user.getBitmapPhoto() == null) {
                        UserImageRequest request = new UserImageRequest(
                                MainActivity.this, MainActivity.this);
                        request.execute(user.getPhoto());
                    } else {
                        imvUserImage.setImageBitmap(user.getBitmapPhoto());
                    }
                    sharedPrefVenues.setCachedVenues(SharedPrefVenues.KEY_USER_NAME, user.getFirstName());
                    tvUserName.setText(user.getFirstName());
                }

            }
        });

        getNearestVenue(accessToken);

    }

    @Override
    public void onError(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onImageFetched(Bitmap bmp) {
        imvUserImage.setImageBitmap(bmp);
    }


    //get nearest Venues
    private void getNearestVenue(String oauth_token) {


        if (gps.canGetLocation()) {

            final String url = URls.getVenues(oauth_token, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));

            if (checkNetConn.isConnectingToInternet()) {

                clientGetAllVenues.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();

                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        venuesData = ParseJson.getAllVenues(new String(responseBody));
                        if (venuesData.getMeta().getCode() == 200) {
                            successVenues = true;
                            sharedPrefVenues.setCachedVenues(SharedPrefVenues.KEY_DATA, new String(responseBody));
                        } else {
                            successVenues = false;
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        progressBar.setVisibility(View.INVISIBLE);
                        if (successVenues) {
                            setUpMap();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            gps.showSettingsAlert();
        }
    }


    private void setUpMap() {
        mMap.clear();
        currentPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .title("MY Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),
                        currentLocation.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);


        if (!sharedPrefVenues.getCachedVenues(SharedPrefVenues.KEY_DATA).equals(SharedPrefVenues.DEFAULT_VALUE)) {

            try {
                venuesData = ParseJson.getAllVenues(sharedPrefVenues.getCachedVenues(SharedPrefVenues.KEY_DATA));
                String image_url;
                Bitmap bmImg;
                double lat, lng;
                LatLng latlng;
                Marker marker;

                for (int i = 0; i < venuesData.getResponse().getVenues().size(); i++) {
                    lat = venuesData.getResponse().getVenues().get(i).getLocation().getLat();
                    lng = venuesData.getResponse().getVenues().get(i).getLocation().getLng();
                    latlng = new LatLng(lat, lng);
                    if (venuesData.getResponse().getVenues().get(i).getCategories().size() == 0) {


                        marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(venuesData.getResponse().getVenues().get(i).getName()));

                    } else {
                        image_url = venuesData.getResponse().getVenues().get(i).getCategories().get(0).getIcon().getPrefix() + "bg_64" + venuesData.getResponse().getVenues().get(i).getCategories().get(0).getIcon().getSuffix();
                        bmImg = Ion.with(MainActivity.this).load(image_url).asBitmap().get();
                        marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg)).title("\u200e" + venuesData.getResponse().getVenues().get(i).getName()));
                    }
                    mHashMap.put(marker, i);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Please Try again Later", Toast.LENGTH_SHORT).show();
            }

        } else {

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentPositionMarker)) {
                    llGallery.setVisibility(View.GONE);
                } else {
                    checkInPos = mHashMap.get(marker);
                    builder.setTitle(venuesData.getResponse().getVenues().get(checkInPos).getName());

                    builder.setMessage("The distance : " + venuesData.getResponse().getVenues().get(checkInPos).getLocation().getDistance() + " M");

                    getVenue(venuesData.getResponse().getVenues().get(checkInPos).getId());

                }
                marker.showInfoWindow();

                return false;
            }
        });

    }


    private void getVenue(String venueID) {
        String urlVenue = URls.getVenueDetails(venueID, sharedPrefVenues.getCachedVenues(SharedPrefVenues.KEY_OAUTH_TOKEN));

        clientGetVenue.get(urlVenue, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                venueData = ParseJson.getVenue(new String(responseBody));
                if (venueData.getMeta().getCode() == 200) {
                    successVenueID = true;
                } else {
                    successVenueID = false;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                successVenueID = false;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.setVisibility(View.INVISIBLE);
                if (successVenueID) {
                    imagesUrl = new ArrayList<String>();
                    llGallery.setVisibility(View.VISIBLE);
                    if (venueData.getResponse().getVenue().getPhotos().getGroups().size() != 0) {
                        int imageSize = venueData.getResponse().getVenue().getPhotos().getGroups().get(0).getItems().size();
                        String prefix;
                        String suffix;
                        imagesUrl = new ArrayList<String>();

                        for (int j = 0; j < imageSize; j++) {
                            prefix = venueData.getResponse().getVenue().getPhotos().getGroups().get(0).getItems().get(j).getPrefix();
                            suffix = venueData.getResponse().getVenue().getPhotos().getGroups().get(0).getItems().get(j).getSuffix();
                            imagesUrl.add(prefix + "200x200" + suffix);
                        }
                        if (imageSize == 0) {
                            imagesUrl.add(getResources().getString(R.string.no_images));
                        }
                    } else {
                        imagesUrl.add(getResources().getString(R.string.no_images));
                    }
                    galleryAdapter = new GalleryAdapter(MainActivity.this, imagesUrl);
                    recyclerViewGallery.setAdapter(galleryAdapter);
                }
            }
        });
    }


}
