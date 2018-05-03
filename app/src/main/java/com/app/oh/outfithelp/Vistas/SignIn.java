package com.app.oh.outfithelp.Vistas;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.oh.outfithelp.R;

public class SignIn extends Activity {
    TextView tvIniciaSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        tvIniciaSesion = findViewById(R.id.TVIniciaSesion);
        tvIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(SignIn.this, LogIn.class);
                startActivity(miIntent);
                SignIn.this.finish();
            }
        });
    }
}
