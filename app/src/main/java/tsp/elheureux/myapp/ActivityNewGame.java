package tsp.elheureux.myapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.SharedPreferences;
public class ActivityNewGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String joueur1login = prefs.getString("joueur1login", "Joueur 1");
        String joueur2login = prefs.getString("joueur2login", "Joueur 2");
        String joueur3login = prefs.getString("joueur3login", "Joueur 3");
        EditText editJoueur1 = (EditText) findViewById(R.id.Joueur_1);
        editJoueur1.setText(joueur1login);
        EditText editJoueur2 = (EditText) findViewById(R.id.Joueur_2);
        editJoueur2.setText(joueur2login);
        EditText editJoueur3 = (EditText) findViewById(R.id.Joueur_3);
        editJoueur3.setText(joueur3login);
    }


    public void Retour(View view) {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
    public void Valider(View view) {
        
        EditText editJoueur1 = (EditText) findViewById(R.id.Joueur_1);
        String joueur1 = editJoueur1.getText().toString();
        
        EditText editJoueur2 = (EditText) findViewById(R.id.Joueur_2);
        String joueur2 = editJoueur2.getText().toString();
        
        EditText editJoueur3 = (EditText) findViewById(R.id.Joueur_3);
        String joueur3 = editJoueur3.getText().toString();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("joueur1login", joueur1);
        editor.putString("joueur2login", joueur2);
        editor.putString("joueur3login", joueur3);
        editor.commit();



        Intent i = new Intent(this, Tour_1.class);
        i.putExtra("joueur1",joueur1);
        i.putExtra("joueur2",joueur2);
        i.putExtra("joueur3",joueur3);
        startActivity(i);
    }

}

