package com.rhcloud.nodejsapp_develovers.soserverandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;


public class Login extends ActionBarActivity {


    Button joinBtn;
    EditText userNameEditText;


    private static final String TAG = Login.class.getSimpleName();


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_URL);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        joinBtn = (Button) findViewById(R.id.btnJoin);
        userNameEditText = (EditText)findViewById(R.id.nickname);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Login Button Clicked");
                    attemptLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void attemptLogin() throws JSONException {
        joinBtn.setEnabled(false);
        String userName = userNameEditText.getText().toString().trim();
        //Verify if the userNameEditText is empty
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "Cual es su nombre?", Toast.LENGTH_LONG).show();
            joinBtn.setEnabled(true);
            return;

        }

        // Create a JSon data to Ask with user is available
        JSONObject newUserData = new JSONObject()
                .put("userName", userName);

        mSocket.emit(Constants.FLAG_NEW_USER, newUserData);

        Log.d(TAG,"BroadCast the data : "+ newUserData.toString() );
        Intent intent = new Intent( this , MainActivity.class);
        intent.putExtra("name", userName);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mSocket.connected()) {
            mSocket.connect();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
