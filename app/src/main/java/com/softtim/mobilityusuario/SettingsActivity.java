package com.softtim.mobilityusuario;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
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
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends AppCompatActivity {
    String LOG_TAG="SettingsActivity";
    CallbackManager callbackManager;
    LoginButton loginButton;
    String fbEmail,fbID,fbName, nick,tel,email;
    int idUsuario=0;
    TextView username,mail,num;
    SharedPreferences preferences;
    Button logout;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);

        idUsuario=preferences.getInt(getString(R.string.p_id_usuario),0);

        username=(TextView)findViewById(R.id.stUsername);
        mail=(TextView)findViewById(R.id.stMail);
        num=(TextView)findViewById(R.id.stNum);
        logout=(Button)findViewById(R.id.stLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salir();
            }
        });

        String un;

        if (preferences.contains(getString(R.string.p_nick))){
            un=preferences.getString(getString(R.string.p_nick),"Usuario");
            username.setText(un);
        }else if (preferences.contains(getString(R.string.p_mail))){
            un=preferences.getString(getString(R.string.p_mail),"Usuario");
            username.setText(un);
        }

        String ml;
        if (preferences.contains(getString(R.string.p_mail))) {
            ml = preferences.getString(getString(R.string.p_mail), "Miembro");
            mail.setText(ml);
        }

        String nm;
        if (preferences.contains(getString(R.string.p_tel))) {
            nm = preferences.getString(getString(R.string.p_tel), "");
            num.setText(nm);
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton)findViewById(R.id.login_button_s);
        loginButton.setReadPermissions("email","public_profile","user_friends");

        if (idUsuario!=0) {
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    //get token to after handle data with it
                    String token = loginResult.getAccessToken().getToken();
                    Log.e(LOG_TAG, "loginButtoon success. loginresult= " + loginResult.toString() + " token=" + token);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    Log.e(LOG_TAG, "Graph1 resp= " + response.toString());
                                    fbEmail = object.optString("email", "");
                                    fbID = object.optString("id");
                                    fbName = object.optString("name");

                                    new GraphRequest(
                                            loginResult.getAccessToken(),
                                            "/"+fbID+"/friends/",
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {
                                                /* handle the result */
                                                    Log.e(LOG_TAG, "friends resp= " + response.toString());
                                                }
                                            }
                                    ).executeAsync();


                                    //falta OCNFIRMAR
                                    syncFB();

                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,picture");
                    parameters.putString("edges", "friends");
                    request.setParameters(parameters);
                    request.executeAsync();

                    //post!?

                }//Aqui

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(LOG_TAG, "FB error. error= " + error.toString());
                    Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void salir(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("FMS");
        intent.putExtra("whatToDo","salir");
        startActivity(intent);
        finish();
    }

    private void syncFB(){
        Log.e(LOG_TAG, "before syncFB usuario=" + idUsuario+" fb="+fbID);
        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url = getString(R.string.mainURL)+"/Usuario/asociar_usuario_facebook";
        parametros.put("idFacebookAndroid",fbID);
        parametros.put("idUsuario",idUsuario);
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //
                String resp=new String(responseBody);
                Toast.makeText(getApplicationContext(), "¡Te has conectado correctamente!", Toast.LENGTH_LONG)
                        .show();

                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(getString(R.string.p_id_fb),fbID);
                editor.commit();

                Log.e(LOG_TAG, "syncFB= " + resp);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Error de conexión "+statusCode+".\nIntentando consultar el status actual.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
