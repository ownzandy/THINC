package com.example.ownzandy.thinc;

import android.content.Intent;

import android.util.Log;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import java.net.URI;
import android.util.Base64;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HTTP;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity {

    private Button test;

    private EditText loginText;
    private TextView authText;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       loginText = (EditText)findViewById(R.id.loginText);

        test = (Button) findViewById(R.id.testing);

        loginText = (EditText)findViewById(R.id.loginText);
        authText = (TextView)findViewById(R.id.authText);
        authText.setVisibility(View.GONE);

        onclickTest();

    }

    //DELETE AFTER TEST
    public void onclickTest(){
        test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
//DELETE DELETE DELETE DELETE DELETE DELETE

    public void login(View view) {
        pass = loginText.getText().toString();
        new LongRunningGetIO().execute();

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

    private class LongRunningGetIO extends AsyncTask <Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            String text = null;
            try {
                HttpEntity entity = checkAuthentication(pass);
                text = getASCIIContentFromEntity(entity);

            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected HttpEntity checkAuthentication(String api_key) {
            HttpEntity entity = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet();
                URI uri = new URI("https://api.truevault.com/v1/users");
                httpGet.setURI(uri);
                httpGet.addHeader(BasicScheme.authenticate(
                        new UsernamePasswordCredentials(api_key, ""),
                        HTTP.UTF_8, false));
                HttpResponse response = httpClient.execute(httpGet, localContext);
                entity = response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return entity;
        }

        protected void onPostExecute(String results) {
            if (results.contains("success")) {
                Intent QRIntent = new Intent(LoginActivity.this, QRActivity.class);
                QRIntent.putExtra("authKey", pass);
                startActivity(QRIntent);
            }
            else {
                authText.setVisibility(View.VISIBLE);
            }
        }
    }
}
