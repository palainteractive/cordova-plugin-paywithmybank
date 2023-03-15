package com.boydgaming.paywithmybank;

import android.content.Intent;

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
        callInProgress = callbackContext;

        Iterator dataIter = this.establishData.keySet().iterator();
        while( dataIter.hasNext()) {
            String key = (String)dataIter.next();
            logger.info( "PWMB: selectBankWidget(): "+key+" == "+(String)this.establishData.get( key));
        }

        this.cordova.getActivity().setContentView( R.layout.layout);
        PayWithMyBankView trustlyWidget = this.cordova.getActivity().findViewById( R.id.trustlyWidgetView);
        trustlyWidget.selectBankWidget( establishData).onBankSelected( new PayWithMyBankCallback() {
            @Override
            public void handle(Object o, Object o2) {
                logger.info( "PWMB: onBankSelected callback()");
                logger.info( "PWMB: o="+o.toString());

                if( o2 instanceof HashMap) {
                    HashMap data = (HashMap)o2;
                    String paymentProviderId = (String)data.get( "paymentProviderId");
                    logger.info( "PWMB: paymentProviderId = "+paymentProviderId);
                    establishData.put( "paymentProviderId", paymentProviderId);
                }

                Intent intent = new Intent( cordova.getActivity(), LightboxActivity.class);
                intent.putExtra(LightboxActivity.ESTABLISH_DATA, (Serializable) establishData);
                cordova.getActivity().startActivity(intent);

                callInProgress.success( new JSONObject( establishData));
            }
        });

//        if (message != null && message.length() > 0) {
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }
    }
}
