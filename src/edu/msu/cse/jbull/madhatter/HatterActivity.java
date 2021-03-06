package edu.msu.cse.jbull.madhatter;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class HatterActivity extends Activity {

	 /**
     * Request code when selecting a picture
     */
    private static final int SELECT_PICTURE = 1;
    
    /**
     * The color of the hat
     */
    private static final int GOT_COLOR = 2;
    
    private static final String PARAMETERS = "parameters";
    
	/**
     * The hatter view object
     */
    private HatterView hatterView = null;
    
    /**
     * The color select button
     */
    private Button colorButton = null;
    
    /**
     * The feather checkbox
     */
    private CheckBox featherCheck = null;
    
    /**
     * The hat choice spinner
     */
    private Spinner spinner;
    
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            // Response from the picture selection activity
            Uri imageUri = data.getData();
            
            // We have to query the database to determine the document ID for the image
            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":")+1);
            cursor.close();

            // Next, we query the content provider to find the path for this 
            // document id. 
            cursor = getContentResolver().query( 
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            
            if(path != null) {
                Log.i("Path", path);
                hatterView.setImagePath(path);
            }
        }
		else if(requestCode == GOT_COLOR && resultCode == Activity.RESULT_OK){
			
			// Color response
			int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
			String colorString = Integer.toString(color);
			Log.i("color", colorString);
			hatterView.setColor(color);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hatter);
		
		/*
         * Get some of the views we'll keep around
         */
        hatterView = (HatterView)findViewById(R.id.hatterView);
        colorButton = (Button)findViewById(R.id.buttonColor);
        setFeatherCheck((CheckBox)findViewById(R.id.checkFeather));
        spinner = (Spinner) findViewById(R.id.spinnerHat);
        
        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
             R.array.hats_spinner, android.R.layout.simple_spinner_item);
        
        
        
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                    int pos, long id) {
            	hatterView.setHat(pos);
            	updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
            
        });
        
        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            hatterView.getFromBundle(PARAMETERS, savedInstanceState);
            
            
            spinner.setSelection(hatterView.getHat());
            updateUI();
        }
	}
	
	/**
     * Handle a Picture button press
     * @param view
     */
    public void onPicture(View view) {
    	// Get a picture from the gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    
    /**
     * Handle a Color button press
     * @param view
     */
    public void onColor(View view) {
    	// Get a new color for the hat from the color select activity
    	Intent intent = new Intent(this, ColorSelectActivity.class);
    	startActivityForResult(intent, GOT_COLOR);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		hatterView.putToBundle(PARAMETERS, outState);
	}
	
	/**
     * Handle a Feather check
     * @param view
     */
    public void onFeatherCheck(View view) {
    	// Set the value of the checkmark bool
    	if(hatterView.isDrawFeather())
    		hatterView.setDrawFeather(false);
    	else
    		hatterView.setDrawFeather(true);
    }
    
    /**
     * Ensure the user interface components match the current state
     */
    private void updateUI() {
    	if(hatterView.getHat() == 2)
    	{
    		colorButton.setEnabled(true);
    	}
    	else
    	{
    		colorButton.setEnabled(false);
    	}
    	
    	spinner.setSelection(hatterView.getHat());
    }
    
    /*
     * Both of these were created so that Eclipse would
     * stop saying that featherCheck's value was not used
     */
 
	public CheckBox getFeatherCheck() {
		return featherCheck;
	}

	public void setFeatherCheck(CheckBox featherCheck) {
		this.featherCheck = featherCheck;
	}

}
