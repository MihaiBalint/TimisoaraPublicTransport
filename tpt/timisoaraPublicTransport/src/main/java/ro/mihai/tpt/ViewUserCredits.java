/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2012  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.tpt;

import ro.mihai.tpt.R;
import ro.mihai.tpt.utils.AppPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUserCredits extends Activity {
	private AppPreferences prefs = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_user_credits);
    	findViewById(R.id.menu_button).setVisibility(View.GONE);
		String entered_name = getAppPreferences().getAchieveUserName();
		if (entered_name.length() > 0)
			((TextView)findViewById(R.id.medal_shelf_name)).setText(entered_name);
    }
    
	protected final AppPreferences getAppPreferences() {
		if(null==prefs) {
			prefs = new AppPreferences(this);
		}
		return prefs;
	}

    public void finishActivity(View trigger) {
		finish();
	}
    
    public void changeYourName(View trigger) {
    	final EditText name = new EditText(this);

    	// Set the default text to a link of the Queen
    	name.setHint("");

    	new AlertDialog.Builder(this)
    	  .setTitle("")
    	  .setMessage(R.string.achvYourName)
    	  .setView(name)
    	  .setPositiveButton(R.string.lblAffirmative, new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int whichButton) {
    			  String entered_name = name.getText().toString();
    			  ((TextView)findViewById(R.id.medal_shelf_name)).setText(entered_name);
    			  getAppPreferences().setAchieveUserName(entered_name);
    		  }
    	  })
    	  .setNegativeButton(R.string.lblNegative, new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int whichButton) {
    		  }
    	  })
    	  .show();     	
    	
    }
}