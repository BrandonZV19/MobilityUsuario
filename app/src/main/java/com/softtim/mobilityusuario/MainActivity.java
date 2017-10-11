package com.softtim.mobilityusuario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    HashMap<Integer,Marker> markerHashMap;
    View vCompar, vLugar;
    TextView vcUser,vcStatus,vcMarcaModelo,vcPlacas,vcColor,vcAño;
    TextView vlTitulo,vlDescripcion,vlHorario,vlDireccion;
    ImageView vlImagen;
    EditText tvDenue;
    ImageView unidadCM;
    AlertDialog dialogCompar, dialogLugar;
    int MY_PERMISSIONS_REQUEST_LOCATION=1,MENU_ITEM_SHARE=2,MENU_ITEM_CONDUC=3,MY_PERMISSIONS_REQUEST_CALL = 9;
    Marker mOrigen, mDestino,mLocation, mTaxi;
    String LOG_TAG="MainActivity";
    String timeAprox,typeS="";
    Polyline polyline;
    GoogleApiClient mGoogleApiClient;
    long MIN_TIME_BW_UPDATES=1000 * 10;
    double costSelected;
    int  typeSelected=0, metros,segundos,cntLibre=0;
    double latO=0, longO=0, latD=0, longD=0;
    isBetterLocation isBetter=new isBetterLocation();
    boolean shouldEnd=false,resumed,originLocation=false, currentValid = false, isFirstTime=true,
            mostrarCercanos=true,delay=false;
    Location currentLocation,currentBestLocation;
    private GoogleMap mMap;
    LatLng currentLatLng, lasLatLng, morelia=new LatLng(19.7003216,-101.1983066),orig,dest;
    //LatLng gdl=new LatLng(20.6690673,-103.3528848);
    private Geocoder mGeocoder;
    List<Address> addressInfo,latlgnInfo;
    Address inputAddress;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    LayoutInflater inflater;
    NavigationView navigationView;
    Menu navigationMenu;
    View headerView;
    String idSesion;
    HashMap<Integer,Marker> markerHashMapCom;
    int idUsuario,currentStatus, currentService, currentConfirmado, currentCancelado,
            currentCanceladoV,currentCostoU,currentCostoS,currentLlegaOrigen,
            currentSolicitadoPP,currentCostoPP,currentAcreditadoPP,countLlego=0, count2=0,
            countElapsedSol=0,count3=0,countAcreditadoPP=0,countConfirmado=0,count4=0;
    float currentCalificado,currentCostoC;
    long tiempoInicio=0,duracionFinal=0,tiempoSolicitado;
    Toolbar toolbar;
    Realm realm;
    AlertDialog dialogiIniciaSesion, confirmarSolicitud, calificarEfectivo;
    String coloniaO,coloniaD,dirO,dirD,onlyColO="",onlyColD="",currentIdCelular,fbID;
    View promptCE;
    Button cancelarSolicitudServicio,solicitarServicio,cancelarServicio,pagarPayPal;
    TextView estado,username, origenTxt,destinoTxt, tipoTxt, patchA1, fondoCostos;
    TextView costoUnoUno,costoUnoDos,costoDosDos,costoUnoTres,costoDosTres,costoTresTres,
            costoUnoCuatro,costoDosCuatro,costoTresCuatro,costoCuatroCuatro;
    TextView sMarcaModelo,smPlcs,smPlacas,smClr,smColor,smBack;
    Button tipoUnoUno,tipoUnoDos,tipoDosDos,tipoUnoTres,tipoDosTres,tipoTresTres,
            tipoUnoCuatro,tipoDosCuatro,tipoTresCuatro,tipoCuatroCuatro;
    EditText referenciaEdit, comentariosE, costoE;
    RatingBar ratingE;
    AutoCompleteTextView auto1,auto2;
    Servicio currentRealmService;
    View promptDSS;
    TextView ducNombre, ducTel, ducMail, ducMarcaModelo, ducAño, ducPlacas;
    ImageView ducConductor,ducUnidad,smUnidad;
    AlertDialog dialogServicio;
    PlaceAutocompleteAdapter mAdapter;
    TextView mPlaceDetailsText,mPlaceDetailsAttribution;
    HashMap<Integer,Marker> markersUnidadesCercanas,markersCompartidos;
    LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(19.70069804989642, -101.274118394775370), new LatLng(19.736572205952953, -101.10417363159178));

    ArrayList<Compartido> listCompartidos=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize Realm
        Realm.init(getApplicationContext());

        //Configuracion de la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logosimpletoolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        //Inicializacion de la barra lateral despegable
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);

        //Configuracion del nombre del usuario en la barra lateral despegable
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        username = (TextView) headerView.findViewById(R.id.sideUsername);

        markersUnidadesCercanas=new HashMap<>();

        idSesion=preferences.getString(getString(R.string.p_sesion),null);
        currentIdCelular=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        markerHashMapCom=new HashMap<>();

        //TODO:
        //Si en las preferencias no se encuentra el ID del usuario se cierra la sesion

        //Si contiene la preferencia active significa que estaba en sesion y hay que notificar al usuario
        if (!preferences.contains(getString(R.string.p_id_usuario))){
            if (progressDialog!=null){
                progressDialog.dismiss();
            }

            shouldEnd=true;

            isFirstTime=false; //para evitar que entre en Onresume y se quede el dialogo
            if (preferences.contains("active")){

                dialogiIniciaSesion=null;
                dialogiIniciaSesion=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("¡UPS!")
                        .setCancelable(false)
                        .setMessage("Por favor inicia sesión") //pasar a strings
                        //Agregar codigo de error
                        .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shouldEnd = true;

                                SharedPreferences.Editor editor=preferences.edit();
                                editor.remove(getString(R.string.p_id_usuario));
                                editor.remove(getString(R.string.p_nick));
                                editor.remove(getString(R.string.p_mail));
                                editor.remove(getString(R.string.p_tel));
                                editor.remove(getString(R.string.p_pass));
                                editor.remove("active");
                                editor.commit();

                                dialog.dismiss();
                                Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intentm);
                                finish();
                            }
                        })
                        .show();
            }else {
                //Si no contiene la preferencia active significa que el usuario no estaba en sesion
                //y se le puede mostrar la actividad de login sin notificarle
                Log.e(LOG_TAG,"onCreate not idUsuario");
                Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentm);
                finish();
            }
        }else {

            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.e("dismiss in","onCreate before");
            }
            progressDialog=null;
            if(MainActivity.this.getParent()!=null){
                progressDialog=new ProgressDialog(MainActivity.this.getParent());
            }else{
                progressDialog=new ProgressDialog(MainActivity.this);
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Cargando...");
            if(!MainActivity.this.isFinishing()) {
                progressDialog.show();
                Log.e("show in","onCreate");
            }


            idUsuario=preferences.getInt(getString(R.string.p_id_usuario),0);
            Log.e(LOG_TAG,"onCreate idUsuario: "+idUsuario);

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, 0 /* clientId */, this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();


                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();

                String un;

                if (preferences.contains(getString(R.string.p_nick))){
                    un=preferences.getString(getString(R.string.p_nick),"Usuario");
                    username.setText(un);
                }else if (preferences.contains(getString(R.string.p_mail))){
                        un=preferences.getString(getString(R.string.p_mail),"Usuario");
                        username.setText(un);
                    }else if (preferences.contains(getString(R.string.p_tel))){
                        un=preferences.getString(getString(R.string.p_tel),"Usuario");
                        username.setText(un);
                }

                if (mostrarCercanos) {
                    getCurrentStatus("1");
                }else {
                    getCurrentStatus("0");
                }

            }
        }

        //Creacion del dialogo para la cofirmacion de una solicitud de servicio
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        inflater = MainActivity.this.getLayoutInflater();
        final View prompt = inflater.inflate(R.layout.confirmar_solicitud, null);
        origenTxt = (TextView) prompt.findViewById(R.id.confirmOrigen);
        destinoTxt = (TextView) prompt.findViewById(R.id.confirmDestino);
        tipoTxt = (TextView) prompt.findViewById(R.id.confirmTipo);
        referenciaEdit  = (EditText) prompt.findViewById(R.id.confirmRefer);
        builder.setView(prompt)
                .setCancelable(false)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        solicitarServicio();
                    }
                });

        confirmarSolicitud=builder.create();

        //Configuracion de objetos visuales
        auto1=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        auto2=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);
        costoUnoUno=(TextView)findViewById(R.id.tvUnoUno);
        costoUnoUno.setVisibility(View.GONE);
        costoUnoDos=(TextView)findViewById(R.id.tvDosUno);
        costoUnoDos.setVisibility(View.GONE);
        costoDosDos=(TextView)findViewById(R.id.tvDosDos);
        costoDosDos.setVisibility(View.GONE);
        costoUnoTres=(TextView)findViewById(R.id.tvTresUno);
        costoUnoTres.setVisibility(View.GONE);
        costoDosTres=(TextView)findViewById(R.id.tvDosTres);
        costoDosTres.setVisibility(View.GONE);
        costoTresTres=(TextView)findViewById(R.id.tvTresTres);
        costoTresTres.setVisibility(View.GONE);
        costoUnoCuatro=(TextView)findViewById(R.id.tvUnoCuatro);
        costoUnoCuatro.setVisibility(View.GONE);
        costoDosCuatro=(TextView)findViewById(R.id.tvDosCuatro);
        costoDosCuatro.setVisibility(View.GONE);
        costoTresCuatro=(TextView)findViewById(R.id.tvTresCuatro);
        costoTresCuatro.setVisibility(View.GONE);
        costoCuatroCuatro=(TextView)findViewById(R.id.tvCuatroCuatro);
        costoCuatroCuatro.setVisibility(View.GONE);

        tipoUnoUno=(Button)findViewById(R.id.bUnoUno);
        tipoUnoUno.setVisibility(View.GONE);
        tipoUnoDos=(Button)findViewById(R.id.bDosUno);
        tipoUnoDos.setVisibility(View.GONE);
        tipoUnoDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipoUnoDos.setBackgroundColor(Color.parseColor("#e6333333"));
                tipoDosDos.setBackgroundColor(Color.parseColor("#00000000"));
                typeSelected=(int)tipoUnoDos.getTag();
            }
        });
        tipoDosDos=(Button)findViewById(R.id.bDosDos);
        tipoDosDos.setVisibility(View.GONE);
        tipoDosDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipoDosDos.setBackgroundColor(Color.parseColor("#e6333333"));
                tipoUnoDos.setBackgroundColor(Color.parseColor("#00000000"));
                typeSelected=(int)tipoDosDos.getTag();
            }
        });
        tipoUnoTres=(Button)findViewById(R.id.bTUnoTres);
        tipoUnoTres.setVisibility(View.GONE);
        tipoUnoTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipoUnoTres.setBackgroundColor(Color.parseColor("#e6333333"));
                tipoDosTres.setBackgroundColor(Color.parseColor("#00000000"));
                tipoTresTres.setBackgroundColor(Color.parseColor("#00000000"));
                typeSelected=(int)tipoUnoTres.getTag();
                typeS=tipoUnoTres.getText().toString();
                costSelected=(double)costoUnoTres.getTag();
            }
        });
        tipoDosTres=(Button)findViewById(R.id.bDosTres);
        tipoDosTres.setVisibility(View.GONE);
        tipoDosTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipoUnoTres.setBackgroundColor(Color.parseColor("#00000000"));
                tipoDosTres.setBackgroundColor(Color.parseColor("#e6333333"));
                tipoTresTres.setBackgroundColor(Color.parseColor("#00000000"));
                typeSelected=(int)tipoDosTres.getTag();
                typeS=tipoDosTres.getText().toString();
                costSelected=(double)costoDosTres.getTag();
            }
        });
        tipoTresTres=(Button)findViewById(R.id.bTresTres);
        tipoTresTres.setVisibility(View.GONE);
        tipoTresTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipoUnoTres.setBackgroundColor(Color.parseColor("#00000000"));
                tipoDosTres.setBackgroundColor(Color.parseColor("#00000000"));
                tipoTresTres.setBackgroundColor(Color.parseColor("#e6333333"));
                typeSelected=(int)tipoTresTres.getTag();
                typeS=tipoTresTres.getText().toString();
                costSelected=(double)costoTresTres.getTag();

            }
        });
        tipoUnoCuatro=(Button)findViewById(R.id.bUnoCuatro);
        tipoUnoCuatro.setVisibility(View.GONE);
        tipoDosCuatro=(Button)findViewById(R.id.bDosCuatro);
        tipoDosCuatro.setVisibility(View.GONE);
        tipoTresCuatro=(Button)findViewById(R.id.bTresCuatro);
        tipoTresCuatro.setVisibility(View.GONE);
        tipoCuatroCuatro=(Button)findViewById(R.id.bCuatroCuatro);
        tipoCuatroCuatro.setVisibility(View.GONE);

        patchA1=(TextView)findViewById(R.id.patchTocaA1);
        patchA1.setVisibility(View.GONE);

        fondoCostos=(TextView)findViewById(R.id.fondoCostos);
        fondoCostos.setVisibility(View.GONE);

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS/*falta : que sean dinamicos */,
                null);

        auto2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = mAdapter.getItem(i);
                assert item != null;
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {

                        if (!places.getStatus().isSuccess()) {
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        // Format details of the place for display and show it in a TextView.
                        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                                place.getId(), place.getAddress(), place.getPhoneNumber(),
                                place.getWebsiteUri()));

                        // Display the third party attributions if set.
                        final CharSequence thirdPartyAttribution = places.getAttributions();
                        if (thirdPartyAttribution == null) {
                            mPlaceDetailsAttribution.setVisibility(View.GONE);
                        } else {
                            mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                            mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
                        }

                        places.release();
                    }
                });



                String text = auto2.getText().toString();
                mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    latlgnInfo = mGeocoder.getFromLocationName(text, 1);
                    inputAddress=latlgnInfo.get(0);
                    if (latlgnInfo.size()>0){
                        LatLng destine = new LatLng(inputAddress.getLatitude(),inputAddress.getLongitude());
                        mDestino.setPosition(destine);

                        latD = mDestino.getPosition().latitude;
                        longD = mDestino.getPosition().longitude;

                        mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        addressInfo = mGeocoder.getFromLocation(latD, longD, 1);

                        //Log.e("Address info name", "complete="+addressInfo+" line 0="+addressInfo.get(0)+" line 1="+addressInfo.get(1)+" line 2="+addressInfo.get(2)+" size="+addressInfo.size());

                        String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                        coloniaD=split[1]+" "+split[2];

                        //onlyColD=addressInfo.get(0).getAddressLine(1);

                        dirD=addressInfo.get(0).getAddressLine(0);

                        auto2.setText(addressInfo.get(0).getAddressLine(0));

                        addressInfo.clear();

                        //Actualizar camara
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(mDestino.getPosition());
                        builder.include(mOrigen.getPosition());
                        LatLngBounds bounds = builder.build();
                        int padding = 70; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.moveCamera(cu);

                    }else {
                        //falta 1
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("catch 1 auto2",e.toString());
                }

                latO= currentLatLng.latitude;
                longO=currentLatLng.longitude;
                try {
                    addressInfo = mGeocoder.getFromLocation(latO, longO, 1);

                    String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                    coloniaO=split[1]+" "+split[2];

                    dirO=addressInfo.get(0).getAddressLine(0);

                    patchA1.setEnabled(false);
                    patchA1.setVisibility(View.GONE);

                    auto1.setText(addressInfo.get(0).getAddressLine(0));

                    addressInfo.clear();
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "onCreate auto2 catch");
                    }
                    e.printStackTrace();
                    Log.e("catch 2 auto2",e.toString());
                }

                dest=new LatLng(latD,longD);
                orig=new LatLng(latO,longO);
                if (orig != null && dest!=null){
                    new getMetersAndSecs().execute(new DirectionsUrl().getUrl(orig, dest));
                }

            }
        });

        // Register a listener that receives callbacks when a suggestion has been selected
        auto1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = mAdapter.getItem(i);
                assert item != null;
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {

                        if (!places.getStatus().isSuccess()) {
                            places.release();
                            Log.e("Not success","gg");
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        // Format details of the place for display and show it in a TextView.
                        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                                place.getId(), place.getAddress(), place.getPhoneNumber(),
                                place.getWebsiteUri()));

                        // Display the third party attributions if set.
                        final CharSequence thirdPartyAttribution = places.getAttributions();
                        if (thirdPartyAttribution == null) {
                            mPlaceDetailsAttribution.setVisibility(View.GONE);
                        } else {
                            mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                            mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
                        }

                        places.release();
                    }
                });



                String text = auto1.getText().toString();
                mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    latlgnInfo = mGeocoder.getFromLocationName(text, 1);
                    inputAddress=latlgnInfo.get(0);
                    if (latlgnInfo.size()>0){
                        LatLng destine = new LatLng(inputAddress.getLatitude(),inputAddress.getLongitude());
                        mOrigen.setPosition(destine);

                        latO = mOrigen.getPosition().latitude;
                        longO = mOrigen.getPosition().longitude;

                        mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            addressInfo = mGeocoder.getFromLocation(latO, longO, 1);

                        String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                        coloniaO=split[1]+" "+split[2];

                        dirO=addressInfo.get(0).getAddressLine(0);

                        patchA1.setEnabled(false);
                        patchA1.setVisibility(View.GONE);

                        auto1.setText(addressInfo.get(0).getAddressLine(0));

                            addressInfo.clear();

                        //Actualizar camara
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(mOrigen.getPosition());
                        builder.include(mDestino.getPosition());
                        LatLngBounds bounds = builder.build();
                        int padding = 70; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.moveCamera(cu);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("catch 1 auto1",e.toString());
                }

                latD= mDestino.getPosition().latitude;
                longD=mDestino.getPosition().longitude;
                try {
                    addressInfo = mGeocoder.getFromLocation(latD, longD, 1);

                    //Log.e("Address info", "complete="+addressInfo+" line 0="+addressInfo.get(0)+" line 1="+addressInfo.get(1)+" line 2="+addressInfo.get(2)+" size="+addressInfo.size());

                    String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                    coloniaD=split[1]+" "+split[2];

                    //onlyColD=addressInfo.get(0).getAddressLine(1);

                    dirD=addressInfo.get(0).getAddressLine(0);

                    auto2.setText(addressInfo.get(0).getAddressLine(0));

                    addressInfo.clear();
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "onCreate auto1 catch");
                    }
                    e.printStackTrace();
                    Log.e("catch 2 auto1",e.toString());
                }

                orig=new LatLng(latO,longO);
                dest=new LatLng(latD,longD);
                if (dest != null && orig!=null){
                    new getMetersAndSecs().execute(new DirectionsUrl().getUrl(orig, dest));
                }

            }
        });
        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.

        auto1.setAdapter(mAdapter);
        auto2.setAdapter(mAdapter);

        cancelarSolicitudServicio=(Button)findViewById(R.id.buttonCancelarSolicitar);

        cancelarSolicitudServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrigen!=null){
                    mOrigen.remove();
                }
                if (mDestino!= null){
                    mDestino.remove();
                }
                originLocation=false;
                toolbar.setTitle("");
                auto1.setText("");
                auto2.setText("");
                auto1.setVisibility(View.GONE);
                patchA1.setVisibility(View.GONE);
                auto2.setVisibility(View.GONE);
                cancelarSolicitudServicio.setVisibility(View.GONE);

                clearTypesPrices();
                solicitarServicio.setVisibility(View.GONE);
                auto1.setEnabled(false);
                auto2.setEnabled(false);
                cancelarSolicitudServicio.setEnabled(false);
                solicitarServicio.setEnabled(false);
                isFirstTime=false;

                estado.setVisibility(View.GONE);
                if (polyline!=null){
                    polyline.remove();
                }
                polyline=null;
                cancelarServicio.setVisibility(View.GONE);

                if (currentStatus>=1) {
                    requestCancelarServicio();
                }

            }
        });

        solicitarServicio=(Button)findViewById(R.id.buttonSolicitarServicio);
        solicitarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originLocation){
                    //TODO:
                    //Origen se sincroniza con ubicacion
                    //escuchar y mandar ubicaciones aun con la app en pausa
                    if (isSomethingNull(latO,latD,coloniaO,coloniaD,dirO,dirD) || auto2.getText().toString().trim().length()<6 ){
                        //Algun campo no se ha completado
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(Html.fromHtml("<font color='#000000'>Algún campo no se ha completado</font>"))
                                .setCancelable(false)
                                .setNeutralButton("Aceptar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();
                    }else {
                        if (typeSelected!=0) {
                            //TODO: Importante
                            //Realizar calculo de precio. ¿Mostrar en la solicitud?
                            origenTxt.setText("(Se sincroniza con tu ubicación)");
                            destinoTxt.setText(dirD);
                            tipoTxt.setText(typeS);
                            confirmarSolicitud.show();
                            referenciaEdit.requestFocus();
                        }else {
                            //No eligio tipo de unidad
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(Html.fromHtml("<font color='#000000'>No has seleccionado ningun tipo de unidad</font>"))
                                    .setCancelable(false)
                                    .setNeutralButton("Aceptar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            }).show();
                        }
                    }

                }else if (isSomethingNull(latO,latD,coloniaO,coloniaD,dirO,dirD) || auto2.getText().toString().trim().length()<6 || auto1.getText().toString().trim().length()<6){
                    //Algun campo no se ha completado
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(Html.fromHtml("<font color='#000000'>Algún campo no se ha completado</font>"))
                            .setCancelable(false)
                            .setNeutralButton("Aceptar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).show();
                }else {
                    if (typeSelected!=0) {
                        //TODO: Importante
                        //Realizar calculo de precio y mostrar en la solicitud
                        origenTxt.setText(dirO);
                        destinoTxt.setText(dirD);
                        tipoTxt.setText(typeS);
                        confirmarSolicitud.show();
                        referenciaEdit.requestFocus();
                    }else {
                        //No eligio tipo de unidad
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(Html.fromHtml("<font color='#000000'>No has seleccionado ningun tipo de unidad</font>"))
                                .setCancelable(false)
                                .setNeutralButton("Aceptar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();
                    }
                }
            }
        });
        auto1.setVisibility(View.GONE);
        patchA1.setVisibility(View.GONE);
        auto2.setVisibility(View.GONE);
        cancelarSolicitudServicio.setVisibility(View.GONE);
        clearTypesPrices();
        solicitarServicio.setVisibility(View.GONE);
        auto1.setEnabled(false);
        auto2.setEnabled(false);
        cancelarSolicitudServicio.setEnabled(false);
        solicitarServicio.setEnabled(false);

        cancelarServicio=(Button)findViewById(R.id.cancelarServicio);
        cancelarServicio.setVisibility(View.GONE);
        cancelarServicio.setEnabled(false);
        cancelarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(Html.fromHtml("<font color='#000000'>¿Desea cancelar el servicio?</font>"))
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //TODO:
                                        //LLamar metodo con el Webservice para cancelar el servicio actual
                                        requestCancelarServicio();
                                    }
                                }).show();


            }
        });

        pagarPayPal=(Button)findViewById(R.id.bPayPal);
        pagarPayPal.setEnabled(false);
        pagarPayPal.setVisibility(View.GONE);
        pagarPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Post id Serivicio

                if(currentStatus>3){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(Html.fromHtml("<font color='#000000'>Pagar...</font>"))
                            .setCancelable(false)

                            /*
                            .setNegativeButton("Con PayPal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    Intent pp=new Intent(MainActivity.this, PagarPayPal.class);
                                    startActivity(pp);
                                }
                            })
                            */

                            .setPositiveButton("En efectivo",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            calificarServicio("efectivo");
                                        }
                                    })

                            .show();
                }else {
                    Intent pp=new Intent(MainActivity.this, PagarPayPal.class);
                    startActivity(pp);
                }
                /* else {


                    if (currentSolicitadoPP>0 && currentCostoPP>=10){
                        //Webview
                        Intent pp=new Intent(MainActivity.this, PagarPayPal.class);
                        startActivity(pp);

                    }else {

                        //Solicita cantidad

                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Log.e("dismiss in","paypal1 before");
                        }
                        progressDialog=null;
                        if(MainActivity.this.getParent()!=null){
                            progressDialog=new ProgressDialog(MainActivity.this.getParent());
                        }else{
                            progressDialog=new ProgressDialog(MainActivity.this);
                        }
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Cargando...");
                        if(!MainActivity.this.isFinishing()) {
                            progressDialog.show();
                            Log.e("show in","paypal1");
                        }

                        AsyncHttpClient cliente = new AsyncHttpClient();
                        RequestParams parametros = new RequestParams();
                        String url = getString(R.string.mainURL)+"/Servicios/usuario_solicita_paypal";
                        parametros.put("idServicio",currentService);
                        cliente.post(url, parametros, new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String resp=new String( responseBody);
                                Log.e(LOG_TAG, "statusCode "+resp);
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Log.e("dismiss in", "paypal1 last");
                                }
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Se ha solicitado al conductor ingresar la cantidad a pagar.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                pagarPayPal.setEnabled(false);

                                currentSolicitadoPP=1;
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putInt(getString(R.string.current_solicitado_pp),1);
                                editor.commit();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e(LOG_TAG, "statusCode "+statusCode);
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Log.e("dismiss in", "paypal1 last");
                                }
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status actual.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                        //

                    }


                }
                */

            }
        });

        markerHashMap=new HashMap<>();

        tvDenue=(EditText)findViewById(R.id.tvDenue);
        tvDenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEvent();
            }
        });
        tvDenue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()) { // match ENTER key
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    searchEvent();

                    return true; // indicate that we handled event, won't propagate it
                }

                return false;
            }
        });

        smClr=(TextView)findViewById(R.id.smClr);
        smColor=(TextView)findViewById(R.id.smColor);
        sMarcaModelo=(TextView)findViewById(R.id.smMarcaModelo);
        smPlcs=(TextView)findViewById(R.id.smPlcs);
        smPlacas=(TextView)findViewById(R.id.smPlacas);
        smBack=(TextView)findViewById(R.id.smBack);
        smUnidad=(ImageView)findViewById(R.id.smUnidad);

        estado=(TextView)findViewById(R.id.tvEstado);
        estado.setVisibility(View.GONE);

        navigationMenu=navigationView.getMenu();

    }

    private void searchEvent() {

        //mMap.clear(); falta solo quitar markers de este modulo

        String type;
        if (tvDenue.getText()!=null && tvDenue.getText().length()>0){
            type=(tvDenue.getText().toString().trim().toLowerCase());
        }else {
            type=" ";
        }
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //dialog s

        if (type.trim().length()>0){

            getDenue(currentLocation,type.toLowerCase().trim());

        }

        realm.close();
    }

    private void getDenue(Location location, String var){
        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        progressDialog=null;

        if(MainActivity.this.getParent()!=null){
            progressDialog=new ProgressDialog(MainActivity.this.getParent());
        }else{
            progressDialog=new ProgressDialog(MainActivity.this);
        }

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Cargando...");

        if(!MainActivity.this.isFinishing())
        {
            progressDialog.show();
        }

        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();

        double lat=location.getLatitude();
        double lng=location.getLongitude();
        String url="http://www3.inegi.org.mx/sistemas/api/denue/v1/consulta/buscar/"+var+"/"+lat+","+lng+"/1000/017fe03f-f989-4b99-81d7-489610567a52";
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LOG_TAG, "Denue response:"+new String(responseBody));

                try{
                    JSONArray arrayJSONLugares= new JSONArray(new String(responseBody));

                    for (int x=0; x<arrayJSONLugares.length(); x++){
                        //TODO:
                        //Llenar la base de datos de lugares con cada json y sus datos
                        // vistas y opciones para los mismos
                        double latitud,longitud;
                        int id;
                        String nombre,descripcion,direccion,categoria
                                ,sitio,correo,telefono,d1,d2,d3,d4,d5;

                        id=     arrayJSONLugares.getJSONObject(x).getInt("Id");
                        nombre=     arrayJSONLugares.getJSONObject(x).getString("Nombre");
                        descripcion=arrayJSONLugares.getJSONObject(x).getString("Razon_social");
                        //imagen=     arrayJSONLugares.getJSONObject(x).getString("aImagenLugar");

                        d1=  arrayJSONLugares.getJSONObject(x).getString("Tipo_vialidad");
                        d2=  arrayJSONLugares.getJSONObject(x).getString("Calle");
                        d3=  arrayJSONLugares.getJSONObject(x).getString("Num_Exterior");
                        d4=  arrayJSONLugares.getJSONObject(x).getString("Colonia");
                        d5=  arrayJSONLugares.getJSONObject(x).getString("CP");

                        direccion=d1+" "+d2+" "+d3+" "+d4+" "+d5;

                        categoria=  arrayJSONLugares.getJSONObject(x).getString("Clase_actividad");

                        if (arrayJSONLugares.getJSONObject(x).isNull("Sitio_internet")){
                            sitio="No disponible";
                        }else {
                            sitio=  arrayJSONLugares.getJSONObject(x).getString("Sitio_internet");
                        }

                        if (arrayJSONLugares.getJSONObject(x).isNull("Correo_e")){
                            correo="No disponible";
                        }else {
                            correo=  arrayJSONLugares.getJSONObject(x).getString("Correo_e");
                        }

                        if (arrayJSONLugares.getJSONObject(x).isNull("Telefono")){
                            telefono="No disponible";
                        }else {
                            telefono=  arrayJSONLugares.getJSONObject(x).getString("Telefono");
                        }
                        latitud=  arrayJSONLugares.getJSONObject(x).getDouble("Latitud");
                        longitud=  arrayJSONLugares.getJSONObject(x).getDouble("Longitud");

                        if (sitio==null || !sitio.contains(".")){
                            sitio="No disponible";
                        }

                        if (correo==null || !correo.contains("@")){
                            correo="No disponible";
                        }

                        if (telefono==null || telefono.trim().length()<7){
                            telefono="No disponible";
                        }

                        final Lugar lugar=new Lugar();
                        lugar.setId(id);
                        lugar.setNombre(nombre);
                        lugar.setDescripcion(descripcion);
                        lugar.setDireccion(direccion);
                        lugar.setCategoria(categoria);
                        lugar.setLatitud(latitud);
                        lugar.setLongitud(longitud);
                        lugar.setSitio(sitio);
                        lugar.setCorreo(correo);
                        lugar.setTelefono(telefono);
                        lugar.setDescripcionCorta(descripcion);
                        lugar.setHorario("(No disponible)");

                        LatLng loc=new LatLng(latitud,longitud);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markgeneral);
                        if ( categoria.toLowerCase().contains(" bar") || categoria.toLowerCase().contains("cantina")
                                || categoria.toLowerCase().contains("cerve") || categoria.toLowerCase().contains("mezc")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markbar);
                        }else if (categoria.toLowerCase().contains("hotel")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markhotel);
                        }else if (categoria.toLowerCase().contains("restaurant")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markrestaurant);
                        }else if (categoria.toLowerCase().contains("medic")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markfarmacia);
                        }else if (categoria.toLowerCase().contains("hospi")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markhospital);
                        }else if (categoria.toLowerCase().contains("flor")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markfloreria);
                        }else if (categoria.toLowerCase().contains("cafe")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markcafe);
                        } else if (categoria.toLowerCase().contains("comercio")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markcomercial);
                        }else if (categoria.toLowerCase().contains("religio")){
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.markiglesia);
                        }

                        if (markerHashMap.get(id)!=null){
                            //Ya estaba
                        }else {
                            //añadir
                            Marker marker=mMap.addMarker(new MarkerOptions()
                                    .position(loc)
                                    .icon(icon));
                            marker.setTag(id);
                            markerHashMap.put(id,marker);
                        }

                        //falta agregar al builder de vista

                        if (realm!=null){
                            if (realm.isClosed() || realm.isEmpty()) {
                                realm=null;
                                RealmConfiguration config = new RealmConfiguration
                                        .Builder()
                                        .deleteRealmIfMigrationNeeded()
                                        .build();

                                Realm.setDefaultConfiguration(config);

                                realm = Realm.getDefaultInstance();
                                realm.setAutoRefresh(true);
                            }
                        }else {
                            RealmConfiguration config = new RealmConfiguration
                                    .Builder()
                                    .deleteRealmIfMigrationNeeded()
                                    .build();

                            Realm.setDefaultConfiguration(config);

                            realm = Realm.getDefaultInstance();
                            realm.setAutoRefresh(true);
                        }

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // This will create a new object in Realm or throw an exception if the
                                // object already exists (same primary key)
                                // realm.copyToRealm(obj);

                                // This will update an existing object with the same primary key
                                // or create a new one if the primary key doesn't exists
                                realm.copyToRealmOrUpdate(lugar);
                            }
                        });

                    }
                    realm.close();

                    //sonido


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (progressDialog!=null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"NearPlaces:"+" statusCode:"+statusCode);
                if (progressDialog!=null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @SuppressLint("StringFormatInvalid")
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public static Boolean isSomethingNull(Object... objects) {
        for (Object o: objects) {
            if (o == null) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.e("BTest","code 3");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.e("BTest","code 1");
            super.onBackPressed();
            Log.e("BTest","code 2");
        }
    }


    private void compartirCon(){
        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
        if (preferences.contains(getString(R.string.p_id_fb))) {
            fbID=preferences.getString(getString(R.string.p_id_fb),"");

            final JSONObject[] data = new JSONObject[1];
            final JSONArray[] amigos = new JSONArray[1];

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+fbID+"/friends/",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                                                /* handle the result */
                            Log.e(LOG_TAG, "friends gresp= " + response.toString());
                            try {

                                amigos[0]=response.getJSONObject().getJSONArray("data");
                                Log.e("FG data", amigos[0].toString());

                            } catch (JSONException e) {
                                Log.e("Exepction post graph", e.toString());
                                e.printStackTrace();
                            }

                            postCompartir(amigos[0]);

                        }
                    }
            ).executeAsync();
        }else {
            //Error
        }

    }

    private void postCompartir(@Nullable JSONArray array){

        //Log.e("postCOmpoartir",array.toString());

        if (array!=null){

            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.e("dismiss in","requestCancelar before");
            }
            progressDialog=null;
            if(MainActivity.this.getParent()!=null){
                progressDialog=new ProgressDialog(MainActivity.this.getParent());
            }else{
                progressDialog=new ProgressDialog(MainActivity.this);
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Cargando...");
            if(!MainActivity.this.isFinishing()) {
                progressDialog.show();
                Log.e("show in","requestCancelar");
            }

            String postArray="{\"data\":"+array+"";

            AsyncHttpClient cliente = new AsyncHttpClient();
            RequestParams parametros = new RequestParams();
            String url = getString(R.string.mainURL)+"/Usuario/compartir_servicio";
            parametros.put("idUsuario", idUsuario);
            parametros.put("idServicio", currentService);
            parametros.put("usuariosFB", array);

            Log.e(LOG_TAG," compartir idServicio "+currentService +" idUsuario "+idUsuario+ " post array = " + array+" string="+array.toString());

            cliente.post(url, parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response=new String(responseBody);

                    Log.e(LOG_TAG," compartir Reponse: "+response);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "requestCancelar last");
                    }

                    Toast.makeText(MainActivity.this,"Servicio compartido correctamente",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("cancelarServicio","Failed "+statusCode);
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "requestCancelar last");
                    }
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nPor favor intentalo de nuevo mas tarde.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (preferences.contains(getString(R.string.current_service)) && preferences.contains(getString(R.string.current_status))) {

            menu.add(R.id.gPrincipalMain,MENU_ITEM_SHARE,1,"Compartir").setIcon(R.drawable.ic_share_white_36dp);
            menu.add(R.id.gPrincipalMain,MENU_ITEM_CONDUC,1,"Datos").setIcon(R.drawable.ic_drive_eta_white_36dp);

            //menu.add(Menu.NONE, MENU_ITEM_SHARE, Menu.NONE, "Compartir").setIcon(R.drawable.ic_share_white_36dp);
            //menu.add(Menu.NONE, MENU_ITEM_CONDUC, Menu.NONE, "Unidad/Conductor").setIcon(R.drawable.ic_drive_eta_white_36dp);
        }

        MenuItem cercanosAction = menu.findItem(R.id.action_publi_markers);
        if (mostrarCercanos){
            cercanosAction.setTitle("No mostrar negocios cercanos");
        }else {
            cercanosAction.setTitle("Mostrar negocios cercanos");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == MENU_ITEM_SHARE){
            //TODO:
            //Llamar el metodo para compartir el servicio
            compartirCon();
        }

        if (id == MENU_ITEM_CONDUC){
            actionUnitDetails();

        }

        if (id == R.id.action_locate) {
            if (mMap!=null) {
                if (currentLatLng!=null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                }
            }
            return true;
        }

        if (id == R.id.action_publi_markers){

            preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
            if (preferences.contains("mostrarCercanos")) {
                mostrarCercanos=preferences.getBoolean("mostrarCercanos",false);
                if (mostrarCercanos){
                    mostrarCercanos=false;
                    Toast.makeText(MainActivity.this, "Negocios cercanos desactivados ", Toast.LENGTH_SHORT).show();
                }else {
                    mostrarCercanos=true;
                    Toast.makeText(MainActivity.this, "Negocios cercanos activados ", Toast.LENGTH_SHORT).show();
                }
            }else {
                mostrarCercanos=false;
                Toast.makeText(MainActivity.this, "Negocios cercanos desactivados ", Toast.LENGTH_SHORT).show();
            }
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("mostrarCercanos",mostrarCercanos);
            editor.commit();
            invalidateOptionsMenu();

            return true;
        }

        if (id == R.id.action_refresh){
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    void actionUnitDetails(){
        //TODO:
        //Vista de datos del conductor y unidad

        if (preferences.contains(getString(R.string.current_status))){
            if (preferences.getInt(getString(R.string.current_status),0)>1) {

                promptDSS = inflater.inflate(R.layout.datos_unidad_conductor, null);
                ducNombre = (TextView) promptDSS.findViewById(R.id.ducNombre);
                ducMail = (TextView) promptDSS.findViewById(R.id.ducEmail);
                ducTel = (TextView) promptDSS.findViewById(R.id.ducTel);
                ducMarcaModelo = (TextView) promptDSS.findViewById(R.id.ducMarcaModelo);
                //ducAño = (TextView) promptDSS.findViewById(R.id.ducAño);
                ducPlacas = (TextView) promptDSS.findViewById(R.id.ducPlacas);
                ducConductor = (ImageView) promptDSS.findViewById(R.id.ducConductor);
                ducUnidad = (ImageView) promptDSS.findViewById(R.id.ducUnidad);
                ducTel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+" + ducTel.getText().toString().trim()));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                    android.Manifest.permission.CALL_PHONE)) {

                                Snackbar.make(v, "Mobility requiere permisos para realizar llamadas.\nPor favor habilitalos en la configuración de tu dispositivo.", Snackbar.LENGTH_LONG)
                                        .setAction("Configuración", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                final Intent i = new Intent();
                                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                                i.setData(Uri.parse("package:" + getPackageName()));
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                startActivity(i);
                                            }
                                        }).show();

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL);

                            } else {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL);

                            }


                        } else {
                            startActivity(callIntent);
                        }

                    }
                });

                try{

                ducNombre.setText(currentRealmService.getNombre());
                ducMail.setText(currentRealmService.getEmail());
                ducTel.setText(String.valueOf(currentRealmService.getTel()));
                ducMarcaModelo.setText(currentRealmService.getMarca() + " " + currentRealmService.getModelo());
                //ducAño.setText(currentRealmService.getAño());
                ducPlacas.setText(currentRealmService.getPlacas());
                Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/conductores/" + currentRealmService.getFoto_c()).into(ducConductor);
                Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/unidades/" + currentRealmService.getFoto_un()).into(ducUnidad);

                if (dialogServicio != null) {
                    dialogServicio = null;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(Html.fromHtml("<font color='#000000'>Detalles</font>"))
                        .setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //Se cierra la vista y el servicio sigue en lista hasta que llegue una push
                                //para descartarlo
                            }
                        });

                if (promptDSS.getParent() == null) {
                    dialogServicio = builder.create();
                    dialogServicio.setView(promptDSS);
                    dialogServicio.show();
                } else {
                    promptDSS = null; //set it to null
                    // now initialized yourView and its component again
                    promptDSS = inflater.inflate(R.layout.datos_unidad_conductor, null);
                    ducNombre = (TextView) promptDSS.findViewById(R.id.ducNombre);
                    ducMail = (TextView) promptDSS.findViewById(R.id.ducEmail);
                    ducTel = (TextView) promptDSS.findViewById(R.id.ducTel);
                    ducMarcaModelo = (TextView) promptDSS.findViewById(R.id.ducMarcaModelo);
                    //ducAño = (TextView) promptDSS.findViewById(R.id.ducAño);
                    ducPlacas = (TextView) promptDSS.findViewById(R.id.ducPlacas);
                    ducConductor = (ImageView) promptDSS.findViewById(R.id.ducConductor);
                    ducUnidad = (ImageView) promptDSS.findViewById(R.id.ducUnidad);
                    ducTel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+" + ducTel.getText().toString().trim()));
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                        android.Manifest.permission.CALL_PHONE)) {

                                    Snackbar.make(v, "Mobility requiere permisos para realizar llamadas.\nPor favor habilitalos en la configuración de tu dispositivo.", Snackbar.LENGTH_LONG)
                                            .setAction("Configuración", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    final Intent i = new Intent();
                                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                                    i.setData(Uri.parse("package:" + getPackageName()));
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                    startActivity(i);
                                                }
                                            }).show();

                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{android.Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL);

                                } else {

                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{android.Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL);

                                }


                            } else {
                                startActivity(callIntent);
                            }

                        }
                    });

                    ducNombre.setText(currentRealmService.getNombre());
                    ducMail.setText(currentRealmService.getEmail());
                    ducTel.setText(String.valueOf(currentRealmService.getTel()));
                    ducMarcaModelo.setText(currentRealmService.getMarca() + " " + currentRealmService.getModelo());
                    //ducAño.setText(currentRealmService.getAño());
                    ducPlacas.setText(currentRealmService.getPlacas());
                    Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/conductores/" + currentRealmService.getFoto_c()).into(ducConductor);
                    Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/unidades/" + currentRealmService.getFoto_un()).into(ducUnidad);

                    dialogServicio = builder.create();
                    dialogServicio.setView(promptDSS);
                    dialogServicio.show();
                }

            }catch(Exception ignored){}

            }
        }
    }

    void requiresSolicitar(){
        Log.e("tst reqsol","requiresSolicitar in");
        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
        if (!preferences.contains(getString(R.string.current_service)) && !preferences.contains(getString(R.string.current_status))) {
            //TODO:
            //falta : validar cambios de variable en cada accion y en prefs

            if (mDestino!=null && !mDestino.isVisible() || solicitarServicio.getVisibility()!=View.VISIBLE) {
                Log.e("tst reqsol","requiresSolicitar first if");
            if (currentValid) {
                Log.e("tst reqsol","requiresSolicitar second if");
                LatLng dest = new LatLng(currentLatLng.latitude + 0.0020, currentLatLng.longitude);

                mDestino = mMap.addMarker(new MarkerOptions()
                        .position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerb))
                        .title("Destino")
                        .draggable(true));

                //No puede solicitar hasta que concrete servicio pendiente

                /*

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Deseas que el origen de tu solicitud se sincronice con tu ubicación?")
                        .setTitle(Html.fromHtml("<font color='#000000'>Origen de servicio</font>"))
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mOrigen = mMap.addMarker(new MarkerOptions()
                                        .position(orig)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markera))
                                        .title("Origen")
                                        .draggable(true));
                                mOrigen.showInfoWindow();

                                originLocation=false;

                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        originLocation = true;
                                        auto1.setText("Ubicación actual");
                                        auto1.setTextColor(Color.DKGRAY);
                                        auto1.setEnabled(false);
                                    }
                                }).show();

               */


                originLocation = true;
                auto1.setVisibility(View.VISIBLE);
                patchA1.setVisibility(View.VISIBLE);
                patchA1.setEnabled(true);
                auto1.setEnabled(false);
                auto1.setText("Tu ubicación ");
                patchA1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        patchA1.setEnabled(false);
                        patchA1.setVisibility(View.GONE);
                        auto1.setEnabled(true);
                        final LatLng orig = new LatLng(currentLatLng.latitude + 0.0020, currentLatLng.longitude + 0.0020);
                        mOrigen = mMap.addMarker(new MarkerOptions()
                                .position(orig)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markera))
                                .title("Origen")
                                .draggable(true));
                        mOrigen.showInfoWindow();

                        originLocation = false;

                        auto1.setText("");
                    }
                });

                auto2.setVisibility(View.VISIBLE);
                cancelarSolicitudServicio.setVisibility(View.VISIBLE);
                solicitarServicio.setVisibility(View.VISIBLE);
                auto2.setEnabled(true);
                cancelarSolicitudServicio.setEnabled(true);
                solicitarServicio.setEnabled(true);

                mDestino.showInfoWindow();

            }
        }


        }else {
            //No puede solicitar hasta que concrete servicio pendiente
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No puedes solicitar un nuevo servicio si no has finalizado el servicio actual o si tu pago no ha sido procesado.")
                    .setTitle(Html.fromHtml("<font color='#000000'>Te encuentras en un proceso de servicio</font>"))
                    .setCancelable(false)
                    .setNeutralButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent action;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_solicitar) {
            //actionRequiredByUser solicitar

            requiresSolicitar();
        } else if (id == R.id.nav_historial) {
            action=new Intent(MainActivity.this, Historial.class);
            startActivity(action);
        } else if (id == R.id.nav_salir) {
            salir();

        } else if (id == R.id.nav_compartidos) {
            //TODO:
            //Puede ya haber markers de servicios compartidos.
            //Lista de servicios compartidos:
            //Al darle click a un item se muestra la ubicacion de ese usuario en tiempo real ¿Solicitar mediante un WS?
            //Recibir pushs de cada servicio compartido

        } else if (id == R.id.nav_balance) {
            //TODO:
            //Mostrar información de pagos:
            //Historial, saldo, status, preferencias de pago, opcion para recargar.
        }  else if (id == R.id.nav_preferencias) {
            action=new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(action);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed=true;

        if (isFirstTime){
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.e("dismiss in","OnResume before start");
            }
            progressDialog=null;
            if(MainActivity.this.getParent()!=null){
                progressDialog=new ProgressDialog(MainActivity.this.getParent());
            }else{
                progressDialog=new ProgressDialog(MainActivity.this);
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Comprobando...");
            if(!MainActivity.this.isFinishing()) {
                progressDialog.show();
                Log.e("show in","onResume");
            }
        }


        if (realm!=null){
            if (realm.isClosed() || realm.isEmpty()) {
                realm=null;
                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm = Realm.getDefaultInstance();
                realm.setAutoRefresh(true);
            }
        }else {
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);

            realm = Realm.getDefaultInstance();
            realm.setAutoRefresh(true);
        }

        if (!delay){
            if (mGoogleApiClient != null) {
                if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                    Log.e(LOG_TAG, "onResume connecting");
                }
            }
        }

        Log.e(LOG_TAG,"onResume");
        if (mMap!=null && currentLatLng!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,14));
        }
        if (mostrarCercanos) {
            getCurrentStatus("1");
        }else {
            getCurrentStatus("0");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed=false;

        if (realm!=null && !realm.isClosed()) {
            Log.e(LOG_TAG, "Close realm ");
            realm.close();
        }

        if (mLocation!=null) {
            mLocation.remove();
        }
        mLocation=null;

        if (!delay){
            if (mGoogleApiClient!=null){
                if (mGoogleApiClient.isConnected()){
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                    mGoogleApiClient.disconnect();
                }
            }
        }

        isFirstTime=true;

        //Validar
        if (!originLocation){
            //
        }
    }



    private void getCurrentStatus(@Nullable String publi){
        /**TODO:
         * Retorna un json con la siguiente información:
         *
         * servicioActivo es un status que indica si existe un servicio propio actualmente,
         * si es 1 extraigo el json datosServicio.
         *
         * servicioCompartido es un status que indica si existen servicios compartidos,
         * si es 1 extraigo el arrayJson serviciosCompartidos.
         *
         * login es un satus que indica si la sesion actual es valida o no,
         * 1 es sesion valida, 0 es sesion invalida (cerrar sesion).
         */

        final String[] login = new String[1];
        final String[] servicioActivo = new String[1];
        final String[] servicioCompartido = new String[1];
        final String[] publicidad = new String[1];
        final JSONObject[] jsonObject = new JSONObject[1];

        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.e("dismiss in","getCurrentStatus before");
        }
        progressDialog=null;
        if(MainActivity.this.getParent()!=null){
            progressDialog=new ProgressDialog(MainActivity.this.getParent());
        }else{
            progressDialog=new ProgressDialog(MainActivity.this);
        }

        if (isFirstTime){
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Comprobando...");
            if (currentValid) {
                if (!MainActivity.this.isFinishing()) {
                    progressDialog.show();
                    Log.e("show in", "getCurrentStatus");
                }
            }
        }


        Log.e(LOG_TAG," getCurrentStatus before: idSesion="+idSesion);

        double lat=0,lng=0;
        if (currentLocation!=null) {
            lat = currentLocation.getLatitude();
            lng = currentLocation.getLongitude();
        }

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Principal/inicia_principal";
        parametros.put("idUsuario",idUsuario);
        parametros.put("idSesion", idSesion);
        parametros.put("idCelular",currentIdCelular);
        parametros.put("dLatitud", lat);
        parametros.put("dLongitud",lng);
        if (publi!=null){
            parametros.put("lPublicidad",publi);
        }else {
            parametros.put("lPublicidad","0");
        }

        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LOG_TAG," getCurrentStatus response:"+ new String(responseBody));
                currentValid=true;
                try {
                    jsonObject[0] =new JSONObject(new String(responseBody));
                    login[0] = jsonObject[0].getString("login");
                    servicioActivo[0] = jsonObject[0].getString("servicioActivo");
                    servicioCompartido[0] = jsonObject[0].getString("servicioCompartido");
                    publicidad[0] = jsonObject[0].getString("publicidad");
                    Log.e(LOG_TAG, "response:"+new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    if (login[0].contains("0")){
                        if (mGoogleApiClient!=null){
                            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                            }
                        }

                        Log.e(LOG_TAG," getCurrentStatus login 0");
                        if (dialogiIniciaSesion!=null && dialogiIniciaSesion.isShowing()){
                            dialogiIniciaSesion.dismiss();
                        }
                        dialogiIniciaSesion=null;
                        dialogiIniciaSesion=new AlertDialog.Builder(MainActivity.this)
                                .setTitle("¡UPS!")
                                .setCancelable(false)
                                .setMessage("Parece que has iniciado sesión en otro dispositivo. Solo puedes tener una sesion activa a la vez.") //pasar a strings
                                //Agregar codigo de error
                                .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        shouldEnd = true;

                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.remove(getString(R.string.p_id_usuario));
                                        editor.remove(getString(R.string.p_nick));
                                        editor.remove(getString(R.string.p_pass));
                                        editor.remove(getString(R.string.p_sesion));
                                        editor.remove("activo");
                                        editor.commit();

                                        LoginManager.getInstance().logOut();

                                        dialog.dismiss();
                                        Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intentm);
                                        finish();

                                    }
                                })
                                .show();
                    }

                    if (servicioActivo[0].contains("1")){
                        Log.e(LOG_TAG," getCurrentStatus servicioActivo 1");

                        try{
                            Log.e(LOG_TAG," getCurrentStatus servicioActivo 1 inside try");

                            realm = Realm.getDefaultInstance();
                            final Servicio servicio = new Servicio();

                            JSONObject jsonServicio= jsonObject[0].getJSONObject("datosServicio");
                            //Fill data
                            int id;

                            String nombre;
                            int cancelado;
                            int cancelado_v;
                            int confirmado;
                            int año;
                            String foto_c;
                            String foto_un;
                            String email;
                            String fecha;
                            String modelo;
                            String marca;
                            String placas;
                            double lat_d;
                            double lng_d;
                            double lat_o;
                            double lng_o;
                            float calif=0;
                            String referencia;
                            String dir_dest;
                            String dir_orig;
                            String col_dest;
                            String col_orig;
                            int status;
                            String tel;
                            int costo_s=0;
                            int costo_u=0;
                            double costo_c = 0;
                            int taxiLlegaOrigen=0;
                            int ppCosto=0,ppSolicitado=0,ppAcreditado=0;

                            //TODO: Importante
                            //Se necesitan todos los datos del servicio aqui: si esta cancelado, fecha, status de pago, de confirmaciones, etc.
                            //El usuario pudo haber borrado historial, cache de la app o ocurrido cualquer perdida de información.
                            //Las notificaciones push que se hayan mandado anteriormente no se repetiran y pudo perderse esa información.


                            id=         jsonServicio.getInt("idServicio");//a
                            status=     jsonServicio.getInt("idStatus");//a
                            lat_d=      jsonServicio.getDouble("dLatitudDestino");//a
                            lng_d=      jsonServicio.getDouble("dLongitudDestino");//a
                            lat_o=      jsonServicio.getDouble("dLatitudOrigen");//a
                            lng_o=      jsonServicio.getDouble("dLongitudOrigen");//a
                            referencia= jsonServicio.getString("aReferencia");//a
                            dir_dest=   jsonServicio.getString("aDireccionDestino");//a
                            dir_orig=   jsonServicio.getString("aDireccionOrigen");//a
                            col_dest=   jsonServicio.getString("aColoniaDestino");//a
                            col_orig=   jsonServicio.getString("aColoniaOrigen");//a
                            fecha=      jsonServicio.getString("fFechaHoraSolicitud");
                            confirmado=0;
                            cancelado=0;
                            cancelado_v=0;

                            servicio.setId(id);
                            servicio.setStatus(status);

                            servicio.setCancelado(0);//Siempre es 0 aqui
                            servicio.setCancelado_v(0);//siempre 0

                            servicio.setFecha(fecha);

                            servicio.setLat_d(lat_d);
                            servicio.setLng_d(lng_d);
                            servicio.setLat_o(lat_o);
                            servicio.setLng_o(lng_o);
                            servicio.setReferencia(referencia);
                            servicio.setDir_dest(dir_dest);
                            servicio.setDir_orig(dir_orig);
                            servicio.setColonia_dest(col_dest);
                            servicio.setColonia_orig(col_orig);
                            servicio.setFecha(fecha);

                            if (status>1){
                                modelo=     jsonServicio.getString("aModelo");//a
                                marca=      jsonServicio.getString("aMarca");//a
                                placas=     jsonServicio.getString("aPlacas");//a

                                if (jsonServicio.isNull("lConfirmadoConductor")){
                                    confirmado= 0;//a
                                }else {
                                    confirmado= jsonServicio.getInt("lConfirmadoConductor");//a
                                }

                                año=        jsonServicio.getInt("nAnio");//a
                                foto_c=     jsonServicio.getString("aFotografia");//a
                                foto_un=    jsonServicio.getString("aFotografiaUnidad");//a
                                email=      jsonServicio.getString("aEmail");//a
                                nombre=     jsonServicio.getString("aNombreConductor");//a
                                tel=        jsonServicio.getString("aTelefono");//a
                                costo_s=    jsonServicio.getInt("dCostoSugerido");//a
                                costo_u=    jsonServicio.getInt("dCostoUsuario");//a
                                costo_c=    jsonServicio.getDouble("dCostoConductor");//a
                                taxiLlegaOrigen=    jsonServicio.getInt("lTaxiLlegaOrigen");
                                if (jsonServicio.isNull("dCalificacionServicio")){
                                    calif=0;
                                }else {
                                    calif=      jsonServicio.getInt("dCalificacionServicio");
                                }

                                ppCosto=        jsonServicio.getInt("dCostoPaypal");
                                ppAcreditado=   jsonServicio.getInt("idStatusPaypal"); //2 completado //1 fallo
                                ppSolicitado=   jsonServicio.getInt("lSolicitudPaypal");

                                servicio.setNombre(nombre);
                                servicio.setConfirmado(confirmado);
                                servicio.setAño(año);
                                servicio.setFoto_c(foto_c);
                                servicio.setFoto_un(foto_un);
                                servicio.setEmail(email);
                                servicio.setModelo(modelo);
                                servicio.setMarca(marca);
                                servicio.setPlacas(placas);
                                servicio.setTel(tel);
                                servicio.setCostoSug(costo_s);
                                servicio.setCostoUs(costo_u);
                                servicio.setCostoCond(costo_c);
                                servicio.setCalif_serv(calif);

                            }

                            currentRealmService=servicio;

                            //cancelado=  jsonServicio.getInt("lCancelado"); Siempre 0
                            //cancelado_v=jsonServicio.getInt(""); Siempre 0

                            if (realm!=null && realm.isClosed()){
                                RealmConfiguration config = new RealmConfiguration
                                        .Builder()
                                        .deleteRealmIfMigrationNeeded()
                                        .build();

                                Realm.setDefaultConfiguration(config);

                                realm=Realm.getDefaultInstance();
                                realm.setAutoRefresh(true);
                            }

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // This will create a new object in Realm or throw an exception if the
                                    // object already exists (same primary key)
                                    // realm.copyToRealm(obj);

                                    // This will update an existing object with the same primary key
                                    // or create a new one if the primary key doesn't exists
                                    realm.copyToRealmOrUpdate(servicio);
                                }
                            });


                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt(getString(R.string.current_service),id);
                            editor.putInt(getString(R.string.current_status),status);
                            editor.putInt(getString(R.string.current_confirmado),confirmado);
                            editor.putInt(getString(R.string.current_cancelado),cancelado);
                            editor.putInt(getString(R.string.current_cancelado_v),cancelado_v);
                            editor.putFloat(getString(R.string.current_costo_conductor),(float)costo_c);
                            editor.putInt(getString(R.string.current_costo_usuario),costo_u);
                            editor.putInt(getString(R.string.current_costo_sugerido),costo_s);
                            editor.putFloat(getString(R.string.current_calificado),calif);
                            editor.putInt(getString(R.string.current_llego_origen),taxiLlegaOrigen);
                            editor.putInt(getString(R.string.current_acreditado_pp),ppAcreditado);
                            editor.putInt(getString(R.string.current_costo_pp),ppCosto);
                            editor.putInt(getString(R.string.current_solicitado_pp),ppSolicitado);
                            editor.commit();

                    } catch (JSONException e) {
                            Log.e(LOG_TAG," getCurrentStatus servicioActivo 1 catch e: "+e.toString());
                            e.printStackTrace();
                }
                        servicioEnCurso();

                    }else {
                        //statusLibre(); //jsut for test and not delete prefs manually
                        Log.e(LOG_TAG,"notActive currentStatus="+currentStatus);
                        if (currentStatus!=0){
                            if (cntLibre<1){//hacerlo una sola vez
                                Log.e(LOG_TAG," ctnLibre=0");
                                cntLibre=cntLibre+1;
                                //Si current status es menor que 4 y el ws  nos dice
                                // que no hay servicio, quiere decir que se cancelo. Avisar al usuario.
                                Log.e(LOG_TAG," currentStatus!=0");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Tu servicio ha sido cancelado")
                                        .setTitle(Html.fromHtml("<font color='#000000'>:I</font>"))
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        statusLibre();
                                                        dialog.cancel();
                                                        isFirstTime=true;// en ambos casos el usuario queda libre,
                                                        // debe quedar como si fuera primera vez
                                                        //para que automaticamente salga solicitar
                                                    }
                                                }).show();
                            }else {
                                Log.e(LOG_TAG," ctnLibre="+cntLibre);
                            }
                        }

                    }

                    if (servicioCompartido[0].contains("1")){
                        //TODO:
                        //Existe uno o mas servicios compartidos
                        //Extraer cada json dentro del jsonArray serviciosCompartidos,
                        //cada json es un servicio compartido diferente

                        if (realm!=null){
                            if (realm.isClosed() || realm.isEmpty()) {
                                realm=null;
                                RealmConfiguration config = new RealmConfiguration
                                        .Builder()
                                        .deleteRealmIfMigrationNeeded()
                                        .build();

                                Realm.setDefaultConfiguration(config);

                                realm = Realm.getDefaultInstance();
                                realm.setAutoRefresh(true);
                            }
                        }else {
                            RealmConfiguration config = new RealmConfiguration
                                    .Builder()
                                    .deleteRealmIfMigrationNeeded()
                                    .build();

                            Realm.setDefaultConfiguration(config);

                            realm = Realm.getDefaultInstance();
                            realm.setAutoRefresh(true);
                        }

                        try{
                        Log.e(LOG_TAG," getCurrentStatus servicioCompartido 1");
                        JSONArray serviciosCompartidos= jsonObject[0].getJSONArray("serviciosCompartidos");

                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markercompartido);

                        for (int x=0; x<serviciosCompartidos.length(); x++){
                            //TODO:
                            //Llenar la base de datos de servicios compartidos con cada json y sus datos
                            //falta : base de datos de servicios compartidos, vistas y opciones para los mismos

                            final Compartido compartido=new Compartido();

                            String userName=serviciosCompartidos.getJSONObject(x).getJSONObject("usuarioAlta").getString("aNombreUsuario");
                            int idS=serviciosCompartidos.getJSONObject(x).getInt("idServicio");
                            String placas=serviciosCompartidos.getJSONObject(x).getString("aPlacas");
                            String marca=serviciosCompartidos.getJSONObject(x).getString("aMarca");
                            String modelo=serviciosCompartidos.getJSONObject(x).getString("aModelo");
                            String color=serviciosCompartidos.getJSONObject(x).getString("aColor");
                            String año=serviciosCompartidos.getJSONObject(x).getString("nAnio");
                            String fotoUnidad=serviciosCompartidos.getJSONObject(x).getString("aFotografiaUnidad");
                            int status=serviciosCompartidos.getJSONObject(x).getInt("idStatus");
                            double lat=serviciosCompartidos.getJSONObject(x).getDouble("dLatitudRegistro");
                            double lng=serviciosCompartidos.getJSONObject(x).getDouble("dLongitudRegistro");

                            compartido.setUserName(userName);
                            compartido.setIdServ(idS);
                            compartido.setPlacas(placas);
                            compartido.setMarca(marca);
                            compartido.setModelo(modelo);
                            compartido.setColor(color);
                            compartido.setAño(año);
                            compartido.setFotoUnidad(fotoUnidad);
                            compartido.setSatus(status);
                            compartido.setLat(lat);
                            compartido.setLng(lng);

                            LatLng currentLU=new LatLng(lat,lng);

                            //primera vez que se agrega
                            if (markerHashMapCom.get(idS)==null){
                                Marker marker=mMap.addMarker(new MarkerOptions()
                                        .position(currentLU)
                                        .title("Compartido: "+userName)
                                        .icon(icon));
                                marker.setTag(idS);

                                markerHashMapCom.put(idS,marker);
                                marker.showInfoWindow();
                            }else {
                                //Si ya existe:

                                LatLng ultima=markerHashMapCom.get(idS).getPosition();
                                LatLngInterpolator interpolator = new LatLngInterpolator.Spherical();
                                interpolator.interpolate(10,ultima,currentLU);
                                MarkerAnimation.animateMarkerToICS(markerHashMapCom.get(idS),currentLU,interpolator);

                            }


                            //Si el status de este servicio compartido ya es 4, eliminar marker y
                            //datos del mismo
                            if (status>=4){
                                if (markerHashMapCom.get(idS)!=null){
                                    markerHashMapCom.get(idS).remove();
                                }
                                final RealmResults<Compartido> results = realm
                                        .where(Compartido.class)
                                        .equalTo("idServ",idS)
                                        .findAll();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        results.deleteAllFromRealm();
                                    }
                                });
                            }


                            listCompartidos.add(compartido);
                        }

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(listCompartidos);
                                }
                            });


                            realm.close();

                            //falta : sonido

                            if (serviciosCompartidos.length()<=1){
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Tienes un servicio compartido", Snackbar.LENGTH_LONG)
                                        .setAction("", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //TODO:
                                                //Llamar metodo para mostrar servicios compartidos
                                            }
                                        }).show();
                            }else if (serviciosCompartidos.length()>1){
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Tienes "+ serviciosCompartidos.length() +" servicios compartidos", Snackbar.LENGTH_LONG)
                                        .setAction("", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //TODO:
                                                //Llamar metodo para mostrar servicios compartidos
                                            }
                                        }).show();
                            }

                    } catch (JSONException e) {
                            Log.e("catch Compartidos",e.toString());
                            e.printStackTrace();
                }


                    }else {
                        Log.e("Map clear","code 1");
                        //mMap.clear(); falta rmeover markes compartidos pasados
                        markerHashMapCom.clear();
                        listCompartidos.clear();
                    }

                    if (publicidad[0].contains("1")) {

                        if (mostrarCercanos){

                            try {
                                JSONArray arrayJSONLugares = jsonObject[0].getJSONArray("datosPublicidad");

                                for (int x = 0; x < arrayJSONLugares.length(); x++) {
                                    //TODO:
                                    //Llenar la base de datos de lugares con cada json y sus datos
                                    // vistas y opciones para los mismos
                                    double latitud, longitud;
                                    int id;
                                    String nombre, descripcion, direccion, categoria, sitio, correo, telefono, imagen;

                                    id = arrayJSONLugares.getJSONObject(x).getInt("idNegocio");
                                    nombre = arrayJSONLugares.getJSONObject(x).getString("aNombreNegocio");
                                    descripcion = arrayJSONLugares.getJSONObject(x).getString("aDescripcionNegocio");
                                    imagen = arrayJSONLugares.getJSONObject(x).getString("aImagen");

                                    direccion = arrayJSONLugares.getJSONObject(x).getString("aDireccion");
                                    latitud = arrayJSONLugares.getJSONObject(x).getDouble("dLatitudNegocio");
                                    longitud = arrayJSONLugares.getJSONObject(x).getDouble("dLongitudNegocio");

                                    categoria = "general";

                                    if (arrayJSONLugares.getJSONObject(x).isNull("Sitio_internet")) {
                                        sitio = "No disponible";
                                    } else {
                                        sitio = arrayJSONLugares.getJSONObject(x).getString("Sitio_internet");
                                    }

                                    if (arrayJSONLugares.getJSONObject(x).isNull("Correo_e")) {
                                        correo = "No disponible";
                                    } else {
                                        correo = arrayJSONLugares.getJSONObject(x).getString("Correo_e");
                                    }

                                    if (arrayJSONLugares.getJSONObject(x).isNull("Telefono")) {
                                        telefono = "No disponible";
                                    } else {
                                        telefono = arrayJSONLugares.getJSONObject(x).getString("Telefono");
                                    }


                                    if (sitio == null || !sitio.contains(".")) {
                                        sitio = "No disponible";
                                    }

                                    if (correo == null || !correo.contains("@")) {
                                        correo = "No disponible";
                                    }

                                    if (telefono == null || telefono.trim().length() < 7) {
                                        telefono = "No disponible";
                                    }

                                    final Lugar lugar = new Lugar();
                                    lugar.setId(id);
                                    lugar.setNombre(nombre);
                                    lugar.setDescripcion(descripcion);
                                    lugar.setDireccion(direccion);
                                    lugar.setCategoria(categoria);
                                    lugar.setLatitud(latitud);
                                    lugar.setLongitud(longitud);
                                    lugar.setImagen(imagen);
                                    lugar.setSitio(sitio);
                                    lugar.setCorreo(correo);
                                    lugar.setTelefono(telefono);
                                    lugar.setDescripcionCorta(descripcion);
                                    lugar.setHorario("(No disponible)");

                                    LatLng loc = new LatLng(latitud, longitud);
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markgeneral);
                                    if (categoria.toLowerCase().contains(" bar") || categoria.toLowerCase().contains("cantina")
                                            || categoria.toLowerCase().contains("cerve") || categoria.toLowerCase().contains("mezc")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markbar);
                                    } else if (categoria.toLowerCase().contains("hotel")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markhotel);
                                    } else if (categoria.toLowerCase().contains("restaurant")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markrestaurant);
                                    } else if (categoria.toLowerCase().contains("medic")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markfarmacia);
                                    } else if (categoria.toLowerCase().contains("hospi")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markhospital);
                                    } else if (categoria.toLowerCase().contains("flor")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markfloreria);
                                    } else if (categoria.toLowerCase().contains("cafe")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markcafe);
                                    } else if (categoria.toLowerCase().contains("comercio")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markcomercial);
                                    } else if (categoria.toLowerCase().contains("religio")) {
                                        icon = BitmapDescriptorFactory.fromResource(R.drawable.markiglesia);
                                    }

                                    if (markerHashMap.get(id) != null) {
                                        //Ya estaba
                                    } else {
                                        //añadir
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(loc)
                                                .icon(icon));
                                        marker.setTag(id);
                                        markerHashMap.put(id, marker);
                                        marker.showInfoWindow();
                                    }

                                    //falta agregar al builder de vista

                                    if (realm != null) {
                                        if (realm.isClosed() || realm.isEmpty()) {
                                            realm = null;
                                            RealmConfiguration config = new RealmConfiguration
                                                    .Builder()
                                                    .deleteRealmIfMigrationNeeded()
                                                    .build();

                                            Realm.setDefaultConfiguration(config);

                                            realm = Realm.getDefaultInstance();
                                            realm.setAutoRefresh(true);
                                        }
                                    } else {
                                        RealmConfiguration config = new RealmConfiguration
                                                .Builder()
                                                .deleteRealmIfMigrationNeeded()
                                                .build();

                                        Realm.setDefaultConfiguration(config);

                                        realm = Realm.getDefaultInstance();
                                        realm.setAutoRefresh(true);
                                    }

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            // This will create a new object in Realm or throw an exception if the
                                            // object already exists (same primary key)
                                            // realm.copyToRealm(obj);

                                            // This will update an existing object with the same primary key
                                            // or create a new one if the primary key doesn't exists
                                            realm.copyToRealmOrUpdate(lugar);
                                        }
                                    });

                                }
                                realm.close();

                                //sonido


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                    }

                if ( progressDialog.isShowing() && (!isFirstTime || currentStatus>=1 || shouldEnd)) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "getCurrentStatus last");
                }else {
                    Log.e("not dismissed", "currentLction="+currentLocation+"api="+mGoogleApiClient.isConnected()+"firsttime="+isFirstTime);
                    if (currentLocation==null){
                        if (mGoogleApiClient.isConnected()){
                            mGoogleApiClient.disconnect();
                        }
                        mGoogleApiClient.connect();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO: Importante
                //Fallo al consutlar el status actual, la funcionalidad de la app esta comprometida.
                //Ejemplo: el usuario podria realizar una nueva solicitud que resultaria en errores e incosistencias
                //Comprobar currentValid antes de cada accion
                currentValid=false;
                Log.e(LOG_TAG, "statusCode "+statusCode);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in", "getCurrentStatus last");
                }
                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status actual.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void requestCancelarServicio(){

        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.e("dismiss in","requestCancelar before");
        }
        progressDialog=null;
        if(MainActivity.this.getParent()!=null){
            progressDialog=new ProgressDialog(MainActivity.this.getParent());
        }else{
            progressDialog=new ProgressDialog(MainActivity.this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Cargando...");
        if(!MainActivity.this.isFinishing()) {
            progressDialog.show();
            Log.e("show in","requestCancelar");
        }

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Servicios/cancelar_servicio_usuario";
        parametros.put("idServicio", currentService);
        parametros.put("idUsuario", idUsuario);

        Log.e(LOG_TAG," cancelarServcicio idServicio "+currentService +" idUsuario "+idUsuario);

        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response=new String(responseBody);

                Log.e(LOG_TAG," cancelarServcicio Reponse: "+response);

                Log.e(LOG_TAG," statuslibre code 3");
                statusLibre();
                mLocation=null;
                finish();
                startActivity(getIntent());

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in", "requestCancelar last");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("cancelarServicio","Failed "+statusCode);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in", "requestCancelar last");
                }
                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nPor favor intentalo de nuevo mas tarde.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void statusLibre(){

        SharedPreferences.Editor editor=preferences.edit();
        editor.remove(getString(R.string.current_service));
        editor.remove(getString(R.string.current_status));
        editor.remove(getString(R.string.current_confirmado));
        editor.remove(getString(R.string.current_cancelado));
        editor.remove(getString(R.string.current_cancelado_v));
        editor.remove(getString(R.string.current_costo_sugerido));
        editor.remove(getString(R.string.current_costo_conductor));
        editor.remove(getString(R.string.current_costo_usuario));
        editor.remove(getString(R.string.current_calificado));
        editor.remove(getString(R.string.current_solicitado_pp));
        editor.remove(getString(R.string.current_acreditado_pp));
        editor.remove(getString(R.string.current_costo_pp));
        editor.remove("INI_TIME");
        editor.remove("INI_SOL");
        editor.remove("typeSelected");
        editor.remove(getString(R.string.factor_metro_1_1));
        editor.remove(getString(R.string.factor_metro_2_1));
        editor.remove(getString(R.string.factor_metro_2_2));
        editor.remove(getString(R.string.factor_metro_3_1));
        editor.remove(getString(R.string.factor_metro_3_2));
        editor.remove(getString(R.string.factor_metro_3_3));
        editor.remove(getString(R.string.factor_metro_4_1));
        editor.remove(getString(R.string.factor_metro_4_2));
        editor.remove(getString(R.string.factor_metro_4_3));
        editor.remove(getString(R.string.factor_metro_4_4));
        editor.commit();

        desactivarSM();

        currentService=0;
        currentStatus=0;
        currentConfirmado=0;
        currentCancelado=0;
        currentCanceladoV=0;
        currentCostoC=0;
        currentCostoU=0;
        currentCostoS=0;
        currentCalificado=0;
        orig=null;
        dest=null;
        countConfirmado=0;
        countAcreditadoPP=0;
        countLlego=0;
        count2=0;
        count3=0;
        countElapsedSol=0;
        tiempoInicio=0;
        tiempoSolicitado=0;
        cntLibre=0;

        if (!isFirstTime){
            if (mOrigen!=null){
                mOrigen.remove();
            }
            mOrigen=null;

            if (mDestino != null){
                mDestino.remove();
            }
            mDestino=null;
        }

        isFirstTime=true;//why

        //RESET Vista y texto de botones

        if (polyline!=null){
            polyline.remove();
        }
        polyline=null;

        if(mTaxi!=null){
            mTaxi.remove();
        }
        mTaxi=null;

        originLocation=false;
        toolbar.setTitle("");
        auto1.setText("");
        auto2.setText("");
        auto1.setVisibility(View.GONE);
        auto2.setVisibility(View.GONE);
        cancelarSolicitudServicio.setVisibility(View.GONE);
        solicitarServicio.setVisibility(View.GONE);
        clearTypesPrices();
        auto1.setEnabled(false);
        auto2.setEnabled(false);
        cancelarSolicitudServicio.setEnabled(false);
        solicitarServicio.setEnabled(false);
        cancelarServicio.setVisibility(View.GONE);
        cancelarServicio.setEnabled(false);
        estado.setVisibility(View.GONE);
        estado.setEnabled(false);
        pagarPayPal.setEnabled(false);
        pagarPayPal.setVisibility(View.GONE);
        patchA1.setVisibility(View.GONE);
        patchA1.setEnabled(false);

        Log.e("Map clear","code 2");
        mMap.clear();

        if (currentRealmService!=null){
            if (realm!=null && realm.isClosed()){
                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();
                realm.setAutoRefresh(true);
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // This will create a new object in Realm or throw an exception if the
                    // object already exists (same primary key)
                    // realm.copyToRealm(obj);

                    // This will update an existing object with the same primary key
                    // or create a new one if the primary key doesn't exists
                    realm.copyToRealmOrUpdate(currentRealmService);
                }
            });
        }

        currentRealmService=null;

        if (realm!=null && !realm.isClosed()){
            realm.close();
        }

    }

    private void servicioEnCurso(){
        //TODO:
        //Llamar este metodo en cada cambio de status o caso de uso, desde webservices o pushs.
        //Analizar cada posible caso de combinaciones y su acción.

        //falta : sonidos

        Log.e("servicioCurso","in");

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
        if (preferences.contains(getString(R.string.current_service)) && preferences.contains(getString(R.string.current_status)) ) {
            currentService=preferences.getInt(getString(R.string.current_service),0);
            currentStatus=preferences.getInt(getString(R.string.current_status),0);
            currentConfirmado=preferences.getInt(getString(R.string.current_confirmado),0);
            currentCancelado=preferences.getInt(getString(R.string.current_cancelado),0);
            currentCanceladoV=preferences.getInt(getString(R.string.current_cancelado_v),0);
            currentCostoC=preferences.getFloat(getString(R.string.current_costo_conductor),0);
            currentCostoU=preferences.getInt(getString(R.string.current_costo_usuario),0);
            currentCostoS=preferences.getInt(getString(R.string.current_costo_sugerido),0);
            currentCalificado=preferences.getFloat(getString(R.string.current_calificado),0);
            currentLlegaOrigen=preferences.getInt(getString(R.string.current_llego_origen),0);
            currentCostoPP=preferences.getInt(getString(R.string.current_costo_pp),0);
            currentAcreditadoPP=preferences.getInt(getString(R.string.current_acreditado_pp),0);
            currentSolicitadoPP=preferences.getInt(getString(R.string.current_solicitado_pp),0);
            tiempoSolicitado=preferences.getLong("TIME_SOL",0);
            tiempoInicio=preferences.getLong("INI_TIME",0);
            Log.e("servicioCurso","status"+currentStatus);

            if (currentStatus>=1 || currentStatus<4){
                cancelarServicio.setVisibility(View.VISIBLE);
                cancelarServicio.setEnabled(true);
                estado.setVisibility(View.VISIBLE);
                estado.setText("Servicio activo");

                auto1.setVisibility(View.GONE);
                auto2.setVisibility(View.GONE);
                cancelarSolicitudServicio.setVisibility(View.GONE);
                if (mDestino!=null){
                    mDestino.setDraggable(false);
                }
                if (mOrigen!=null){
                    mOrigen.setDraggable(false);
                }
                clearTypesPrices();
                solicitarServicio.setVisibility(View.GONE);
                auto1.setEnabled(false);
                auto2.setEnabled(false);
                cancelarSolicitudServicio.setEnabled(false);
                solicitarServicio.setEnabled(false);

            }

            if (realm==null){
                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();
                realm.setAutoRefresh(true);
            }
            if (realm!=null && realm.isClosed()){
                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();
                realm.setAutoRefresh(true);
            }

            if (currentRealmService==null){
                // All changes to data must happen in a transaction
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        currentRealmService = realm.where(Servicio.class).equalTo("id",currentService).findFirst();
                    }
                });
            }

            if (currentStatus>=2){
                pagarPayPal.setEnabled(true);
                pagarPayPal.setVisibility(View.VISIBLE);
            }

            if (currentCancelado==1){
                estado.setText("Servicio cancelado");
                Log.e(LOG_TAG," statuslibre code 4");
                statusLibre();
                currentRealmService.setCancelado(1);
                if (currentCanceladoV==0){
                    //Servicio cancelado, mostrar y confirmar que el usuario confirmo de enterado
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Tu servicio ha sido cancelado")
                            .setTitle(Html.fromHtml("<font color='#000000'>:I</font>"))
                            .setCancelable(false)
                            .setNeutralButton("Aceptar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).show();
                }
            }else {

                if (currentAcreditadoPP == 1) {
                    //El cliente ha solicitado el pago PP
                    //desactivar el boton
                    pagarPayPal.setEnabled(false);
                    pagarPayPal.setTextColor(Color.DKGRAY);
                }

                //Si ya llego el costo, y no ha entrado a la actividad de pago, snack y habilitar boton que ahora ira a l webview
                if (currentCostoPP >= 10 && currentAcreditadoPP < 2) {
                    pagarPayPal.setEnabled(true);
                    pagarPayPal.setTextColor(Color.WHITE);
                    pagarPayPal.setText("Pagar $" + currentCostoPP + ".00");
                    Log.e("servicioCurso", "status" + currentStatus + " CostoPP " + currentCostoPP + " Acreditado 0");

                        /*
                        estado.setText("Pago de servicio electrónico");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Se ha recibido el costo del servicio", Snackbar.LENGTH_LONG)
                                .setAction("Pagar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent pp=new Intent(MainActivity.this, PagarPayPal.class);
                                        startActivity(pp);
                                    }
                                }).show();
                        */

                }

                if (currentAcreditadoPP == 2) {
                    if (countAcreditadoPP < 1) {
                        //El cliente ha pagado correctamente con PP
                        //desactivar el boton, poner pagado
                        pagarPayPal.setText("Pagado");
                        pagarPayPal.setEnabled(false);
                        pagarPayPal.setTextColor(Color.DKGRAY);
                    }
                }


                LatLng permanentDest = null;

                if (realm == null || realm.isClosed()) {
                    RealmConfiguration config = new RealmConfiguration
                            .Builder()
                            .deleteRealmIfMigrationNeeded()
                            .build();

                    Realm.setDefaultConfiguration(config);

                    realm = Realm.getDefaultInstance();
                    realm.setAutoRefresh(true);
                }

                try{

                permanentDest = new LatLng(currentRealmService.getLat_d(), currentRealmService.getLng_d());


                realm.close();

                if (currentStatus == 2) {

                    if (realm == null || realm.isClosed()) {
                        RealmConfiguration config = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();

                        Realm.setDefaultConfiguration(config);

                        realm = Realm.getDefaultInstance();
                        realm.setAutoRefresh(true);
                    }

                    if (!isSomethingNull(currentRealmService.getLat_o(), currentRealmService.getLng_o())) {
                        dest = new LatLng(currentRealmService.getLat_o(), currentRealmService.getLng_o());
                    }

                    realm.close();

                } else {

                    if (realm == null || realm.isClosed()) {
                        RealmConfiguration config = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();

                        Realm.setDefaultConfiguration(config);

                        realm = Realm.getDefaultInstance();
                        realm.setAutoRefresh(true);
                    }

                    dest = new LatLng(currentRealmService.getLat_d(), currentRealmService.getLng_d());


                    realm.close();
                }

                if (orig == null) {

                    if (realm == null || realm.isClosed()) {
                        RealmConfiguration config = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();

                        Realm.setDefaultConfiguration(config);

                        realm = Realm.getDefaultInstance();
                        realm.setAutoRefresh(true);
                    }


                    orig = new LatLng(currentRealmService.getLat_o(), currentRealmService.getLng_o());


                    realm.close();

                }

                if (mDestino == null || !mDestino.isVisible()) {
                    if (permanentDest != null) {
                        mDestino = mMap.addMarker(new MarkerOptions()
                                .position(permanentDest)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerb))
                                .title("Destino")
                                .draggable(true));
                    }
                }
                if (mOrigen == null || !mOrigen.isVisible()) {
                    mOrigen = mMap.addMarker(new MarkerOptions()
                            .position(orig)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markera))
                            .title("Origen")
                            .draggable(true));
                }

            }catch (Exception ignored){}

                if (currentStatus==1) {
                    long elapsed = ((System.currentTimeMillis() - tiempoSolicitado));
                    elapsed = TimeUnit.MILLISECONDS.toMinutes(elapsed);
                    if (elapsed >= 4 && countElapsedSol < 1) {
                        countElapsedSol = countElapsedSol + 1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(Html.fromHtml("<font color='#000000'>:I</font>"))
                                .setMessage("No hemos encontrado taxis disponibles en estos momentos. ¿Desea continuar buscando o cancelar la solicitud?")
                                .setCancelable(false)
                                .setNegativeButton("Cancelar solicitud", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        cancelarSolicitudServicio.callOnClick();
                                    }
                                })
                                .setPositiveButton("Continuar buscando",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();
                    }
                }

                if (currentStatus==2){
                    if (count2<1) {
                        //para que solo lo haga una vez
                        count2=count2+1;
                        currentRealmService.setStatus(2);
                    }

                    if (currentConfirmado==1){
                        if (countConfirmado<1){
                            countConfirmado=countConfirmado+1;
                            invalidateOptionsMenu();
                            //TODO: Importante
                            //Servicio asignado y confirmado
                            //Desde este caso, si se encuentra informacion prevista de pagos el usuario puede realziar el pago,
                            //con la opcion de descontar si tiene saldo suficiente en su cuenta, o por medio de PayPal

                            //La informacion de pagos debera contener la cantidad y previemante haberse aceptado por el conductor.

                            //Se recrea el menu para que se agregue la opcion de compartir

                            actionUnitDetails();
                            activarSM();

                            estado.setText("Conductor confirmado");
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Tu conductor viene en camino", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.e("servicioCurso","status"+currentStatus+" confirmado 1");
                        }

                    }else if (currentConfirmado==0){
                        estado.setText("Solicitud en curso");
                        Log.e("servicioCurso","status"+currentStatus+" confirmado 0");
                    }

                    if (currentLlegaOrigen>0 && currentStatus==2){
                        estado.setText("Unidad en punto de origen");

                        if (countLlego<1) {
                            countLlego = countLlego + 1;
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("¡Tu taxi ha llegado!")
                                    .setMessage("Tu taxi ah indicado que se encuentra en el punto de origen")
                                    //Agregar codigo de error
                                    .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                    }

                }

                if (currentStatus==3) {
                    if (count3<1) {
                        //para que solo lo haga una vez
                        count3=count3+1;
                        currentRealmService.setStatus(3);
                        if (tiempoInicio==0) {
                            //Si se reinicio la actividad y las variables puede volver a entrar,
                            //aqui, pero si las preferencias nos dicen que tiempoInicio ya tiene
                            //valor entonces no le damos valor de nuevo
                            tiempoInicio = System.currentTimeMillis();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong("INI_TIME", tiempoInicio);
                            editor.commit();
                        }
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Viaje en curso", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    desactivarSM();
                    Log.e("servicioCurso","status"+currentStatus+" subio");
                }

                if (currentStatus>=4){

                    if (count4<=1){
                        count4=count4+1;

                        currentRealmService.setStatus(4);

                        Log.e("calculoFinal","");

                        Log.e("calculoFinal","timpoInicio="+tiempoInicio);

                        duracionFinal = ((System.currentTimeMillis() - tiempoInicio));
                        Log.e("calculoFinal","final step1="+duracionFinal);
                        duracionFinal = TimeUnit.MILLISECONDS.toMinutes(duracionFinal);
                        Log.e("calculoFinal","final step2="+duracionFinal);

                        //Toast.makeText(MainActivity.this,"Duracion del servicio: "+duracionFinal+" minutos",Toast.LENGTH_LONG).show();

                        //falta costo final falta validar entre maximos 2 3 o 4 y 1, solo esta para mas 3

                        double costoFinal=0;
                        double factorM;
                        double factorS;
                        double metrosC;
                        double segundosC;



                        switch (typeSelected){
                            case 3:
                                factorM=(double)preferences.getFloat(getString(R.string.factor_metro_3_3),0);
                                Log.e("calculoFinal","factorM="+factorM);
                                factorS=(double)preferences.getFloat(getString(R.string.factor_segundo_3_3),0);
                                Log.e("calculoFinal","factorS="+factorS);
                                metrosC=metros*factorM;
                                Log.e("calculoFinal","metros="+metros);
                                Log.e("calculoFinal","metrosC="+metrosC);
                                segundosC=duracionFinal*60;
                                Log.e("calculoFinal","segundosC="+segundosC);
                                segundosC=segundosC*factorS;
                                Log.e("calculoFinal","segundosC step2="+segundosC);
                                costoFinal=metrosC+segundosC;
                                Toast.makeText(MainActivity.this,"Costo final calculado: "+costoFinal+"",Toast.LENGTH_LONG).show();
                                Log.e("calculoFinal","tipo=3, costoFinal="+costoFinal);
                                break;

                            case 2:
                                factorM=(double)preferences.getFloat(getString(R.string.factor_metro_3_2),0);
                                factorS=(double)preferences.getFloat(getString(R.string.factor_segundo_3_2),0);
                                metrosC=metros*factorM;
                                segundosC=duracionFinal*60;
                                segundosC=segundosC*factorS;
                                costoFinal=metrosC+segundosC;
                                Toast.makeText(MainActivity.this,"Costo final calculado: "+costoFinal+"",Toast.LENGTH_LONG).show();
                                Log.e("calculoFinal","tipo=3, costoFinal="+costoFinal);
                                break;

                            case 1:
                                factorM=(double)preferences.getFloat(getString(R.string.factor_metro_3_1),0);
                                factorS=(double)preferences.getFloat(getString(R.string.factor_segundo_3_1),0);
                                metrosC=metros*factorM;
                                segundosC=duracionFinal*60;
                                segundosC=segundosC*factorS;
                                costoFinal=metrosC+segundosC;
                                Toast.makeText(MainActivity.this,"Costo final calculado: "+costoFinal+"",Toast.LENGTH_LONG).show();
                                Log.e("calculoFinal","tipo=3, costoFinal="+costoFinal);
                                break;

                            default: break;
                        }

                    }

                    cancelarServicio.setEnabled(false);
                    cancelarServicio.setVisibility(View.GONE);

                    pagarPayPal.setEnabled(true);
                    pagarPayPal.setText("Pagar");


                    if (currentCostoU<10 && currentCostoC>=10){
                        //TODO: Importante
                        //El usuario ya bajo del taxi y aun no ha pagado (pueden ser dias)
                        //no puede hacer nada hasta pagar, puede pagar con paypal, hacerse ua recarga con pronto pago,
                        //o recibir una push para liberarse.
                        //El conductor al que se le debe el pago podra ver en su balance los pagos pendientes.
                        Log.e("servicioCurso","status"+currentStatus+" pagado  efectivo 0");
                        estado.setText("Por favor relaiza el pago de tu servicio");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No se ha acreditado ningún pago de tu servicio", Snackbar.LENGTH_LONG)
                                .setAction("Pagar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle(Html.fromHtml("<font color='#000000'>Pagar...</font>"))
                                                .setCancelable(false)
                                                .setPositiveButton("En efectivo",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                calificarServicio("efectivo");
                                                            }
                                                        }).show();
                                    }
                                }).show();

                    }

                    if (currentCostoPP>=10 && currentAcreditadoPP<2){
                        Log.e("servicioCurso","status"+currentStatus+" pagado paypal 0");
                        estado.setText("Por favor relaiza el pago de tu servicio");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No se ha acreditado ningún pago de tu servicio", Snackbar.LENGTH_LONG)
                                .setAction("Pagar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle(Html.fromHtml("<font color='#000000'>Pagar...</font>"))
                                                .setCancelable(false)
                                                .setPositiveButton("En efectivo",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                calificarServicio("efectivo");
                                                            }
                                                        }).show();
                                    }
                                }).show();
                    }

                    if (currentCalificado==0 && currentCostoU>=10 && currentCostoC>=10){
                        Log.e("servicioCurso","status"+currentStatus+" calificado 0");
                        estado.setText("Califica tu servicio");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Califica el servicio ofrecido por el conductor", Snackbar.LENGTH_LONG)
                                .setAction("Calificar", null).show();
                        calificarServicio("pagado");
                    }

                    if (currentAcreditadoPP==2 && currentCalificado==0){
                        Log.e("servicioCurso","status"+currentStatus+" calificado 0");
                        estado.setText("Califica tu servicio");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Califica el servicio ofrecido por el conductor", Snackbar.LENGTH_LONG)
                                .setAction("Calificar", null).show();
                        calificarServicio("pagado");
                    }

                    if (currentCostoU>=10 && currentCostoC<10 && currentCalificado>0){
                        //El usuario bajo al taxi
                        estado.setText("Finalizacion del viaje");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Ha finalizado el viaje", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.e("servicioCurso","status"+currentStatus+" bajo");

                        Log.e(LOG_TAG," statuslibre code 5");
                        statusLibre();
                    }

                    if (currentAcreditadoPP==2 && currentCalificado>0){
                        //El usuario bajo al taxi
                        estado.setText("Finalizacion del viaje");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Ha finalizado el viaje", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.e("servicioCurso","status"+currentStatus+" bajo");

                        Log.e(LOG_TAG," statuslibre code 6");
                        statusLibre();
                    }

                }

            }


        }else {
            Log.e("servicioCurso","preferences doesnt contains info");
        }
    }



    private void calificarServicio(String type){

        if (type.contains("efectivo")){
            //calificacion indicando monto pagado en efectivo

            promptCE = inflater.inflate(R.layout.calificacion_efectivo, null);
            ratingE=(RatingBar) promptCE.findViewById(R.id.ratingE);
            costoE=(EditText) promptCE.findViewById(R.id.cantidadE);
            costoE.setText(String.valueOf(currentCostoC));
            comentariosE=(EditText) promptCE.findViewById(R.id.comentariosE);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setView(promptCE)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (progressDialog!=null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                Log.e("dismiss in","calificar before");
                            }
                            progressDialog=null;
                            if(MainActivity.this.getParent()!=null){
                                progressDialog=new ProgressDialog(MainActivity.this.getParent());
                            }else{
                                progressDialog=new ProgressDialog(MainActivity.this);
                            }
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.setTitle("Cargando...");
                            if(!MainActivity.this.isFinishing()) {
                                progressDialog.show();
                                Log.e("show in","calificar");
                            }

                            try{

                            double calif=ratingE.getNumStars();
                            String comens=comentariosE.getText().toString();
                            double cost=0;
                            if (costoE.getText().toString().trim().length()>1){
                                cost=Double.parseDouble(costoE.getText().toString());
                            }


                            if (isSomethingNull(calif,comens,cost) || ratingE.getNumStars()==0 || cost<10){
                                //Faltan datos
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(Html.fromHtml("<font color='#000000'>Por favor completa la calificacion y el costo correctamente</font>"))
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                }).show();



                            }else {

                                AsyncHttpClient cliente = new AsyncHttpClient();
                                RequestParams parametros = new RequestParams();
                                String url = getString(R.string.mainURL)+"/Servicios/calificacion_usuario";
                                parametros.put("idServicio", currentService);
                                parametros.put("dCalificacionServicio", calif);
                                parametros.put("aComentarioUsuario", comens);
                                parametros.put("dCostoUsuario", cost);

                                Log.e(LOG_TAG," calificarServcicio idServicio "+currentService +" costo "+cost +" calif "+calif);

                                currentRealmService.setCalif_serv(calif);
                                currentRealmService.setCostoCond(cost);

                                cliente.post(url, parametros, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String response=new String(responseBody);

                                        Log.e(LOG_TAG," calificarServcicio Reponse: "+response);
                                        calificarEfectivo.dismiss();
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                            Log.e("dismiss in", "calificar last");
                                        }

                                        Log.e(LOG_TAG," statuslibre code 7");
                                        statusLibre();
                                        mLocation=null;
                                        finish();
                                        startActivity(getIntent());

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.e("calificarServicio","Failed "+statusCode);
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                            Log.e("dismiss in", "calificar last");
                                        }
                                        Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nPor favor intentalo de nuevo mas tarde.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });

                            }

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(LOG_TAG,"costoPP catch "+e.toString());
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Por favor ingresa los valores correctamente", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        }
                    });
            if (promptCE.getParent()==null){
                calificarEfectivo=builder.create();
                calificarEfectivo.show();
            }else{
                promptCE= null; //set it to null
                // now initialized yourView and its component again
                promptCE = inflater.inflate(R.layout.calificacion_efectivo, null);
                ratingE=(RatingBar) promptCE.findViewById(R.id.ratingE);
                costoE=(EditText) promptCE.findViewById(R.id.cantidadE);
                comentariosE=(EditText) promptCE.findViewById(R.id.comentariosE);
                calificarEfectivo=builder.create();
                calificarEfectivo.show();
            }

        }else {
            //Calificar sin ingresar costo
            //Falta hacer vistas y nombres unicos para este tipo de calificacion

            promptCE = inflater.inflate(R.layout.calificacion_pagado, null);
            ratingE=(RatingBar) promptCE.findViewById(R.id.ratingP);
            comentariosE=(EditText) promptCE.findViewById(R.id.comentariosP);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setView(promptCE)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                                     if (progressDialog!=null && progressDialog.isShowing()) {
                                         progressDialog.dismiss();
                                         Log.e("dismiss in","calificar2 before");
                                     }
                                     progressDialog=null;
                            if(MainActivity.this.getParent()!=null){
                                progressDialog=new ProgressDialog(MainActivity.this.getParent());
                            }else{
                                progressDialog=new ProgressDialog(MainActivity.this);
                            }
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.setTitle("Cargando...");
                            if(!MainActivity.this.isFinishing()) {
                                progressDialog.show();
                                Log.e("show in","calificar2");
                            }

                            try{

                            double calif=ratingE.getNumStars();
                            String comens=comentariosE.getText().toString();

                            if (isSomethingNull(calif,comens) || ratingE.getNumStars()==0){
                                //Faltan datos
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(Html.fromHtml("<font color='#000000'>Por favor completa la calificacion y el costo correctamente</font>"))
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                            }else {

                                AsyncHttpClient cliente = new AsyncHttpClient();
                                RequestParams parametros = new RequestParams();
                                String url = getString(R.string.mainURL)+"/Servicios/calificacion_usuario";
                                parametros.put("idServicio", currentService);
                                parametros.put("dCalificacionServicio", calif);
                                parametros.put("aComentarioUsuario", comens);

                                Log.e(LOG_TAG," calificarServcicio idServicio "+currentService +" calif "+calif);

                                currentRealmService.setCalif_serv(calif);

                                cliente.post(url, parametros, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String response=new String(responseBody);

                                        Log.e(LOG_TAG," calificarServcicio Reponse: "+response);
                                        calificarEfectivo.dismiss();
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                            Log.e("dismiss in", "calificar2 last");
                                        }

                                        Log.e(LOG_TAG," statuslibre code 8");
                                        statusLibre();
                                        finish();
                                        startActivity(getIntent());

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.e("calificarServicio","Failed "+statusCode);
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                            Log.e("dismiss in", "calificar2 last");
                                        }
                                        Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nPor favor intentalo de nuevo mas tarde.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });

                            }

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(LOG_TAG,"costoPP catch "+e.toString());
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Por favor ingresa los valores correctamente", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        }
                    });
            if (promptCE.getParent()==null){
                calificarEfectivo=builder.create();
                calificarEfectivo.show();
            }else{
                promptCE= null; //set it to null
                // now initialized yourView and its component again
                promptCE = inflater.inflate(R.layout.calificacion_pagado, null);
                ratingE=(RatingBar) promptCE.findViewById(R.id.ratingP);
                comentariosE=(EditText) promptCE.findViewById(R.id.comentariosP);
                calificarEfectivo=builder.create();
                calificarEfectivo.show();
            }

        }

    }

    private void getMyTaxi(){
        //Conductor/ubicacion_usuario

        //post idConductor

        //dLatitudRegistro
        //idServicio

        final JSONObject[] taxiData = new JSONObject[1];


        Log.e(LOG_TAG," getMyTaxi before "+ idUsuario);

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Usuario/ubicacion_unidad";
        parametros.put("idUsuario",idUsuario);
        //Mandar token prob.
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LOG_TAG," getMyTaxi response:"+ new String(responseBody));
                currentValid=true;

                try {
                    taxiData[0] = new JSONObject(new String(responseBody));

                    //Agregar cada unidad en forma de marker al mapa

                    int id;
                    double lat;
                    double lng;

                    id=    taxiData[0].getInt("idServicio");
                    lat=   taxiData[0].getDouble("dLatitudRegistro");
                    lng=   taxiData[0].getDouble("dLongitudRegistro");

                    LatLng currentLU=new LatLng(lat,lng);

                    if (mTaxi==null){
                        mTaxi=mMap.addMarker(new MarkerOptions()
                                .position(currentLU)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.munidad))
                                .title("Taxi"));
                    }else {
                        //Si ya se encontraba en el mapa
                        //Obtenemos el marker y lo animamos a la nueva locacion

                        LatLng ultima=mTaxi.getPosition();
                        LatLngInterpolator interpolator = new LatLngInterpolator.Spherical();
                        interpolator.interpolate(10,ultima,currentLU);
                        MarkerAnimation.animateMarkerToICS(mTaxi,currentLU,interpolator);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"getMyTaxi statusCode"+statusCode);
                //TODO: Importante
                //Fallo al consutlar el status actual, la funcionalidad de la app esta comprometida.
                //Ejemplo: el conductor podria recibir o aceptar un nuevo servicio que resultaria en errores e incosistencias
                //Comprobar currentValid antes de cada accion

                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status de la unidad actual.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    void getUnidadesCercanas(){
        /** TODO:
        * Retorna un json con la siguiente información:
        *
        */

        final JSONArray[] unidadesCerca = new JSONArray[1];


        Log.e(LOG_TAG," getUnidadesCercanas before "+ idUsuario);

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Servicios/unidades_cercanas";
        parametros.put("idUsuario",idUsuario);
        parametros.put("dLatitud",currentLocation.getLatitude());
        parametros.put("dLongitud",currentLocation.getLongitude());
        //Mandar token prob.
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LOG_TAG," getUnidadesCercanas response:"+ new String(responseBody));
                currentValid=true;

                try {
                    unidadesCerca[0] = new JSONArray(new String(responseBody));

                    if (unidadesCerca[0].length()<1){
                        //Zero units, delete all currents
                        if (markersUnidadesCercanas.size()>0){
                            for (int key: markersUnidadesCercanas.keySet()) {
                                markersUnidadesCercanas.get(key).remove();
                            }
                        }
                        markersUnidadesCercanas.clear();

                    }else {
                        for (int x = 0; x < unidadesCerca[0].length(); x++) {
                            //Agregar cada unidad en forma de marker al mapa y al hashmap

                            int id;
                            double lat;
                            double lng;

                            id=       unidadesCerca[0].getJSONObject(x).getInt("idUnidad");
                            lat=      unidadesCerca[0].getJSONObject(x).getDouble("dLatitudRegistro");
                            lng=      unidadesCerca[0].getJSONObject(x).getDouble("dLongitudRegistro");

                            LatLng currentLU=new LatLng(lat,lng);

                            if (markersUnidadesCercanas.get(id)!=null){
                                //Si ya se encontraba en el hashmap tambien ya se encontraba en el mapa
                                //Obtenemos el marker y lo animamos a la nueva locacion
                                LatLng ultima=markersUnidadesCercanas.get(id).getPosition();

                                LatLngInterpolator interpolator = new LatLngInterpolator.Spherical();
                                interpolator.interpolate(10,ultima,currentLU);
                                MarkerAnimation.animateMarkerToICS(markersUnidadesCercanas.get(id),currentLU,interpolator);

                            }else {
                                //Si no se encontraba simeplemnte añanadimos el marker al hashmap y al mapa
                                markersUnidadesCercanas.put(id,mMap.addMarker(new MarkerOptions()
                                        .position(currentLU)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.munidad))
                                        .title("Unidad "+id)));
                            }

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"getUnidadesCercanas statusCode"+statusCode);
                //TODO: Importante
                //Fallo al consutlar el status actual, la funcionalidad de la app esta comprometida.
                //Ejemplo: el conductor podria recibir o aceptar un nuevo servicio que resultaria en errores e incosistencias
                //Comprobar currentValid antes de cada accion

                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status actual.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void solicitarServicio(){
        //TODO:
        //Se realiza la solicitud del servicio con el webservice.
        //Se espera recibir como respuesta el ID del servicio, y el servicio por estar solicitado se
        //encuentra en status 1 y con confirmacion de conductor 0

        //Despues de algunos minutos de espera (que posiblemente puedan ser preferenciables por el usuario),
        //se le notifica al usuario que no ha respondido ningún conductor, con la opcion de cancelar la solicitud
        //o continuar esperando.

                 if (progressDialog!=null && progressDialog.isShowing()) {
                     progressDialog.dismiss();
                     Log.e("dismiss in","solicitarServicio before");
                 }
                 progressDialog=null;
        if(MainActivity.this.getParent()!=null){
            progressDialog=new ProgressDialog(MainActivity.this.getParent());
        }else{
            progressDialog=new ProgressDialog(MainActivity.this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Cargando...");
        if(!MainActivity.this.isFinishing()) {
            progressDialog.show();
            Log.e("show in","solicitarServicio");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Servicios/solicitar_servicio";
        parametros.put("dLatitudOrigen", latO);
        parametros.put("dLongitudOrigen", longO);
        parametros.put("dLatitudDestino", latD);
        parametros.put("dLongitudDestino", longD);
        parametros.put("fFechaHoraSolicitud", dateFormat.format(date));
        parametros.put("idUsuario", idUsuario);
        parametros.put("aDireccionOrigen", dirO);
        parametros.put("aDireccionDestino", dirD);
        parametros.put("aColoniaDestino", coloniaD);
        parametros.put("aColoniaOrigen", coloniaO);
        parametros.put("aReferencia", referenciaEdit.getText().toString());
        parametros.put("idTipoUnidad", typeSelected);
        parametros.put("dCostoConductor", costSelected);
        //metodo de pago

        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("typeSelected",typeSelected);
        editor.commit();

        Log.e("solicitarServicio","idUsuario "+idUsuario+" tipo="+typeSelected);

        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String response=new String(responseBody);

                    patchA1.setEnabled(false);
                    patchA1.setVisibility(View.GONE);

                    originLocation=false;
                    toolbar.setTitle("");
                    auto1.setText("");
                    auto2.setText("");
                    auto1.setVisibility(View.GONE);
                    auto2.setVisibility(View.GONE);
                    cancelarSolicitudServicio.setVisibility(View.GONE);
                    if (mDestino!=null){
                        mDestino.setDraggable(false);
                    }
                    if (mOrigen!=null){
                        mOrigen.setDraggable(false);
                    }
                    clearTypesPrices();
                    solicitarServicio.setVisibility(View.GONE);
                    auto1.setEnabled(false);
                    auto2.setEnabled(false);
                    cancelarSolicitudServicio.setEnabled(false);
                    solicitarServicio.setEnabled(false);
                    patchA1.setVisibility(View.GONE);
                    patchA1.setEnabled(false);

                    //falta : guardar tiempo para empezar a contar

                    final Servicio servicio = new Servicio();
                    int id = Integer.parseInt(response.trim());
                    servicio.setId(id);

                    if (realm!=null && realm.isClosed()){
                        RealmConfiguration config = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();

                        Realm.setDefaultConfiguration(config);

                        realm=Realm.getDefaultInstance();
                        realm.setAutoRefresh(true);
                    }

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // This will create a new object in Realm or throw an exception if the
                            // object already exists (same primary key)
                            // realm.copyToRealm(obj);

                            // This will update an existing object with the same primary key
                            // or create a new one if the primary key already exists
                            realm.copyToRealmOrUpdate(servicio);
                        }
                    });

                    //No agreguar current service sino hasta aceptar

                    realm.close();

                    tiempoSolicitado = System.currentTimeMillis();

                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt(getString(R.string.current_service),id);
                    editor.putInt(getString(R.string.current_status),1);
                    editor.putLong("TIME_SOL", tiempoSolicitado);
                    editor.commit();

                    Log.e("solicitarServicio","Succes "+response);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.e("dismiss in", "solicitarServicio last");
                    }
                    if (mostrarCercanos) {
                        getCurrentStatus("1");
                    }else {
                        getCurrentStatus("0");
                    }
                    Toast.makeText(getApplicationContext(), "Servicio solicitado correctamente. Espera la respuesta de tu taxi.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("solicitarServicio","Failed "+statusCode);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in", "solicitarServicio last");
                }
                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nPor favor intentalo de nuevo mas tarde.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void salir(){
        //If puede salir:
        if (currentStatus==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea cerrar la sesión actual?")
                    .setTitle(Html.fromHtml("<font color='#000000'>Advertencia</font>"))
                    .setCancelable(false)
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                    .setPositiveButton("Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (mGoogleApiClient != null) {
                                        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                                            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                                        }
                                    }

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.remove(getString(R.string.p_id_usuario));
                                    editor.remove(getString(R.string.p_nick));
                                    editor.remove(getString(R.string.p_pass));
                                    editor.remove("active");
                                    editor.commit();

                                    LoginManager.getInstance().logOut();

                                    shouldEnd = true;

                                    dialog.dismiss();
                                    Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intentm);
                                    Toast.makeText(MainActivity.this, "Hasta pronto", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).show();
        }else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "No puedes cerrar sesion estando en un servicio", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //TODO:
        //Se actualiza la iformacion de la actividad recibida de pushs u otras actividades

        if (realm==null){
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);

            realm=Realm.getDefaultInstance();
            realm.setAutoRefresh(true);
        }
        if (realm!=null && realm.isClosed()){
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);

            realm=Realm.getDefaultInstance();
            realm.setAutoRefresh(true);
        }

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);


        if (intent.hasCategory("FMS")){
            String message=intent.getStringExtra("whatToDo");
            switch (message){
                case "sesion":
                    Log.e(LOG_TAG," pushBadSesion");
                    if (dialogiIniciaSesion!=null && dialogiIniciaSesion.isShowing()){
                        dialogiIniciaSesion.dismiss();
                    }
                    dialogiIniciaSesion=null;
                    dialogiIniciaSesion=new AlertDialog.Builder(MainActivity.this)
                            .setTitle("¡UPS!")
                            .setMessage("Parece que has iniciado sesión en otro dispositivo. Solo puedes tener una sesion activa a la vez.") //pasar a strings
                            //Agregar codigo de error
                            .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    shouldEnd = true;

                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.remove(getString(R.string.p_id_usuario));
                                    editor.remove(getString(R.string.p_nick));
                                    editor.remove(getString(R.string.p_pass));
                                    editor.remove(getString(R.string.p_sesion));
                                    editor.remove("activo");
                                    editor.commit();

                                    dialog.dismiss();
                                    Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intentm);
                                    finish();
                                }
                            })
                            .show();
                    break;
                case "test":
                    Toast.makeText(MainActivity.this,"Just Testing. Fine.",Toast.LENGTH_SHORT).show();
                    break;
                case "servicio_cancelado":
                    servicioEnCurso();
                    break;
                case "confirmado":
                    isFirstTime=false;
                    if (mostrarCercanos) {
                        getCurrentStatus("1");
                    }else {
                        getCurrentStatus("0");
                    }
                    break;
                case "status_servicio":
                    isFirstTime=false;
                    servicioEnCurso();
                    break;
                case "salir":
                    salir();
                    break;
                default:
                    Log.e(LOG_TAG,"onNewIntent:"+"whatToDo="+message);
            }
        }

        if (intent.hasCategory("newToken")){
            String token=getIntent().getStringExtra("token");
            PostToken postToken=new PostToken();
            postToken.post(token,getApplicationContext());
        }
    }

    private void newLocation(){
        //TODO:
        //Actualizar la interfaz segun la nueva ubicacion

        Log.e(LOG_TAG,"newLocation");

        if (mMap!=null){

            if (mLocation==null || !mLocation.isVisible()){
                if (mLocation!=null){
                    mLocation.remove();
                }
                mLocation=null;
                mLocation = mMap.addMarker(new MarkerOptions()
                        .title("Ubicación actual")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mlocation))
                        .position(currentLatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,14));

            }else {
                if (lasLatLng!=null){
                    LatLngInterpolator interpolator = new LatLngInterpolator.Spherical();
                    interpolator.interpolate(10,lasLatLng,currentLatLng);
                    MarkerAnimation.animateMarkerToICS(mLocation,currentLatLng,interpolator);

                }else {

                    mLocation.remove();

                    mLocation = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mlocation))
                            .title("Ubicación actual")
                            .position(currentLatLng));
                }
            }

            if (currentService != 0 && currentStatus!=0){
                if (currentLatLng != null && dest != null) {
                    new DownloadTask().execute(new DirectionsUrl().getUrl(currentLatLng, dest));
                }
            }

        }

    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        final boolean[] isMarkerRotating = {false};
        if(!isMarkerRotating[0]) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating[0] = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating[0] = false;
                    }
                }
            });
        }
    }

    private void getPeriodicFusedLocation() {
        //TODO:
        //Solicitar actualizaciones de ubicacion

        final Context context = this;

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        final LocationRequest finalMLocationRequest = mLocationRequest;
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                //final LocationSettingsStates LocationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.e("getPeriodicFusedLocatio","SUCCES");
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                                Snackbar.make(getWindow().getDecorView().getRootView(), "Mobility requiere permisos para acceder a tu ubicación.\nPor favor habilitalos en la configuración de tu dispositivo.", Snackbar.LENGTH_LONG)
                                        .setAction("Configuración", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                final Intent i = new Intent();
                                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                                i.setData(Uri.parse("package:" + context.getPackageName()));
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                context.startActivity(i);
                                            }
                                        }).show();

                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            } else {

                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }

                            ///Fue a permisos

                        }else {
                            //gg same
                            if (mGoogleApiClient.isConnected()) {
                                Log.e("error location", "currentLction=" + currentLocation + "api=" + mGoogleApiClient.isConnected() + " firsttime=" + isFirstTime);
                                LocationServices.FusedLocationApi.requestLocationUpdates(
                                        mGoogleApiClient, finalMLocationRequest, MainActivity.this);
                            }
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("getPeriodicFusedLocatio","Error in RESOLUTION_REQUIRED");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("¡UPS!")
                                .setMessage("Parece que hay un problema con los requerimientos para utilizar esta aplicacion." +
                                        "Por favor revisa tu dispositivo o tus ajustes antes de continuar.")
                                //Agregar codigo de error
                                .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.remove(getString(R.string.p_id_usuario));
                                        editor.remove(getString(R.string.p_nick));
                                        editor.remove(getString(R.string.p_mail));
                                        editor.remove(getString(R.string.p_tel));
                                        editor.remove(getString(R.string.p_pass));
                                        editor.commit();

                                        shouldEnd = true;

                                        dialog.dismiss();
                                        Intent intentm = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intentm);
                                        Toast.makeText(MainActivity.this, "Hasta pronto", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .show();

                        Log.e("getPeriodicFusedLocatio","Error=SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        finish();
                        startActivity(getIntent());

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        if (mGoogleApiClient.isConnected()){
                            mGoogleApiClient.disconnect();
                        }
                        //errorUbicaciones();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(LOG_TAG, "onConnected");
        if (resumed) {
            delay=true;
            getPeriodicFusedLocation();
            Log.e(LOG_TAG, "resumed");
        }else {
            Log.e(LOG_TAG, "not resumed");
            if (mGoogleApiClient!=null){
                if (mGoogleApiClient.isConnected()){
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Fallo la coneccion de googleAPIClient

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("¡UPS!")
                .setMessage("Parece que hay un problema para acceder a tu ubicación.")
                //Agregar codigo de error
                .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Modificar o NO modificar preferencias para que VULEVA a inicia sesion
                        //Reiniciar actividad

                        shouldEnd = true;

                        dialog.dismiss();
                        Intent intentm = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intentm);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Nueva actualizacion automatica de ubicacion
        delay=false;
        Log.e(LOG_TAG,"onLocationChanged"+location.toString());
        if (currentLocation!=null) {
            if (isBetter.isBetterLocation(location,currentBestLocation)) {
                currentBestLocation=location;
                if (idUsuario!=0){
                    if (currentService!=0){
                        if (mostrarCercanos) {
                            getCurrentStatus("1");
                        }else {
                            getCurrentStatus("0");
                        }
                    }
                    if (resumed){
                        if (mostrarCercanos) {
                            getCurrentStatus("1");
                        }else {
                            getCurrentStatus("0");
                        }
                        if (currentStatus>=2) {
                            getMyTaxi();
                        }else {
                            getUnidadesCercanas();
                        }
                    }
                    PostLocation postLocation = new PostLocation();
                    postLocation.post(currentLocation, idUsuario, MainActivity.this);
                    Log.e(LOG_TAG, "onLocationChanged: posting new better location");
                }else {
                    Log.e(LOG_TAG, "onLocationChanged: idUsuario 0");
                }

            }

            long dif=location.getTime()-currentLocation.getTime();
            long difSecs= TimeUnit.MILLISECONDS.toSeconds(dif);

            //Esto para que solo se compruebe cada 5 segundos o mas
            //Por que este metodo puede ser llamado muchas veces en un mismo segundo
            if (difSecs>=5){
                //Sin importar si la nueva locacion es mejor o no
                // se actualiza la variable sin postear nada y se actualiza la interfaz en newLocation()
                currentLocation=location;
                lasLatLng=currentLatLng;
                currentLatLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                if (resumed) {
                    newLocation();
                }

                if (isFirstTime && resumed){
                    Log.e("tst reqsol", "is fisrttime resumed");
                    preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
                    currentStatus=preferences.getInt(getString(R.string.current_status),0);
                    if (currentStatus<1) {
                        Log.e("tst reqsol", "current status is < 1");
                        requiresSolicitar();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Log.e("dismiss in", "oLC fisrttime resumed");
                        }
                        isFirstTime = false;
                    }
                    newLocation();
                    if (mMap!=null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                    }
                }

                if (!currentValid && resumed){
                    if (mostrarCercanos) {
                        getCurrentStatus("1");
                    }else {
                        getCurrentStatus("0");
                    }
                }
            }else {
                Log.e(LOG_TAG, "onLocationChanged: not 5 or more");
            }
        }else {
            //Solo de damos el primer valor a la locacion y derivados y posteamos
            currentLocation=location;
            lasLatLng=currentLatLng;
            currentLatLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());


            if (idUsuario!=0){
                if (resumed && isFirstTime){
                    Log.e("tst reqsol", "is fisrttime resumed");
                    preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
                    currentStatus=preferences.getInt(getString(R.string.current_status),0);
                    if (currentStatus<1) {
                        Log.e("tst reqsol", "current status is < 1");
                        requiresSolicitar();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Log.e("dismiss in", "oLC fisrttime resumed");
                        }
                        isFirstTime = false;
                    }
                    newLocation();
                    if (mMap!=null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                    }

                    if (currentService!=0){
                        if (mostrarCercanos) {
                            getCurrentStatus("1");
                        }else {
                            getCurrentStatus("0");
                        }
                    }
                    if (mostrarCercanos) {
                        getCurrentStatus("1");
                    }else {
                        getCurrentStatus("0");
                    }
                    if (currentStatus>=2) {
                        getMyTaxi();
                    }else {
                        getUnidadesCercanas();
                    }
                }
                PostLocation postLocation = new PostLocation();
                postLocation.post(currentLocation, idUsuario, MainActivity.this);
                Log.e(LOG_TAG, "onLocationChanged: posting new better location");
            }else {
                Log.e(LOG_TAG, "onLocationChanged: idUsuario 0");
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);


        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gdl,14));


         if (currentLatLng!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,14));
        }else {
             mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(morelia,14));
         }


         mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
             @Override
             public boolean onMarkerClick(Marker marker) {

                 if(marker.getTitle()!=null) {
                     if (marker.getTitle().contains("axi")) {
                         if (currentStatus >= 1) {
                             actionUnitDetails();
                         }
                         return true;
                     }
                 }

                     if (marker.getTag()!=null){

                         int id = (int) marker.getTag();

                         if (realm != null) {
                             if (realm.isClosed() || realm.isEmpty()) {
                                 realm = null;
                                 RealmConfiguration config = new RealmConfiguration
                                         .Builder()
                                         .deleteRealmIfMigrationNeeded()
                                         .build();

                                 Realm.setDefaultConfiguration(config);

                                 realm = Realm.getDefaultInstance();
                                 realm.setAutoRefresh(true);
                             }
                         } else {
                             RealmConfiguration config = new RealmConfiguration
                                     .Builder()
                                     .deleteRealmIfMigrationNeeded()
                                     .build();

                             Realm.setDefaultConfiguration(config);

                             realm = Realm.getDefaultInstance();
                             realm.setAutoRefresh(true);
                         }

                         final Compartido tempo = realm.where(Compartido.class)
                                 .equalTo("idServ", id)
                                 .findFirst();

                         if (tempo==null){
                             Log.e(LOG_TAG, "compar is null");
                         }


                         if (tempo!=null && marker.getTitle().contains("ompartido")){
                             //compartido
                             Log.e(LOG_TAG, "compar " + tempo.toString());
                             if (vCompar != null) {
                                 vCompar = null;
                             }

                             vCompar = inflater.inflate(R.layout.ventana_compartido, null);
                             vcUser = (TextView) vCompar.findViewById(R.id.usernameCM);
                             vcStatus = (TextView) vCompar.findViewById(R.id.statusCM);
                             vcMarcaModelo = (TextView) vCompar.findViewById(R.id.marcaModeloCM);
                             vcPlacas = (TextView) vCompar.findViewById(R.id.placasCM);
                             vcColor = (TextView) vCompar.findViewById(R.id.colorCM);
                             vcAño = (TextView) vCompar.findViewById(R.id.añoCM);
                             unidadCM = (ImageView) vCompar.findViewById(R.id.unidadCM);

                             vcUser.setText(tempo.getUserName());
                             vcStatus.setText(String.valueOf(tempo.getSatus()));
                             String mm = tempo.getMarca() + " " + tempo.getModelo();
                             vcMarcaModelo.setText(mm);
                             vcPlacas.setText(tempo.getPlacas());
                             vcColor.setText(tempo.getColor());
                             vcAño.setText(tempo.getAño());
                             Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/unidades/" + tempo.getFotoUnidad()).into(unidadCM);

                             if (dialogCompar != null) {
                                 if (dialogCompar.isShowing()) {
                                     dialogCompar.dismiss();
                                 }
                                 dialogCompar = null;
                             }

                             AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                     .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {
                                             dialogInterface.dismiss();
                                         }
                                     });

                             if (vCompar.getParent() == null) {
                                 dialogCompar = builder.create();
                                 dialogCompar.setView(vCompar);
                                 dialogCompar.show();
                             } else {
                                 vCompar = null;

                                 vCompar = inflater.inflate(R.layout.ventana_compartido, null);
                                 vcUser = (TextView) vCompar.findViewById(R.id.usernameCM);
                                 vcStatus = (TextView) vCompar.findViewById(R.id.statusCM);
                                 vcMarcaModelo = (TextView) vCompar.findViewById(R.id.marcaModeloCM);
                                 vcPlacas = (TextView) vCompar.findViewById(R.id.placasCM);
                                 vcColor = (TextView) vCompar.findViewById(R.id.colorCM);
                                 vcAño = (TextView) vCompar.findViewById(R.id.añoCM);
                                 unidadCM = (ImageView) vCompar.findViewById(R.id.unidadCM);

                                 vcUser.setText(tempo.getUserName());
                                 vcStatus.setText(String.valueOf(tempo.getSatus()));
                                 String mmm = tempo.getMarca() + " " + tempo.getModelo();
                                 vcMarcaModelo.setText(mmm);
                                 vcPlacas.setText(tempo.getPlacas());
                                 vcColor.setText(tempo.getColor());
                                 vcAño.setText(tempo.getAño());
                                 Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/unidades/" + tempo.getFotoUnidad()).into(unidadCM);

                                 dialogCompar = builder.create();
                                 dialogCompar.setView(vCompar);
                                 dialogCompar.show();
                             }

                             realm.close();
                             //End compar

                         }else {
                             //Lugar
                             Log.e(LOG_TAG, "entro a lugar ");
                             final Lugar tempoL= realm.where(Lugar.class)
                                     .equalTo("id", id)
                                     .findFirst();
                             if (tempoL!=null) {
                                 Log.e(LOG_TAG, "lugar " + tempoL.toString());
                                 Log.e(LOG_TAG, "lugar " + tempoL.toString());

                                 final Lugar lugar = new Lugar();
                                 lugar.setId(tempoL.getId());
                                 lugar.setNombre(tempoL.getNombre());
                                 lugar.setDescripcion(tempoL.getDescripcion());
                                 if (tempoL.getImagen() != null) {
                                     lugar.setImagen(tempoL.getImagen());
                                 }
                                 //lugar.setPromo(promo);

                                 lugar.setHorario(tempoL.getHorario());
                                 lugar.setDireccion(tempoL.getDireccion());
                                 lugar.setCategoria(tempoL.getCategoria());
                                 lugar.setSitio(tempoL.getSitio());
                                 lugar.setCorreo(tempoL.getCorreo());
                                 lugar.setTelefono(tempoL.getTelefono());
                                 if (tempoL.getIdRuta() != 0) {
                                     lugar.setIdRuta(tempoL.getIdRuta());
                                     lugar.setOrden(tempoL.getOrden());
                                 }

                                 final double lat = tempoL.getLatitud();
                                 final double lng = tempoL.getLongitud();


                                 if (vLugar != null) {
                                     vLugar = null;
                                 }

                                 vLugar = inflater.inflate(R.layout.ventana_lugar, null);
                                 vlTitulo = (TextView) vLugar.findViewById(R.id.vLugarTitulo);
                                 vlDescripcion = (TextView) vLugar.findViewById(R.id.vLugarDeescripcion);
                                 vlDireccion = (TextView) vLugar.findViewById(R.id.vLugarDireccion);
                                 vlHorario = (TextView) vLugar.findViewById(R.id.vLugarHorario);
                                 vlImagen = (ImageView) vLugar.findViewById(R.id.vLugarImagen);
                                 vlTitulo.setText(tempoL.getNombre());
                                 vlDescripcion.setText(tempoL.getDescripcionCorta());
                                 vlDireccion.setText(tempoL.getDireccion());
                                 vlHorario.setText(tempoL.getHorario());

                                 if (tempoL.getImagen() != null && tempoL.getImagen().trim().length() > 1) {
                                     Picasso.with(MainActivity.this).load(getString(R.string.imagenesLugaresURLTaxi) + tempoL.getImagen()).into(vlImagen);
                                 }

                                 if (dialogLugar != null) {
                                     if (dialogLugar.isShowing()) {
                                         dialogLugar.dismiss();
                                     }
                                     dialogLugar = null;
                                 }

                                 AlertDialog.Builder builderL = new AlertDialog.Builder(MainActivity.this)
                                         .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface, int i) {
                                                 dialogInterface.dismiss();
                                             }
                                         });

                                 if (vLugar.getParent() == null) {
                                     dialogLugar = builderL.create();
                                     dialogLugar.setView(vLugar);
                                     dialogLugar.show();
                                 } else {
                                     vLugar = null;

                                     vLugar = inflater.inflate(R.layout.ventana_lugar, null);
                                     vlTitulo = (TextView) vLugar.findViewById(R.id.vLugarTitulo);
                                     vlDescripcion = (TextView) vLugar.findViewById(R.id.vLugarDeescripcion);
                                     vlDireccion = (TextView) vLugar.findViewById(R.id.vLugarDireccion);
                                     vlHorario = (TextView) vLugar.findViewById(R.id.vLugarHorario);
                                     vlImagen = (ImageView) vLugar.findViewById(R.id.vLugarImagen);

                                     vlTitulo.setText(tempoL.getNombre());
                                     vlDescripcion.setText(tempoL.getDescripcion());
                                     vlDireccion.setText(tempoL.getDireccion());
                                     vlHorario.setText(tempoL.getHorario());
                                     if (tempoL.getImagen() != null) {
                                         Picasso.with(MainActivity.this).load(getString(R.string.imagenesLugaresURLTaxi) + tempoL.getImagen()).into(vlImagen);
                                     }
                                     dialogLugar = builderL.create();
                                     dialogLugar.setView(vLugar);
                                     dialogLugar.show();
                                 }

                                 realm.close();
                                 //Ends lugar

                             }//here
                         }

                     }

                 return false;
             }
         });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                //ACCION sin validar cada marker especifico

                latD = mDestino.getPosition().latitude;
                longD = mDestino.getPosition().longitude;

                if (progressDialog!=null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in","dragEnd before");
                }
                progressDialog=null;
                if(MainActivity.this.getParent()!=null){
                    progressDialog=new ProgressDialog(MainActivity.this.getParent());
                }else{
                    progressDialog=new ProgressDialog(MainActivity.this);
                }
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Cargando...");
                if(!MainActivity.this.isFinishing()) {
                    progressDialog.show();
                    Log.e("show in","dragEnd");
                }

                mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                if (!originLocation){

                    latO = mOrigen.getPosition().latitude;
                    longO = mOrigen.getPosition().longitude;

                    if (marker.getTitle().contains("rigen")){
                        try {
                            addressInfo = mGeocoder.getFromLocation(latO, longO, 1);

                            String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                            coloniaO=split[1]+" "+split[2];

                            dirO=addressInfo.get(0).getAddressLine(0);

                            patchA1.setEnabled(false);
                            patchA1.setVisibility(View.GONE);

                            auto1.setText(addressInfo.get(0).getAddressLine(0));

                            orig=new LatLng(latO,longO);
                            if (dest != null){
                                new getMetersAndSecs().execute(new DirectionsUrl().getUrl(orig, dest));
                            }

                            //autocomplete API google necesita client id, revisar *****************

                            addressInfo.clear();
                        } catch (Exception e) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                Log.e("dismiss in", "dragEnd catch "+e.toString());
                            }
                            e.printStackTrace();
                        }
                    }

                }else {

                    latO = currentLatLng.latitude;
                    longO = currentLatLng.longitude;

                        try {
                            addressInfo = mGeocoder.getFromLocation(latO, longO, 1);

                            String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                            coloniaO=split[1]+" "+split[2];

                            dirO=addressInfo.get(0).getAddressLine(0);

                            patchA1.setEnabled(false);
                            patchA1.setVisibility(View.GONE);

                            auto1.setText(addressInfo.get(0).getAddressLine(0));

                            //TODO:
                            //call method to consult units and prices
                            //tarifarioW(addressInfo.get(0).getLocality());
                            orig=new LatLng(latO,longO);
                            if (dest != null){
                                new getMetersAndSecs().execute(new DirectionsUrl().getUrl(orig, dest));
                            }

                            //autocomplete API google necesita client id, revisar *****************

                            addressInfo.clear();

                        } catch (Exception e) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                Log.e("dismiss in", "dragEnd catch 2 "+e.toString());
                            }
                            e.printStackTrace();
                        }

                }

                if (marker.getTitle().contains("estino")){
                    try {
                        addressInfo = mGeocoder.getFromLocation(latD, longD, 1);

                        //Log.e("Address info loc", "complete="+addressInfo+" line 0="+addressInfo.get(0)+" line 1="+addressInfo.get(1)+" line 2="+addressInfo.get(2)+" size="+addressInfo.size());

                        String[] split=addressInfo.get(0).getAddressLine(0).split(",");

                        coloniaD=split[1]+" "+split[2];

                        //onlyColD=addressInfo.get(0).getAddressLine(1);

                        dirD=addressInfo.get(0).getAddressLine(0);

                        auto2.setText(addressInfo.get(0).getAddressLine(0));

                        //TODO:
                        //call method to consult units and prices
                        //tarifarioW(addressInfo.get(0).getLocality());
                        dest=new LatLng(latD,longD);
                        if (orig != null){
                            new getMetersAndSecs().execute(new DirectionsUrl().getUrl(orig, dest));
                        }

                        addressInfo.clear();

                    } catch (Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Log.e("dismiss in", "dragEnd catch3 "+e.toString());
                        }
                        e.printStackTrace();
                    }

                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e("dismiss in", "dragEnds last");
                }

            }

        });

        //requiresSolicitar();

    }

    private void activarSM(){
        try {
            smBack.setVisibility(View.VISIBLE);
            smColor.setVisibility(View.VISIBLE);
            smClr.setVisibility(View.VISIBLE);
            smPlcs.setVisibility(View.VISIBLE);
            smPlacas.setVisibility(View.VISIBLE);
            smUnidad.setVisibility(View.VISIBLE);
            sMarcaModelo.setVisibility(View.VISIBLE);

            smPlacas.setText(currentRealmService.getPlacas());
            Picasso.with(MainActivity.this).load("http://softtim.mx/mobilityV2/assets/img/unidades/" + currentRealmService.getFoto_un()).into(smUnidad);
            sMarcaModelo.setText(currentRealmService.getMarca() + " " + currentRealmService.getModelo());

        }catch (Exception e){
            Log.e(LOG_TAG,"Catch activarSM "+e.toString());
        }

    }

    private void desactivarSM(){
        sMarcaModelo.setVisibility(View.GONE);
        smPlacas.setVisibility(View.GONE);
        smUnidad.setVisibility(View.GONE);
        smPlcs.setVisibility(View.GONE);
        smClr.setVisibility(View.GONE);
        smColor.setVisibility(View.GONE);
        smBack.setVisibility(View.GONE);
    }

    private void clearTypesPrices(){
        fondoCostos.setVisibility(View.GONE);
        costoUnoUno.setVisibility(View.GONE);
        costoUnoDos.setVisibility(View.GONE);
        costoDosDos.setVisibility(View.GONE);
        costoUnoTres.setVisibility(View.GONE);
        costoDosTres.setVisibility(View.GONE);
        costoTresTres.setVisibility(View.GONE);
        costoUnoCuatro.setVisibility(View.GONE);
        costoDosCuatro.setVisibility(View.GONE);
        costoTresCuatro.setVisibility(View.GONE);
        costoCuatroCuatro.setVisibility(View.GONE);
        tipoUnoUno.setVisibility(View.GONE);
        tipoUnoDos.setVisibility(View.GONE);
        tipoDosDos.setVisibility(View.GONE);
        tipoUnoTres.setVisibility(View.GONE);
        tipoDosTres.setVisibility(View.GONE);
        tipoTresTres.setVisibility(View.GONE);
        tipoUnoCuatro.setVisibility(View.GONE);
        tipoDosCuatro.setVisibility(View.GONE);
        tipoTresCuatro.setVisibility(View.GONE);
        tipoCuatroCuatro.setVisibility(View.GONE);
    }

    private void typesPrices(){


        final int[] id = new int[4];
        final String[] tipo = new String[4];
        final double[] factor = new double[4];
        final double[] segundo = new double[4];
        final double[] metro = new double[4];

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Usuario/tipos_unidades";
        parametros.put("dLatitud",currentLocation.getLatitude());
        parametros.put("dLongitud",currentLocation.getLongitude());
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LOG_TAG," typesPrices response:"+ new String(responseBody));

                final JSONArray[] jsonArray = new JSONArray[1];
                int jLenght=1;
                try {
                    jsonArray[0]=new JSONArray(new String(responseBody));
                    jLenght=jsonArray[0].length();
                    for (int x=0;x<jLenght;x++){
                        Log.e("For", x+ " lenght " + jsonArray[0].length());
                        id[x]=jsonArray[0].getJSONObject(x).getInt("idTipoUnidad");
                        tipo[x]=jsonArray[0].getJSONObject(x).getString("aTipoUnidad");
                        factor[x]=jsonArray[0].getJSONObject(x).getDouble("dFactor");
                        segundo[x]=jsonArray[0].getJSONObject(x).getDouble("dFactorTiempo");
                        metro[x]=jsonArray[0].getJSONObject(x).getDouble("dFactorDistancia");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                fondoCostos.setText("");

                preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();

                switch (jLenght){
                    case 3:
                        //Tipo y costo 1 de 3
                        tipoUnoTres.setVisibility(View.VISIBLE);
                        tipoUnoTres.setText(tipo[0]);
                        tipoUnoTres.setTag(id[0]);

                        double costUnoTres=(metros*metro[0]) + (segundos*segundo[0]);
                        costUnoTres=costUnoTres*factor[0];
                        costUnoTres = Math.round(costUnoTres * 100) / 100;
                        if(costUnoTres<40){
                            costUnoTres=40;
                        }
                        Log.e("costo",costUnoTres+"");
                        int cost11=(int)costUnoTres;
                        Log.e("costo int ",cost11+"");
                        String cost1=variacion(cost11,1);
                        costoUnoTres.setVisibility(View.VISIBLE);
                        costoUnoTres.setText(cost1);
                        costoUnoTres.setTag(costUnoTres);

                        editor.putFloat(getString(R.string.factor_metro_3_1),(float)metro[0]);
                        editor.putFloat(getString(R.string.factor_segundo_3_1),(float)segundo[0]);

                        //Tipo y costo 2 de 3
                        tipoDosTres.setVisibility(View.VISIBLE);
                        tipoDosTres.setText(tipo[1]);
                        tipoDosTres.setTag(id[1]);

                        double costDosTres=(metros*metro[1]) + (segundos*segundo[1]);
                        costDosTres=costDosTres*factor[1];
                        costDosTres = Math.round(costDosTres * 100) / 100;
                        if(costDosTres<40){
                            costDosTres=40;
                        }
                        Log.e("costo",costDosTres+"");
                        int cost22=(int)costDosTres;
                        Log.e("costo int ",cost22+"");
                        String cost2=variacion(cost22,2);
                        costoDosTres.setVisibility(View.VISIBLE);
                        costoDosTres.setText(cost2);
                        costoDosTres.setTag(costDosTres);

                        editor.putFloat(getString(R.string.factor_metro_3_2),(float)metro[1]);
                        editor.putFloat(getString(R.string.factor_segundo_3_2),(float)segundo[1]);

                        //tipo y costo 3 de 3
                        tipoTresTres.setVisibility(View.VISIBLE);
                        tipoTresTres.setText(tipo[2]);
                        tipoTresTres.setTag(id[2]);

                        double costTresTres=(metros*metro[2]) + (segundos*segundo[2]);
                        costTresTres=costTresTres*factor[2];
                        costTresTres = Math.round(costTresTres * 100) / 100;
                        if(costTresTres<40){
                            costTresTres=40;
                        }
                        Log.e("costo",costTresTres+"");
                        int cost33=(int)costTresTres;
                        Log.e("costo int ",cost33+"");
                        String cost3=variacion(cost33,3);
                        costoTresTres.setVisibility(View.VISIBLE);
                        costoTresTres.setText(cost3);
                        costoTresTres.setTag(costTresTres);

                        editor.putFloat(getString(R.string.factor_metro_3_3),(float)metro[2]);
                        editor.putFloat(getString(R.string.factor_segundo_3_3),(float)segundo[2]);

                        editor.commit();

                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private String variacion(int costo,int tipo){
        String toShow = "Sin datos de costo";
        if (tipo==1) {
            if (costo <= 70) {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            } else {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            }
        }else if (tipo==2){
            if (costo <= 70) {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            } else {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            }
        }else if (tipo==3){
            if (costo <= 70) {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            } else {
                int v1 = costo - 5;
                int v2 = costo + 5;
                toShow = "$ "+v1 + " - " + "$ "+v2;
            }
        }
        return toShow;
    }

    private class getMetersAndSecs extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Show calculando
            fondoCostos.setText("Calculando...");
            fondoCostos.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = DownloadAPIDirectionsInfo.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.e("Polylilinea sugerida", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            JSONObject jObject;
            try {
                jObject = new JSONObject(result);

                DirectionsJSONParser parser = new DirectionsJSONParser();
                parser.parse(jObject);
                segundos = parser.getSegundos();
                metros = parser.getMetros();
                Log.e("getMS", "m="+metros+" s="+segundos);
            } catch (Exception e) {
                Log.e("catch getMS", e.toString());
                e.printStackTrace();
            }

            typesPrices();

        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(polyline!=null){
                polyline.remove();
            }
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = DownloadAPIDirectionsInfo.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.e("Polylilinea sugerida", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            JSONObject jObject;
            try {
                jObject = new JSONObject(result);

                DirectionsJSONParser parser = new DirectionsJSONParser();
                parser.parse(jObject);
                timeAprox = parser.getAproxTime();
                String dist = parser.getDistance();
                String bPoint;
                PolylineOptions polylineOptions = parser.getPolylineOptions();
                polylineOptions.color(0x60000000);
                estado.setVisibility(View.VISIBLE);
                if (currentStatus==2){
                    estado.setText("Taxi a "+dist+". Llega en "+timeAprox +" aprox ");
                }
                if (currentStatus==3){
                    bPoint="destino";
                    estado.setText(dist+", "+timeAprox +" aprox a "+bPoint);
                }
                polyline = mMap.addPolyline(polylineOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tarifarioW(String locality){

        //Array JSON que cada JSON contenga tipo de unidad y costo, segun el lengh se usara
        //la combinacion de botones, mas lenght=4

        //Show dialog

        //Usuario/costo_tarifario

        //post aColoniaOrigen aColoniaDestino aCiudad

        //dCosto

        final JSONObject[] costData = new JSONObject[1];


        Log.e(LOG_TAG," typesPrices loc "+ locality +" a="+onlyColO+" d="+onlyColD);

        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Usuario/costo_tarifario";
        parametros.put("aColoniaOrigen",onlyColO);
        parametros.put("aColoniaDestino",onlyColD);
        parametros.put("aCiudad",locality);
        //Mandar token prob.
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp=new String(responseBody);
                Log.e(LOG_TAG," typesPrices response:"+ new String(responseBody));

                costoUnoUno.setVisibility(View.VISIBLE);

                if (resp!=null) {
                    if (resp.contains("rror")){
                        costoUnoUno.setText("(Sin datos de estimacion de costo)");
                    }else {

                        try {
                            costData[0] = new JSONObject(new String(responseBody));

                            String costo=costData[0].getString("dCosto");
                            costoUnoUno.setText("Costo estimado: "+costo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    costoUnoUno.setText("(Sin datos de estimacion de costo)");
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"getMyTaxi statusCode"+statusCode);
                //TODO: Importante
                //Fallo al consutlar el status actual, la funcionalidad de la app esta comprometida.
                //Ejemplo: el conductor podria recibir o aceptar un nuevo servicio que resultaria en errores e incosistencias
                //Comprobar currentValid antes de cada accion

                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status actual.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
}
