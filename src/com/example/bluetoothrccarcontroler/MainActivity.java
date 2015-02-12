package com.example.bluetoothrccarcontroler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayAdapter<String> listAdapter;
	Button connectNew;
	ListView listView;
	BluetoothAdapter btAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if(btAdapter == null){
        	Toast.makeText(getApplicationContext(), "No bluetooth detect", 0).show();
        	//finish(); //finish activity
        } else {
        	if(!btAdapter.isEnabled()){
        		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(intent, 1);
        	}
        }
    }
    
    private void init(){
    	connectNew = (Button)findViewById(R.id.bConnectNew);
    	listView=(ListView)findViewById(R.id.listView);
    	listAdapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,0);
    	listView.setAdapter(listAdapter);
    	btAdapter = BluetoothAdapter.getDefaultAdapter();		
    			
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_CANCELED){
    		Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
    		//finish();
    	}
    }
}
