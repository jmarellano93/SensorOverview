# Module 7 Android Sensor Integration Exerercise Answers and Software Documentation

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

D. Addressing the Exercise Questions

With the application running, the exercise questions can now be answered.

1. "What type/model of Android device are you using?"
- The answer is directly provided by the user's initial query: Samsung S20+ 5G, Model: SM-G986B/DS.

2. "How many sensors are available?"
- Carefully count the number of unique sensor names displayed by the "sensoroverview" application running on the Samsung S20+ 5G.

3. "List all the available sensors?"
- Transcribe the complete list of sensor names exactly as they appear in the application.
- The Samsung S20+ (SM-G986B/DS, likely the Exynos variant for international markets) is known to include several physical sensors. Specifications typically list an Accelerometer, Gyroscope, Compass (Magnetometer), Barometer, Proximity sensor, and Ambient light sensor.
- The list generated by the application may include these hardware sensors as well as "virtual" or "composite" sensors. These are software-defined sensors that derive their data by processing or fusing inputs from one or more physical sensors (e.g., a "Linear Acceleration Sensor" often combines accelerometer and gyroscope data to remove gravity's influence). It is important to note any such virtual sensors, as they often provide data that is more directly usable for specific applications. The exact list can vary slightly based on the Android OS version and Samsung's specific Hardware Abstraction Layer (HAL) implementation.

**Table 2: Sensors Expected and Potentially Observed on Samsung S20+ 5G (SM-G986B/DS)**

| Sensor Name (Example from App)     | Android Sensor Type (Constant)     | Brief Description / Common Use                                                                 |
|------------------------------------|------------------------------------|--------------------------------------------------------------------------------------------------|
| [Name from App Output]             | `Sensor.TYPE_ACCELEROMETER`        | Measures acceleration forces (including gravity) along X, Y, Z axes. Used for motion detection, orientation, vibration. |
| [Name from App Output]             | `Sensor.TYPE_GYROSCOPE`            | Measures rate of rotation around X, Y, Z axes. Used for orientation, gesture recognition, image stabilization. |
| [Name from App Output]             | `Sensor.TYPE_MAGNETIC_FIELD`       | Measures ambient geomagnetic field strength along X, Y, Z axes. Used for compass functionality. |
| [Name from App Output]             | `Sensor.TYPE_LIGHT`                | Measures ambient light level (illuminance). Used for automatic screen brightness adjustment. |
| [Name from App Output]             | `Sensor.TYPE_PROXIMITY`            | Measures proximity of an object relative to the device screen (binary or distance). Used to turn off screen during calls. |
| [Name from App Output]             | `Sensor.TYPE_PRESSURE`             | Measures atmospheric pressure. Used for altimeter functionality, weather prediction. |
| [Name from App Output]             | `Sensor.TYPE_GRAVITY`              | (Composite) Measures force of gravity along X, Y, Z axes. Derived from accelerometer, sometimes magnetometer/gyroscope. |
| [Name from App Output]             | `Sensor.TYPE_LINEAR_ACCELERATION`  | (Composite) Measures acceleration force excluding gravity along X, Y, Z axes. Derived from accelerometer and gyroscope. |
| [Name from App Output]             | `Sensor.TYPE_ROTATION_VECTOR`      | (Composite) Measures device orientation as a quaternion or rotation vector. Derived from accelerometer, gyroscope, magnetometer. |
| ... (other sensors listed)         | ... (corresponding type)           | ... (description) |

4. "Which sensor could be used to measure Parkinson’s disease?"
- Primary Sensors:

    - Accelerometer: This is a cornerstone for PD monitoring. It measures linear acceleration and can detect and quantify tremors (their frequency and amplitude), assess gait characteristics (such as step length, cadence, and variability), analyze balance (postural sway), and evaluate bradykinesia (slowness and decrement in amplitude of movement).

    - Gyroscope: This sensor measures angular velocity (rate of rotation). It is essential for analyzing rotational components of movement, such as those occurring during tremors, assessing postural stability, and tracking orientation changes during gait or other motor tasks.

- Secondary/Supporting Sensors (Conceptual for Advanced Monitoring):

    - Magnetometer (Compass): While not a primary detector of PD motor symptoms, the magnetometer can be used in sensor fusion algorithms alongside the accelerometer and gyroscope. This combination allows for more accurate and robust tracking of device orientation in 3D space, which can be beneficial for complex movement analysis.

    - Microphone (for Voice Analysis): Parkinson's disease can significantly affect speech, leading to symptoms like hypophonia (soft voice), monotone speech, and dysarthria (slurred speech). Although the microphone is not listed as a "sensor" in the same category by Sensor.TYPE_ALL, it is a critical data acquisition component on a smartphone. Voice recordings captured by the microphone can be analyzed to detect and quantify these speech-related PD symptoms.

- The answer to this question requires a synthesis of information: confirming the presence of accelerometers and gyroscopes from the app's output on the Samsung S20+ 5G, and then linking these specific sensors to their established roles in PD symptom measurement based on the available research on mHealth applications for Parkinson's disease. This connection underscores the exercise's relevance to medical software development by moving beyond simple technical enumeration to applied knowledge.

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


