package vishwarajanand.seesound;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;

public class home extends AppCompatActivity {

  private final static String LOG_TAG = "home";
  //Create placeholder for user's consent to record_audio permission.
  //This will be used in handling callback
  private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
  private VisualizerView visualizerView;
  private MediaRecorder recorder = new MediaRecorder();
  private Handler handler = new Handler();
  final Runnable updater = new Runnable() {
    public void run() {
      handler.postDelayed(this, 50);
      if (recorder != null && visualizerView != null) {
        int maxAmplitude = 0;
        try {
          maxAmplitude = recorder.getMaxAmplitude();
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
    // TODO: Check abt this, it's a upgrade legacy code which has stopped working!
    getSupportActionBar().hide();
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_home);
    visualizerView = (VisualizerView) findViewById(R.id.visualizer);
    requestRecordAudioPermission();
  }

  private void requestRecordAudioPermission() {
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
          MY_PERMISSIONS_RECORD_AUDIO);
    }
    //If permission is granted, then go ahead recording audio
    else if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED) {

      //Go ahead with recording audio now
      startAudioRecorder();
    }
  }

  //Handling callback
  @Override
  public void onRequestPermissionsResult(int requestCode,
      String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_RECORD_AUDIO: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          toast("Listening to the phone audio, permission was granted, yay!");
          startAudioRecorder();
        } else {
          Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
        }
        return;
      }
    }
  }

  private void startAudioRecorder() {
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    recorder.setOutputFile("/dev/null");
    try {
      recorder.prepare();
      toast("Started listening to audio.");
      recorder.start();
    } catch (IllegalStateException | IOException ex) {
      Log.e(LOG_TAG, "Exception while initializing audio record: ", ex);
    } catch (RuntimeException rex) {
      Log.e(LOG_TAG, "Runtime Exception while initializing audio record: ", rex);
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
