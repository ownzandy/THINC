package com.example.ownzandy.thinc;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends Activity {

    static final int QR_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //goes to qr activity when button is pressed
    public void gotoQR(View v){
        Intent intent = new Intent(this, QRActivity.class);
        startActivityForResult(intent, QR_REQUEST);

    }

    //executes when qr activity is completed
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == QR_REQUEST) {
            if (resultCode == RESULT_OK) {
                String resultString = data.getExtras().getString("id");
                Toast.makeText(this, resultString, Toast.LENGTH_LONG).show();
                Intent intentRESTAPI = new Intent(this, RESTAPI.class);
                startActivity(intentRESTAPI);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
