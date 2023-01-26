package com.example.andrei.chatapplication.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrei
 * Simple Network functions like GET and POST
 * TODO move the public static strings to the resources dir
 */
public class NetworkHelper {

    public static String HELLO_SERVICE_URL = "http://cesi.cleverapps.io/hello";
    public static String PING_SERVICE_URL = "http://cesi.cleverapps.io/ping";
    public static String SIGNUP_SERVICE_URL = "http://cesi.cleverapps.io/signup";
    public static String SIGNIN_SERVICE_URL = "http://cesi.cleverapps.io/signin";
    public static String RECEIVE_MESSAGES_URL = "http://cesi.cleverapps.io/messages";
    public static String SEND_MESSAGES_URL = "http://cesi.cleverapps.io/messages";

    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }

    public static String sendMessage(String message, String token) {
        Log.d("Andrei", "sendMessage: now sending this message " + message + " with the token " + token);
        HashMap<String, String> params = new HashMap<>();
        params.put("message", message);
        return POST(SEND_MESSAGES_URL, params, token);

    }

    public static String getMessages(String token) {

        return GET(RECEIVE_MESSAGES_URL, token);

    }

    public static String signup(String username, String pwd, String urlPhoto) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("pwd", pwd);
        params.put("urlPhoto", urlPhoto);
        /* do the post */
        return POST(SIGNUP_SERVICE_URL, params, null);
    }

    public static String signin(String username, String pwd) {
        HashMap<String, String> params = new HashMap<>();
        /* do the post */
        params.put("username", username);
        params.put("pwd", pwd);
        return POST(SIGNIN_SERVICE_URL, params, null);

    }

    public static String hello(String name) {

        return GET(HELLO_SERVICE_URL + "?name=" + name, null);

    }

    public static String ping() {

        return POST(PING_SERVICE_URL, null, null);


    }

    public static String POST(String urlString, HashMap<String, String> params, String token) {

        // get a stream to receive HTTP response
        InputStream inputStream = null;

        try {
            URL url = new URL(urlString);

            Log.d("Calling URL", url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            /* it's a post */
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            if (token != null) {
                conn.setRequestProperty("token", token);
            }
            /* get the outputstream of the connection */
            OutputStream os = conn.getOutputStream();
            /* write to it */
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            /* add all params */
            writer.write(concatParams(params));
            writer.flush();
            writer.close();
            os.close();

            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();

            Log.d("NetworkHelper", "The response code is: " + response);

            inputStream = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = NetworkHelper.readIt(inputStream);

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {
            Log.e("NetworkHelper", e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("NetworkHelper", e.getMessage());
                }
            }
        }
    }

    private static String GET(String urlString, String token) {
        // stream to receive the HTTP response
        InputStream inputStream = null;

        try {
            /* get the URL to use */
            URL url = new URL(urlString);

            Log.d("Calling URL", url.toString());
            /* create the connection */
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            /* setup the connection */
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            /* since it's a GET */
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            /* add the token if it's non null */
            if (token != null) {
                conn.setRequestProperty("token", token);
            }
            /* Starts the query */
            conn.connect();

            int response = conn.getResponseCode();

            if (response != HttpURLConnection.HTTP_OK) {

                Log.d("Andrei:NetworkHelper", "GET: connection HTTP answer failed: " + response);

            } else {
                Log.d("Andrei:NetworkHelper", "The response code is: " + response);

                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = NetworkHelper.readIt(inputStream);
                /* convert to string */
                return contentAsString;
            }
            return null;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {
            Log.e("NetworkHelper", e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("NetworkHelper", e.getMessage());
                }
            }
        }
    }

    /**
     * Concat params to be send
     *
     * @param params
     * @return
     */
    private static String concatParams(Map<String, String> params) {
        /* use a string builder because of varied size */
        StringBuilder sb = new StringBuilder();

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Andrei: concatParams", "Error adding param", e);
                }
            }
        }
        return sb.toString();
    }

    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream) throws IOException {
        int ch;
        StringBuffer sb = new StringBuffer();

        while ((ch = stream.read()) != -1) {
            sb.append((char) ch);
        }

        Reader reader = null;

        reader = new InputStreamReader(stream, "UTF-8");

        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        return sb.toString();
    }
}
