package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.oh.outfithelp.R;

public class LogIn extends Activity {

    TextView TVRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        TVRegistrarse = findViewById(R.id.TVRegistrate);
        TVRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(LogIn.this, SignIn.class);
                startActivity(miIntent);
                LogIn.this.finish();
            }
        });
    }
}
