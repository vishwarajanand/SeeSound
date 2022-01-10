package vishwarajanand.seesound;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class home extends AppCompatActivity {

  private final static String LOG_TAG = "home";
  //Create placeholder for user's consent to record_audio permission.
  //This will be used in handling callback
  public static final int APP_PERMISSIONS_RECORD_AUDIO = 1;
  private VisualizerView visualizerView;
  private MediaRecorder recorder;
  private Handler handler = new Handler();

  TextView txtLbl;
  final Runnable updater = new Runnable() {
    public void run() {
      handler.postDelayed(this, 50);
      if (recorder != null && visualizerView != null) {
        int maxAmplitude = 0;
        try {
          maxAmplitude = recorder.getMaxAmplitude();
          txtLbl.setVisibility(View.INVISIBLE);
        } catch (IllegalStateException ex) {
          Log.e(LOG_TAG, "Exception while recording microphone: ", ex);
        } catch (RuntimeException rex) {
          Log.e(LOG_TAG, "Runtime Exception while recording microphone: ", rex);
        }
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
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_home);
    visualizerView = (VisualizerView) findViewById(R.id.visualizer);
    txtLbl = (TextView) findViewById(R.id.app_label);

    visualizerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Go ahead with recording audio now
        startAudioRecorder();
      }
    });
  }

  private void RequestPermissions() {
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {

      //When permission is not granted by user, show them message why this permission is needed.
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.RECORD_AUDIO)) {
        Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
      }
      // Show user dialog to grant permission to record audio
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.RECORD_AUDIO},
          APP_PERMISSIONS_RECORD_AUDIO);
    }
    //If permission is granted, then go ahead recording audio
    else if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED) {

    }
    ActivityCompat.requestPermissions(home.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, APP_PERMISSIONS_RECORD_AUDIO);

  }

  //Handling callback
  @Override
  public void onRequestPermissionsResult(int requestCode,
      String permissions[], int[] grantResults) {
    switch (requestCode) {
      case APP_PERMISSIONS_RECORD_AUDIO: {
        if (grantResults.length > 0) {
          boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
          boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
          if (permissionToRecord && permissionToStore) {
            Toast.makeText(getApplicationContext(), "Record Audio Permission Granted", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(getApplicationContext(), "Record Audio Permission Denied", Toast.LENGTH_LONG).show();
          }
        }
        break;
      }
    }
  }
  public boolean CheckPermissions() {
    // this method is used to check permission
    int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
    int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
    return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
  }

  private void startAudioRecorder() {
    if (CheckPermissions()) {
      recorder = new MediaRecorder();
      // String manufacturer = Build.MANUFACTURER;
      // if (manufacturer.toLowerCase().contains("google")) {
      //   recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
      // } else {
      //   // recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
      //   recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
      // }
      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      File file = null;
      try {
        file = File.createTempFile("prefix", ".extension", getApplicationContext().getCacheDir());
        recorder.setOutputFile(file);
      } catch (IOException e) {
        e.printStackTrace();
        recorder.setOutputFile("/dev/null");
      }
      try {
        toast("Try prepare listening to audio.");
        recorder.prepare();
        toast("Try start listening to audio.");
        recorder.start();
        toast("Started listening to audio.");
      } catch (IllegalStateException | IOException ex) {
        toast("Audio listener failed -> IllegalStateException | IOException");
        Log.e(LOG_TAG, "Exception while initializing audio record: ", ex);
      } catch (RuntimeException rex) {
        toast("Audio listener failed -> RuntimeException");
        Log.e(LOG_TAG, "Runtime Exception while initializing audio record: ", rex);
      }
    }else {
      RequestPermissions();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(updater);
    if (recorder != null) {
      try {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

      } catch (IllegalStateException ex) {
        Log.e(LOG_TAG, "Exception while releasing audio record: ", ex);
      }
    }
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
