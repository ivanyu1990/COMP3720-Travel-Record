package com.example.android.location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import com.example.android.location.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toast.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class CameraActivity extends FragmentActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	int TAKE_PHOTO_CODE = 0;
	public static int count = 0;
	private Connection con;
	private LocationRequest mLocationRequest;
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/mobileComputing/";
	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;
	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity);
		if(count== 0)
		readFile();
		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Note that location updates are off until the user turns them on
		mUpdatesRequested = false;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);

		mLocationClient.connect();
		// here,we are making a folder named picFolder to store pics taken by
		// the camera using this application
		File newdir = new File(dir);
		newdir.mkdirs();

		Button capture = (Button) findViewById(R.id.btnCapture);
		capture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// here,counter will be incremented each time,and the picture
				// taken by camera will be stored as 1.jpg,2.jpg and likewise.
				String file = dir + "Location" + count + ".jpg";
				File newfile = new File(file);

				Uri outputFileUri = Uri.fromFile(newfile);

				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

				startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
			Log.d("CameraDemo", "Pic saved");
			//
			count++;
			File s = new File(dir + "data.txt");
			if (servicesConnected()) {

				// Get the current location
				try {
					mLocationClient.requestLocationUpdates(mLocationRequest,
							this);

					Location currentLocation = mLocationClient
							.getLastLocation();
					File text = new File(dir + "data.txt");
					FileOutputStream out = new FileOutputStream(text, true);
					out.write((LocationUtils.getLatLng(this, currentLocation) + "\r\n")
							.getBytes());
					out.close();
					Log.i("as1234",
							"uhi"
									+ LocationUtils.getLatLng(this,
											currentLocation));

					//mLocationClient.removeLocationUpdates(this);
				} catch (Exception e) {
					Log.i("errorfound",
							"uhi" + e.getLocalizedMessage() + e.getMessage());
				}
			}
		}
	}

	public void history(View view) {
		Log.i("as1234", "uhi007");
		try {
			Intent cameraIntent = new Intent(this, MyListActivity.class);
			startActivity(cameraIntent);
		} catch (Exception e) {
			Log.i("a", "hell" + e.getLocalizedMessage());
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		// mConnectionStatus.setText(R.string.connected);
		Log.d("connceted", "cconn");
		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// mConnectionStatus.setText(R.string.disconnected);
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

		}
	}

	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {

		// Report to the UI that the location was updated
		// mConnectionStatus.setText(R.string.location_updated);

		// In the UI, set the latitude and longitude to the value received
		// mLatLng.setText(LocationUtils.getLatLng(this, location));
	}

	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		// mConnectionState.setText(R.string.location_requested);
	}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
			}
			return false;
		}
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

	private void readFile() {
		try {
			File a = new File(dir);
			if (a != null) {
				File listDir[] = a.listFiles();
				Log.i("h", "fail" + dir);
				for (int i = 0; i < listDir.length; i++) {
					if (listDir[i] != null && listDir[i].isFile()
							&& !listDir[i].isDirectory()
							&& listDir[i].getName().contains("jpg")) {
						Log.i("haha", "" + listDir[i].getName());
						count++;
					}
				}
			}
		} catch (Exception e) {

		}
	}
	
	public void onBackPressed() {
	}
}