package com.example.android.location;

import java.io.File;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowImage extends Activity {
	private double lat;
	private double log;
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/mobileComputing/";
	Bitmap myBitmap;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image);
		Bundle a = this.getIntent().getExtras();
		int position = a.getInt("showImg");
		lat = a.getDouble("lat");
		log = a.getDouble("log");
		ImageView view = (ImageView) findViewById(R.id.imageView1);
		File imgFile = new File(dir + "Location" + position + ".jpg");
		myBitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));

		view.setImageBitmap(myBitmap);
		if(isOnline())
		try {
			JSONParser jsp = new JSONParser();
			Log.i("hihihi","http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+ log+"&sensor=true");
			JSONObject obj = jsp
					.getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+ log+"&sensor=true");
			if(obj.getJSONArray("results").length() > 0){
				if(obj.getJSONArray("results").getJSONObject(0).getString("formatted_address") != null){
					Log.i("hihihi", obj.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
					TextView tw = (TextView) findViewById(R.id.textView1);
					tw.setText(obj.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
				}
			}
			//Log.i("hi", obj.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
		} catch (Exception e) {
			Log.i("tag", e.getMessage());
		}
		/*
		 * ImageView view = (ImageView)findViewById(R.id.imageView1); Intent i =
		 * new Intent(); Bundle p = i.getExtras(); int position =
		 * Integer.parseInt((String) p.get("showImg"));
		 */
		/*
		 * File imgFile = new File(dir); if(imgFile.exists()){
		 * 
		 * Bitmap myBitmap =
		 * BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		 * 
		 * ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
		 * myImage.setImageBitmap(myBitmap);
		 * 
		 * }
		 */

	}

	public void showMap(View view) {
		//myBitmap.recycle();
		String uriBegin = "geo:" + lat + "," + log;
		String query = lat + "," + log + "(" + ")";
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery;
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	public void onBackPressed() {
	    // your code.
		myBitmap.recycle();
		startActivity(new Intent(this, MyListActivity.class));
	}
	
	public boolean isOnline() {
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

		if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
			Toast.makeText(getBaseContext(), "No Internet connection!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
