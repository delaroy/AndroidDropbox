package com.delaroystudios.dropbox;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    // Replace APP_KEY from your APP_KEY
    final static private String APP_KEY = "hxexhzbowt7ldk1";
    // Relace APP_SECRET from your APP_SECRET
    final static private String APP_SECRET = "cw04rbphob70oww";

    //
    private DropboxAPI<AndroidAuthSession> mDBApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // callback method
        initialize_session();
    }

    /**
     *  Initialize the Session of the Key pair to authenticate with dropbox
     *
     */
    protected void initialize_session(){

        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        //Pass app key pair to the new DropboxAPI object.
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        // MyActivity below should be your activity class name
        //start session
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
    }

    /**
     * Callback register method to execute the upload method
     * @param view
     */
    public void uploadFiles(View view){

        new Upload().execute();
    }


    /**
     *  Asynchronous method to upload any file to dropbox
     */
    public class Upload extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){

        }

        protected String doInBackground(String... arg0) {

            DropboxAPI.Entry response = null;

            try {

                // Define path of file to be upload
                File file = new File("./sdcard/images.jpg");
                FileInputStream inputStream = new FileInputStream(file);

                //put the file to dropbox
                response = mDBApi.putFile("/screens.png", inputStream,
                        file.length(), null, null);
                Log.e("DbExampleLog", "The uploaded file's rev is: " + response.rev);

            } catch (Exception e){

                e.printStackTrace();
            }

            return response.rev;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.isEmpty() == false){
                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                Log.e("DbExampleLog", "The uploaded file's rev is: " + result);
            }
        }
    }

    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
}
