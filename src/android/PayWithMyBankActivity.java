package com.boydgaming.paywithmybank;

import android.content.Intent;
import android.app.Application;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.paywithmybank.android.sdk.interfaces.PayWithMyBankCallback;
import com.paywithmybank.android.sdk.views.PayWithMyBankView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class echoes a string called from JavaScript.
 */
public class PayWithMyBankActivity extends AppCompatActivity {
    public static String ESTABLISH_DATA = "establishData";
    Map<String,String> establishData = new HashMap();
    Logger logger = Logger.getLogger( "PayWithMyBankActivity");
    ActivityResultLauncher<Intent> mStartLightboxForResult;
    protected PayWithMyBankView trustlyWidget;
    protected Resources resources;
    protected Application app;
    protected String package_name;

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        AppCompatActivity myInstance = this;
        super.onCreate( savedInstanceState);

        mStartLightboxForResult = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result) {
                logger.info( "PWMB: PayWithMyBank...onActivityResult(): ");
                trustlyWidget.removeAllViewsInLayout();
                //trustlyWidget.destroy();
                finish();
            }
        });

        Intent intent = getIntent();
        this.establishData = (Map<String,String>)intent.getSerializableExtra( ESTABLISH_DATA);

        this.selectBankWidget();
    }

    private void selectBankWidget() {
        app=this.getApplication();
        package_name = app.getPackageName();
        resources = app.getResources();

        Iterator dataIter = this.establishData.keySet().iterator();
        while( dataIter.hasNext()) {
            String key = (String)dataIter.next();
            logger.info( "PWMB: selectBankWidget(): "+key+" == "+(String)this.establishData.get( key));
        }

        int layout_id = resources.getIdentifier( "layout", "layout", package_name);
        logger.info( "PWMB: layout_id == "+layout_id);
        int trustly_widget_view_id = resources.getIdentifier( "trustlyWidgetView", "id", package_name);
        logger.info( "PWMB: trustly_widget_view_id == "+trustly_widget_view_id);

        this.setContentView( layout_id);
        trustlyWidget = this.findViewById( trustly_widget_view_id);
        trustlyWidget.selectBankWidget( establishData).onBankSelected( new PayWithMyBankCallback<com.paywithmybank.android.sdk.interfaces.PayWithMyBank, Map<String, String>>() {
            @Override
            public void handle(com.paywithmybank.android.sdk.interfaces.PayWithMyBank o, Map<String, String> o2) {
                logger.info( "PWMB: onBankSelected callback()");

                if( o2 instanceof HashMap) {
                    HashMap data = (HashMap)o2;
                    String paymentProviderId = (String)data.get( "paymentProviderId");
                    logger.info( "PWMB: paymentProviderId = "+paymentProviderId);
                    establishData.put( "paymentProviderId", paymentProviderId);
                }

                Intent intent = new Intent( getApplicationContext(), LightboxActivity.class);
                intent.putExtra(LightboxActivity.ESTABLISH_DATA, (Serializable) establishData);
                mStartLightboxForResult.launch( intent);
            }
        });
    }
}
