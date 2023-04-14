package com.boydgaming.paywithmybank;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.logging.Logger;

public class RedirectActivity extends AppCompatActivity {
    public static String ESTABLISH_DATA = "establishData";
    Logger logger = Logger.getLogger( "LightboxActivity");

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState);
        logger.info( "PWMB: RedirectActivity: onCreate");
        Intent intent = new Intent( this, LightboxActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity( intent);
        finish();
    }
}
