package ro.mihai.tpt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.mihai.tpt.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayTimes extends Activity {
	private static City city = null;
	private TableLayout tbl;
	private Line selectedLine;
	private String err;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tbl = (TableLayout)findViewById(R.id.TableLayout);
        
        ProgressBar b = (ProgressBar)findViewById(R.id.ProgressBar);
        
        
        new Thread(new LoadFile(b)).start();
    }
    
    private View spacer() {
		View spacer = new View(this);
		spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 2));		
		spacer.setBackgroundColor(Color.argb(200, 226, 226, 226));
		return spacer;
	}
    
    private View selectLine(Line... lines) {
    	LinearLayout ll = new LinearLayout(this);
    	
    	for(Line line : lines) {
        	Button ls = new Button(this);
        	ls.setText("  "+line.getName()+"  ");
        	ls.setOnClickListener(new SelectOneLine(line));
        	ll.addView(ls);
    	}
    	
    	return ll;
    }
    
    private View buttons() {
    	LinearLayout ll = new LinearLayout(this);
    	
    	Button upd = new Button(this);
    	upd.setText(R.string.upd);
    	upd.setOnClickListener(new Update());
    	ll.addView(upd);

    	Button sel = new Button(this);
    	sel.setText(R.string.sel);
    	sel.setOnClickListener(new SelectLines());
    	ll.addView(sel);
    	
    	if(selectedLine!=null) {
    		TextView t = new TextView(this);
			t.setTextSize(18);
			t.setText("  "+selectedLine.getName());
			ll.addView(t);
    	}
    		
    	return ll;
    }
    
    private void update() {
        if(selectedLine==null) return; 
		int ec = 0;
		ShowLineTimes lt = new ShowLineTimes();
		Line sel = selectedLine;
		for(Station s:sel.getSortedStations()) {
			if(sel!=selectedLine) break;
			sel.startUpdate(s);
			this.runOnUiThread(lt);
			ec = sel.updateStation(ec, s);
		}
		sel.updateSort();
		this.runOnUiThread(new ShowLineTimes()); // make sure it refreshes
    }
    
    private void showLineTimesView() {
    	tbl.removeAllViews();
        tbl.addView(buttons());
        tbl.addView(spacer());
        if(selectedLine!=null) {
        	List<TableRow> rows = selectedLine.timeView(this);
        	for(TableRow r : rows)
        		tbl.addView(r);
        }
    }
    
    
    private void showSelectLinesView() {
    	if(err!=null) {
    		TextView status = (TextView)findViewById(R.id.StatusText);
    		status.setText(err);
    	}
    	tbl.removeAllViews();
    	tbl.addView(selectLine(
    		city.getLine("33"), 
    		city.getLine("E1"), 
    		city.getLine("E5"), 
    		city.getLine("E7"), 
    		city.getLine("E8") 
    	));
    	tbl.addView(selectLine(
        	city.getLine("Tb14"), 
    		city.getLine("Tb15"), 
    		city.getLine("Tb16"), 
    		city.getLine("Tb19") 
    	));
    	tbl.addView(selectLine(
        	city.getLine("Tb11"), 
    		city.getLine("Tb17"), 
    		city.getLine("Tb18") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("Tv7a"), 
    		city.getLine("Tv7b"), 
    		city.getLine("Tv9"), 
    		city.getLine("Tv9b") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("Tv1"), 
    		city.getLine("Tv2"), 
    		city.getLine("Tv3b"), 
    		city.getLine("Tv4"), 
    		city.getLine("Tv5") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("Tv6"), 
    		city.getLine("Tv8"), 
    		city.getLine("5"), 
    		city.getLine("3"), 
    		city.getLine("3a") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("E2"), 
    		city.getLine("E3"), 
    		city.getLine("E4"), 
    		city.getLine("E6"), 
    		city.getLine("E7b") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("M22"), 
    		city.getLine("M22a"), 
    		city.getLine("M22b"), 
    		city.getLine("M27") 
    	));
        tbl.addView(selectLine(
    		city.getLine("M30"), 
    		city.getLine("M35"), 
    		city.getLine("M36")
    	));
    	tbl.addView(selectLine(
        	city.getLine("13"), 
    		city.getLine("21"), 
    		city.getLine("26"), 
    		city.getLine("26a"), 
    		city.getLine("26b") 
    	));
    	tbl.addView(selectLine(
    		city.getLine("28"),
            city.getLine("32"), 
            city.getLine("32a"), 
        	city.getLine("40") 
    	));
        tbl.addView(selectLine(
    		city.getLine("44"), 
    		city.getLine("44a"), 
    		city.getLine("46") 
    	));
    }
    
    private class Update implements OnClickListener, Runnable {
    	private boolean running = false;
    	
    	public void onClick(View v) {
    		if(running) return;
    		running = true;
    		new Thread(this).start();
    	}
    	public void run() {
    		update();
    		running = false;
    	}
    }
    private class SelectLines implements OnClickListener {
    	public void onClick(View v) {
    		showSelectLinesView();
    	}
    }
    private class SelectOneLine implements OnClickListener {
    	private Line selectable;
    	public SelectOneLine(Line selectable) {
    		this.selectable = selectable;
    	}
    	public void onClick(View v) {
    		selectedLine = selectable;
    		showLineTimesView();
    	}
    }
    
    private class ShowLineTimes implements Runnable {
    	private long last=0;;
		public void run() {
			long crt = System.currentTimeMillis(); 
			if(crt>last && (crt-last)<500) return;
			last = crt;
			showLineTimesView(); 
		}
	}

    private class ShowSelectLineView implements Runnable {
		public void run() { showSelectLinesView(); }
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
	        		city = RATT.loadStoredCityOrDownloadAndCache(DisplayTimes.this, this);
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