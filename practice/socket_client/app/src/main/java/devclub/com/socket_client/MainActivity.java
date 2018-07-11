package devclub.com.socket_client;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.SocketException;


public class MainActivity extends AppCompatActivity {

    public Socket socket;
    public DataOutputStream oos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }
    public void connect(View v) {
        Log.d("Button", "connect was clicked ");
        try {

            TextView ip_textview = (TextView)findViewById(R.id.ip);
            TextView message_textview = (TextView)findViewById(R.id.message);


            String host = ip_textview.getText().toString();
            socket = new Socket(host, 12346);

            oos = new DataOutputStream(socket.getOutputStream());
            oos.writeUTF(message_textview.getText().toString());
            oos.flush();

            oos.close();
            socket.close();

        }catch (Exception e){
            Log.d("Exception", e.toString());
        }
    }

}
