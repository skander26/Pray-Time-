package com.praytime;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView today;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardView1;
        CardView cardView2;
        CardView cardView3;
        CardView cardView4;
        CardView cardView5;

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        List<CardView> cardViews = new ArrayList<>();

        cardViews.add(cardView1);
        cardViews.add(cardView2);
        cardViews.add(cardView3);
        cardViews.add(cardView4);
        cardViews.add(cardView5);

        today = findViewById(R.id.today_date);
        TextView nextpray = findViewById(R.id.nextpray);

        TextView fajer_remaingin = findViewById(R.id.rest_time);
        TextView fajrpray = findViewById(R.id.pray_time);

        TextView dhuhr_remaingin = findViewById(R.id.rest_time2);
        TextView dhuhrpray = findViewById(R.id.pray_time2);

        TextView aser_remaingin = findViewById(R.id.rest_time3);
        TextView aserpray = findViewById(R.id.pray_time3);

        TextView maghrib_remaingin = findViewById(R.id.rest_time4);
        TextView maghribpray = findViewById(R.id.pray_time4);

        TextView isha_remaingin = findViewById(R.id.rest_time5);
        TextView ishapray = findViewById(R.id.pray_time5);


        //---------------------------------------------


        //---------------------------------------------


        String formattedDate = getdate();
        today.setText(formattedDate.toString());

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String location = "tunisie";
        String url = "https://api.aladhan.com/v1/calendarByAddress/2024/5?address=" + location + "&method=2"; // URL of the API endpoint

        // Request a JSON object response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Extracting data from the JSON object
                    JSONArray data = response.getJSONArray("data");
                    JSONObject timings = data.getJSONObject(0).getJSONObject("timings");

                    String fajr = timings.getString("Fajr");
                    String dhuhr = timings.getString("Dhuhr");
                    String asr = timings.getString("Asr");
                    String maghrib = timings.getString("Maghrib");
                    String isha = timings.getString("Isha");

                    // Format the time as needed
                    fajr = format(fajr);
                    dhuhr = format(dhuhr);
                    asr = format(asr);
                    maghrib = format(maghrib);
                    isha = format(isha);

                    fajrpray.setText(fajr);
                    dhuhrpray.setText(dhuhr);
                    aserpray.setText(asr);
                    maghribpray.setText(maghrib);
                    ishapray.setText(isha);

                    String finalFajr = fajr;
                    String finalDhuhr = dhuhr;
                    String finalAsr = asr;
                    String finalMaghrib = maghrib;
                    String finalIsha = isha;

                    fetchdata(fajr, dhuhr, asr, maghrib, isha);


                    nextpray.setText( "الصلاة القادمة : " +select_time(cardViews));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            private void fetchdata(String fajr, String dhuhr, String asr, String maghrib, String isha) throws ParseException {
                String remainingTime = timeremaining(fajr, fajer_remaingin);
                fajer_remaingin.setText("+" + remainingTime);

                //dhuhr
                remainingTime = timeremaining(dhuhr, dhuhr_remaingin);
                dhuhr_remaingin.setText("+" + remainingTime);

                //asr
                remainingTime = timeremaining(asr, aser_remaingin);
                aser_remaingin.setText("+" + remainingTime);

                //maghrib
                remainingTime = timeremaining(maghrib, maghrib_remaingin);
                maghrib_remaingin.setText("+" + remainingTime);

                //isha
                remainingTime = timeremaining(isha, isha_remaingin);
                isha_remaingin.setText("+" + remainingTime);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    @NonNull
    private static String getdate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Customize the format as needed
        return dateFormat.format(currentDate);
    }

    private String format(String pray) {
        if (pray.length() >= 5) {
            pray = pray.substring(0, pray.length() - 5); // Get the first characters
        }
        return pray;
    }

    private String timeremaining(String pray, TextView pray_name) throws ParseException {
        LocalDateTime currentTime = LocalDateTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        String timenow = String.format("%02d:%02d", hour, minute);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        // Parsing the Time Period
        Date date2 = simpleDateFormat.parse(pray);
        Date date1 = simpleDateFormat.parse(timenow);

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds = (date2.getTime() - date1.getTime());

        // Calculating the difference in Hours
        long differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;
        if (differenceInHours < 0 || differenceInMilliSeconds < 0) {
            pray_name.setTextColor(Color.RED);
            return "00:00";
        }

        // Calculating the difference in Minutes
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000)) % 60;

        // Calculating the difference in Seconds

        String formattedMinutes = String.format("%02d", differenceInMinutes);
        String formattedHours = String.format("%02d", differenceInHours);

        // Printing the answer
        if (differenceInHours > 0) {
            return (String.valueOf(formattedHours) + ":" + String.valueOf(formattedMinutes));
        }

        return "00" + ":" + String.valueOf(formattedMinutes);
    }

    public CharSequence select_time(List<CardView> cardViews) throws ParseException {
        LocalTime localTime = LocalTime.now();
        int prayhour = localTime.getHour();
        int minute = localTime.getMinute();
        LocalTime time = LocalTime.of(prayhour, minute); // Current time

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < cardViews.size(); i++) {
            View childview = cardViews.get(i).getChildAt(1);
            TextView textView = (TextView) childview;
            Date date2 = simpleDateFormat.parse(textView.getText().toString());
            long localhour = (date2.getTime() / (60 * 60 * 1000)) % 24 + 1;
            long minutes = (date2.getTime() / (60 * 1000)) % 60;
            LocalTime praytime = LocalTime.of((int) localhour, (int) minutes); // Prayer time

            if (time.isBefore(praytime)) {
                // If current time is before this prayer time
                cardViews.get(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3A029")));
                TextView result = (TextView) cardViews.get(0).getChildAt(0);
                return result.getText();
            }
        }
        cardViews.get(0).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3A029")));
        TextView result = (TextView) cardViews.get(0).getChildAt(0);
        return result.getText();
    }


}
