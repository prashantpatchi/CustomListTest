package win.prashant.customlisttest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.Listviewdemo);
        new JsonCategory().execute("http://transport.opendata.ch/v1/locations?query=Zurich");
    }

    private class JsonCategory extends AsyncTask<String, String, List<StationDetails>> {
        @Override
        protected List<StationDetails> doInBackground(String... strings) {
            //connection create
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                // responce

                String finalJson = buffer.toString();
                JSONObject parentobject = new JSONObject(finalJson);
                JSONArray parentarray = parentobject.getJSONArray("stations");
                List<StationDetails> stationlist = new ArrayList<>();

                // is in for loop to read all the data line cloum by cloum
                for (int i = 0; i < parentarray.length(); i++) {
                    JSONObject finalObject = parentarray.getJSONObject(i);
                    StationDetails stationDetails1 = new StationDetails();
                    String pname = finalObject.getString("name");

                    // get method setName from Stations details class

                    stationDetails1.setName(pname);
                    /*String productimage = "http://sarwar.webvalleytech.com/upload/" + finalObject.getString("pic");
                    stationDetails1.setProductImage(productimage);*/
                    stationlist.add(stationDetails1);

                }

                return stationlist;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        //show the Result when backgroud method is finished
        @Override
        protected void onPostExecute(List<StationDetails> stationDetails) {
            super.onPostExecute(stationDetails);

            // now we will make array adapter and customize
            StationAdapter pp=new StationAdapter(MainActivity.this,R.layout.customlistview,stationDetails);
            listView.setAdapter(pp);

        }
    }
    private class StationAdapter extends ArrayAdapter {
        public List<StationDetails>stationDetails;
        private int resources;
        private LayoutInflater inflater;
        public StationAdapter(Context context, int resource, List<StationDetails>objects) {
            super(context, resource, objects);
            stationDetails=objects;
            this.resources=resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null)
            {
                convertView=inflater.inflate(resources,null);
            }
            textView=convertView.findViewById(R.id.textViewStationname);
            textView.setText(stationDetails.get(position).getName());
           /* imageView=convertView.findViewById(R.id.imageView);
            ImageLoader.getInstance().displayImage(productDetails.get(position).getProductImage(),imageView);*/
            return convertView;

        }
    }
}
