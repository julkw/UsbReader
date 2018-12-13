package com.usbreaderapp.diserver.usbreader;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ServerConnection extends AsyncTask <JSONObject,Void,String> {
    String baseUrl = "http://10.0.2.2:5000/";

    // TODO: catch no Internet and save events in file to be send later instead

    @Override
    protected String doInBackground(JSONObject... params) {
        try {
            URL url = new URL(baseUrl+"event");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            // currently sending plain text data
            String data = params[0].toString();
            bw.write(data);
            bw.flush();
            bw.close();

            InputStream ips = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));

            String line;
            String res = "";
            while ((line = br.readLine())!=null)
            {
                res += line;
            }

            br.close();
            ips.close();
            os.close();
            connection.disconnect();
            return res;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
