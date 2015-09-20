package com.example.ownzandy.thinc;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Intent;
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

    private Intent mainIntent;
    private String authKey;
    private String docKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restapi);
        authKey = getIntent().getExtras().getString("authKey");
        docKey = getIntent().getExtras().getString("docKey");
        mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("authKey", authKey);
        new LongRunningGetIO().execute();
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
                Log.v("response", text);
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
                mainIntent.putExtra("myData", infoMap);
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
            for (int i = 0;i < givenArr.length(); i++) {
                test.add(givenArr.getString(i));
            }
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

        protected HttpEntity addDocument(String doc, String user) throws FileNotFoundException, UnsupportedEncodingException {
                        HttpEntity entity = null;
                        byte[] data = doc.getBytes("UTF-8");
                        String base64 = "ew0KICAicmVzb3VyY2VUeXBlIjogIlBhdGllbnQiLA0KICAiZW50cnkiOiBbDQogICAgew0KICAgICAgInVwZGF0ZWQiOiAiMjAxNS0wOS0yMFQwNDowOTo1Mi44MTYtMDA6MDAiLA0KICAgICAgImlkZW50aWZpZXIiOiBbDQogICAgICAgIHsNCiAgICAgICAgICAidXNlIjogInVzdWFsIiwNCiAgICAgICAgICAibGFiZWwiOiAiU01BUlQgSG9zcGl0YWwgTVJOIiwNCiAgICAgICAgICAic3lzdGVtIjogInVybjpvaWQ6MC4xLjIuMy40LjUuNi43IiwNCiAgICAgICAgICAidmFsdWUiOiAiODg4ODgwMiINCiAgICAgICAgfQ0KICAgICAgXSwNCiAgICAgICJuYW1lIjogWw0KICAgICAgICB7DQogICAgICAgICAgInVzZSI6ICJvZmZpY2lhbCIsDQogICAgICAgICAgImZhbWlseSI6IFsNCiAgICAgICAgICAgICJTbWl0aCINCiAgICAgICAgICBdLA0KICAgICAgICAgICJnaXZlbiI6IFsNCiAgICAgICAgICAgICJFbWlseSINCiAgICAgICAgICBdDQogICAgICAgIH0NCiAgICAgIF0sDQogICAgICAiZ2VuZGVyIjogew0KICAgICAgICAiY29kaW5nIjogWw0KICAgICAgICAgIHsNCiAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL2hsNy5vcmcvZmhpci92My9BZG1pbmlzdHJhdGl2ZUdlbmRlciIsDQogICAgICAgICAgICAiY29kZSI6ICJGIiwNCiAgICAgICAgICAgICJkaXNwbGF5IjogIkZlbWFsZSINCiAgICAgICAgICB9DQogICAgICAgIF0NCiAgICAgIH0sDQogICAgICAiYmlydGhEYXRlIjogIjE5NzUtMDUtMjAiLA0KICAgICAgImNhcmVQcm92aWRlciI6IFsNCiAgICAgICAgew0KICAgICAgICAgICJjb21wYW55IjogIlVuaXRlZEhlYWx0aGNhcmUiLA0KICAgICAgICAgICJwb2xpY3lOdW1iZXIiOiAiZWR3YXJkaXNnYXkiLA0KICAgICAgICAgICJtZW1iZXJJRCI6ICI2NjY2NjYiLA0KICAgICAgICAgICJncm91cE51bWJlciI6ICIxMjMxMjMxMjMiDQogICAgICAgIH0NCiAgICAgIF0sDQogICAgICAiYWN0aXZlIjogdHJ1ZQ0KICAgIH0sDQogICAgew0KICAgICAgInJlc291cmNlVHlwZSI6ICJBbGxlcmdpZXMiLA0KICAgICAgInRvdGFsUmVzdWx0cyI6IDQsDQogICAgICAiZW50cnkiOiBbDQogICAgICAgIHsNCiAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIkFsbGVyZ3lJbnRvbGVyYW5jZS9MaXNpbm9wcmlsIiwNCiAgICAgICAgICAiY29udGVudCI6IHsNCiAgICAgICAgICAgICJyZXNvdXJjZVR5cGUiOiAiQWxsZXJneUludG9sZXJhbmNlIiwNCiAgICAgICAgICAgICJjcml0aWNhbGl0eSI6ICJoaWdoIiwNCiAgICAgICAgICAgICJzZW5zaXRpdml0eVR5cGUiOiAiYWxsZXJneSIsDQogICAgICAgICAgICAicmVjb3JkZWREYXRlIjogIjIwMTMiLA0KICAgICAgICAgICAgInN0YXR1cyI6ICJjb25maXJtZWQiLA0KICAgICAgICAgICAgInN1YmplY3QiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC9FbWlseSBTbWl0aCINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAic3Vic3RhbmNlIjogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIiNzdWJzdGFuY2UiLA0KICAgICAgICAgICAgICAiZGlzcGxheSI6ICJMaXNpbm9wcmlsIg0KICAgICAgICAgICAgfQ0KICAgICAgICAgIH0NCiAgICAgICAgfSwNCiAgICAgICAgew0KICAgICAgICAgICJ0aXRsZSI6ICJBbGxlcmd5SW50b2xlcmFuY2UvRHVzdCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIkFsbGVyZ3lJbnRvbGVyYW5jZSIsDQogICAgICAgICAgICAiY3JpdGljYWxpdHkiOiAiaGlnaCIsDQogICAgICAgICAgICAic2Vuc2l0aXZpdHlUeXBlIjogImFsbGVyZ3kiLA0KICAgICAgICAgICAgInJlY29yZGVkRGF0ZSI6ICIyMDEzIiwNCiAgICAgICAgICAgICJzdGF0dXMiOiAiY29uZmlybWVkIiwNCiAgICAgICAgICAgICJzdWJqZWN0Ijogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIlBhdGllbnQvRW1pbHkgU21pdGgiDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN1YnN0YW5jZSI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICIjc3Vic3RhbmNlIiwNCiAgICAgICAgICAgICAgImRpc3BsYXkiOiAiRHVzdCINCiAgICAgICAgICAgIH0NCiAgICAgICAgICB9DQogICAgICAgIH0sDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiQWxsZXJneUludG9sZXJhbmNlL1RyZWVQb2xsZW4iLA0KICAgICAgICAgICJ1cGRhdGVkIjogIjIwMTUtMDktMjBUMDQ6MzY6MjkuMzI5LTAwOjAwIiwNCiAgICAgICAgICAiY29udGVudCI6IHsNCiAgICAgICAgICAgICJyZXNvdXJjZVR5cGUiOiAiQWxsZXJneUludG9sZXJhbmNlIiwNCiAgICAgICAgICAgICJjcml0aWNhbGl0eSI6ICJoaWdoIiwNCiAgICAgICAgICAgICJzZW5zaXRpdml0eVR5cGUiOiAiYWxsZXJneSIsDQogICAgICAgICAgICAicmVjb3JkZWREYXRlIjogIjIwMTMiLA0KICAgICAgICAgICAgInN0YXR1cyI6ICJjb25maXJtZWQiLA0KICAgICAgICAgICAgInN1YmplY3QiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC9FbWlseSBTbWl0aCINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAic3Vic3RhbmNlIjogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIiNzdWJzdGFuY2UiLA0KICAgICAgICAgICAgICAiZGlzcGxheSI6ICJUcmVlIFBvbGxlbiINCiAgICAgICAgICAgIH0NCiAgICAgICAgICB9DQogICAgICAgIH0sDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiQWxsZXJneUludG9sZXJhbmNlL1NocmltcCIsDQogICAgICAgICAgInVwZGF0ZWQiOiAiMjAxNS0wOS0yMFQwNDozNjoyOS4zMjktMDA6MDAiLA0KICAgICAgICAgICJjb250ZW50Ijogew0KICAgICAgICAgICAgInJlc291cmNlVHlwZSI6ICJBbGxlcmd5SW50b2xlcmFuY2UiLA0KICAgICAgICAgICAgImNyaXRpY2FsaXR5IjogImhpZ2giLA0KICAgICAgICAgICAgInNlbnNpdGl2aXR5VHlwZSI6ICJhbGxlcmd5IiwNCiAgICAgICAgICAgICJyZWNvcmRlZERhdGUiOiAiMjAxMyIsDQogICAgICAgICAgICAic3RhdHVzIjogImNvbmZpcm1lZCIsDQogICAgICAgICAgICAic3ViamVjdCI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICJQYXRpZW50L0VtaWx5IFNtaXRoIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJzdWJzdGFuY2UiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiI3N1YnN0YW5jZSIsDQogICAgICAgICAgICAgICJkaXNwbGF5IjogIlNocmltcCINCiAgICAgICAgICAgIH0NCiAgICAgICAgICB9DQogICAgICAgIH0NCiAgICAgIF0NCiAgICB9LA0KICAgIHsNCiAgICAgICJyZXNvdXJjZVR5cGUiOiAiTWVkaWNhdGlvbiIsDQogICAgICAidG90YWxSZXN1bHRzIjogNCwNCiAgICAgICJlbnRyeSI6IFsNCiAgICAgICAgew0KICAgICAgICAgICJ0aXRsZSI6ICJNZWRpY2F0aW9uUHJlc2NyaXB0aW9uL0FsbGVncmEiLA0KICAgICAgICAgICJ1cGRhdGVkIjogIjIwMTUtMDktMjBUMDU6MjY6MTkuNDg2LTAwOjAwIiwNCiAgICAgICAgICAiY29udGVudCI6IHsNCiAgICAgICAgICAgICJyZXNvdXJjZVR5cGUiOiAiTWVkaWNhdGlvblByZXNjcmlwdGlvbiIsDQogICAgICAgICAgICAic3RhdHVzIjogImFjdGl2ZSIsDQogICAgICAgICAgICAicGF0aWVudCI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICJQYXRpZW50L0VtaWx5IFNtaXRoIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJtZWRpY2F0aW9uIjogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIiNtZWQiLA0KICAgICAgICAgICAgICAiZGlzcGxheSI6ICJhbGxlZ3JhIDYwIG1nIG9yYWwgdGFibGV0Ig0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJkb3NhZ2VJbnN0cnVjdGlvbiI6IFsNCiAgICAgICAgICAgICAgew0KICAgICAgICAgICAgICAgICJ0ZXh0IjogIjEgdGFiIGRhaWx5ICAiLA0KICAgICAgICAgICAgICAgICJ0aW1pbmdTY2hlZHVsZSI6IHsNCiAgICAgICAgICAgICAgICAgICJldmVudCI6IFsNCiAgICAgICAgICAgICAgICAgICAgew0KICAgICAgICAgICAgICAgICAgICAgICJzdGFydCI6ICIyMDA3Ig0KICAgICAgICAgICAgICAgICAgICB9DQogICAgICAgICAgICAgICAgICBdDQogICAgICAgICAgICAgICAgfQ0KICAgICAgICAgICAgICB9DQogICAgICAgICAgICBdDQogICAgICAgICAgfQ0KICAgICAgICB9LA0KICAgICAgICB7DQogICAgICAgICAgInRpdGxlIjogIk1lZGljYXRpb25QcmVzY3JpcHRpb24vU2luZ3VsYWlyIiwNCiAgICAgICAgICAidXBkYXRlZCI6ICIyMDE1LTA5LTIwVDA1OjI2OjE5LjQ4Ni0wMDowMCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIk1lZGljYXRpb25QcmVzY3JpcHRpb24iLA0KICAgICAgICAgICAgInN0YXR1cyI6ICJhY3RpdmUiLA0KICAgICAgICAgICAgInBhdGllbnQiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC9FbWlseSBTbWl0aCINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAibWVkaWNhdGlvbiI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICIjbWVkIiwNCiAgICAgICAgICAgICAgImRpc3BsYXkiOiAiU2luZ3VsYWlyIDEwbWciDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgImRvc2FnZUluc3RydWN0aW9uIjogWw0KICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgInRleHQiOiAiMSB0YWIgZGFpbHkgYXQgYmVkdGltZSIsDQogICAgICAgICAgICAgICAgInRpbWluZ1NjaGVkdWxlIjogew0KICAgICAgICAgICAgICAgICAgImV2ZW50IjogWw0KICAgICAgICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgICAgICAgInN0YXJ0IjogIjIwMTAtMDQtMTIiDQogICAgICAgICAgICAgICAgICAgIH0NCiAgICAgICAgICAgICAgICAgIF0NCiAgICAgICAgICAgICAgICB9DQogICAgICAgICAgICAgIH0NCiAgICAgICAgICAgIF0NCiAgICAgICAgICB9DQogICAgICAgIH0sDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiTWVkaWNhdGlvblByZXNjcmlwdGlvbi9DaGFudGl4IiwNCiAgICAgICAgICAidXBkYXRlZCI6ICIyMDE1LTA5LTIwVDA1OjI2OjE5LjQ4Ni0wMDowMCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIk1lZGljYXRpb25QcmVzY3JpcHRpb24iLA0KICAgICAgICAgICAgInN0YXR1cyI6ICJjb21wbGV0ZWQiLA0KICAgICAgICAgICAgInBhdGllbnQiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC9FbWlseSBTbWl0aCINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAibWVkaWNhdGlvbiI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICIjbWVkIiwNCiAgICAgICAgICAgICAgImRpc3BsYXkiOiAiQ2hhbnRpeCAxbWciDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgImRvc2FnZUluc3RydWN0aW9uIjogWw0KICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgInRleHQiOiAiMSB0YWIgdHdpY2UgZGFpbHkiLA0KICAgICAgICAgICAgICAgICJ0aW1pbmdTY2hlZHVsZSI6IHsNCiAgICAgICAgICAgICAgICAgICJldmVudCI6IFsNCiAgICAgICAgICAgICAgICAgICAgew0KICAgICAgICAgICAgICAgICAgICAgICJzdGFydCI6ICIyMDEyLTExLTA2IiwNCiAgICAgICAgICAgICAgICAgICAgICAiZW5kIjogIjIwMTMtMDItMDQiDQogICAgICAgICAgICAgICAgICAgIH0NCiAgICAgICAgICAgICAgICAgIF0NCiAgICAgICAgICAgICAgICB9DQogICAgICAgICAgICAgIH0NCiAgICAgICAgICAgIF0NCiAgICAgICAgICB9DQogICAgICAgIH0sDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiTWVkaWNhdGlvblByZXNjcmlwdGlvbi9MZXhhcHJvIiwNCiAgICAgICAgICAidXBkYXRlZCI6ICIyMDE1LTA5LTIwVDA1OjI2OjE5LjQ4Ni0wMDowMCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIk1lZGljYXRpb25QcmVzY3JpcHRpb24iLA0KICAgICAgICAgICAgInN0YXR1cyI6ICJhY3RpdmUiLA0KICAgICAgICAgICAgInBhdGllbnQiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC84ODg4ODAyIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJtZWRpY2F0aW9uIjogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIiNtZWQiLA0KICAgICAgICAgICAgICAiZGlzcGxheSI6ICJMZXhhcHJvIDEwbWciDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgImRvc2FnZUluc3RydWN0aW9uIjogWw0KICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgInRleHQiOiAiMSB0YWIgZGFpbHkiLA0KICAgICAgICAgICAgICAgICJ0aW1pbmdTY2hlZHVsZSI6IHsNCiAgICAgICAgICAgICAgICAgICJldmVudCI6IFsNCiAgICAgICAgICAgICAgICAgICAgew0KICAgICAgICAgICAgICAgICAgICAgICJzdGFydCI6ICIyMDExLTA0LTE3Ig0KICAgICAgICAgICAgICAgICAgICB9DQogICAgICAgICAgICAgICAgICBdDQogICAgICAgICAgICAgICAgfQ0KICAgICAgICAgICAgICB9DQogICAgICAgICAgICBdDQogICAgICAgICAgfQ0KICAgICAgICB9DQogICAgICBdDQogICAgfSwNCiAgICB7DQogICAgICAicmVzb3VyY2VUeXBlIjogIlByb2NlZHVyZXMiLA0KICAgICAgInRvdGFsUmVzdWx0cyI6IDEsDQogICAgICAiZW50cnkiOiBbDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiUHJvY2VkdXJlL09waHRoYWxtaWMiLA0KICAgICAgICAgICJ1cGRhdGVkIjogIjIwMTUtMDktMjBUMDU6NTQ6NDcuNzc2LTAwOjAwIiwNCiAgICAgICAgICAiY29udGVudCI6IHsNCiAgICAgICAgICAgICJyZXNvdXJjZVR5cGUiOiAiUHJvY2VkdXJlIiwNCiAgICAgICAgICAgICJzdWJqZWN0Ijogew0KICAgICAgICAgICAgICAicmVmZXJlbmNlIjogIlBhdGllbnQvRW1pbHkgU21pdGgiDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInR5cGUiOiB7DQogICAgICAgICAgICAgICJ0ZXh0IjogIk9waHRoYWxtaWMgZXhhbWluYXRpb24gYW5kIGV2YWx1YXRpb24iDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgImRhdGUiOiB7DQogICAgICAgICAgICAgICJzdGFydCI6ICIyMDExLTEwLTI2IiwNCiAgICAgICAgICAgICAgImVuZCI6ICIyMDExLTEwLTI2Ig0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJub3RlcyI6ICJub3JtYWwiDQogICAgICAgICAgfQ0KICAgICAgICB9DQogICAgICBdDQogICAgfSwNCiAgICB7DQogICAgICAicmVzb3VyY2VUeXBlIjogIkNvbmRpdGlvbnMiLA0KICAgICAgInRvdGFsUmVzdWx0cyI6IDQsDQogICAgICAiZW50cnkiOiBbDQogICAgICAgIHsNCiAgICAgICAgICAidGl0bGUiOiAiQ29uZGl0aW9uL0FzdGhtYSIsDQogICAgICAgICAgInVwZGF0ZWQiOiAiMjAxNS0wOS0yMFQwNTo1ODoxMC40MTgtMDA6MDAiLA0KICAgICAgICAgICJjb250ZW50Ijogew0KICAgICAgICAgICAgInJlc291cmNlVHlwZSI6ICJDb25kaXRpb24iLA0KICAgICAgICAgICAgInRleHQiOiB7DQogICAgICAgICAgICAgICJzdGF0dXMiOiAiZ2VuZXJhdGVkIiwNCiAgICAgICAgICAgICAgImRpdiI6ICI8ZGl2PkFzdGhtYTwvZGl2PiINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAic3ViamVjdCI6IHsNCiAgICAgICAgICAgICAgInJlZmVyZW5jZSI6ICJQYXRpZW50Lzg4ODg4MDIiDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgImNvZGUiOiB7DQogICAgICAgICAgICAgICJjb2RpbmciOiBbDQogICAgICAgICAgICAgICAgew0KICAgICAgICAgICAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vc25vbWVkLmluZm8vc2N0IiwNCiAgICAgICAgICAgICAgICAgICJjb2RlIjogIjE5NTk2NzAwMSIsDQogICAgICAgICAgICAgICAgICAiZGlzcGxheSI6ICJBc3RobWEiDQogICAgICAgICAgICAgICAgfQ0KICAgICAgICAgICAgICBdLA0KICAgICAgICAgICAgICAidGV4dCI6ICJBc3RobWEiDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN0YXR1cyI6ICJjb25maXJtZWQiLA0KICAgICAgICAgICAgIm9uc2V0RGF0ZSI6ICIxOTg1Ig0KICAgICAgICAgIH0NCiAgICAgICAgfSwNCiAgICAgICAgew0KICAgICAgICAgICJ0aXRsZSI6ICJDb25kaXRpb24vQXN0aG1hIiwNCiAgICAgICAgICAidXBkYXRlZCI6ICIyMDE1LTA5LTIwVDA1OjU4OjEwLjQxOC0wMDowMCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIkNvbmRpdGlvbiIsDQogICAgICAgICAgICAidGV4dCI6IHsNCiAgICAgICAgICAgICAgInN0YXR1cyI6ICJnZW5lcmF0ZWQiLA0KICAgICAgICAgICAgICAiZGl2IjogIjxkaXY+VG9iYWNjbyB1c2UgZGlzb3JkZXI8L2Rpdj4iDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN1YmplY3QiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC84ODg4ODAyIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJjb2RlIjogew0KICAgICAgICAgICAgICAiY29kaW5nIjogWw0KICAgICAgICAgICAgICAgIHsNCiAgICAgICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL3Nub21lZC5pbmZvL3NjdCIsDQogICAgICAgICAgICAgICAgICAiY29kZSI6ICI4OTc2NTAwNSIsDQogICAgICAgICAgICAgICAgICAiZGlzcGxheSI6ICJUb2JhY2NvIHVzZSBkaXNvcmRlciINCiAgICAgICAgICAgICAgICB9DQogICAgICAgICAgICAgIF0sDQogICAgICAgICAgICAgICJ0ZXh0IjogIlRvYmFjY28gdXNlIGRpc29yZGVyIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJzdGF0dXMiOiAiY29uZmlybWVkIiwNCiAgICAgICAgICAgICJvbnNldERhdGUiOiAiMTk5NSIsDQogICAgICAgICAgICAiYWJhdGVtZW50RGF0ZSI6ICIyMDEyLTEwLTE1Ig0KICAgICAgICAgIH0NCiAgICAgICAgfSwNCiAgICAgICAgew0KICAgICAgICAgICJ0aXRsZSI6ICJDb25kaXRpb24vSHlwZXJ0ZW5zaW9uIiwNCiAgICAgICAgICAidXBkYXRlZCI6ICIyMDE1LTA5LTIwVDA1OjU4OjEwLjQxOC0wMDowMCIsDQogICAgICAgICAgImNvbnRlbnQiOiB7DQogICAgICAgICAgICAicmVzb3VyY2VUeXBlIjogIkNvbmRpdGlvbiIsDQogICAgICAgICAgICAidGV4dCI6IHsNCiAgICAgICAgICAgICAgInN0YXR1cyI6ICJnZW5lcmF0ZWQiLA0KICAgICAgICAgICAgICAiZGl2IjogIjxkaXY+SHlwZXJ0ZW5zaW9uLCBiZW5pZ248L2Rpdj4iDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN1YmplY3QiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC84ODg4ODAyIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJjb2RlIjogew0KICAgICAgICAgICAgICAiY29kaW5nIjogWw0KICAgICAgICAgICAgICAgIHsNCiAgICAgICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL3Nub21lZC5pbmZvL3NjdCIsDQogICAgICAgICAgICAgICAgICAiY29kZSI6ICIxMjAxMDA1IiwNCiAgICAgICAgICAgICAgICAgICJkaXNwbGF5IjogIkh5cGVydGVuc2lvbiwgYmVuaWduIg0KICAgICAgICAgICAgICAgIH0NCiAgICAgICAgICAgICAgXSwNCiAgICAgICAgICAgICAgInRleHQiOiAiSHlwZXJ0ZW5zaW9uLCBiZW5pZ24iDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN0YXR1cyI6ICJjb25maXJtZWQiLA0KICAgICAgICAgICAgIm9uc2V0RGF0ZSI6ICIyMDEwLTA0LTEyIg0KICAgICAgICAgIH0NCiAgICAgICAgfSwNCiAgICAgICAgew0KICAgICAgICAgICJ0aXRsZSI6ICJDb25kaXRpb24vRGVwcmVzc2lvbiIsDQogICAgICAgICAgInVwZGF0ZWQiOiAiMjAxNS0wOS0yMFQwNTo1ODoxMC40MTgtMDA6MDAiLA0KICAgICAgICAgICJjb250ZW50Ijogew0KICAgICAgICAgICAgInJlc291cmNlVHlwZSI6ICJDb25kaXRpb24iLA0KICAgICAgICAgICAgInRleHQiOiB7DQogICAgICAgICAgICAgICJzdGF0dXMiOiAiZ2VuZXJhdGVkIiwNCiAgICAgICAgICAgICAgImRpdiI6ICI8ZGl2PkRlcHJlc3Npb248L2Rpdj4iDQogICAgICAgICAgICB9LA0KICAgICAgICAgICAgInN1YmplY3QiOiB7DQogICAgICAgICAgICAgICJyZWZlcmVuY2UiOiAiUGF0aWVudC84ODg4ODAyIg0KICAgICAgICAgICAgfSwNCiAgICAgICAgICAgICJjb2RlIjogew0KICAgICAgICAgICAgICAiY29kaW5nIjogWw0KICAgICAgICAgICAgICAgIHsNCiAgICAgICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL3Nub21lZC5pbmZvL3NjdCIsDQogICAgICAgICAgICAgICAgICAiY29kZSI6ICI0MTAwNjAwNCIsDQogICAgICAgICAgICAgICAgICAiZGlzcGxheSI6ICJEZXByZXNzaW9uIg0KICAgICAgICAgICAgICAgIH0NCiAgICAgICAgICAgICAgXSwNCiAgICAgICAgICAgICAgInRleHQiOiAiRGVwcmVzc2lvbiINCiAgICAgICAgICAgIH0sDQogICAgICAgICAgICAic3RhdHVzIjogImNvbmZpcm1lZCIsDQogICAgICAgICAgICAib25zZXREYXRlIjogIjIwMTEtMDQtMTciDQogICAgICAgICAgfQ0KICAgICAgICB9DQogICAgICBdDQogICAgfQ0KICBdDQp9=";
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
            startActivity(mainIntent);
        }
    }
}
