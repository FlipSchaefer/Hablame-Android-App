package chatbot.morpheus.de.hablame_android_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FEFAActivity extends Activity implements AdapterView.OnItemClickListener {
    public static final String BUZZWORD_HEAD = "starte köpfe test";
    public static final String BUZZWORD_EYE = "starte augentest";
    public static final String EXTRAK_KEY = "modus";
    private static final String[] eigenschaftenListe = new String[]{"Freude", "Trauer", "Furcht", "Zorn", "Überraschung", "Ekel", "Neutral"};
    private static final String baseUrl = "http://194.95.221.229:8080/fefatest";
    private static final String TAG = FEFAActivity.class.getSimpleName();
    private final List<MetaData> meta = new ArrayList<>();
    private ListView eigenschaften;

    private int right = 0;
    private int wrong = 0;
    private int durchlauf = 0;
    private String modus;
    private ImageView imageView;
    private MetaData metaData;
    private MediaPlayer m;
    private ProgressBar fefaprogress;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fefatest);
        this.modus = getIntent().getExtras().getString(EXTRAK_KEY);

        this.eigenschaften = (ListView) findViewById(R.id.eigenschaften);
        this.eigenschaften.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eigenschaftenListe));
        this.eigenschaften.setOnItemClickListener(this);

        this.imageView = (ImageView) findViewById(R.id.bild);
        this.fefaprogress = (ProgressBar) findViewById(R.id.fefaprogress);
        this.fefaprogress.setProgress(0);
        this.fefaprogress.setSecondaryProgress(0);
        String username = new UsersData(this).loadFromPreferences();
        new LoadMetaData().execute((Void) null);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final String s = eigenschaften.getItemAtPosition(position).toString();
        spieleLogik(s, metaData);
        if (durchlauf <= meta.size()) {
            this.metaData = this.meta.get(ermittleZahl(meta.size()));
            ladeBilder(metaData);
        } else {
            Toast.makeText(this, "Spiel beendet", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    public int ermittleZahl(int n) {
        final Random randomZahl = new Random(System.currentTimeMillis());
        return randomZahl.nextInt(n);
    }


    //ermittelt Bild URL und übergibt den URL LoadImage()
    public void ladeBilder(MetaData meta) {
        String bildurl = null;

        if (modus.equals(BUZZWORD_HEAD)) {
            bildurl = baseUrl + "/Koepfe/" + meta.fileName;

        } else if (modus.equals(BUZZWORD_EYE)) {
            bildurl = baseUrl + "/Augen/" + meta.fileName;
        }
        new LoadImage().execute(bildurl);
    }


    //überprüft ausgewählten Wert im vgl. zum Bild auf Richtigkeit
    public void spieleLogik(String s, MetaData z) {
        durchlauf++;
        if (z.traitFirst.equals(s) || z.traitSecond.equals(s)) {
            right++;
            this.fefaprogress.setProgress(fefaprogress.getProgress() + 1);
        } else {
            soundPlayback();
            wrong++;
        }
        this.fefaprogress.setSecondaryProgress(fefaprogress.getSecondaryProgress() + 1);
    }


    //spielt den Ton ab
    public void soundPlayback() {
        m = MediaPlayer.create(this, R.raw.crow);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                releaseMediaplayer();
            }
        });
        m.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaplayer();
    }

    private void releaseMediaplayer() {
        if (m != null) {
            m.release();
            m = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FEFA Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://chatbot.morpheus.de.hablame_android_app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FEFA Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://chatbot.morpheus.de.hablame_android_app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap = null;

        protected Bitmap doInBackground(String... args) {
            try {
                final URLConnection urlConnection = new URL(args[0]).openConnection();
                bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            imageView.setImageBitmap(image);
        }
    }

    private class LoadMetaData extends AsyncTask<Void, Void, Void> {

        //läd Meta vom Server
        private String urlstring = "";
        private BufferedReader read = null;

        @Override
        protected Void doInBackground(final Void... params) {

            if (modus.equals(BUZZWORD_HEAD)) {
                urlstring = baseUrl + "/meta_koepfe.txt";
            } else if (modus.equals(BUZZWORD_EYE)) {
                urlstring = baseUrl + "/meta_augen.txt";
            }

            try {
                URLConnection urlConnection = new URL(urlstring).openConnection();
                read = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                ;
                String line = "";

                while ((line = read.readLine()) != null) {
                    meta.add(new MetaData(line));
                }

            } catch (IOException ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            } finally {
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            fefaprogress.setMax(meta.size());
            metaData = meta.get(0);
            ladeBilder(metaData);
        }
    }

    private class MetaData {
        String fileName = "";
        String traitFirst = "";
        String traitSecond = "";

        public void setMetaData(String fileName) {
            setMetaData(fileName, "", "");
        }

        public void setMetaData(String fileName, String traitFirst) {
            setMetaData(fileName, traitFirst, "");
        }

        public void setMetaData(String fileName, String traitFirst, String traitSecond) {
            this.fileName = fileName;
            this.traitFirst = traitFirst;
            this.traitSecond = traitSecond;
        }

        public MetaData(final String line) {
            final String[] split = line.split(",");
            switch (split.length) {
                case 1:
                    setMetaData(split[0]);
                    break;
                case 2:
                    setMetaData(split[0], split[1]);
                    break;
                case 3:
                    setMetaData(split[0], split[1], split[2]);
                    break;
                default:
                    break;

            }
        }
    }
}

