package chatbot.morpheus.de.hablame_android_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/* Created by Philipp Schäfer on 25.03.2016. */
public class GameActivity extends Activity implements Texty.TextyCallback
{

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String BUZZWORD_RPS = "schere stein papier";
    private static final String baseUrl = "http://berrypi.no-ip.org/";

    private int punkte;
    private boolean doneTTS = true;

    //Layout Sachen
    protected ImageButton schere_Button;
    protected ImageButton stein_Button;
    protected ImageButton papier_Button;

    protected ImageView computer_pic;

    protected TextView points;
    protected TextView computer_choice;
    protected TextView user_choice;

    //WebServerManager
    private WebServerManager webServer;

    //Speak things
    private Texty texty;
    private HablameAudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        points           = (TextView) findViewById(R.id.punkte);
        computer_choice  = (TextView) findViewById(R.id.computer_choice);
        computer_pic     = (ImageView) findViewById(R.id.computer_pic);
        user_choice      = (TextView) findViewById(R.id.user_choice);
        webServer        = new WebServerManager();

        Handler handler   = new Handler();
        this.audioManager = new HablameAudioManager(getBaseContext());
        this.texty        = new Texty(getApplicationContext(), this, this.audioManager, handler);

        texty.speakWhenReady("Ok, lass uns Schere Stein Papier spielen");
        addListenerOnButton();

    }

    public String translate(String word)
    {
        String convert = "";

        if(word.equals("Paper"))
        {
            convert = "Papier";
        }
        if(word.equals("Rock"))
        {
            convert = "Stein";
        }
        if(word.equals("Scissors"))
        {
            convert = "Schere";
        }
        return convert;
    }


    public void getResult(BufferedReader input)
    {
        String line;

        try
        {
            while ((line = input.readLine()) != null)
            {
                if(line.contains("Sie nahmen"))
                {
                    user_choice.setText(translate(line.substring(12)));
                }
                if(line.contains("Computer nahm"))
                {
                    computer_choice.setText(translate(line.substring(15)));
                    if(line.substring(15).equals("Rock"))
                        computer_pic.setImageResource(R.drawable.stein);
                    if(line.substring(15).equals("Scissors"))
                        computer_pic.setImageResource(R.drawable.schere);
                    if(line.substring(15).equals("Paper"))
                        computer_pic.setImageResource(R.drawable.papier);
                }

                if(line.contains("+1"))                   //Falls gewonnen steht im echo String "+1"
                {
                    punkte++;
                    texty.speakWhenReady("Du hast gewonnen");
                    points.setText(String.valueOf(punkte));
                }
                if(line.contains("-1"))                   //Falls verloren steht im echo String "-1"
                {
                    punkte--;
                    texty.speakWhenReady("Du hast verloren");
                    points.setText(String.valueOf(punkte));
                }
                if(line.contains("0"))
                {
                    texty.speakWhenReady("Unentschieden!");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addListenerOnButton()
    {

        schere_Button   = (ImageButton) findViewById(R.id.schere_id);
        stein_Button    = (ImageButton) findViewById(R.id.stein_id);
        papier_Button   = (ImageButton) findViewById(R.id.papier_id);

        schere_Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (doneTTS) {
                    doneTTS = false;

                    try {
                        getResult(new loadWebServer().execute("Scissors").get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stein_Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {

                if(doneTTS)
                {
                    doneTTS = false;

                    try
                    {
                        getResult(new loadWebServer().execute("Rock").get());
                    }
                    catch (InterruptedException | ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        papier_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (doneTTS) {
                    doneTTS = false;

                    try {
                        getResult(new loadWebServer().execute("Paper").get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //Speech and Text things
    @Override
    public void doneWithTts()
    {
        Log.d(TAG, "Done with Speaking");
        doneTTS = true;

        //Um nach fertig sprechen die Auswahl des Computerbilds zu löschen
        //muss im Orginal Thread der das Bild erzeugt hat, das Bild auf null setzen
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                computer_pic.setImageDrawable(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        webServer.EndConnection();
        this.audioManager.abandonAudioFocus();
        this.texty.destroy();
        super.onDestroy();
    }

    public class loadWebServer extends AsyncTask<String, Void, BufferedReader>
    {
        @Override
        protected BufferedReader doInBackground(String... choice)
        {
            webServer.ConnectWebServer(baseUrl);
            webServer.SendPOST(choice[0]);
            return webServer.ReceivePost();
        }
    }
}