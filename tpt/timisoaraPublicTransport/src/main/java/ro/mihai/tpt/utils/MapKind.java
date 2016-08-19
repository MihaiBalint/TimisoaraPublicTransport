/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2016  Mihai Balint

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
package ro.mihai.tpt.utils;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import ro.mihai.tpt.R;

/**
 * Created by Mihai Balint on 8/15/16.
 */
public enum MapKind {
    BUS(R.drawable.map_bus_collapsed, "map_bus.html", 35, 0, 0),
    ELECTRIC(R.drawable.map_electric_collapsed, "map_electric.html", 35, 0, 0);

    public final int collapsedId;
    private final String expanded_html;
    private final int scale;
    private final int scrollX;
    private final int scrollY;

    private MapKind(int collapsedId, String expanded_html, int scale, int scrollX, int scrollY) {
        this.collapsedId = collapsedId;
        this.expanded_html = expanded_html;
        this.scale = scale;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    private class MapWebViewClient extends WebViewClient {
        public void onPageFinished(WebView web, String url) {
            web.setInitialScale(scale);
            web.scrollTo(scrollX, scrollY);
        }
    }

    public void setupWebView(WebView web) {
        web.setWebViewClient(new MapWebViewClient());
        web.setInitialScale(scale);
        web.getSettings().setBuiltInZoomControls(true);
        web.scrollTo(scrollX, scrollY);
        web.loadUrl("file:///android_asset/" + expanded_html);
    }
}
