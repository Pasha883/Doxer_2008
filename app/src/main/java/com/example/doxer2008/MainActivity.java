package com.example.doxer2008;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.button);
        TextView resultText = findViewById(R.id.resultText);

        b.setOnClickListener(v -> {
            try {
                EditText t = findViewById(R.id.editTextText);
                String ip = t.getText().toString();
                String apiUrl = "http://ip-api.com/json/" + ip;

                new Thread(() -> {
                    HttpURLConnection connection = null;
                    URL url;
                    try {
                        url = new URL(apiUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(10000);
                        connection.connect();

                        Scanner sc = new Scanner(connection.getInputStream());
                        StringBuilder result = new StringBuilder();
                        while (sc.hasNextLine()) {
                            result.append(sc.nextLine());
                        }
                        sc.close();

                        String formattedJson;
                        try {
                            JSONObject json = new JSONObject(result.toString());
                            formattedJson = json.toString(4);
                        } catch (Exception e) {
                            formattedJson = "Ошибка парсинга JSON: " + e.getMessage();
                        }

                        String finalFormattedJson = formattedJson;
                        runOnUiThread(() -> resultText.setText(finalFormattedJson));

                    } catch (MalformedURLException e) {
                        runOnUiThread(() -> resultText.setText("Некорректный URL: " + e.getMessage()));
                    } catch (IOException e) {
                        runOnUiThread(() -> resultText.setText("Ошибка подключения: " + e.getMessage()));
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }).start();
            } catch (Exception e) {
                Log.e("APP", e.getMessage());
            }
        });
    }
}
