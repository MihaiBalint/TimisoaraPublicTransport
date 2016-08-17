package ro.mihai.tpt;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;

import ro.mihai.tpt.utils.AndroidSharedObjects;
import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.MapKind;

/**
 * Created by Mihai Balint on 8/15/16.
 */
public class ViewMap extends CityActivity {

    @Override
    protected final void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_expanded);
        AndroidSharedObjects.instance().getMapKind().setupWebView((WebView)findViewById(R.id.map_expanded_view));
    }
}
