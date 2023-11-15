package com.example.myfristbutton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather extends AppCompatActivity {
    private TextView textViewCurrentWeatherResult;
    private TextView textViewWeatherForecastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewCurrentWeatherResult = findViewById(R.id.textView_left);
        textViewWeatherForecastResult = findViewById(R.id.textView_right);

        Button button_weather = findViewById(R.id.button_weather);
        Button button_forecast = findViewById(R.id.button_forecast);

        String targetCityLatitude = "49.2827";
        String targetCityLongitude = "-123.1207";

        button_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTasks().execute("https://api.openweathermap.org/data/2.5/weather?", targetCityLatitude, targetCityLongitude);

//                Intent intent = new Intent(com.example.myfristbutton.UniversityList.this, activityMapping.get(checkedId));
//                startActivity(intent);
            }
        });

        button_forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTasks().execute("http://api.openweathermap.org/data/2.5/forecast?", targetCityLatitude, targetCityLongitude);

//                Intent intent = new Intent(com.example.myfristbutton.UniversityList.this, activityMapping.get(checkedId));
//                startActivity(intent);
            }
        });
    }


    private class MyAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog to show the user what is happening
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetch data from the API in the background.
            String baseUrl = params[0];
            String keyword;

            if (baseUrl.equals("https://api.openweathermap.org/data/2.5/weather?"))
            {
                keyword = "currentweather";
            }
            else
            {
                keyword = "weatherforecast";
            }

            String result = "";
            try {

                String latitude = params[1];
                String longitude = params[2];
                String exclude = "YOUR_PART_TO_EXCLUDE"; // 例如 hourly, daily
                String apiKey = "0d247ee32b817da34f975ca40b410ca8";

                String urlString = baseUrl
                        + "lat=" + latitude
                        + "&lon=" + longitude
                        + "&limit=500&appid=" + apiKey;

                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();
                    }

                    result += "keyword:" + keyword;

                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "Exception: " + e.getMessage();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String keyword = "keyword:";
            int index = result.indexOf(keyword);
            String type = result.substring(index + keyword.length()).trim();
            String actualReponse = result.substring(0, index).trim();

            JSONObject coord;
            String lat;
            String log;
            try {
                JSONObject jsonObject = new JSONObject(actualReponse);

                // there is only coord for current weather response but not for weather forecast. check the comment of their actual response
                coord = jsonObject.getJSONObject("coord");
                lat = coord.getString("lat");
                log = coord.getString("lon");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (type.equalsIgnoreCase("currentweather"))
            {
//current weather response
                //                     here is the api link:  https://openweathermap.org/current
// {"coord":{"lon":-106.3468,"lat":56.1304},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"base":"stations","main":{"temp":270.1,"feels_like":263.84,"temp_min":270.1,"temp_max":270.1,"pressure":999,"humidity":88,"sea_level":999,"grnd_level":941},"visibility":10000,"wind":{"speed":5.98,"deg":307,"gust":13.52},"clouds":{"all":100},"dt":1699983160,"sys":{"country":"CA","sunrise":1699972814,"sunset":1700002774},"timezone":-21600,"id":6140815,"name":"Sandy Lake","cod":200}

                // TODO: display weather info in UI, above is the sample response

                textViewCurrentWeatherResult.setText(actualReponse);
            }
            else
            {


//weather forcast response  {"cod":"200","message":0,"cnt":40,"list":[{"dt":1699984800,"main":{"temp":278.52,"feels_like":278.52,"temp_min":278.52,"temp_max":279.32,"pressure":1019,"sea_level":1019,"grnd_level":1015,"humidity":90,"temp_kf":-0.8},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":75},"wind":{"speed":0.86,"deg":351,"gust":1.27},"visibility":10000,"pop":0,"sys":{"pod":"d"},"dt_txt":"2023-11-14 18:00:00"},{"dt":1699995600,"main":{"temp":279.19,"feels_like":278.26,"temp_min":279.19,"temp_max":280.54,"pressure":1019,"sea_level":1019,"grnd_level":1014,"humidity":82,"temp_kf":-1.35},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":77},"wind":{"speed":1.52,"deg":258,"gust":1.59},"visibility":10000,"pop":0,"sys":{"pod":"d"},"dt_txt":"2023-11-14 21:00:00"},{"dt":1700006400,"main":{"temp":279.02,"feels_like":279.02,"temp_min":279.02,"temp_max":279.27,"pressure":1018,"sea_level":1018,"grnd_level":1013,"humidity":80,"temp_kf":-0.25},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":78},"wind":{"speed":1.26,"deg":273,"gust":1.59},"visibility":10000,"pop":0,"sys":{"pod":"d"},"dt_txt":"2023-11-15 00:00:00"},{"dt":1700017200,"main":{"temp":278.06,"feels_like":278.06,"temp_min":278.06,"temp_max":278.06,"pressure":1017,"sea_level":1017,"grnd_level":1013,"humidity":79,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":88},"wind":{"speed":0.73,"deg":10,"gust":0.94},"visibility":10000,"pop":0,"sys":{"pod":"n"},"dt_txt":"2023-11-15 03:00:00"},{"dt":1700028000,"main":{"temp":277.68,"feels_like":277.68,"temp_min":277.68,"temp_max":277.68,"pressure":1017,"sea_level":1017,"grnd_level":1013,"humidity":81,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":87},"wind":{"speed":1.3,"deg":56,"gust":1.32},"visibility":10000,"pop":0,"sys":{"pod":"n"},"dt_txt":"2023-11-15 06:00:00"},{"dt":1700038800,"main":{"temp":277.63,"feels_like":277.63,"temp_min":277.63,"temp_max":277.63,"pressure":1016,"sea_level":1016,"grnd_level":1012,"humidity":80,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":100},"wind":{"speed":1.02,"deg":73,"gust":1.08},"visibility":10000,"pop":0,"sys":{"pod":"n"},"dt_txt":"2023-11-15 09:00:00"},{"dt":1700049600,"main":{"temp":277.61,"feels_like":277.61,"temp_min":277.61,"temp_max":277.61,"pressure":1017,"sea_level":1017,"grnd_level":1013,"humidity":81,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":100},"wind":{"speed":0.21,"deg":26,"gust":0.73},"visibility":10000,"pop":0,"sys":{"pod":"n"},"dt_txt":"2023-11-15 12:00:00"},{"dt":1700060400,"main":{"temp":277.98,"feels_like":277.98,"temp_min":277.98,"temp_max":277.98,"pressure":1016,"sea_level":1016,"grnd_level":1012,"humidity":83,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":100},"wind":{"speed":0.3,"deg":125,"gust":1.15},"visibility":10000,"pop":0,"sys":{"pod":"n"},"dt_txt":"2023-11-15 15:00:00"},{"dt":1700071200,"main":{"temp":278.8,"feels_like":278.8,"temp_min":278.8,"temp_max":278.8,"pressure":1018,"sea_level":1018,"grnd_level":1014,"humidity":82,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"clouds":{"all":100},"wind":{"speed":1.26,"deg":250,"gust":2.25},"visibility":10000,"pop":0,"sys":{"pod":"d"},"dt_txt":"2023-11-15 18:00:00"},{"dt":1700082000,"main":{"temp":279.66,"feels_like":278.01,"temp_min":279.66,"temp_max":279.66,"pressure":1017,"sea_level":1017,"grnd_level":1013,"humidity":76,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"clouds":{"all":94},"wind":{"speed":2.29,"deg":290,"gust":3.5},"visibility":10000,"pop":0.26,"sys":{"pod":"d"},"dt_txt":"2023-11-15 21:00:00"},{"dt":170

                // TODO: display weather forecast info in UI, above is the sample response

                textViewWeatherForecastResult.setText(actualReponse);
            }
        }
    }
}