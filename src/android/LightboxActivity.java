package com.boydgaming.paywithmybank;

import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
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
        AppCompatActivity myInstance = this;
        super.onCreate( savedInstanceState);

        Application app=this.getApplication();
        String package_name = app.getPackageName();
        Resources resources = app.getResources();
        int activity_light_box_id = resources.getIdentifier( "activity_light_box", "layout", package_name);
        logger.info( "PWMB: activity_light_box_id == "+activity_light_box_id);
        int light_box_widget_id = resources.getIdentifier( "lightBoxWidget", "id", package_name);
        logger.info( "PWMB: light_box_widget_id == "+light_box_widget_id);
        setContentView( activity_light_box_id);

        Intent intent = getIntent();
        Map<String,String> establishData = (Map<String,String>)intent.getSerializableExtra( ESTABLISH_DATA);

        lightboxView = findViewById( light_box_widget_id);
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
                                lightboxView.destroy();
                                myInstance.finish();
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
                                lightboxView.destroy();
                                myInstance.finish();
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
