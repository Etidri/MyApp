package tsp.elheureux.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
public void NewGame(View v) {

    Intent i = new Intent(this, ActivityNewGame.class);
    startActivity(i);
}
}

