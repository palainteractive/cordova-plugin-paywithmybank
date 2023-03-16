package com.boydgaming.paywithmybank;

import android.content.Intent;
import android.app.Application;
import android.content.res.Resources;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.paywithmybank.android.sdk.interfaces.PayWithMyBankCallback;
import com.paywithmybank.android.sdk.views.PayWithMyBankView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class echoes a string called from JavaScript.
 */
public class PayWithMyBank extends CordovaPlugin {
    Map<String,String> establishData = new HashMap();
    CallbackContext callInProgress;
    Logger logger = Logger.getLogger( "PayWithMyBank");

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("selectBankWidget")) {
            JSONObject jobj = args.getJSONObject(0);
            Iterator keysIter= jobj.keys();
            while( keysIter.hasNext()) {
                String key = (String)keysIter.next();
                if( null != jobj.getString( key)) {
                    this.establishData.put(key, (String)jobj.getString(key));
                } else {
                    this.establishData.put(key, "");
                }
            }

            this.selectBankWidget( callbackContext);
            return true;
        }
        return false;
    }

    private void selectBankWidget( CallbackContext callbackContext) {
        Application app=cordova.getActivity().getApplication();
        String package_name = app.getPackageName();
        Resources resources = app.getResources();

        callInProgress = callbackContext;

        Iterator dataIter = this.establishData.keySet().iterator();
        while( dataIter.hasNext()) {
            String key = (String)dataIter.next();
            logger.info( "PWMB: selectBankWidget(): "+key+" == "+(String)this.establishData.get( key));
        }

        int layout_id = resources.getIdentifier( "layout", "layout", package_name);
        logger.info( "PWMB: layout_id == "+layout_id);
        int trustly_widget_view_id = resources.getIdentifier( "trustlyWidgetView", "id", package_name);
        logger.info( "PWMB: trustly_widget_view_id == "+trustly_widget_view_id);

        this.cordova.getActivity().setContentView( layout_id);
        PayWithMyBankView trustlyWidget = this.cordova.getActivity().findViewById( trustly_widget_view_id);
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

                Intent intent = new Intent( cordova.getActivity(), LightboxActivity.class);
                intent.putExtra(LightboxActivity.ESTABLISH_DATA, (Serializable) establishData);
                ActivityResultLauncher<Intent> mStartForResult = cordova.getActivity().registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult( ActivityResult result) {
                        logger.info( "PWMB: PayWithMyBank...onActivityResult(): ");
                        callInProgress.success( new JSONObject( establishData));
                    }
                });
                mStartForResult.launch( intent);
            }
        });
    }
}
