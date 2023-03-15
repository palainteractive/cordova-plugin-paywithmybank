package com.boydgaming.paywithmybank;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class PayWithMyBank extends CordovaPlugin {
    Map<String,String> establishData = new HashMap();
    CallbackContext callInProgress;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("selectBankWidget")) {
            Map<String,String> eData = args.getJSONObject(0).toMap();
            Iterator keysIter= eData.keys();
            while( keysIter.hasNext()) {
                String key = (String)keysIter.next();
                if( null != call.getString( key)) {
                    this.establishData.put(key, call.getString(key));
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
            Logger.info( "PWMB: selectBankWidget(): "+key+" == "+(String)this.establishData.get( key));
        }

        this.getActivity().setContentView( R.layout.layout);
        PayWithMyBankView trustlyWidget = bridge.getActivity().findViewById( R.id.trustlyWidgetView);
        trustlyWidget.selectBankWidget( establishData).onBankSelected( new PayWithMyBankCallback() {
            @Override
            public void handle(Object o, Object o2) {
                Logger.info( "PWMB: onBankSelected callback()");
                Logger.info( "PWMB: o="+o.toString());

                if( o2 instanceof HashMap) {
                    HashMap data = (HashMap)o2;
                    String paymentProviderId = (String)data.get( "paymentProviderId");
                    Logger.info( "PWMB: paymentProviderId = "+paymentProviderId);
                    establishData.put( "paymentProviderId", paymentProviderId);
                }

                Intent intent = new Intent( bridge.getActivity(), LightboxActivity.class);
                intent.putExtra(LightboxActivity.ESTABLISH_DATA, (Serializable) establishData);
                bridge.getActivity().startActivity(intent);

                try {
                    call.success( new JSONObject( establishData));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        if (message != null && message.length() > 0) {
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }
    }
}
