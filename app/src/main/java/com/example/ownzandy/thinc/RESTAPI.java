package com.example.ownzandy.thinc;

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

public class RESTAPI extends Activity {

    private String authKey;
    private String docKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restapi);
        authKey = getIntent().getExtras().getString("authKey");
        docKey = getIntent().getExtras().getString("docKey");
        findViewById(R.id.my_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Button b = (Button) findViewById(R.id.my_button);
                b.setClickable(false);
                new LongRunningGetIO().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restapi, menu);
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
                HttpEntity entity = readDocument(docKey, authKey);
                text = getASCIIContentFromEntity(entity);
                text = process64(text);
                 String json = text;
                  JSONObject obj = new JSONObject(json);
                  text = obj.getString("hello");
            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected String process64(String text) {
            String replace = text.replace("=", "");
            String decode = new String(Base64.decode(replace, 0));
            return decode;
        }

        protected HttpEntity listDocuments(String user) {
            HttpEntity entity = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet();
                URI uri = new URI("https://api.truevault.com/v1/vaults/c0b9d46b-6f6b-4dfd-89ab-7dcc9d88dfa1/documents");
                httpGet.setURI(uri);
                httpGet.addHeader(BasicScheme.authenticate(
                        new UsernamePasswordCredentials(user, ""),
                        HTTP.UTF_8, false));
                HttpResponse response = httpClient.execute(httpGet, localContext);
                entity = response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return entity;
        }

        protected HttpEntity addDocument(String doc, String user) throws FileNotFoundException, UnsupportedEncodingException {
            HttpEntity entity = null;
            byte[] data = doc.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost();
                URI uri = new URI("https://api.truevault.com/v1/vaults/c0b9d46b-6f6b-4dfd-89ab-7dcc9d88dfa1/documents");
                httpPost.setURI(uri);
                httpPost.addHeader(BasicScheme.authenticate(
                        new UsernamePasswordCredentials(user, ""),
                        HTTP.UTF_8, false));
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                nameValuePair.add(new BasicNameValuePair("document", base64 + "="));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                HttpResponse response = httpClient.execute(httpPost, localContext);
                entity = response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return entity;
        }

        protected HttpEntity readDocument(String id, String user) {
            HttpEntity entity = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet();
                URI uri = new URI("https://api.truevault.com/v1/vaults/c0b9d46b-6f6b-4dfd-89ab-7dcc9d88dfa1/documents/" + id);
                httpGet.setURI(uri);
                httpGet.addHeader(BasicScheme.authenticate(
                        new UsernamePasswordCredentials(user, ""),
                        HTTP.UTF_8, false));
                HttpResponse response = httpClient.execute(httpGet, localContext);
                entity = response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return entity;
        }

        protected void onPostExecute(String results) {
            if (results != null) {
                EditText et = (EditText) findViewById(R.id.my_edit);
                et.setText(results);
            }
            Button b = (Button) findViewById(R.id.my_button);
            b.setClickable(true);
        }
    }
}
