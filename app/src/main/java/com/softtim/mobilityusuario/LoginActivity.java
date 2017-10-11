package com.softtim.mobilityusuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import org.json.JSONException;
import org.json.JSONObject;



public class LoginActivity extends AppCompatActivity {

    Button iniciar;
    EditText user,pass;
    SharedPreferences preferences;
    String LOG_TAG="LoginActivity";
    String currentIdCelular,aToken,fbID,fbName,fbEmail;
    ProgressDialog progressDialog;
    AlertDialog dialogCampoVacio;
    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView registro;
    AccessToken accessToken;
    double lat=0,lng=0;
    AccessTokenTracker accessTokenTracker;

    //Validar iniciar en main, datos de inicio de sesion y preferencias



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logosimpletoolbar);
        toolbar.setTitle("Acceder");
        setSupportActionBar(toolbar);

        aToken=FirebaseInstanceId.getInstance().getToken();

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);


        SingleShotLocationProvider.requestSingleUpdate(LoginActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.e("Location Login", "my location is " + location.toString());
                        lat=location.latitude;
                        lng=location.longitude;
                    }
                });

        registro=(TextView)findViewById(R.id.irRegistrar);
        registro.setPaintFlags(registro.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,Registro.class);
                startActivity(intent);
            }
        });

        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.accediendo));

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //get token to after handle data with it
                final String token=loginResult.getAccessToken().getToken();
                accessToken=loginResult.getAccessToken();
                Log.e(LOG_TAG,"loginButtoon success. loginresult= "+loginResult.toString()+" token="+token);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.e(LOG_TAG,"Graph1 resp= "+response.toString());
                                fbEmail = object.optString("email", "");
                                fbID = object.optString("id");
                                fbName = object.optString("name");

                                final JSONObject[] data = new JSONObject[1];
                                final JSONObject[] amigos = new JSONObject[1];

                                new GraphRequest(
                                        loginResult.getAccessToken(),
                                        "/"+fbID+"/friends/",
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                                /* handle the result */
                                                Log.e(LOG_TAG, "friends resp= " + response.toString());
                                                try {
                                                    data[0] =new JSONObject(response.toString());

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                ).executeAsync();



                                if (fbID!=null) {
                                    //falta CONFIRMAR
                                    login(1, data[0]);
                                }else {
                                    Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
                                            .show();
                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }//Aqui

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(LOG_TAG,"FB error. error= "+error.toString());
                Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
                        .show();
            }
        });

        user=(EditText)findViewById(R.id.loginUser);
        pass=(EditText)findViewById(R.id.loginPass);

        iniciar=(Button)findViewById(R.id.loginGo);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getText().toString().trim().length()>0
                        && pass.getText().toString().trim().length()>0){
                    login(2,null);
                }else {
                    dialogCampoVacio=null; //Antes de crear una nueva vista del dialog lo ponemos como null
                    dialogCampoVacio=new AlertDialog.Builder(LoginActivity.this)
                            .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setMessage(getString(R.string.campos_vacio))
                            .show();
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void login(final int type, @Nullable final JSONObject data){

        aToken=FirebaseInstanceId.getInstance().getToken();

        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.e("dismiss in","c stat before");
        }
        progressDialog=null;
        if(LoginActivity.this.getParent()!=null){
            progressDialog=new ProgressDialog(LoginActivity.this.getParent());
        }else{
            progressDialog=new ProgressDialog(LoginActivity.this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        if(!LoginActivity.this.isFinishing()) {
            progressDialog.show();
            Log.e("show in","");
        }

        if (lat==0 || lng==0){
            SingleShotLocationProvider.requestSingleUpdate(LoginActivity.this,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            Log.e("Location Login", "my location is " + location.toString());
                            lat=location.latitude;
                            lng=location.longitude;
                        }
                    });
        }

        final String passMD5;
        final String usuario;

            passMD5 = Cifrar.toMD5(pass.getText().toString().trim());
            usuario = user.getText().toString().trim();


        currentIdCelular=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AsyncHttpClient client=new AsyncHttpClient();
        final RequestParams params=new RequestParams();
        String url;
        if (type==2) {
            params.put("aUsuario", usuario);
            params.put("aPassword",passMD5);
            url=getString(R.string.mainURL)+"/Inicio/login_usuario";
        }else {
            params.put("idFacebookAndroid", fbID);
            url=getString(R.string.mainURL)+"/Inicio/login_usuario_facebook";
        }
        params.put("aToken",aToken);
        params.put("idCelular",currentIdCelular);
        params.put("dLatitud",lat);
        params.put("dLongitud",lng);
        //marca y modelo
        //SO v

        Log.e(LOG_TAG,"Usuario:"+user.getText().toString().trim()+
                " Password:"+passMD5+" ID:"+currentIdCelular +" Token:"+aToken);

        if (aToken!=null && aToken.length()>1){

            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e(LOG_TAG,"response:"+ new String(responseBody));
                    try {
                        JSONObject jsonObject=new JSONObject(new String(responseBody));
                        String respuesta=jsonObject.getString("login");
                        //String idCelular=jsonObject.getString("idCelular");
                        if (respuesta.contains("1")){
                            //No esta usuario logeado en otro cel
                            //User y pass estan bien
                            //usuario
                            //pass
                            //idUsuario
                            //idCelular 1 bien 2 se ha cerrado en otro cel
                            //lPasswordTemporal
                            //login
                            //Falta nombre del usuario

                            int idUsuario=jsonObject.getInt("idUsuario");
                            String lPasswordTemporal=jsonObject.getString("lPasswordTemporal");
                            String idSesion=jsonObject.getString("idSesion");
                            String aEmail=jsonObject.getString("aEmail");
                            String aTelefono=jsonObject.getString("aTelefono");
                            String aNombre=jsonObject.getString("aNombreUsuario");

                            if (lPasswordTemporal.contains("1")){
                                //Cambiar la contraseña generada por una nueva
                                //
                            }

                            //Guardar datos de usuario en preferencias
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putInt(getString(R.string.p_id_usuario),idUsuario);
                            if (type==2) {
                                editor.putString(getString(R.string.p_nick), usuario);
                                editor.putString(getString(R.string.p_pass), passMD5);
                            }
                            editor.putString(getString(R.string.p_mail), aEmail);
                            editor.putString(getString(R.string.p_tel), aTelefono);
                            editor.putString(getString(R.string.p_name), aNombre);
                            editor.putInt("active",1);
                            editor.putString(getString(R.string.p_sesion),idSesion);
                            editor.putString(getString(R.string.p_id_fb),fbID);
                            if (type==1){
                                if (data!=null){
                                    editor.putString("amigos",data.toString());
                                    editor.putString(getString(R.string.currentAccessToken),accessToken.toString());
                                }
                            }
                            editor.commit();

                            Intent intent= new Intent(LoginActivity.this, MainActivity.class);

                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();

                        }else {
                            if (respuesta.contains("0")) {
                                String usuario = jsonObject.getString("usuario");
                                String passw = jsonObject.getString("pass");

                                if (type==1){

                                    progressDialog.dismiss();

                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle("Esta cuenta de facebook no se encuentra asociada")
                                            .setMessage("Por favor inicia sesion normalmente y asocia tu cuenta de Facebook en preferencias. Si eres un usuario nuevo y no estas registrador puedes" +
                                                    " pulsa en Registro.")
                                            //Agregar codigo de error
                                            .setNeutralButton("Registro", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent= new Intent(LoginActivity.this,Registro.class);
                                                    intent.addCategory("fb");
                                                    intent.putExtra("name",fbName);
                                                    intent.putExtra("email",fbEmail);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    LoginManager.getInstance().logOut();
                                                }
                                            })
                                            .show();

                                }else {

                                    if (usuario.contains("0")) {

                                        progressDialog.dismiss();

                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("¡UPS!")
                                                .setMessage("Tu usuario es incorrecto.")
                                                //Agregar codigo de error
                                                .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();

                                    } else if (passw.contains("0")) {

                                        pass.setText("");

                                        progressDialog.dismiss();

                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("¡UPS!")
                                                .setMessage("Tu contraseña es incorrecta.")
                                                //Agregar codigo de error
                                                .setNeutralButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();

                                    } else{

                                        String idCelu=jsonObject.getString("idCelular");

                                        if (idCelu.contains("2")) {

                                            progressDialog.dismiss();

                                            new AlertDialog.Builder(LoginActivity.this)
                                                    .setTitle("¡UPS!")
                                                    .setMessage("No has podido inicar sesion por que te encuentras en proceso de servicio con otro celular.")
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

                            }
                            progressDialog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "catch "+e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error 421", Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(LOG_TAG, "statusCode:"+statusCode);
                    //Alert dialog con informacion del error
                    progressDialog.dismiss();
                }
            });

        }else {
            Toast.makeText(getApplicationContext(), "Error al intentar contactar los servicios de Google 421", Toast.LENGTH_LONG)
                    .show();
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialogCampoVacio!=null && dialogCampoVacio.isShowing()){
            dialogCampoVacio.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }


}
