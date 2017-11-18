package vishwarajanand.seesound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class home extends AppCompatActivity {

    private AudioRecieve audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        requestRecordAudioPermission();
        audio = new AudioRecieve();
    }

    private void requestRecordAudioPermission() {
        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), requiredPermission) ==
                    PackageManager.PERMISSION_GRANTED) {
                // put your code for Version>=Marshmallow
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this,
                            "App required access to audio", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This method is called when the  permissions are given
            toast("Started listening to audio.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        audio.startMic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        audio.stopMic();
    }

    private void toast(String message){
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }
}
