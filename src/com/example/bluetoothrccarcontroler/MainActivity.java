package com.example.bluetoothrccarcontroler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VerticalSeekBar;

public class MainActivity extends Activity implements OnClickListener, OnSeekBarChangeListener, SensorEventListener{

	//layout elements
	Button openButton, closeButton;
	VerticalSeekBar verticalSeekBar;
	TextView tvSpeed;
	TextView tvWheelAngle;
	
	//bluetooth
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;
	OutputStream mOutputStream;
	InputStream mInputStream;
	
	//accelerometer
	Sensor accelerometer;
	SensorManager sensorManager;
	
	boolean openedBT = false;
	
	long lastUpdate = 0;
	
	//values
	int direction = 243;
	int speed = 1;
	int angle = 90;
	int others = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        
        
        openButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        verticalSeekBar.setOnSeekBarChangeListener(this);
        
        
		  Thread t = new Thread() {
			  @Override
			  public void run() {
				  
			    try {
			      while (!isInterrupted()) {
			        Thread.sleep(50);
			        runOnUiThread(new Runnable() {
			          @Override
			          public void run() {
			        	 if(openedBT){
			        	  try {
			        		  sendData(direction, speed, angle, others);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        	 
			          }
			          }
			        });
			      }
			    } catch (InterruptedException e) {
			    }
			  }
			};
		
			t.start(); //start thread
        
        
    }
    
	protected void turnOnBT() throws IOException {
		// TODO Auto-generated method stub
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(mBluetoothAdapter == null){
    		Toast.makeText(getApplicationContext(), "No BT adapter", 0).show();
    	} 
    	else {
    		if(!mBluetoothAdapter.isEnabled()){
    			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(enableBluetooth,0);
    		} 
    		
    		if(mBluetoothAdapter.isEnabled()){
				Set<BluetoothDevice>pairedDevices = mBluetoothAdapter.getBondedDevices();
				if(pairedDevices.size() > 0){
					for(BluetoothDevice device : pairedDevices){
						if(device.getName().equals("HC-06")){
							mBluetoothDevice = device;
							break;
						}
					}
				} // end if
    		
			
				Toast.makeText(getApplicationContext(), "Device found", 0).show();
				UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
				mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid); 
				mBluetoothSocket.connect();
				mOutputStream = mBluetoothSocket.getOutputStream();
				mInputStream = mBluetoothSocket.getInputStream();
				Toast.makeText(getApplicationContext(), "BT opened", 0).show();
				openedBT = true;
    		}
    			
    		
    	}
	}

	protected void getPairedDevices() {

	}
	
	protected void sendData(int direction, int speed, int angle, int others) throws IOException{
		byte[] buffer = new byte[4];
		buffer[0]= (byte)direction;
		buffer[1]= (byte)speed;
		buffer[2]= (byte)angle;
		buffer[3]= (byte)others;
		try {
			mOutputStream.write(buffer);
			mOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void init(){
		//layout
		openButton = (Button)findViewById(R.id.btnOpen);
		closeButton = (Button) findViewById(R.id.btnClose);
    	tvSpeed = (TextView)findViewById(R.id.tvSpeed);
    	tvWheelAngle = (TextView)findViewById(R.id.tvWheelAngle);
    	verticalSeekBar = (VerticalSeekBar)findViewById(R.id.seekBar);
    	
    	//accelerometer
    	sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    	accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	
    	
    }

	protected void initBT(){
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnOpen:
			try {
				turnOnBT();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;		
		case R.id.btnClose:
			
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		if(progress < 10){
			speed = 10 - progress;
			direction = 242;
			tvSpeed.setText(String.valueOf(10-progress));
		}
		
		if(progress > 10){
			speed = progress - 10;
			direction = 241;
			tvSpeed.setText(String.valueOf(progress-10));
		}
		
		if(progress == 10){
			direction = 243;
			tvSpeed.setText(String.valueOf(0));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		//speed = seekBar.getProgress();
		//direction = 241;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		direction = 243;
		verticalSeekBar.setProgressAndThumb(10);
	}

	//accelerometer
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		long curTime = System.currentTimeMillis();
		if((curTime-lastUpdate) > 50){
			lastUpdate = curTime;
			
			tvWheelAngle.setText(String.valueOf(event.values[1]));
			if(event.values[1] > (-6.6) && event.values[1] < (6.2)) 
				angle = 90 + (int)(event.values[1] * 11);
			if(event.values[1] < (-6.6))
				angle = 17;
			if(event.values[1] > (6.2))
				angle = 158; 
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void onStart() {
        super.onStart();
        
    }



}
