package com.example.bluetoothclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button connectButton;
	Button mcLaptopButton;
	EditText macAddress;
	String mcLaptopMAC = "00:1F:E1:BD:A6:3A";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(connectHandler);
		
		mcLaptopButton = (Button) findViewById(R.id.buttonLaptop);
		mcLaptopButton.setOnClickListener(mcLaptopConnectHandler);
		
		macAddress = (EditText) findViewById(R.id.macAddressField);

	}

	View.OnClickListener connectHandler = new View.OnClickListener() {
		public void onClick(View v) {

			String mac = macAddress.getText().toString();
			if (mac == "") { 
				Toast.makeText(getApplicationContext(),
	                    "Please provide MAC Address.",
	                    Toast.LENGTH_LONG).show();
				return;
				}
			Intent doConnect = new Intent(getApplicationContext(), doConnect.class);
			doConnect.putExtra("mac", mac);
			//doConnect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(doConnect);
		}
	};
	
	View.OnClickListener mcLaptopConnectHandler = new View.OnClickListener() {
		public void onClick(View v) {

			Intent doConnect = new Intent(getApplicationContext(), doConnect.class);
			doConnect.putExtra("mac", mcLaptopMAC);
			//doConnect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(doConnect);
		}
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
