package com.example.android.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MyListActivity extends ListActivity {
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/mobileComputing/";
	private ArrayList<Double> longi = new ArrayList<Double>();
	private ArrayList<Double> lati = new ArrayList<Double>();
	private ArrayList<String> values = new ArrayList<String>();
	private JSONObject jObject = null;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// String[] values = new String[] {} ;
		readFile();
		readDataFile();

		String[] val = new String[values.size()];
		int i = 0;
		for (String s : values) {
			val[i] = s;
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, val);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		String label = "";
		/*
		String uriBegin = "geo:" + lati.get(position) + ","
				+ longi.get(position);
		String query = lati.get(position) + "," + longi.get(position) + "("
				+ label + ")";
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery;
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);
		*/
		Intent intent = new Intent(this, ShowImage.class);
		intent.putExtra("showImg", position);
		intent.putExtra("lat", lati.get(position));
		intent.putExtra("log", longi.get(position));
		startActivity(intent);
	}

	private void readDataFile() {
		File data = new File(dir + "data.txt");
		if (data != null) {
			try {
				Scanner scanner = new Scanner(data);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();

					String[] log = line.split(",");
					lati.add(Double.parseDouble(log[0]));
					longi.add(Double.parseDouble(log[1]));
					Log.i("heha", log[0] + " " + log[1]);
					// System.out.println(line);
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readFile() {

		File a = new File(dir);
		if (a != null) {
			File listDir[] = a.listFiles();
			Log.i("h", "fail" + dir);
			for (int i = 0; i < listDir.length; i++) {
				if (listDir[i] != null && listDir[i].isFile()
						&& !listDir[i].isDirectory()
						&& listDir[i].getName().contains("jpg")) {
					Log.i("haha", "" + listDir[i].getName());
					values.add(listDir[i].getName());
				}
			}
		}
	}
	public void onBackPressed() {
		startActivity(new Intent(this, CameraActivity.class));
	}

}
