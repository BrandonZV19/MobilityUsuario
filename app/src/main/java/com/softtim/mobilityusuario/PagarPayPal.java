package com.softtim.mobilityusuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PagarPayPal extends AppCompatActivity {
    Button hechopp;
    WebView webPP;
    SharedPreferences preferences;
    String idServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar_pay_pal);

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        webPP=(WebView)findViewById(R.id.webPP);
        hechopp=(Button)findViewById(R.id.hechoPP);
        hechopp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentm = new Intent(PagarPayPal.this, MainActivity.class);
                startActivity(intentm);
                finish();
            }
        });

        if (preferences.contains(getString(R.string.current_service))) {
            idServicio = String.valueOf(preferences.getInt((getString(R.string.current_service)),0));
            WebSettings webSettings = webPP.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webPP.setWebViewClient(new WebViewClient());

            String url = getString(R.string.mainURL)+"/Paypal/enviar_pago";
            String postData="";
            try {
                postData = "idServicio=" + URLEncoder.encode(idServicio,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            webPP.postUrl(url,postData.getBytes());

            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt(getString(R.string.current_acreditado_pp),1);
            editor.commit();

        }else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Error 404", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Intent intentm = new Intent(PagarPayPal.this, MainActivity.class);
            startActivity(intentm);
            finish();
        }
    }
}
