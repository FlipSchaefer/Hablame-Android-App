package chatbot.morpheus.de.hablame_android_app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/* Created by Philipp Schäfer on 25.03.2016. */
public class WebServerManager
{

    private HttpURLConnection connection;
    private BufferedReader input;

    public void ConnectWebServer(String baseUrl)
    {
        try
        {
            //Verbindung aufbauen zum Webserver
            System.out.println("Connecting...");
            URL url    = new URL(baseUrl);
            connection = (HttpURLConnection) url.openConnection();

            try
            {
                //Verbindung sagen was Sache ist
                connection.setDoInput(true);                    //Input wird erwartet
                connection.setDoOutput(true);                   //Output kann gesendet werden
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");            //POST da GET in die URL schreibt-->Sichtbar für alle
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

            }
            catch (ProtocolException e)
            {
                e.printStackTrace();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void SendPOST(String choice)
    {

        try
        {
            String data = "user_choice=" + URLEncoder.encode(choice, "UTF-8");

            try
            {
                //Senden der POST-Daten
                System.out.println("Data: " + data);
                System.out.println("Sending Data...");
                DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                dataOut.writeBytes(data);
                dataOut.flush();
                dataOut.close();
                System.out.println("Sending Data done");


            }

            catch (IOException e)
            {
                System.out.println("Irgendwas ist kaputt :O");
                connection.disconnect();
                e.printStackTrace();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }


    public BufferedReader ReceivePost()
    {
        System.out.println("Set Reader...");

        input = null;
        try
        {
            System.out.println("Read Input...");
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return input;

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void EndConnection()
    {
        try
        {
            input.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        connection.disconnect();
    }
}