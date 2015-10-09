package com.rhcloud.nodejsapp_develovers.soserverandroid;

import android.content.Intent;
import android.os.Bundle;


import android.app.Activity;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private EditText mInputMessageView;
    private Button sendButton;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private static final String TAG = MainActivity.class.getSimpleName();

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    //Text info
    private String name;

    //Socket
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_URL);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We connect the socket
        if(!mSocket.connected()) {
            mSocket.connected();
        }

        mInputMessageView = (EditText) findViewById(R.id.inputMsg);
        sendButton = (Button) findViewById(R.id.btnSend);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");

        //Button Listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mInputMessageView.getText().length()>1) {
                    sendMessageToServer(mInputMessageView.getText().toString().trim());
                    mInputMessageView.setText("");
                }
            }
        });

        listMessages = new ArrayList<Message>();
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        //Listeners for Socket
        mSocket.on(Constants.FLAG_MESSAGE, onNewMessage);

        buildGoogleApiClient();

    }


    //Send Message to the server

    private void sendMessageToServer(String message){

        if (mCurrentLocation == null) {
            Log.d(TAG, "No se ha recibido ninguna posición gps");
            Toast.makeText(this, "No se ha recibido ninguna posicíon gps! ", Toast.LENGTH_LONG).show();
            return;
        }

        try {
        JSONObject msg = new JSONObject()
                .put(Constants.JSON_WHO, name)
                .put(Constants.JSON_MESSAGE, message)
                .put(Constants.JSON_LAT, mCurrentLocation.getLatitude())
                .put(Constants.JSON_LON, mCurrentLocation.getLongitude());
            mSocket.emit(Constants.FLAG_MESSAGE, msg);

            if(mSocket.connected()) {

                Log.d(TAG, "Sent a message to Server " + msg.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // Processing new message
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject message = (JSONObject) args[0];
                    Log.d(TAG, String.format("Got string message! %s", message.toString()));
                    // add the message to view
                    parseMessage(message);
                }
            });
        }
    };

    //Parse the message from JSon and put in the Interface chat
    private void parseMessage(final JSONObject jObj) {

        try {
            // JSON node 'lat' and 'lon'
            String lat = jObj.getString(Constants.JSON_LAT);
            String lon = jObj.getString(Constants.JSON_LON);
            // JSON node 'message'
            String message = jObj.getString(Constants.JSON_MESSAGE);
            // JSON node 'who'
            String who = jObj.getString(Constants.JSON_WHO);
            boolean isSelf =false;

            // Verify if the message is from this client
            if (name.equals(who)) {
            isSelf  = true;
             }
                Message m = new Message(who, message, isSelf, lat, lon);

                // Appending the message to chat list
                appendMessage(m);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Appending message to list view
     * */
    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);

                adapter.notifyDataSetChanged();

            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }


    }
}