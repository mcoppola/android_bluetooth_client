package com.example.bluetoothclient;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
	    
	    ConnectThread doConnect = new ConnectThread(mac, MY_UUID);
	    Thread connectThread = new Thread(doConnect);
	    connectThread.run();
	    
	    
	}
	
	
	private class ConnectThread extends Thread {
	    private BluetoothSocket mmSocket = null;
	 
	    public ConnectThread(String mac, UUID uuid) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        Log.v(TAG, "uuid ===" + uuid);
	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
/*	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(uuid);
	            
	        } catch (IOException e) { }
	        mmSocket = tmp;*/
	        
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
	            }
	            return;
	        }
	        
	        if (D)
				logText = logText + "CONNECTED TO SERVER\n";
	            textLogView.setText(logText);
	 
	        // Do write
	        doWriteToSocket(mmSocket);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	    
	}
	
	public void doWriteToSocket(BluetoothSocket btSocket) {
		try {
            outStream = btSocket.getOutputStream();
	    } catch (IOException e) {
	            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
	    }
	
	    String message = "Hello message from client to server.";
	    byte[] msgBuffer = message.getBytes();
	    try {
	            outStream.write(msgBuffer);
	    } catch (IOException e) {
	            Log.e(TAG, "ON RESUME: Exception during write.", e);
	    }
	    
	    if (D)
			logText = logText + "Write Successful\n";
            textLogView.setText(logText);
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
