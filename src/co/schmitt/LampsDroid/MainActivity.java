package co.schmitt.LampsDroid;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private static final String TAG = "co.schmitt.lampsdroid.MainActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button button1 = (Button) findViewById(R.id.lamp_button_1);
        Button button2 = (Button) findViewById(R.id.lamp_button_2);
        Button button3 = (Button) findViewById(R.id.lamp_button_3);
        Button button4 = (Button) findViewById(R.id.lamp_button_4);
        Button buttonAll = (Button) findViewById(R.id.lamp_button_all);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state(1))
                    lightLamp(1);
                else
                    turnOffLamp(1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state(2))
                    lightLamp(2);
                else
                    turnOffLamp(2);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state(3))
                    lightLamp(3);
                else
                    turnOffLamp(3);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state(4))
                    lightLamp(4);
                else
                    turnOffLamp(4);
            }
        });

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state(0))
                    turnOffLamps();
                else
                    lightAllLamps();
            }
        });
    }

//    private View.OnClickListener() clickListener {
//
//    }

    private boolean state(int lampId) {
        String stateKey = getString(R.string.state) + lampId;
//        if (lampId > 0 && lampId < 5) {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(stateKey, false);
//        }
//        return false;
    }

    private void lightLamp(int lampId) {
        String stateKey = getString(R.string.state) + lampId;
        if (lampId > 0 && lampId < 5) {
            try {
                Process process = Runtime.getRuntime().exec("/system/bin/ssh -i /extSdCard/id_rsa.1 pschmitt@192.168.1.3 sudo /home/pschmitt/bin/lamps " + lampId + " on");
            } catch (IOException e) {
                e.printStackTrace();
            }
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(stateKey, true).commit();
        }
    }

    private void turnOffLamp(int lampId) {
        String stateKey = getString(R.string.state) + lampId;
        if (lampId > 0 && lampId < 5) {
            try {
                Process process = Runtime.getRuntime().exec("/system/bin/ssh -i /extSdCard/id_rsa.1 pschmitt@192.168.1.3 sudo /home/pschmitt/bin/lamps " + lampId + " off");
            } catch (IOException e) {
                e.printStackTrace();
            }
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(stateKey, false).commit();
        }
    }

    /*private void lightAllLamps() {
        try {
            String stateKey = getString(R.string.state) + "0";
            JSch jsch = new JSch();
            Session session = jsch.getSession("pschmitt", "192.168.1.3", 22);
            String cmd = "/system/bin/ssh -i /extSdCard/id_rsa.1 pschmitt@192.168.1.3 sudo /home/pschmitt/bin/lamps on";
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(cmd);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();

            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(stateKey, true).commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }*/

    private void lightAllLamps() {
        String stateKey = getString(R.string.state) + "0";
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ssh -i /extSdCard/id_rsa.1 pschmitt@192.168.1.3 sudo /home/pschmitt/bin/lamps on");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();
            Log.d(TAG, output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(stateKey, true).commit();
    }

    private void turnOffLamps() {
        String stateKey = getString(R.string.state) + "0";
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ssh -i /extSdCard/id_rsa.1 pschmitt@192.168.1.3 sudo /home/pschmitt/bin/lamps off");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(stateKey, false).commit();
    }
}
