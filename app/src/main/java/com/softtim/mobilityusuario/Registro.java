package com.softtim.mobilityusuario;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Registro extends AppCompatActivity {

    String LOG_TAG="Registro";
    EditText Telefono, Email, Nombre, Pass, Pass2;
    Button enviar;
    LoginButton loginButton;
    ProgressDialog progressDialog;
    Boolean valid=false;
    TextView testNombre,testCorreo,testMovil,testPass,testPass2,oTV;
    String fbEmail,fbID,fbName;
    CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        loginButton = (LoginButton)findViewById(R.id.login_button);
        oTV=(TextView)findViewById(R.id.oRTV);

        if (getIntent().hasCategory("fb")){
            loginButton.setVisibility(View.GONE);
            oTV.setVisibility(View.GONE);
            String fbName=getIntent().getStringExtra("name");
            String fbEmail=getIntent().getStringExtra("email");
            Nombre.setText(fbName);
            Email.setText(fbEmail);
            Telefono.requestFocus();
        }else {

            FacebookSdk.sdkInitialize(this.getApplicationContext());

            callbackManager = CallbackManager.Factory.create();

            loginButton.setReadPermissions("email","public_profile","user_friends");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //get token to after handle data with it
                    String token=loginResult.getAccessToken().getToken();
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

                                    new GraphRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/"+fbID+"/friends/",
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {
                                                /* handle the result */
                                                    Log.e(LOG_TAG,"friends resp= "+response.toString());
                                                }
                                            }
                                    ).executeAsync();

                                    if (!fbEmail.equals("") && !fbName.equals("")) {
                                        //handle
                                        Nombre.setText(fbName);
                                        Email.requestFocus();
                                        Email.setText(fbEmail);
                                        Telefono.requestFocus();
                                        oTV.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.GONE);

                                    } else {
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

                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(LOG_TAG,"FB error. error= "+error.toString());
                }
            });

        }

        enviar = (Button)findViewById(R.id.registrarRegistro);
        Nombre = (EditText)findViewById(R.id.nombreRegistro);
        Nombre.setFocusable(true);
        Telefono = (EditText)findViewById(R.id.movilRegistro);
        Telefono.setFocusable(true);
        Email = (EditText)findViewById(R.id.correoRegistro);
        Email.setFocusable(true);
        Pass = (EditText)findViewById(R.id.passRegistro);
        Pass.setFocusable(true);
        Pass2 = (EditText)findViewById(R.id.confirmPassRegistro);
        Pass2.setFocusable(true);

        testNombre=(TextView)findViewById(R.id.nombreTest);
        testCorreo=(TextView)findViewById(R.id.correoTest);
        testMovil=(TextView)findViewById(R.id.numeroTest);
        testPass=(TextView)findViewById(R.id.passTest);
        testPass2=(TextView)findViewById(R.id.confirmPassTest);

        Nombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    validarNombre();
                }
            }
        });

        Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    validarCorreo();
                }
            }
        });

        Telefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    validarTelefono();
                }
            }
        });

        Pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    validarPass1();
                }
            }
        });

        Pass2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    validarPass2();
                }
            }
        });

        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Espere...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()) {
                    if (valid && testNombre.getText().toString().contains("orrecto") &&
                            testCorreo.getText().toString().contains("orrecto") &&
                            testMovil.getText().toString().contains("orrecto") &&
                            testPass2.getText().toString().contains("orrecto")){
                        enviar.setEnabled(false);

                        postRegistro();

                    }else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Por favor completa todos los campos correctamente.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "No estas conectado a internet.", Snackbar.LENGTH_LONG)
                            .setAction("", null).show();
                }

            }
        });

    }

    void validarNombre(){
        final String LOG_TAG="testNombre";

        String nom=Nombre.getText().toString().trim();

        if (nom.length()>4){

            if (validateName(nom)){
                testNombre.setText("Correcto");
                testNombre.setTextColor(Color.parseColor("#00c853"));
                valid=true;
            }else {
                testNombre.setText("Por favor ingresa un nombre valido. Como minimo debes ingresar un nombre y un apellido.");
                testNombre.setTextColor(Color.parseColor("#d50000"));
                valid=false;
            }

        }else {
            testNombre.setText("Por favor ingresa un nombre valido");
            testNombre.setTextColor(Color.parseColor("#d50000"));
            valid=false;
        }
    }

    public static boolean validateName(String txt) {

        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }

    /*
    /Inicio/registro_usuario
    recibe aPassword (en md5), aNombreUsuario, aEmail, aTelefono;
    */

    void postRegistro(){
        final String LOG_TAG="testPostRegitro";

        String pass=Cifrar.toMD5(Pass.getText().toString().trim());
        String nombre=Nombre.getText().toString().trim();
        String mail=Email.getText().toString().trim();
        String tel=Telefono.getText().toString().trim();

        String url=getString(R.string.mainURL)+"/Inicio/registro_usuario";


        //Dialog
        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.e("dismiss in","c stat before");
        }
        progressDialog=null;
        if(Registro.this.getParent()!=null){
            progressDialog=new ProgressDialog(Registro.this.getParent());
        }else{
            progressDialog=new ProgressDialog(Registro.this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        if(!Registro.this.isFinishing()) {
            progressDialog.show();
            Log.e("show in","");
        }

        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("aPassword",pass);
        params.put("aNombreUsuario",nombre);
        params.put("aEmail",mail);
        params.put("aTelefono",tel);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                enviar.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                Intent redirecLogin = new Intent(getBaseContext(),LoginActivity.class);

                startActivity(redirecLogin);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();
                enviar.setEnabled(true);
            }
        });
    }

    void validarCorreo(){
        String correo=Email.getText().toString().trim();
        if (!isValidEmail(correo)) {
            testCorreo.setText("Por favor ingresa un correo electrónico valido.");
            testCorreo.setTextColor(Color.parseColor("#d50000"));
            valid=false;
        }else {

            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.e("dismiss in","c stat before");
            }
            progressDialog=null;
            if(Registro.this.getParent()!=null){
                progressDialog=new ProgressDialog(Registro.this.getParent());
            }else{
                progressDialog=new ProgressDialog(Registro.this);
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Loading...");
            if(!Registro.this.isFinishing()) {
                progressDialog.show();
                Log.e("show in","");
            }

            final String LOG_TAG="testCorreo";
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("aEmail",correo);
            client.post(getString(R.string.mainURL)+"/Usuario/valida_email", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String resp=new String(responseBody);
                    Log.e(LOG_TAG,"response: "+resp);
                    if (resp.contains("1") || resp.contains("rue")){
                        testCorreo.setText("Correcto");
                        testCorreo.setTextColor(Color.parseColor("#00c853"));
                        valid=true;
                    }else {
                        testCorreo.setText("El correo electronico ingresado ya se encuentra registrado");
                        testCorreo.setTextColor(Color.parseColor("#d50000"));
                        valid=false;
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    testCorreo.setText("ERROR (Por favor comprueba tu conexion)");
                    testCorreo.setTextColor(Color.parseColor("#d50000"));
                    valid=false;
                    progressDialog.dismiss();
                }
            });

        }
    }

    void validarTelefono(){
        final String LOG_TAG="testTel";
        String tel=Telefono.getText().toString().trim();

        if (tel.matches("\\d{10}")){

            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Log.e("dismiss in","c stat before");
            }
            progressDialog=null;
            if(Registro.this.getParent()!=null){
                progressDialog=new ProgressDialog(Registro.this.getParent());
            }else{
                progressDialog=new ProgressDialog(Registro.this);
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Loading...");
            if(!Registro.this.isFinishing()) {
                progressDialog.show();
                Log.e("show in","");
            }

            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("aTelefono",tel);
            client.post(getString(R.string.mainURL)+"/Usuario/valida_celular", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String resp=new String(responseBody);
                    Log.e(LOG_TAG,"response: "+resp);
                    if (resp.contains("1") || resp.contains("rue")){
                        testMovil.setText("Correcto");
                        testMovil.setTextColor(Color.parseColor("#00c853"));
                        valid=true;
                    }else {
                        testMovil.setText("El telefono ingresado ya se encuentra registrado");
                        testMovil.setTextColor(Color.parseColor("#d50000"));
                        valid=false;
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    testMovil.setText("ERROR (Por favor comprueba tu conexion)");
                    testMovil.setTextColor(Color.parseColor("#d50000"));
                    valid=false;
                    progressDialog.dismiss();
                }
            });

        }else {
            testMovil.setText("Por favor ingresa un telefono valido (10 digitos).");
            testMovil.setTextColor(Color.parseColor("#d50000"));
            valid=false;
        }
    }

    void validarPass1(){
        final String LOG_TAG="testPass1";
        String pass1=Pass.getText().toString().trim();

        if (pass1.length()>5){
            testPass.setText("Correcto");
            testPass.setTextColor(Color.parseColor("#00c853"));
            valid=true;
        }else {
            testPass.setText("La contraseña debe ser de 6 digitos o mas");
            testPass.setTextColor(Color.parseColor("#d50000"));
            valid=false;
        }

    }

    void validarPass2(){
        final String LOG_TAG="testPass2";
        String pass2=Pass2.getText().toString().trim();

        if (pass2.equals(Pass.getText().toString().trim())){
            testPass2.setText("Correcto");
            testPass2.setTextColor(Color.parseColor("#00c853"));
            valid=true;
        }else {
            testPass2.setText("Las contraseñas no coinciden");
            testPass2.setTextColor(Color.parseColor("#d50000"));
            testPass.setText("Las contraseñas no coinciden");
            testPass.setTextColor(Color.parseColor("#d50000"));
            valid=false;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
