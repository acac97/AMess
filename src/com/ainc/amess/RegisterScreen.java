package com.ainc.amess;

import java.io.ByteArrayOutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterScreen extends Activity {

	private AlertDialog aDialog;
	private boolean adShow;
	private EditText etUN, etPass, etCon, etOrg, etMail, etPhone;
	private CustomDBAdapter myDb;
	private Bitmap bmp;
	private String encodedImage;
	
	public static String BASE_USERS = "base_users";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        if (savedInstanceState != null) {
        	if (savedInstanceState.getBoolean("ad_show")) showDialog();
        }
        
        initActionBar();
        initComponents();
    }
    
    private void openDB() {
		myDb = new CustomDBAdapter(this);
		myDb.open();
	}
    
	private void closeDB() {
		myDb.close();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		closeDB();
	}
    
    private void initComponents() {
    	etUN = (EditText) findViewById(R.id.etRegisterUsername);
    	etPass = (EditText) findViewById(R.id.etRegisterPassword);
    	etCon = (EditText) findViewById(R.id.etRegisterConfirm);
    	etOrg = (EditText) findViewById(R.id.etRegisterOrg);
    	etMail = (EditText) findViewById(R.id.etRegisterEmail);
    	etPhone = (EditText) findViewById(R.id.etRegisterPhone);
    	
    	bmp=BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
    	byte[] b = baos.toByteArray(); 
    	encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
    	    	
    	openDB();
    }

    private void initActionBar() {
        LayoutInflater li = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View cv = li.inflate(R.layout.buttons_bar, null);
        ActionBar ab = getActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        ab.setCustomView(cv, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        cv.findViewById(R.id.actionbar_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (filledET()) showDialog(); else
            	finish();
            }
        });
        cv.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	String res = allDataValid();
            	if (!res.equals("ALL_VALID")) Toast.makeText(RegisterScreen.this, res, Toast.LENGTH_SHORT).show(); else {
            		registerUser();
            		finish();
            	}
            }
        });
    }
    
    private String allDataValid() {
    	String name, pass, con, mail;
    	name = etUN.getText().toString();
    	pass = etPass.getText().toString();
    	con = etCon.getText().toString();
    	mail = etMail.getText().toString();
    	if (pass.equals("")) return "Password empty";
    	if (!pass.equals(con)) return "Password not confirmed"; 
    	if (name.equals("")) return "Name empty";
    	if (!mail.equals("") && !mail.contains("@")) return "Invalid mail address";
    	if (myDb.existAccount(name)) return "Account name already in use";
    	return "ALL_VALID";
    }
    
    private void registerUser() {
    	String name, pass, mail, phone, org;
    	name = etUN.getText().toString();
    	pass = etPass.getText().toString();
    	mail = etMail.getText().toString();
    	phone = etPhone.getText().toString();
    	org = etOrg.getText().toString();
    	
    	myDb.insertRow(name, pass, org, phone, mail, encodedImage);
    	Intent i = new Intent();
    	i.putExtra("request_data_username", name);
    	setResult(RESULT_OK, i);
    	
    	Toast.makeText(RegisterScreen.this, "User registered", Toast.LENGTH_SHORT).show();
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("ad_show", adShow);
	}
	
	private boolean filledET() {
		if (!etUN.getText().toString().equals("")) return true;
		if (!etPass.getText().toString().equals("")) return true;
		if (!etCon.getText().toString().equals("")) return true;
		if (!etOrg.getText().toString().equals("")) return true;
		if (!etMail.getText().toString().equals("")) return true;
		if (!etPhone.getText().toString().equals("")) return true;
		return false;
	}
	
	private void showDialog() {
		AlertDialog.Builder bld = new AlertDialog.Builder(RegisterScreen.this);
    	bld.setTitle("Exit register screen?");
    	bld.setMessage("You are going to lose all changes!");
    	bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adShow = false;
				finish();
			}
		});
    	bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adShow = false;
			}
		});
    	bld.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				adShow = false;
			}
		});
    	aDialog = bld.create();
    	aDialog.show();
    	adShow = true;
	}
 
}
