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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;

@Deprecated
public class LineTimes {

	private static final int textSize = 16;
	public static View viewHeader(Context ctx, Path selectedPath, OnClickListener updateListener) {
    	LinearLayout ll = new LinearLayout(ctx);
    	
    	Button upd = new Button(ctx);
    	upd.setText(R.string.upd);
    	upd.setOnClickListener(updateListener);
    	ll.addView(upd);

    	if(selectedPath!=null) {
    		TextView t = new TextView(ctx);
			t.setTextSize(18);
			// TODO ensure that this fits nicely
			t.setText("  "+selectedPath.getLine().getName()+" ("+selectedPath.getNiceName()+")");
			ll.addView(t);
    	}
    		
    	return ll;
    }	
	
	public static List<TableRow> viewStations(Path line, Context ctx) {
		ArrayList<TableRow> rows = new ArrayList<TableRow>();
		for(Station s:line.getStationsByPath()) {
			rows.add(viewStation(ctx, s, line.getTime1(s), line.getErrs(s)));
			rows.add(viewConnections(ctx, line, s));
		}
		return rows;
	}

	private static TableRow viewStation(Context ctx, Station s, String time, String err) {
		TableRow r = new TableRow(ctx);
		r.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));		

		TextView t;
		
		t = new TextView(ctx);
		t.setTextSize(textSize);
		String label = s.getNicestNamePossible(); 
		t.setText(label.substring(0,Math.min(30, label.length())));
		r.addView(t);

		t = new TextView(ctx);
		t.setTextSize(textSize);
		t.setText("|"+time);
		t.setWidth(45);
		r.addView(t);
		
		t = new TextView(ctx);
		t.setWidth(30);
		t.setTextSize(textSize);
		String e = err;
		if(e.length()>0) {
			t.setBackgroundColor(Color.argb(200, 160, 0, 0));
			t.setTextColor(Color.argb(250, 255, 255, 255));
			if ("upd".equals(e))
				r.setBackgroundColor(Color.argb(200, 101, 55, 0));
			t.setText("  "+e+"  ");
		} else
			t.setText("");
		r.addView(t);
		return r;
	}	
	
	private static TableRow viewConnections(Context ctx, Path linePath, Station s) {
    	LinearLayout ll = new LinearLayout(ctx);
		

		ArrayList<Line> connections = new ArrayList<Line>();
		connections.addAll(s.getLines());
		connections.remove(linePath);
		 
		for(Line p : connections) {
			TextView t = new TextView(ctx);
			t.setTextSize(textSize);
			t.setWidth(30);
			t.setText(p.getName()+" ");
			ll.addView(t);
		}
		TableRow r = new TableRow(ctx);
		TableRow.LayoutParams trp = new TableRow.LayoutParams();
		trp.span = 3;
		r.addView(ll,trp);
		return r;
	}
	
	
    
    
}
