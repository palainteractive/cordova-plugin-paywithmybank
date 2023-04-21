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

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
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
    ActivityResultLauncher<Intent> mStartSelectBankForResult;
    protected PayWithMyBankView trustlyWidget;
    protected Resources resources;
    protected Application app;
    protected String package_name;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize( cordova, webView);
        mStartSelectBankForResult = cordova.getActivity().registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result) {
                // logger.info( "PWMB: PayWithMyBank...SelectBank...onActivityResult(): ");

                callInProgress.success( new JSONObject( establishData));
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("selectBankWidget")) {

            JSONObject establishDict = args.getJSONObject(0);
            Iterator establishIter = establishDict.keys();
            while( establishIter.hasNext()) {
                String key = (String)establishIter.next();
                if( key.equals( "customer")) {
                    JSONObject customerDict = establishDict.getJSONObject("customer");
                    Iterator customerIter = customerDict.keys();
                    while (customerIter.hasNext()) {
                        String customerKey = (String) customerIter.next();
                        if (customerKey.equals("address")) {
                            JSONObject addressDict = customerDict.getJSONObject("address");
                            Iterator addressIter = addressDict.keys();
                            while (addressIter.hasNext()) {
                                String addressKey = (String) addressIter.next();
                                this.establishData.put(key + "." + customerKey + "." + addressKey, addressDict.getString(addressKey));
                            }
                        } else {
                            this.establishData.put(key + "." + customerKey, customerDict.getString(customerKey));
                        }
                    }
                } else {
                    this.establishData.put( key, establishDict.getString( key));
                }
            }
            this.establishData.put( "metadata.urlScheme", "com.boydgaming.paywithmybank://");
            // logger.info( "PWMB: establishData: "+this.establishData.toString());

            this.selectBankWidget( callbackContext);
            return true;
        }
        return false;
    }

    private void selectBankWidget( CallbackContext callbackContext) {
        app=cordova.getActivity().getApplication();
        package_name = app.getPackageName();
        resources = app.getResources();

        callInProgress = callbackContext;

        Iterator dataIter = this.establishData.keySet().iterator();
        while( dataIter.hasNext()) {
            String key = (String)dataIter.next();
            // logger.info( "PWMB: selectBankWidget(): "+key+" == "+(String)this.establishData.get( key));
        }

        Intent intent = new Intent( cordova.getActivity(), PayWithMyBankActivity.class);
        intent.putExtra(LightboxActivity.ESTABLISH_DATA, (Serializable) establishData);
        mStartSelectBankForResult.launch( intent);

    }

    @Override
    public void onNewIntent( Intent intent) {
        if( intent == null || !intent.getAction().equals( Intent.ACTION_VIEW)) {
            return;
        }
        // logger.info( "PWMB: PayWithMyBank CordovaPlugin: onNewIntent()");
        this.webView.sendJavascript("window.PayWithMyBank.proceedToChooseAccount();");

    }
}
