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

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

@Deprecated
public class Utils {

	public static View spacer(Context ctx) {
		View spacer = new View(ctx);
		spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 2));		
		spacer.setBackgroundColor(Color.argb(200, 226, 226, 226));
		return spacer;
	}

	public static View whiteSpace(Context ctx, int height) {
		View spacer = new View(ctx);
		spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));		
		return spacer;
	}

	public static View copyright(Context ctx) {
		TextView copy = new TextView(ctx);
		copy.setText("(c) 2011 Mihai Balint, Kriszti Cseh,\n Portan Cosmin, Florin B.");
		copy.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		copy.setGravity(Gravity.CENTER);
		
		return copy;
	}

}
