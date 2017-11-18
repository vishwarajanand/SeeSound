package vishwarajanand.seesound;

/**
 * Created by dabba on 15/11/17.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecieve {

    private static final String LOG_TAG = "AudioRecieve";
    private boolean mic = false; // Enable mic?

    public void stopMic() {
        Log.i(LOG_TAG, "Ending audio!");
        mic = false;
    }

    public void startMic() {
        // Creates the thread for capturing and transmitting audio
        mic = true;


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Create an instance of the AudioRecord class
                Log.i(LOG_TAG, "Send thread started. Thread id: " + Thread.currentThread().getId());


                int[] sampleRates = new int[]{8000, 11025, 16000, 22050, 44100};
                int sampleRate = 0; // Hertz
                int bufferSize = 0;
                AudioRecord audioRecorder = null;

                for (int i = 0;
                     (bufferSize <= 0 || audioRecorder == null)
                             && (i < sampleRates.length);
                     i++) {
                    // add the rates you wish to check against
                    sampleRate = sampleRates[i];
                    bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
                    audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize);
                }

                if (bufferSize <= 0 || audioRecorder == null) {
                    Log.e(LOG_TAG, "No good bufferSize is found to be supported!");
                    return;
                }

                final short[] audioData = new short[bufferSize];
                final int finalBufferSize = bufferSize;

                AudioRecord.OnRecordPositionUpdateListener positionUpdater = new AudioRecord.OnRecordPositionUpdateListener() {
                    @Override
                    public void onPeriodicNotification(AudioRecord recorder) {
                        Date d = new Date();
                        //it should be every 1 second, but it is actually, "about every 1 second"
                        //like 1073, 919, 1001, 1185, 1204 milliseconds of time.
                        Log.d(LOG_TAG, "periodic notification " + d.toLocaleString() + " mili " + d.getTime());
                        recorder.read(audioData, 0, finalBufferSize);
                    }

                    @Override
                    public void onMarkerReached(AudioRecord recorder) {
                        Log.d(LOG_TAG, "Marker reached");

                        double rms = 0;

                        //do something amazing with audio data
                        for (int i = 0; i < audioData.length; i++) {
                            rms += audioData[i]*audioData[i];
                        }
                        rms /= audioData.length*1.0;
                        rms = Math.sqrt(rms);
                        Log.i(LOG_TAG, "Updater: " + rms);
                    }
                };

                audioRecorder.setRecordPositionUpdateListener(positionUpdater);

                Log.d(LOG_TAG, "Start recording, bufferSize: " + bufferSize);
                if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Recorder could not be initialized");
                    return;
                }

                audioRecorder.startRecording();
                // have a read loop otherwise the listener won't trigger
                while (mic) {
                    Log.v(LOG_TAG, "Audio thread running. Thread id: " + Thread.currentThread().getId());
                    audioRecorder.setNotificationMarkerPosition(1000);
                    audioRecorder.read(audioData, 0, bufferSize);
                }

                audioRecorder.release();
            }
        });
        thread.start();
    }
}
