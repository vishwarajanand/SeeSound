package vishwarajanand.seesound;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;

public class home extends AppCompatActivity {
    private final static String LOG_TAG = "home";

    private VisualizerView visualizerView;
    private MediaRecorder recorder = new MediaRecorder();
    private Handler handler = new Handler();
    final Runnable updater = new Runnable() {
        public void run() {
            handler.postDelayed(this, 50);
            if (recorder != null && visualizerView != null) {
                int maxAmplitude = recorder.getMaxAmplitude();
                if (maxAmplitude > 0) {
                    visualizerView.addAmplitude(maxAmplitude);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_home);
        visualizerView = (VisualizerView) findViewById(R.id.visualizer);
        requestRecordAudioPermission();
    }

    private void requestRecordAudioPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;
            int result = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                toast("Microphone permission granted!");
                startAudioRecorder();
            } else {
                toast("Microphone permission not granted");
                // request permissions
                ActivityCompat.requestPermissions(this, new String[]{requiredPermission}, 101);
            }
        } else {
            Log.e(LOG_TAG, "Old android phone, permissions isnt handled in code.");
            toast("Permissions would be handled on app initialize!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // TODO: [MinSdkUpgrade] Change permission check to Arrays.stream(grantResults).allMatch(x -> x == PackageManager.PERMISSION_GRANTED)
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This method is called when the  permissions are given
            Log.e(LOG_TAG, "Permissions granted successfully, starting audio");
            startAudioRecorder();
        } else {
            toast("Problem with permissions!");
            // TODO: This may cause unintended recirsive depth and hence an app crash.
            if (grantResults.length > 0) {
                Log.e(LOG_TAG, "Rechecking app permissions.");
//                requestRecordAudioPermission();
            } else {
                Log.e(LOG_TAG, "All permissions failed, hence exiting app.");
                finish();
            }
        }
    }

    private void startAudioRecorder() {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/dev/null");
            recorder.prepare();
            toast("Started listening to audio.");
            recorder.start();
        } catch (IllegalStateException | IOException ex) {
            Log.e(LOG_TAG, "Exception while initializing audio record: ", ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
        //recorder.stop();
        recorder.reset();
        recorder.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        handler.post(updater);
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }
}
