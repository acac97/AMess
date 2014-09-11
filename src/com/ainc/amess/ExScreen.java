package com.ainc.amess;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ExScreen extends ListActivity {

	private CustomDBAdapter db;
	private CustomAdapter ca;
	private List<String> vals;
	private List<Bitmap> bmp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponents();
	}
	
	private void initComponents() {
		openDB();
		
		vals = new ArrayList<String>();
		bmp = new ArrayList<Bitmap>();
		
		readAccounts();
		ca = new CustomAdapter(this, vals.toArray(new String[vals.size()]), bmp.toArray(new Bitmap[bmp.size()]));
		setListAdapter(ca);
	}
	
	private void readAccounts() {
		Cursor cursor = db.getAllRows();
		if (cursor.moveToFirst()) do {
			String name = cursor.getString(CustomDBAdapter.COL_USERNAME);
			Bitmap bmim = convertToBitmap(cursor.getString(CustomDBAdapter.COL_USERIMAGE));
			
			vals.add(name);
			bmp.add(bmim);
		} while (cursor.moveToNext());
	}
	
	private Bitmap convertToBitmap(String s) {
		byte[] dcstr = Base64.decode(s, Base64.DEFAULT);
		Bitmap bmp = BitmapFactory.decodeByteArray(dcstr, 0, dcstr.length);
		return bmp;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ex_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void openDB() {
		db = new CustomDBAdapter(this);
		db.open();
	}
    
	private void closeDB() {
		db.close();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		closeDB();
	}
	
	private class CustomAdapter extends ArrayAdapter<String> {
		
		private Context context;
		private String[] values;
		private Bitmap[] bmps;
		
		public CustomAdapter(Context c, String[] li, Bitmap bm[]) {
			super(c, R.layout.list_single, li);
			context = c;
			values = li;
			bmps = bm;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View res = li.inflate(R.layout.list_single, parent, false);
			ImageView iv1 = (ImageView) res.findViewById(R.id.ivImage1);
			TextView tv2 = (TextView) res.findViewById(R.id.txt2);
			
			iv1.setImageBitmap(bmps[position]);
			tv2.setText(values[position]);
			return res;
		}
	}
}
