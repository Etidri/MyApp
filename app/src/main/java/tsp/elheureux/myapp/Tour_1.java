package tsp.elheureux.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.floor;

import java.util.regex.*;

public class Tour_1 extends AppCompatActivity {




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        //On fait deux appels succesifs à l'API de Wikipédia : le premier est une recherche d'article (on donne un mot clé dans l'URL et on accède à une liste des articles le contenant)
        //le second est le détail d'un article : on récupère le contenu d'un article dont on extrait trois mots au hasard.
        //L'idée pour obtenir un générateur de mots au hasard et d'utiliser un des mots que l'on a récupéré au hasard (dans l'appel 2) comme mot clé pour une recherche d'article (appel 1)
        //On sélectionne ensuite un article au hasard parmi ceux associés à ce mot clé, et puis on recherche trois nouveaux mots dans le contenu de ce nouvel article.
        //La boucle peut se réitérer à l'infini !

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_1);
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String URLrecherche = new String("https://fr.wikipedia.org/w/api.php?action=opensearch&format=json&formatversion=2&search="); //URL pour le premier appel
        String URLcontenu = new String("https://fr.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&formatversion=2&titles="); //URL pour le deuxième appel
        String motrecherche = prefs.getString("mot", "Bleu"); //La première recherche d'articles se fera avec le mot clé bleu (valeur par défaut)
        Intent intent = getIntent();
        String joueur1 = intent.getStringExtra("joueur1");
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Au tour de : " + String.valueOf(joueur1));
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText(String.valueOf(numGenerator()));
        URL SearchURL = null;
        try {
            SearchURL = new URL(URLrecherche + motrecherche);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AsyncWikipediaJSONDataSearch PremierAppel_recherche = new AsyncWikipediaJSONDataSearch();
        JSONArray premierresultat = new JSONArray();
        PremierAppel_recherche.execute(SearchURL);
        try {
            premierresultat = PremierAppel_recherche.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray listearticles = new JSONArray(); //On récupère la liste des articles associés au mot clé
        try {
            listearticles = premierresultat.getJSONArray(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int taille = listearticles.length();
        Random rand = new Random();
        int rang = rand.nextInt(taille);
        String nouvelarticle = null;
        try {
            nouvelarticle = listearticles.getString(rang); //On choisit un article au hasard parmi ceux proposés
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nouvelarticle = nouvelarticle.replaceAll("\\s", "_");
        Log.i("PROJET :", "nouvel article "+ nouvelarticle);
        URL ContenuURL = null;
        try {
            String URLencode = new String(URLcontenu + nouvelarticle);
            Log.i("PROJET :", "URL encode "+ URLencode);
            ContenuURL = new URL(URLencode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AsyncWikipediaJSONDataContent PremierAppel_contenu = new AsyncWikipediaJSONDataContent(); //On récupère le contenu de l'article sélectionné
        Log.i("PROJET", "URL a traiter: "+ContenuURL);
        PremierAppel_contenu.execute(ContenuURL);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 48) {
            Toast.makeText(this, "C'est parti !",
                    Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            String joueur2 = intent.getStringExtra("joueur2");
            String joueur3 = intent.getStringExtra("joueur3");
            Intent i = new Intent(this, Tour_2.class);
            i.putExtra("joueur2", joueur2);
            i.putExtra("joueur3", joueur3);
            startActivity(i);
        }
    }

    public String numGenerator() {   //Cette fonction créé un numéro de téléphone au hasard
    String num = "0";
    double rand = Math.random();
    if(rand<0.5) {
        num = num+"6";
    }
    else {
        num = num+"7";
    }
    for(int i = 0 ; i < 8 ; i++) {
        double rando = Math.random();
        double numero = floor(rando*10);
        String numero2 = (numero+"").substring(0,1);

        num = num + numero2;
    }
    return num;
    }
    public void appel(View view) {
        //Cette fonction est appelé au moment où on appuie sur le bouton vert : elle redirige vers l'activité des appels téléphoniques sur Android
        Intent intent = getIntent();
        String joueur2 = intent.getStringExtra("joueur2");
        String joueur3 = intent.getStringExtra("joueur3");
        TextView textView4 = (TextView )findViewById(R.id.textView4);
        CharSequence telnumber1 = textView4.getText();;
        String telnumber = "";
        for(int i = 0; i<10;i++) {
            telnumber += telnumber1.charAt(i);
        }
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telnumber));
        call.putExtra("joueur2",joueur2);
        call.putExtra("joueur3",joueur3);
        startActivityForResult(call,48);
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public class AsyncWikipediaJSONDataSearch extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(URL... url) {


            HttpURLConnection urlConnection = null;
            String result = null;
            try {
                urlConnection = (HttpURLConnection) url[0].openConnection(); // Open
                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream

                result = readStream(in); // Read stream
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            JSONArray json = null;
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return json; // returns the result
        }
    }
    public class AsyncWikipediaJSONDataContent extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... url) {


            HttpURLConnection urlConnection = null;
            String result = null;
            try {
                Log.i("PROJET", "Loading " + url[0]);
                urlConnection = (HttpURLConnection) url[0].openConnection(); // Open
                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream

                result = readStream(in); // Read stream
            }
            catch (MalformedURLException e) { e.printStackTrace();
                Log.i("PROJET", "Error malformed");}
            catch (IOException e) {
                Log.i("PROJET", "Error IO"
                );                e.printStackTrace();
            }
            finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            Log.i("PROJET", "Result " + result);

            JSONObject json = null;
            if (result != null) {
                try {
                    json = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return json; // returns the result
        }
        protected void onPostExecute(JSONObject json) {

            JSONObject json1 = null;
            JSONArray json2 = null;
            JSONObject json3 = null;
            JSONArray json4 = null;
            JSONObject json5 = null;

            //le format des objets JSON sur Wikipédia est un peu galère...


            try {
                json1 = json.getJSONObject("query");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                json2 = json1.getJSONArray("pages");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json3 = json2.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json4 = json3.getJSONArray("revisions");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json5 = json4.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String contenu = null;
            try {
                contenu = json5.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Pattern pattern = Pattern.compile("[a-zA-Zéèùâûî]{4,} "); //On extrait les mots d'au moins 4 caractères
            Matcher matcher = pattern.matcher(contenu);
            List<String> listemots = new ArrayList<>();
            while(matcher.find()) {
                listemots.add(matcher.group());
            }
            int taille = listemots.size();
            //Log.i("salut", Integer.toString(taille));
            Random rand = new Random();
            int rang1 = rand.nextInt(taille);
            int rang2 = rand.nextInt(taille);
            int rang3 = rand.nextInt(taille);
            String mot1 = listemots.get(rang1);
            String mot2 = listemots.get(rang2);
            String mot3 = listemots.get(rang3);
            //On prend trois mots au hasard
            TextView premiermot = findViewById(R.id.premiermot);
            premiermot.setText(mot1);
            TextView deuxiememot = findViewById(R.id.deuxiememot);
            deuxiememot.setText(mot2);
            TextView troisiememot = findViewById(R.id.troisiememot);
            troisiememot.setText(mot3);
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("mot", mot2); //Ici, on a mis à jour le mot qui nous sert à chercher les articles : on va ainsi pouvoir avoir accès à d'autres articles au hasard
            editor.commit();
        }
    }
}


