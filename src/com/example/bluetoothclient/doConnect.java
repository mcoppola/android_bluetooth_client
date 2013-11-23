package com.example.bluetoothclient;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class doConnect extends Activity{
	
	TextView textLogView;
	String logText = "";
	private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;
    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private BluetoothDevice device;
    // Well known SPP UUID (will *probably* map to
    // RFCOMM channel 1 (default) if not in use);
    // see comments in onResume().
    private UUID MY_UUID = null;
    private Point windowSize = new Point();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.do_connect);
		
		textLogView = (TextView) findViewById(R.id.textView1);
		Intent fromMain = getIntent();
		String mac = (String) fromMain.getSerializableExtra("mac");
		
		MY_UUID = new DeviceUuidFactory(getApplicationContext()).getDeviceUuid();
		
		if (D)
			logText = logText + "IN ON CREATE\n";
            textLogView.setText(logText);

	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBluetoothAdapter == null) {
	            Toast.makeText(getApplicationContext(),
	                    "Bluetooth is not available.",
	                    Toast.LENGTH_LONG).show();
	            finish();
	            return;
	    }
	
	    if (!mBluetoothAdapter.isEnabled()) {
	            Toast.makeText(getApplicationContext(),
	                    "Please enable your BT and try again.",
	                    Toast.LENGTH_LONG).show();
	            finish();
	            return;
	    }
	    
	    // get size of window to send to server
	    Display display = getWindowManager().getDefaultDisplay();
	    display.getSize(windowSize);
	    
	    ConnectThread doConnect = new ConnectThread(mac, MY_UUID);
	    Thread connectThread = new Thread(doConnect);
	    connectThread.run();
	   
	    
	    final View touchView = findViewById(R.id.whole_view);
	    touchView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				textLogView.setText(String.valueOf(event.getX()) + " x " + String.valueOf(event.getY()));
	            WriterThread doConnect = new WriterThread("{ \"x\":\""+ event.getX() + 
	            		"\", \"y\":\""+ event.getY() + "\"}");
	    	    Thread connectThread = new Thread(doConnect);
	    	    connectThread.run();
				return false;
			}
	    });
	    
	    
	}
	
	
	private class ConnectThread extends Thread {
	    private BluetoothSocket mmSocket = null;
	 
	    public ConnectThread(String mac, UUID uuid) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        Log.v(TAG, "uuid ===" + uuid);
	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac); 
	        
	        try {
                tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
            } catch (IOException e) {
                Log.e(TAG, "COULDn't GET CONNECTION", e);
            }
            mmSocket = tmp;
	     
	        if (D)
				logText = logText + "GOT LOCAL BT\n";
	            textLogView.setText(logText);
	    }
	 
	    public void run() {
	    	// Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	        if (D)
				logText = logText + "TRYING TO CONNECT\n";
	            textLogView.setText(logText);
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) {
	            	if (D)
	    				logText = logText + "CONNECTION FAILED\n";
	    	            textLogView.setText(logText);
	    	            cancel();
	            }
	            return;
	        }
	        
	        if (D)
				logText = logText + "CONNECTED TO SERVER\n";
	            textLogView.setText(logText);
	            
	        // initiate connect, send window size
	        WriteToServer(mmSocket, "{\"width\":\""+ windowSize.x + "\",\"height\":\"" + windowSize.y+ "\"}");
	        
	        // set socket to global
	        btSocket = mmSocket;
		    
		    // let user know
		    logText = "YOU ARE CONNECTED, SENDING DATA";
            textLogView.setText(logText);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	    
	}
	
	public class WriterThread extends Thread {
		private String message = "";
		private BluetoothSocket socket;
		
		public WriterThread (String message){
			this.message = message;
			this.socket = btSocket;
		}
		
		public void run(){
			WriteToServer(socket, message);
		}
		
		
	}
	
	public void WriteToServer (BluetoothSocket btSocket, String message) {
		try {
            outStream = btSocket.getOutputStream();
	    } catch (IOException e) {
	            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
	    }

	    byte[] msgBuffer = message.getBytes();
	    try {
	            outStream.write(msgBuffer);
	    } catch (IOException e) {
	            Log.e(TAG, "ON RESUME: Exception during write.", e);
	    }
	    		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
