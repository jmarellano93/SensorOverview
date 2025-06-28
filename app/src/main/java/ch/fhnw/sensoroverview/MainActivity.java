package ch.fhnw.sensordatacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> selectedSensors = new ArrayList<>();
    private SensorHandler sensorHandler = new SensorHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Create internal storage directory and JSON file
        File dataDir = new File(getFilesDir(), "SensorDataCollection_Repo");
        if (!dataDir.exists()) {
            boolean dirCreated = dataDir.mkdirs();
            if (dirCreated) {
                Log.d("Init", "Created directory: SensorDataCollection_Repo");

                File initFile = new File(dataDir, "init_session.json");
                try (FileWriter writer = new FileWriter(initFile)) {
                    writer.write("{\"status\": \"ready\", \"message\": \"Folder initialized.\"}");
                    Log.d("Init", "init_session.json created.");
                } catch (IOException e) {
                    Log.e("Init", "Failed to create init_session.json", e);
                }
            }
        }

        // Setup sensor list spinner
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Spinner sensorSpinner = findViewById(R.id.sensorspinner);
        List<String> sensorArray = new ArrayList<>();

        for (Sensor sensor : sensorList) {
            sensorArray.add(sensor.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sensorArray);
        sensorSpinner.setAdapter(adapter);
    }

    public void addButtonClicked(View view) {
        Spinner sensorSpinner = findViewById(R.id.sensorspinner);
        String sensorName = sensorSpinner.getSelectedItem().toString();

        for (Sensor sensor : selectedSensors) {
            if (sensor.getName().equals(sensorName)) return;
        }

        Sensor sensor = getSensor(sensorName);
        selectedSensors.add(sensor);
        updateSelectedSensorList();
    }

    public void startButtonClicked(View view) {
        EditText patientIdInput = findViewById(R.id.patientInput);
        EditText experimentIdInput = findViewById(R.id.experimentInput);
        EditText serverIpInput = findViewById(R.id.serverInput);

        if (patientIdInput.getText().toString().trim().isEmpty()) {
            patientIdInput.setText("0");
        }
        if (experimentIdInput.getText().toString().trim().isEmpty()) {
            experimentIdInput.setText("0");
        }

        int patientId = Integer.parseInt(patientIdInput.getText().toString());
        int experimentId = Integer.parseInt(experimentIdInput.getText().toString());
        String serverIp = serverIpInput.getText().toString();

        Button startButton = findViewById(R.id.startbutton);
        Button addButton = findViewById(R.id.addbutton);
        Button resetButton = findViewById(R.id.resetbutton);

        if (startButton.getText().equals("Start")) {
            patientIdInput.setEnabled(false);
            experimentIdInput.setEnabled(false);
            addButton.setEnabled(false);
            resetButton.setEnabled(false);
            startButton.setText("Stop");

            sensorHandler.setMetaData(patientId, experimentId);
            startCollectingData();
        } else {
            stopCollectingData(serverIp);
            addButton.setEnabled(true);
            resetButton.setEnabled(true);
            startButton.setText("Start");
            patientIdInput.setEnabled(true);
            experimentIdInput.setEnabled(true);
        }
    }

    private void startCollectingData() {
        sensorHandler.getData().clear();
        for (Sensor sensor : selectedSensors) {
            sensorManager.registerListener(sensorHandler, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopCollectingData(String serverIpAddress) {
        sensorManager.unregisterListener(sensorHandler);
        List<DataObject> dataToUpload = sensorHandler.getData();

        // ✅ Save to local JSON file
        File saveFile = new File(getFilesDir() + "/SensorDataCollection_Repo", "session_data.json");
        try (FileWriter writer = new FileWriter(saveFile)) {
            Gson gson = new Gson();
            writer.write(gson.toJson(dataToUpload));
            Log.d("Save", "Data written to session_data.json");
        } catch (IOException e) {
            Log.e("Save", "Failed to write JSON data", e);
        }

        // ✅ Upload each object to the server
        for (DataObject dObj : dataToUpload) {
            uploadData(dObj, serverIpAddress);
        }
    }

    private void uploadData(DataObject dObj, String serverIpAddress) {
        RequestQueue queue = Volley.newRequestQueue(this);
        TextView statusView = findViewById(R.id.statusLabel);
        String url = "http://" + serverIpAddress + ":8080/patient/" + dObj.getPatientId() + "/data";

        Gson gson = new Gson();
        String requestBody = gson.toJson(dObj);
        Log.d("Upload", "Payload: " + requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> statusView.setText("Upload success."),
                error -> statusView.setText("Upload failed: " + error.getMessage())) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };
        queue.add(stringRequest);
    }

    public void resetButtonClicked(View view) {
        selectedSensors.clear();
        updateSelectedSensorList();
    }

    private void updateSelectedSensorList() {
        StringBuilder sensorText = new StringBuilder();
        for (Sensor sensor : selectedSensors) {
            sensorText.append(sensor.getName()).append(System.lineSeparator());
        }
        TextView sensorTextView = findViewById(R.id.selectedsensors);
        sensorTextView.setText(sensorText.toString());
    }

    private Sensor getSensor(String sensorName) {
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getName().equals(sensorName)) {
                return sensor;
            }
        }
        return null;
    }
}
