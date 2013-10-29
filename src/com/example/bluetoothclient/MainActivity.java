package com.example.bluetoothclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	Button connectButton;
	EditText macAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(connectHandler);
		
		macAddress = (EditText) findViewById(R.id.macAddressField);

	}

	View.OnClickListener connectHandler = new View.OnClickListener() {
		public void onClick(View v) {

			String mac = macAddress.getText().toString();
			if (mac == "") {mac = "XX:XX:XX:XX:XX:XX";}
			Intent doConnect = new Intent(MainActivity.this, doConnect.class);
			doConnect.setPackage("com.google.android.youtube");
			doConnect.putExtra("mac", mac);
			doConnect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
