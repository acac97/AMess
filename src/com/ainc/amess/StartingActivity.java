package com.ainc.amess;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartingActivity extends Activity {

	private CustomDBAdapter cdba;
	private EditText etUName, etPass;
	
	private static int REQ_NEW_ACCOUNT = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.starting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_account:
                addNewAccount();
                return true;
            case R.id.action_pick_existing_account:
                pickExistingAccount();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
    	openDB();
    	
    	etUName = (EditText) findViewById(R.id.etUNLogin);
    	etPass = (EditText) findViewById(R.id.etPassLogin);
    }
    
    private void addNewAccount() {
        startActivityForResult(new Intent(this, RegisterScreen.class), REQ_NEW_ACCOUNT);
    }

	private void pickExistingAccount() {
		startActivity(new Intent(this, ExScreen.class));
    }
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_NEW_ACCOUNT) {
				etUName.setText(data.getStringExtra("request_data_username"));
			}
		}
	}
    
    @Override
	protected void onDestroy() {
		super.onDestroy();	
		closeDB();
	}

	private void openDB() {
		cdba = new CustomDBAdapter(this);
		cdba.open();
	}
	
	private void closeDB() {
		cdba.close();
	}
	
	public void loginUserAccount(View v) {
		String name, pass;
		name = etUName.getText().toString();
		pass = etPass.getText().toString();
		
		Cursor cursor = cdba.getUserAccount(name, pass);
		if (!cursor.moveToFirst()) {
			Toast.makeText(StartingActivity.this, "Username or password not valid", Toast.LENGTH_SHORT).show();
		} else {
			String s = cursor.getString(0);
			long l = cursor.getLong(1);
			//TODO start container activity
		}
	}
	
	public void exitApplication(View v) {
		finish();
	}
}
