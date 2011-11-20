/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

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
package ro.mihai.tpt.retired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.mihai.tpt.R;
import ro.mihai.tpt.R.drawable;
import ro.mihai.tpt.R.id;
import ro.mihai.tpt.R.layout;
import ro.mihai.tpt.R.string;
import ro.mihai.tpt.R.style;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;
import ro.mihai.tpt.utils.AndroidCityLoader;
import ro.mihai.util.IMonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

@Deprecated
public class DisplayTimes1 extends Activity {
	private static City city = null;
	private TableLayout tbl;
	private Path selectedPath;
	private ViewState state;
	private String err;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // this makes it completely full-screen
        // we commented it since we want partially full (system bar is visible, window bar hidden)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.loading);
        tbl = (TableLayout)findViewById(R.id.TableLayout);
        ProgressBar b = (ProgressBar)findViewById(R.id.ProgressBar);
        state = ViewState.Loading;
        new Thread(new LoadFile(b)).start();
    }
    
    @Override
    public void onBackPressed() {
		if(state.shouldBackToAndroid()) {
			super.onBackPressed();
			return;
		}
		
		showSelectLinesView();
		selectedPath = null;
    }
    
    private void update(Update u) {
        if(selectedPath==null) return; 
		int ec = 0;
		ShowLineTimes lt = new ShowLineTimes(u);
		Path sel = selectedPath;
		for(Station s:sel.getStationsByPath()) {
			if(sel!=selectedPath) break;
			sel.startUpdate(s);
			this.runOnUiThread(lt);
			ec = sel.updateStation(ec, s);
		}
		sel.updateSort();
		this.runOnUiThread(new ShowLineTimes(u)); // make sure it refreshes
    }
    
    private void showLineTimesView(Update update) {
        if(selectedPath==null) return;
        state = ViewState.SubView;
    	
    	tbl.removeAllViews();
        tbl.addView(LineTimes.viewHeader(this,selectedPath, update));
        tbl.addView(Utils.spacer(this));
    	List<TableRow> rows = LineTimes.viewStations(selectedPath,this);
    	for(TableRow r : rows)
    		tbl.addView(r);
    }
    
    private View selectLine(Line... lines) {
    	LinearLayout ll = new LinearLayout(this);
    	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	ll.setGravity(Gravity.CENTER);
    	
    	for(Line line : lines) {
        	Button ls = new Button(this);
        	ls.setText("  "+line.getName()+"  ");
        	ls.setOnClickListener(new SelectOneLine(line));
        	
        	ls.setTextAppearance(this, R.style.ButtonText);
        	ls.setBackgroundDrawable(getResources().getDrawable( 
        		(line.getName().length()>2)
        		? R.drawable.state_button_wide
        		: R.drawable.state_button
        	));
        	ls.setHeight(40); // 240x320
        	//ls.setHeight(50); // 320x480 -> 50
        	//ls.setHeight(80); // 480x800 -> 80
        	
        	ll.addView(ls);
    	}
    	
    	return ll;
    }
    
    private class ShowTramsView implements OnClickListener { public void onClick(View v) { showTramLinesView(); }}    
    private void showTramLinesView() {
        state = ViewState.SubView;
    	tbl.removeAllViews();

    	tbl.addView(selectLine(
    		city.getLine("Tv1"), 
    		city.getLine("Tv2"), 
    		city.getLine("Tv4"), 
    		city.getLine("Tv5"), 
    		city.getLine("Tv6") 
    	));
    	
    	tbl.addView(selectLine(
    		city.getLine("Tv7a"), 
    		city.getLine("Tv7b"), 
    		city.getLine("Tv8"), 
    		city.getLine("Tv9") 
    	));
    }

    private class ShowTrolleyView implements OnClickListener { public void onClick(View v) { showTrolleyLinesView(); }}    
    private void showTrolleyLinesView() {
        state = ViewState.SubView;
    	tbl.removeAllViews();
    	
    	tbl.addView(selectLine(
    		city.getLine("Tb11"), 
    		city.getLine("Tb14"), 
    		city.getLine("Tb15"), 
    		city.getLine("Tb16") 
    	));

    	tbl.addView(selectLine(
    		city.getLine("Tb17"), 
    		city.getLine("Tb18"), 
    		city.getLine("Tb19") 
    	));
    }
    
    private class ShowBusView implements OnClickListener { public void onClick(View v) { showBusLinesView(); }}    
    private void showBusLinesView() {
        state = ViewState.SubView;
    	tbl.removeAllViews();
    	((View)tbl.getParent().getParent()).setBackgroundResource(R.drawable.bus);
    	tbl.addView(selectLine(
    		city.getLine("E1"), 
    		city.getLine("E2"), 
    		city.getLine("E3"), 
    		city.getLine("E4")
    	));
    	tbl.addView(selectLine(
        	city.getLine("E5"), 
    		city.getLine("E6"), 
    		city.getLine("E7"), 
    		city.getLine("E8") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("3"), 
    		city.getLine("13"), 
    		city.getLine("21"), 
    		city.getLine("28") 
    	));
    	tbl.addView(selectLine(
        	city.getLine("32"), 
    		city.getLine("33"), 
    		city.getLine("40"), 
    		city.getLine("46") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("M24"), 
    		city.getLine("M30"), 
    		city.getLine("M35"), 
    		city.getLine("M36") 
    	));
    	
    }
    
    private View category(int labelResId, int drawResId, OnClickListener action) {
    	LinearLayout ll = new LinearLayout(this);
    	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	ll.setGravity(Gravity.CENTER);
    	
    	Button b = new Button(this);
    	b.setBackgroundDrawable(getResources().getDrawable(drawResId));
    	b.setWidth(127);
    	b.setHeight(50); // 240x320 -> 50 			40
    	//b.setHeight(63); // 320x480 -> 64(75)		50(60)
    	//b.setHeight(100); // 480x800 -> 100(125)	80(100)
    	
    	// b.setText(labelResId);
    	b.setOnClickListener(action);
    	ll.addView(b);
    	return ll;
    }
    
    private class ShowSelectLineView implements Runnable { public void run() { showSelectLinesView(); } }
    private void showSelectLinesView() {
    	state = ViewState.EntryView;
    	if(err!=null) {
    		// TODO put error back in
    		//TextView status = (TextView)findViewById(R.id.StatusText);
    		//status.setText(err);
    	}
    	tbl.removeAllViews();
    	((View)tbl.getParent().getParent()).setBackgroundResource(R.drawable.florin_bg);
    	
    	tbl.addView(selectLine(
    		city.getLine("33"), 
    		city.getLine("Tv4"), 
    		city.getLine("Tv2") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("40"), 
    		city.getLine("Tb14"), 
    		city.getLine("Tb15") 
    	));
    	tbl.addView(Utils.whiteSpace(this,10));
    	tbl.addView(category(R.string.selTrams, R.drawable.tram_button, new ShowTramsView()));
    	tbl.addView(category(R.string.selBus, 	R.drawable.bus_button, new ShowBusView()));
    	tbl.addView(category(R.string.selTrolleys, R.drawable.trolleybus_button, new ShowTrolleyView()));
    	
    	tbl.addView(Utils.copyright(this));
    }
    
    private class Update implements OnClickListener, Runnable {
    	private boolean running = false;
    	
    	public void onClick(View v) {
    		if(running) return;
    		running = true;
    		new Thread(this).start();
    	}
    	public void run() {
    		update(this);
    		running = false;
    	}
    }

    private class SelectOneLine implements OnClickListener, DialogInterface.OnClickListener {
    	private Line selectable;
    	private List<Path> pathList;
    	
    	public SelectOneLine(Line selectable) {
    		this.selectable = selectable;
    	}
    	public void onClick(View v) {
    		pathList = new ArrayList<Path>(selectable.getPaths());
    		assert( !pathList.isEmpty() );
    		
    		if(pathList.size()==1) { // only one path?
    			onClick(null, 0);
    			return;
    		}
    		
    		final CharSequence[] items = new CharSequence[pathList.size()];
    		
    		for(int i=0;i<items.length;i++) 
    			items[i] = pathList.get(i).getNiceName();

    		String title = getString(R.string.selPathLabel) + ": " +selectable.getName();
    		new AlertDialog.Builder(DisplayTimes1.this)
				.setTitle(title)
				.setItems(items, this)
				.create()
				.show();    		
    	}
    	
	    public void onClick(DialogInterface dialog, int item) {
	    	selectedPath = pathList.get(item);
    		showLineTimesView(new Update());
	    }
    	
    }
    
    private class ShowLineTimes implements Runnable {
    	private long last=0;
    	private Update u;
    	
    	public ShowLineTimes(Update u) {
    		this.u = u;
		}
    	
		public void run() {
			long crt = System.currentTimeMillis(); 
			if(crt>last && (crt-last)<500) return;
			last = crt;
			showLineTimesView(u); 
		}
	}

    
    
    private class LoadFile implements Runnable, IMonitor {
    	private ProgressBar b;
    	private int work, max=-1;
    	private long last=0;;
    	
    	public LoadFile(ProgressBar b) {
    		this.b = b;
		}
    	
		public void run() {
	        try {
	        	if (null==city)
	        		city = AndroidCityLoader.loadStoredCityOrDownloadAndCache(DisplayTimes1.this, this);
	        } catch(IOException e) {
	        	err = "Err: "+e.getMessage();
	        	city = new City();
	        	city.setStations(new ArrayList<Station>());
	        }
	        runOnUiThread(new ShowSelectLineView());
		}
		
		public void workComplete() {
			work++;
			if(work>=max) return;
			
			long crt = System.currentTimeMillis(); 
			if(crt>last && (crt-last)<500) return;
			
			runOnUiThread(new Runnable() { public void run() {
				b.setProgress(work);
			}});
		}
		
		public void setMax(int max) {
			b.setMax(this.max = max);
		}

	}
    
}