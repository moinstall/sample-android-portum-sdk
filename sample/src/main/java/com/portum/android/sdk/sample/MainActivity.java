package com.portum.android.sdk.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.portum.android.sdk.PortumFacade;
import com.portum.android.sdk.internal.model.AdFormat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText mAdUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.hello_world);
        textView.setText(String.format(Locale.getDefault(),
                "SDK Version: %s | APP version: %s.%d | Environment: %s",
                PortumFacade.version(), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE,
                BuildConfig.DEBUG ? "test" : "prod"));

        mAdUnitId = (EditText) findViewById(R.id.placement_id);
        mAdUnitId.setText("VYajJnXjNBX");

        PortumFacade.prepare(this);
        PortumFacade.setListener(new TestListener(this));
    }

    public void showAd(View view) {
        PortumFacade.showAd(mAdUnitId.getText().toString(), AdFormat.ANY);
    }
}
