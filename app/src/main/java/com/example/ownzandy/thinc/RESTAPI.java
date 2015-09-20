package com.example.ownzandy.thinc;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.util.Log;
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

        @Override
        protected String doInBackground(Void... params) {
            String text = null;
            try {
                HttpEntity entity = readDocument(docKey, authKey);
                text = getASCIIContentFromEntity(entity);
                text = process64(text);
                String json = text;
                JSONObject obj = new JSONObject(json);
                HashMap<String, ArrayList<String>> infoMap = new HashMap<String, ArrayList<String>>();
                ArrayList<String> nameList = getName(obj);
                ArrayList<String> insuranceList = getInsurance(obj);
                ArrayList<String> allergiesList = getAllergies(obj);
                ArrayList<String> condList = getConditions(obj);
                ArrayList<String> medList = getMeds(obj);
                ArrayList<String> procList = getProcedure(obj);
                infoMap.put("name", nameList);
                infoMap.put("insurance", insuranceList);
                infoMap.put("allergy", allergiesList);
                infoMap.put("condition", condList);
                infoMap.put("medication", medList);
                infoMap.put("procedure", procList);
            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected ArrayList<String> getAllergies(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");
            JSONObject entryArr1 = entryArr.getJSONObject(1);
            JSONArray allergyentryObj = entryArr1.getJSONArray("entry");
            for (int i = 0; i < allergyentryObj.length(); i++) {
                JSONObject contentObject = allergyentryObj.getJSONObject(i).getJSONObject("content");
                JSONObject substanceObject = contentObject.getJSONObject("substance");
                test.add(substanceObject.getString("display"));
            }
            return test;
        }

        protected ArrayList<String> getConditions(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");

            JSONObject entryArr4 = entryArr.getJSONObject(4);
            JSONArray condobjArr = entryArr4.getJSONArray("entry");
            for (int i = 0; i < condobjArr.length(); i++) {
                JSONObject contentObject = condobjArr.getJSONObject(i).getJSONObject("content");

                test.add(contentObject.getJSONObject("code").getJSONArray("coding").getJSONObject(0).getString("display"));
                test.add(contentObject.getString("onsetDate"));
            }
            return test;
        }


        protected ArrayList<String> getProcedure(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");

            JSONObject entryArr3 = entryArr.getJSONObject(3);
            JSONArray procobjArr = entryArr3.getJSONArray("entry");
            for (int i = 0; i < procobjArr.length(); i++) {
                JSONObject contentObject = procobjArr.getJSONObject(i).getJSONObject("content");

                test.add(contentObject.getJSONObject("type").getString("text"));
                test.add(contentObject.getJSONObject("date").getString("end"));
            }
            return test;
        }


        protected ArrayList<String> getMeds(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");

            JSONObject entryArr2 = entryArr.getJSONObject(2);
            JSONArray medentryObj = entryArr2.getJSONArray("entry");
            for (int i = 0; i < medentryObj.length(); i++) {
                JSONObject contentObject = medentryObj.getJSONObject(i).getJSONObject("content");
                JSONObject medObject = contentObject.getJSONObject("medication");

                test.add(medObject.getString("display"));
            }
            return test;
        }

        protected ArrayList<String> getInsurance(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");
            JSONObject entryArr0 = entryArr.getJSONObject(0);
            JSONObject providerObj = entryArr0.getJSONArray("careProvider").getJSONObject(0);
            test.add(providerObj.getString("company"));
            return test;
        }

        protected ArrayList<String> getName(JSONObject obj) throws JSONException {
            ArrayList<String> test = new ArrayList<String>();
            JSONArray entryArr = obj.getJSONArray("entry");
            JSONObject entryArr0 = entryArr.getJSONObject(0);
            JSONArray nameArr = entryArr0.getJSONArray("name");
            JSONObject nameObj = nameArr.getJSONObject(0);
            JSONArray familyArr = nameObj.getJSONArray("family");
            JSONArray givenArr = nameObj.getJSONArray("given");

            test.add(givenArr.getString(0));
            test.add(givenArr.getString(1));
            test.add(familyArr.getString(0));

            return test;
        }

        protected int getIndex(JSONArray arr, String key) throws JSONException{
            for (int i = 0; i < arr.length(); i++) {
                if (arr.get(i) != null) {
                    if (arr.getString(i) == key) {
                        return i;
                    }
                }
            }
            return 0;
        }

        protected String getValueFromObject(JSONObject obj, String key) throws JSONException {
            String name = "";
            try {
                if (obj.getJSONArray(key).length() > 0) {
                    for (int i = 0; i < obj.getJSONArray(key).length(); i++) {
                        name += obj.getJSONArray(key).get(i) + " ";
                    }
                }
            } catch (JSONException e) {
                name = obj.getString(key);
            }
            return name.trim();
        }

        protected String readFile(String path) throws FileNotFoundException{
            String content = new Scanner("").useDelimiter("\\Z").next();
            return content;
        }

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
