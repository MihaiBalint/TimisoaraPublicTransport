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
package ro.mihai.tpt;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetStation extends AppWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateWidget(context, appWidgetManager, appWidgetIds);
	}
	
	public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// ComponentName thisWidget = new ComponentName(context, WidgetStation.class);
		// int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for(int widgetId : appWidgetIds) {
	    	// RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.frag_times_path2);
	    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_station);
	    	addOnClickIntent(context, remoteViews);
	    	appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
	public static void addOnClickIntent(Context context, RemoteViews remoteViews) {
		/*
		Intent editIntent = null; // new Intent(context, SomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				context, 0, editIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		int layout_id = 0; // R.id.background
		remoteViews.setOnClickPendingIntent(layout_id, pendingIntent);
		*/
	}

}
