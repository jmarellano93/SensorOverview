# Module 7 Android Sensor Integration Exercise Answers and Software Documentation

### Android Studio Github Repository: https://github.com/jmarellano93/SensorOverview

### I. Analysis of the androidsensoroverview-main Repository

**A. Key Source File Examination**

**Table 1: androidsensoroverview-main File Breakdown and Enhancement Recommendations**

| File Path (Relative to Project Root) | Core Purpose in the Project | Recommended Modifications/Enhancements for Medical Data Logging (e.g., for Parkinson's monitoring) |
|--------------------------------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `app/src/main/java/ch/fhnw/sensoroverview/MainActivity.java` | Displays a list of all available device sensors. | - Implement `SensorEventListener` interface. <br> - In `onCreate()`: <br> &nbsp;&nbsp; - Obtain `SensorManager`. <br> &nbsp;&nbsp; - Get `Sensor` objects for `Sensor.TYPE_ACCELEROMETER` and `Sensor.TYPE_GYROSCOPE` using `sensorManager.getDefaultSensor()`. Check for null. <br> &nbsp;&nbsp; - Initialize UI elements (TextViews for accelerometer/gyroscope, Start/Stop button). <br> - In `onResume()`: <br> &nbsp;&nbsp; - Register listeners using `SENSOR_DELAY_GAME` (~50Hz), ideal for PD tremor (3–8Hz). <br> - In `onPause()`: <br> &nbsp;&nbsp; - Unregister listeners to conserve power. <br> - Implement `onSensorChanged(SensorEvent event)`: <br> &nbsp;&nbsp; - Use `event.sensor.getType()` to branch between accelerometer and gyroscope. <br> &nbsp;&nbsp; - For accelerometer: extract `ax`, `ay`, `az`; update UI; log to file if active. <br> &nbsp;&nbsp; - For gyroscope: extract `gx`, `gy`, `gz`; update UI; log to file if active. <br> - Implement `onAccuracyChanged(Sensor sensor, int accuracy)` (optional). <br> - Logging logic: <br> &nbsp;&nbsp; - Add toggle button. <br> &nbsp;&nbsp; - On start: <br> &nbsp;&nbsp;&nbsp;&nbsp; - Use `getExternalFilesDir(null)` or request permission for external storage (if needed). <br> &nbsp;&nbsp;&nbsp;&nbsp; - Open/create CSV (`pd_sensor_data.csv`) with headers. <br> &nbsp;&nbsp; - On data change: append formatted log lines. <br> &nbsp;&nbsp; - On stop: flush and close file stream. <br> - Optional: Add function to clear UI/log contents. |
| `app/src/main/AndroidManifest.xml` | Declares MainActivity, application metadata, and permissions. | - For Android < 10 (API 29): <br> &nbsp;&nbsp; `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />` <br> &nbsp;&nbsp; `<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />` <br> - Add (if needed): <br> &nbsp;&nbsp; `<uses-permission android:name="android.permission.HIGH_SAMPLING_RATE_SENSORS" />` <br> &nbsp;&nbsp; `<uses-permission android:name="android.permission.BODY_SENSORS" />` <br> - Add `<uses-feature>` for required sensors: <br> &nbsp;&nbsp; `<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />` |
| `app/src/main/res/layout/activity_main.xml` | Defines the UI with a ScrollView and TextView for listing sensors. | - Modify layout to include: <br> &nbsp;&nbsp; - `TextView`s for live X/Y/Z values (accelerometer, gyroscope). <br> &nbsp;&nbsp; - Logging `Button` (ID: `btn_toggle_logging`). <br> &nbsp;&nbsp; - Optional: `TextView` for logging status or file path. <br> - Consider switching from `ScrollView` to `LinearLayout` or `ConstraintLayout` for better structure. |
| `app/src/main/res/values/strings.xml` | Contains string resources like `app_name`. | - Add: <br> &nbsp;&nbsp; "Start Logging", "Stop Logging", "Logging Active", "Logging Inactive", and any new messages or labels. |
| `README.md` | Provides build instructions and APK location. | - Update to reflect: <br> &nbsp;&nbsp; - New dependencies (if any). <br> &nbsp;&nbsp; - Required permissions. <br> &nbsp;&nbsp; - Logging feature description. <br> &nbsp;&nbsp; - CSV file path and structure. |
| `gradle.properties`, `build.gradle` (Project and App), `settings.gradle` | Configure build settings, SDK versions, dependencies, project structure. | - No changes needed for basic file I/O and sensor usage. <br> - If adding third-party libraries for advanced logging or UI: <br> &nbsp;&nbsp; - Add dependencies to `app/build.gradle`. <br> - Ensure correct `minSdk`, `targetSdk`, and `compileSdk` versions are set. |

1. app/src/main/java/ch/fhnw/sensoroverview/MainActivity.java :
- Purpose: This class serves as the main entry point and the sole Activity in the application. Within its onCreate method, it initializes the SensorManager. It then calls sensorManager.getSensorList(Sensor.TYPE_ALL) to obtain a List of Sensor objects. The code iterates through this list, appending the name of each sensor (obtained via current.getName()) to a StringBuilder. Finally, this compiled string of sensor names is set as the text for the TextView on the screen.
- Significance: This file houses the core logic necessary to answer the exercise questions related to sensor enumeration. A thorough understanding of its onCreate method is fundamental.

2. app/src/main/AndroidManifest.xml :
- Purpose: This standard Android manifest file declares MainActivity as the application's launcher activity. It also defines various application-level properties such as the application icon, label, theme, and backup rules. Notably, the current manifest does not request any specific sensor permissions (e.g., android.permission.BODY_SENSORS or android.permission.HIGH_SAMPLING_RATE_SENSORS). This is because merely listing general sensor information typically does not require explicit permissions beyond those granted to a standard application.
- Significance: This file defines the app's fundamental characteristics and entry point. The absence of specific sensor permissions is an important observation; if the application were to be extended to actively use certain sensitive sensors (like location-based sensors or body sensors, which are highly relevant for advanced PD monitoring), appropriate permissions would need to be declared here and requested from the user at runtime.

3. app/src/main/res/layout/activity_main.xml :
- Purpose: This XML file defines the visual layout for MainActivity. It contains a ScrollView to ensure all sensor names are viewable, even if the list is long, and a TextView with the ID @+id/sensorlist which acts as the container for displaying the sensor list.
- Significance: This file dictates how the collected sensor information is presented to the user.

4. build.gradle (Module: app) and build.gradle (Project) :
- Purpose: The project-level build.gradle file specifies the Android application plugin version (com.android.application' version '8.1.4'). The module-level build.gradle (content not explicitly detailed in the provided summary but its presence is standard) would define dependencies (such as the AppCompat library for backward compatibility), target and minimum SDK versions, and other module-specific Gradle settings. The gradle.properties file  specifies android.useAndroidX=true, indicating the use of AndroidX libraries, and sets org.gradle.jvmargs=-Xmx2048m for Gradle's Java Virtual Machine arguments.   
- Significance: These files are essential for the successful compilation and packaging of the Android application. The exercise's mention of JDK 17 and specific Gradle commands highlights their importance.   

5. settings.gradle :
- Purpose: This file configures repository locations for plugin and dependency resolution (Google, Maven Central, Gradle Plugin Portal) and includes the :app module in the project. It sets the rootProject.name to "sensoroverview".
- Significance: Essential for Gradle to correctly locate dependencies and structure the multi-module project.

**B. Build System and Dependencies**

The project utilizes Gradle as its build automation system, which is evident from the presence of gradlew (Gradle wrapper script for Unix-like systems), gradlew.bat (for Windows), and the gradle/wrapper/gradle-wrapper.properties file. The latter specifies the Gradle distribution URL, indicating that Gradle version 8.0 will be used for building the project, ensuring build consistency across different environments. The README.md file  provides the fundamental Gradle commands for cleaning the project (gradle clean) and assembling a debug build (gradle assembleDebug).

The current implementation in MainActivity.java is purely informational. It demonstrates the capability of sensor discovery (i.e., finding out which sensors exist) but does not engage in sensor data acquisition or event handling (i.e., reading actual data values from the sensors). This distinction is critical in the context of medical software development. For instance, monitoring Parkinson's disease symptoms effectively would require continuous data streams from sensors like the accelerometer and gyroscope, not merely a one-time list of their names. This gap represents a key area for potential enhancement, which will be explored later in this report.

### II Guide to Exercise Completion

**A. Development Environment Configuration**

1. Android Studio Setup:
- It is recommended to have the latest stable version of Android Studio installed.
- The exercise explicitly requires JDK 17. Verify that JDK 17 is installed on the system and that Android Studio is configured to use it for Gradle projects. This setting can typically be found under File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK (or File > Project Structure > SDK Location > Gradle Settings in newer versions).
- Utilize the SDK Manager (Tools > SDK Manager in Android Studio) to install an Android SDK platform that is compatible with the Samsung S20+ 5G. Given that the SM-G986B/DS model can run Android versions from 10 up to 13, and the project's AndroidManifest.xml specifies tools:targetApi="31", an SDK platform around API level 31 (Android 12) or higher would be appropriate.

2. Samsung S20+ 5G (SM-G986B/DS) Configuration:
- Enable Developer Options: On the Samsung S20+ 5G, navigate to Settings > About phone > Software information. Tap repeatedly (usually seven times) on the "Build number" entry until a message indicates that developer mode has been enabled.
- Enable USB Debugging: Return to the main Settings menu, where a new "Developer options" menu will now be visible. Enter this menu and toggle the "USB debugging" option to on.
- Device Connection: Connect the Samsung S20+ 5G to the development computer using a USB cable. On the phone, a prompt may appear asking to "Allow USB debugging" for the connected computer. Accept this prompt. Ensure that the necessary USB drivers for Samsung devices are installed on the computer to facilitate communication.

**B. Project Setup and Build Process**

With the environment configured, the next step is to set up and build the project.

1. Cloning the Repository:
- The exercise instructions specify cloning the project from the URL: https://gitlab.fhnw.ch/david.herzig/androidsensoroverview.
- This can be achieved using a Git client. In a terminal or command prompt, execute: git clone https://gitlab.fhnw.ch/david.herzig/androidsensoroverview.git.
- Alternatively, Android Studio offers an integrated option to "Get from Version Control" (or "Get from VCS").

2. Importing into Android Studio:
- Launch Android Studio.
- Select "Open an Existing Project" (or "Open") and navigate to the directory where the androidsensoroverview project was cloned.
- Allow Android Studio to synchronize the project with Gradle. This process may involve downloading dependencies and setting up the project structure.

3. Building with Gradle:
- Using Android Studio Interface: The simplest method is to use Android Studio's built-in build commands. Navigate to Build > Rebuild Project. Alternatively, the Gradle tool window (View > Tool Windows > Gradle) allows for running specific tasks such as clean followed by assembleDebug.
- Using Command Line:
  - Open a terminal or command prompt and navigate to the root directory of the cloned project.
  - Ensure the ANDROID_HOME environment variable is correctly set to the location of the Android SDK installation, as recommended in the exercise help section.
    Example for Linux/macOS: export ANDROID_HOME=~/Android/Sdk
    Example for Windows: Set it through the Environment Variables in System Settings.
  - Execute the clean task: ./gradlew clean (Linux/macOS) or gradlew.bat clean (Windows)
  - Execute the debug build task: ./gradlew assembleDebug (Linux/macOS) or gradlew.bat assembleDebug (Windows)
- The README.md file within the project confirms that the compiled APK file (Android Package) will be located at app/build/outputs/apk/debug/app-debug.apk.

**C. Application Deployment and Interaction**

Once the build is successful, the application can be deployed to the device.

1. Installing the APK:
- Via Android Studio: If the Samsung S20+ 5G is connected and recognized by Android Studio (visible in the device dropdown menu in the toolbar), clicking the "Run 'app'" button (typically a green play icon) will automatically build the project (if necessary), install the APK onto the selected device, and launch the application.
- Manually via ADB (Android Debug Bridge): If the APK was built using the command line, it can be installed manually using ADB. With the device connected and USB debugging enabled, execute the command: adb install app/build/outputs/apk/debug/app-debug.apk.

2. Running the Application:
- After installation, locate the "sensoroverview" application icon in the app drawer or on a home screen of the Samsung S20+ 5G. The app name is derived from the app_name string resource defined in app/src/main/res/values/strings.xml. Tap the icon to launch the application.

3. Interpreting Output:
- Upon launching, the application will display a vertically scrollable list of sensor names. This list represents all sensors that the Android system on the specific Samsung S20+ 5G device reports as available.

D. Exercise Answers

1. What type/model of Android device are you using?
- Samsung S20+ 5G, Model: SM-G986B/DS.

2. How many sensors are available?
- 18 Sensors in total.

3. List all the available sensors.

**Table 2: Sensors Expected and Potentially Observed on Samsung S20+ 5G (SM-G986B/DS)**

| Sensor Name                                       | Android Sensor Type                         | Description / Common Use                                                                 |
|--------------------------------------------------|---------------------------------------------|------------------------------------------------------------------------------------------|
| Goldfish 3-axis Accelerometer                    | `Sensor.TYPE_ACCELEROMETER`                 | Measures acceleration forces (including gravity). Used for motion detection and orientation. |
| Goldfish 3-axis Gyroscope                        | `Sensor.TYPE_GYROSCOPE`                     | Measures rotation rate around device axes. Used for tracking orientation and gestures.     |
| Goldfish 3-axis Magnetic field sensor            | `Sensor.TYPE_MAGNETIC_FIELD`                | Measures ambient geomagnetic field. Used for compass functionality.                       |
| Goldfish Orientation sensor                      | `Sensor.TYPE_ORIENTATION` (deprecated)      | Computes device orientation angles. Deprecated in favor of fused sensors.                 |
| Goldfish Ambient Temperature sensor              | `Sensor.TYPE_AMBIENT_TEMPERATURE`           | Measures ambient air temperature.                                                        |
| Goldfish Proximity sensor                        | `Sensor.TYPE_PROXIMITY`                     | Detects nearby objects (e.g., face near screen). Used to turn off display during calls.   |
| Goldfish Light sensor                            | `Sensor.TYPE_LIGHT`                         | Measures ambient light level. Used for adjusting screen brightness.                       |
| Goldfish Pressure sensor                         | `Sensor.TYPE_PRESSURE`                      | Measures atmospheric pressure. Used for estimating elevation (barometer).                 |
| Goldfish Humidity sensor                         | `Sensor.TYPE_RELATIVE_HUMIDITY`             | Measures ambient relative humidity.                                                      |
| Goldfish 3-axis Magnetic field sensor (uncalibrated) | `Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED` | Raw magnetometer data without bias compensation. Useful for sensor fusion.               |
| Goldfish 3-axis Gyroscope (uncalibrated)         | `Sensor.TYPE_GYROSCOPE_UNCALIBRATED`        | Raw gyroscope data without drift compensation.                                           |
| Goldfish 3-axis Accelerometer Uncalibrated       | `Sensor.TYPE_ACCELEROMETER_UNCALIBRATED`    | Raw accelerometer data without offset correction.                                        |
| Game Rotation Vector Sensor                      | `Sensor.TYPE_GAME_ROTATION_VECTOR`          | Orientation without magnetic field data. Optimized for gaming.                           |
| GeoMag Rotation Vector Sensor                    | `Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR`   | Fused orientation using accelerometer and magnetometer.                                  |
| Gravity Sensor                                   | `Sensor.TYPE_GRAVITY`                       | Measures direction and magnitude of gravity. Derived from accelerometer and gyroscope.   |
| Linear Acceleration Sensor                       | `Sensor.TYPE_LINEAR_ACCELERATION`           | Acceleration without gravity. Useful for motion analysis.                                |
| Rotation Vector Sensor                           | `Sensor.TYPE_ROTATION_VECTOR`               | Measures device rotation as a vector. Derived from multiple sensors.                     |
| Orientation Sensor                               | `Sensor.TYPE_ORIENTATION` (deprecated)      | Computes azimuth, pitch, and roll. Deprecated—use fused sensor alternatives.             |


4. Which sensor could be used to measure Parkinson’s disease?

- Primary Sensors:

  - Accelerometer
    - What it measures: Changes in velocity along three axes (up/down, left/right, forward/backward).
    - Tremor Analysis: This is the most important sensor for detecting the classic Parkinsonian tremor. It can precisely measure the frequency (typically 4-6 Hz) and amplitude of the rhythmic shaking, even when it's subtle. This is used to distinguish Parkinson's tremor from other types of tremors.
    - Gait and Balance: When placed on the body (e.g., in a pocket or strapped to the waist), it can analyze walking patterns. It can detect a shuffling gait, measure step length and frequency, and identify "freezing of gait" episodes where the person suddenly stops walking.

  - Gyroscope
    - What it measures: The rate of rotation around three axes (pitch, yaw, and roll).
    - Advanced Tremor Analysis: While the accelerometer detects linear shaking, the gyroscope detects the rotational component of a tremor (e.g., the "pill-rolling" motion of the hand).
    - Postural Instability: This is a crucial sensor for assessing balance. By measuring the subtle rotational movements (sway) while a person is standing still, it can quantify postural instability, a key symptom.
    - Turning Difficulty: Parkinson's patients often have difficulty turning, moving their body stiffly "en bloc" (all at once) rather than smoothly. The gyroscope can precisely measure the speed and smoothness of a turn, providing a clear digital biomarker for this symptom.

  - Linear Acceleration Sensor
    - What it measures: Acceleration without the effect of gravity. It is a "virtual" sensor that combines data from the accelerometer and sometimes the gyroscope.
    - Bradykinesia (Slowness of Movement): This is perhaps the best sensor for measuring bradykinesia. By removing gravity, it can isolate user-initiated movements. During tasks like tapping a finger or walking, it can accurately measure the speed and amplitude of the movement. Reduced speed and amplitude are classic signs of bradykinesia.

  - Rotation Vector Sensor
    - What it measures: The device's orientation in 3D space. It is a highly accurate virtual sensor that fuses data from the accelerometer, gyroscope, and magnetic field sensor.
    - Justification for Parkinson's Diagnosis:
    - Comprehensive Posture and Movement Analysis: This sensor provides a stable and reliable picture of the body's orientation and movement over time. It is excellent for analyzing complex activities like getting up from a chair, walking a path, and turning around, providing a holistic view of a patient's balance and coordination.

- Secondary/Supporting Sensors:

  - Magnetic Field Sensor (Magnetometer)
    - What it measures: The ambient magnetic field, acting like a digital compass.
    - Improved Orientation Tracking: By itself, it doesn't measure motor symptoms. However, its data is fused with the accelerometer and gyroscope to create the highly accurate Rotation Vector Sensor. It provides a stable "heading" reference that prevents drift in the gyroscope over time, making it important for long-duration tests of gait and activity.

### III. Future Data Processing for Parkinson's Disease (using Python in Pycharm)

**A. Sensor Data Processing for Parkinson's Disease (using Python in PyCharm)**

Once sensor data (e.g., accelerometer and gyroscope readings) is logged by the enhanced Android application and transferred to a development machine, PyCharm with Python can be utilized for sophisticated analysis.

1. Data Acquisition and Transfer:
- The CSV file generated by the Android app, containing timestamped accelerometer and gyroscope data, needs to be moved from the Samsung S20+ 5G to the computer. This can be done via USB connection (MTP file transfer), cloud storage services, or Android Debug Bridge (adb pull).
2. Pre-processing Techniques:
- Loading Data: The pandas library in Python is indispensable for loading and manipulating tabular data like CSV files. The data can be read into a DataFrame for easy handling.

```python
import pandas as pd
# Example: df = pd.read_csv('path_to_your_sensor_data.csv')
```

- Filtering: Raw sensor data is often noisy. Filtering is crucial to remove artifacts and isolate frequency bands relevant to PD symptoms.
  - Band-pass filters are commonly applied to accelerometer data to isolate tremor signals, which typically occur in the 2.5-12.5 Hz or 3-8 Hz range for Parkinsonian tremor.
  - The scipy.signal library provides functions for designing various digital filters (e.g., butter for Butterworth, cheby1 for Chebyshev) and applying them (e.g., lfilter for causal filtering, or filtfilt for zero-phase filtering).

```python
from scipy.signal import butter, lfilter, filtfilt
def butter_bandpass_filter(data, lowcut, highcut, fs, order=5):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')
    y = filtfilt(b, a, data)  # Zero-phase filter
    return y
# Example usage:
# fs = 50  # Sampling frequency in Hz (e.g., from SENSOR_DELAY_GAME)
# lowcut = 3.0
# highcut = 8.0
# filtered_accel_x = butter_bandpass_filter(df['accel_x'], lowcut, highcut, fs, order=4)
```

- Segmentation: Continuous sensor data streams are typically divided into smaller, fixed-size windows (e.g., 2 to 20 seconds) for feature extraction. Overlapping windows are often used to ensure that events occurring near window boundaries are not missed and to increase the amount of training data for machine learning models.

3. Feature Extraction Methods:

Extracting discriminative features from the pre-processed sensor data is a critical step before applying machine learning models. These features aim to quantify various aspects of movement.

- Time-Domain Features:
  - Statistical measures: Mean, median, standard deviation, variance, root mean square (RMS).
  - Range-based: Minimum, maximum, peak-to-peak amplitude.
  - Energy-related: Signal Magnitude Area (SMA), mean energy.
  - Rate of change: Jerk (time derivative of acceleration), mean absolute deviation.
  - Shape/count-based: Zero-crossing rate, number of peaks.
  - Inter-axis correlation: Correlation coefficient between X, Y, and Z axes.
- Frequency-Domain Features:
  - Dominant frequency (peak frequency in the spectrum), often indicative of tremor frequency.
  - Power spectral density (PSD) in specific frequency bands (e.g., power in the 3-8 Hz band for tremor).
  - Spectral entropy, quantifying the flatness or predictability of the spectrum.
  - Spectral centroid, roll-off, flux.
  - Mel Frequency Cepstral Coefficients (MFCCs), originally from speech processing, have also shown utility in motion analysis from accelerometer signals.
- Python Libraries for Feature Extraction:
  - numpy for fundamental numerical operations.
  - scipy.fft for performing FFT.
  - tsfresh is a powerful library that can automatically extract a comprehensive set of time-series features.
  - librosa for audio-related features like MFCCs, which can be adapted for sensor signals.
  - dispel is an emerging Python library specifically designed for standardizing the extraction of sensor-derived measures from wearable or smartphone data.

### IV. Machine Learning Applications for PD Monitoring

Machine learning (ML) models can be trained on the extracted features to perform various tasks related to PD monitoring.

1. Overview:

ML can help in classifying the presence or absence of PD symptoms (e.g., tremor vs. no tremor), predicting symptom severity scores (correlating with scales like MDS-UPDRS), or differentiating PD patients from healthy controls based on sensor data patterns.

2. Model Types:
- Traditional Supervised Learning Models:
  - Support Vector Machines (SVM)
  - Random Forests
  - Logistic Regression
  - k-Nearest Neighbors (k-NN)
- Deep Learning Models:
  - Recurrent Neural Networks (RNNs), particularly Long Short-Term Memory (LSTM) units, are well-suited for sequential sensor data as they can capture temporal dependencies.
  - Convolutional Neural Networks (CNNs) can be applied to raw sensor data or spectrogram representations to automatically learn hierarchical features.
- The Disease Severity Score Learning (DSSL) algorithm is a specialized machine learning approach designed to generate an objective severity score for PD from smartphone sensor data by exploiting weak supervision based on medication states.

3. Workflow in PyCharm (using scikit-learn):
- The scikit-learn library is a comprehensive toolkit for ML in Python.
- Data Preparation: Split the dataset (features and corresponding labels) into training and testing sets (e.g., using train_test_split from sklearn.model_selection).
- Model Training: Instantiate an ML model (e.g., RandomForestClassifier from sklearn.ensemble) and train it using the training data (model.fit(X_train, y_train)).
- Model Evaluation: Evaluate the trained model's performance on the unseen test data using metrics like accuracy, precision, recall, F1-score, and Area Under the ROC Curve (AUC) (e.g., accuracy_score, classification_report, roc_auc_score from sklearn.metrics).
- Example: One could train a Random Forest classifier using the extracted time-domain and frequency-domain features to distinguish between data segments exhibiting tremor and those that do not (assuming labeled data is available or can be simulated for this exercise).


