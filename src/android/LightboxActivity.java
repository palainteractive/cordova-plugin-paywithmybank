package com.boydgaming.paywithmybank;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.paywithmybank.android.sdk.interfaces.PayWithMyBankCallback;
import com.paywithmybank.android.sdk.views.PayWithMyBankView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LightboxActivity extends AppCompatActivity {
    public static String ESTABLISH_DATA = "establishData";
    private PayWithMyBankView lightboxView;
    Logger logger = Logger.getLogger( "LightboxActivity");

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState);
        setContentView( R.layout.activity_light_box);

        Intent intent = getIntent();
        Map<String,String> establishData = (Map<String,String>)intent.getSerializableExtra( ESTABLISH_DATA);

        lightboxView = findViewById( R.id.lightBoxWidget);
        lightboxView.establish( establishData)
                .onReturn(
                        new PayWithMyBankCallback() {
                            @Override
                            public void handle(Object o, Object o2) {
                                logger.info( "PWMB: LightboxView extablish onRetrun callback()");
                                logger.info( "PWMB: o="+o.toString());

                                if( o2 instanceof HashMap) {
                                    HashMap data = (HashMap)o2;
                                    logger.info( "PWMB: data = "+data.toString());
                                }
                            }
                        }).onCancel(
                        new PayWithMyBankCallback() {
                            @Override
                            public void handle(Object o, Object o2) {
                                logger.info( "PWMB: LightboxView establish onCancel callback()");
                                logger.info( "PWMB: o="+o.toString());

                                if( o2 instanceof HashMap) {
                                    HashMap data = (HashMap)o2;
                                    logger.info( "PWMB: data = "+data.toString());
                                }
                            }
                        });
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        lightboxView.proceedToChooseAccount();
    }
}
