package com.example.myfristbutton;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng point1 = new LatLng(43.855457, -79.34589); //
        LatLng point2 = new LatLng(43.741676, -79.23117); //
        LatLng point3 = new LatLng(43.627499, -79.39569); //
        LatLng point4 = new LatLng(43.629115, -79.51044); //

        PolygonOptions polygonOptions = new PolygonOptions()
                .add(point1, point2, point3, point4, point1) // Add the points and close the polygon
                .strokeColor(Color.RED)
                .fillColor(Color.argb(128, 255, 255, 0)); // 半透明蓝色

        mMap.addPolygon(polygonOptions);

        // Enable zoom controls on the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set the zoom level of the map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point1, 10)); // Adjust the second parameter to set the zoom level, 10 is an example

        // Optional: If you want the map to zoom to the bounds of the polygon
        // Create a LatLngBounds object and include all the points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(point1);
        builder.include(point2);
        builder.include(point3);
        builder.include(point4);
        LatLngBounds bounds = builder.build();

        // Move the camera to that bound with a padding
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonFetchWeather = findViewById(R.id.button_fetch_weather);
        buttonFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Weather.class);
                startActivity(intent);
            }
        });

        Button impactedRegionButton = findViewById(R.id.button_impacted_region);

        impactedRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImpactedRegionActivity.class);
                startActivity(intent);
            }
        });


        Button searchButton = findViewById(R.id.Search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API to fetch weather info

//                DownloadWebpageTask myTask = new DownloadWebpageTask();
//                myTask.execute("https://dd.weather.gc.ca/alerts/cap/");
//
//                try {
//                    System.out.println("@!!!!!inside");
//
//                    String[] result = myTask.get();
//
//                    for (String element : result) {
//                        System.out.println("Result from AsyncTask caplisks 111: " + element);
//                    }
//
//                    List<String> list = process(result);
//
//                    for (String element : list) {
//                        System.out.println("!!!! list 111: " + element);
//                    }
//
//                } catch (InterruptedException e) {
//                    // 处理中断异常
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//
//                System.out.println("@!!!!!结束");
//
//                Intent intent = new Intent(MainActivity.this, UniversityList.class);
//                startActivity(intent);
            }
        });
    }

    private List<String> process(String[] result){

        String baseUrl = "https://dd.weather.gc.ca/alerts/cap/";
        List<String> capLinks = new ArrayList<>(Arrays.asList(result));
        int fileLimit = 10; // Replace with your file limit
        int fileCounter = 0;
        List<String> capData = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        Pattern datePattern = Pattern.compile("(\\d{8})");

        for (int index = 0; index < capLinks.size(); index++) {
            String link = capLinks.get(index);
            System.out.println(fileCounter);

            if (index >= fileLimit) {
                break;
            }

            System.out.println("1111link from AsyncTask caplisks 111: " + link);
//            String capUrl = baseUrl + link;
            String capUrl = link;

            Request request = new Request.Builder().url(capUrl).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Failed to retrieve CAP file from " + capUrl + ". Status Code: " + response.code());
                    continue;
                }

                System.out.println("!!!11111");

                Document capSoup = Jsoup.parse(response.body().string(), "", org.jsoup.parser.Parser.xmlParser());
                Matcher matcher = datePattern.matcher(link);
                System.out.println("!!!22222");

                if (matcher.find()) {
                    System.out.println("!!!11111");

                    String dateFolder = matcher.group(1);
//                    LocalDate alertDate = LocalDate.parse(dateFolder, DateTimeFormatter.ofPattern("yyyyMMdd"));
//
//                    // Extract the rest of the information from the CAP file
//                    // Replace with your logic to parse the CAP file
//                    String capInfo = parseCapFile(capSoup);
//                    capInfo += " " + alertDate.toString();

                    Elements polygon = capSoup.select("polygon");
                    if (!polygon.isEmpty()) {
                        capData.add(polygon.text());
                    }

                    fileCounter++;

                    // Store the CAP file data in the list
//                    capData.add(capInfo);
                    return  capData;
                } else {
                    System.out.println("Couldn't extract date from link: " + link);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return capLinks;
    }
}