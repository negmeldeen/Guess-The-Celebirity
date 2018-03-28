package com.example.muhammad.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebUrls=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int Chosenceleb =0;
    ImageView imageView;
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url= new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream= connection.getInputStream();
                Bitmap mybitMap = BitmapFactory.decodeStream(inputStream);
                return  mybitMap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public  class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection connection=null;
            try {
                url =new  URL(urls[0]);
                connection=(HttpURLConnection) url.openConnection();
                InputStream in =connection.getInputStream();
                InputStreamReader reader =new InputStreamReader(in);
                int data =reader.read();
                while (data!= -1){
                    char current =(char)data;
                    result+=current;
                    data=reader.read();
                }
                return  result;

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =(ImageView) findViewById(R.id.imageView);
        DownloadTask task= new DownloadTask();
        String result= null;

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebUrls.add(m.group(1));

            }
            p= Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(splitResult[1]);

            while (m.find()){
                celebNames.add(m.group(1));
            }
            Random random= new Random();
            Chosenceleb = random.nextInt(celebUrls.size());
            ImageDownloader imageTask= new ImageDownloader();
            Bitmap celebImage;
            celebImage = imageTask.execute(celebUrls.get(Chosenceleb)).get();
            imageView.setImageBitmap(celebImage);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
