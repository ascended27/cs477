package hue.edu.gmu.cs477.project;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity {

    final int RC_PERM = 200;
    String[] perms = {"android.permission.RECORD_AUDIO"};
    Tarsos tarsos;

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean audioAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                tarsos = new Tarsos();
                break;
            default:
                return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    /**
     * Initializes the app when the app is first created
     */
    private void initialize() {
        requestPermissions(perms, RC_PERM);
        setContentView(R.layout.activity_main);
    }

    /**
     * Tarsos wrapper
     */
    private class Tarsos {

        AudioDispatcher dispatcher;
        PitchDetectionHandler handler;
        AudioProcessor processor;

        Tarsos() {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

            handler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                    try {
                        final float pitchInHz = result.getPitch();
                        if (pitchInHz != -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView text = (TextView) findViewById(R.id.textView1);
                                    text.setText("" + pitchInHz);
                                }
                            });
                        }
                    } catch (Exception ex) {
                    }
                }
            };

            processor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.AMDF, 22050, 1024, handler);
        }

        private void attachProcessor() {
            dispatcher.addAudioProcessor(processor);
        }

        private void detachProcessor() {
            dispatcher.removeAudioProcessor(processor);
        }

        private void dispatch() {
            new Thread(dispatcher, "Audio Dispatcher").start();
        }
    }
}
