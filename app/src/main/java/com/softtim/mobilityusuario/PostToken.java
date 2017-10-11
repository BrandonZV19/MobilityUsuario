package com.softtim.mobilityusuario;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/**
 * Created by softtim on 9/14/16.
 */
public class PostToken {
    SharedPreferences preferences;
    AsyncHttpClient client=new AsyncHttpClient();
    RequestParams params=new RequestParams();
    String TAG="PostToken";

    public void post(String token, Context context){
        preferences=context.getApplicationContext().getSharedPreferences(context.getString(R.string.preferences), Context.MODE_PRIVATE);
        int idUser=preferences.getInt("idUsuario",0);


        String url="http://softtim.mx/mobilityV2"+"/Usuario/cambio_token";

        //Mandar encriptado
        //Validar valores nulos de los parametros a postear


        params.put("idUsuario",idUser);
        params.put("aToken",token);
        Log.e(TAG, "params:"+ params.toString());
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(TAG, "response:"+ Arrays.toString(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG,"response:"+ Arrays.toString(responseBody)+" statusCode:"+statusCode);
            }
        });
    }
}
