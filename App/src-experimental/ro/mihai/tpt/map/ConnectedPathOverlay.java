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
package ro.mihai.tpt.map;

import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;



/*
 * Copyright 2010 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Custom implementation of the ItemizedOverlay class from the google maps library.
 * 
 * @author Sebastian Schlaak
 * @author Karsten Groll
 */
public abstract class ConnectedPathOverlay extends Overlay implements Runnable {
	private Drawable defaultMarker;
	private Canvas bitmapWrapper;
	private Bitmap bitmap;
	private Bitmap shaddowBitmap;
	private Bitmap tempBitmapForSwap;
	private Drawable itemMarker;
	private Point itemPixelPositon;
	private Point itemPosOnDisplay;
	private Point displayPositonBeforeDrawing;
	private Point displayPositonAfterDrawing;
	private Matrix matrix;
	private OverlayItem item;
	private Thread thread;
	
	/**
	 * This is where the overlays are drawn on before the canvas is touched.
	 */
	protected Bitmap bmp;

	/**
	 * To ensure that the mapview is set.
	 */
	private boolean isReady = false;

	/**
	 * The reference to the mapview class.
	 */
	protected MapView mapView;

	/**
	 * The shadows x-offset. This feature is not yet implemented!
	 */
	protected static float SHADOW_X_SKEW = -0.8999999761581421f;

	/**
	 * The shadows y-offset. This feature is not yet implemented!
	 */
	protected static float SHADOW_Y_SKEW = 0.5f;	

	/**
	 * Construct an Overlay
	 * 
	 * @param defaultMarker
	 *            the default drawable for each item in the overlay.
	 * @param context
	 *            the context.
	 */
	public ConnectedPathOverlay(Drawable defaultMarker) {
		this.defaultMarker = defaultMarker;
		this.thread = new Thread(this);
		setup();
	}

	private void setup() {
		this.matrix = new Matrix();
		
		thread.start();
	}

	/**
	 * Return the numbers of items.
	 * 
	 * @return numbers of items in this overlay.
	 */
	abstract public int size();

	/**
	 * Pause the Thread.
	 * 
	 * @param pauseInSeconds
	 *            time in seconds to sleep.
	 */
	public void pause(int pauseInSeconds) {
		try {
			Thread.sleep(pauseInSeconds * 1000);
		} catch (InterruptedException e) {
			thread.interrupt();
			Logger.e(new Exception("Not Implemented"));
		}
	}

	/**
	 * Add an overlayItem to this overlay.
	 * 
	 * @param overlayItem
	 *            the new overlay item.
	 */
	public abstract void addOverLay(OverlayItem overlayItem);

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		// iterate over all overlay items
		for (int i = 0; i < size(); i++) {
			item = createItem(i);
			if (hitTest(item, item.getMarker(), (int) event.getX(), (int) event.getY())) {
				onTap(i);
				return true;
			}
		}
		return true;
	}

	/**
	 * Access and create the actual Items.
	 * 
	 * @param i
	 *            the index of the item.
	 * @return the overlay item.
	 */
	abstract protected OverlayItem createItem(int i);

	/**
	 * Adjusts a drawable of an item so that (0,0) is the center.
	 * 
	 * @param balloon
	 *            the drawable to center.
	 * @param itemPosRelative
	 *            the position of the item.
	 * @return the adjusted drawable.
	 */
	protected Drawable boundCenter(Drawable balloon, Point itemPosRelative) {
		balloon.setBounds((int) itemPosRelative.x - balloon.getIntrinsicWidth() / 2,
				(int) itemPosRelative.y - balloon.getIntrinsicHeight() / 2,
				(int) itemPosRelative.x + balloon.getIntrinsicWidth() / 2,
				(int) itemPosRelative.y + balloon.getIntrinsicHeight() / 2);
		return balloon;
	}

	/**
	 * Adjusts the drawable of an item so that (0,0) is the center of the bottom row.
	 * 
	 * @param balloon
	 *            the drawable to center.
	 * @param itemPosRelative
	 *            the position of the item.
	 * @return the adjusted drawable.
	 */
	protected Drawable boundCenterBottom(Drawable balloon, Point itemPosRelative) {
		balloon.setBounds((int) itemPosRelative.x - balloon.getIntrinsicWidth() / 2,
				(int) itemPosRelative.y - balloon.getIntrinsicHeight(), (int) itemPosRelative.x
						+ balloon.getIntrinsicWidth() / 2, (int) itemPosRelative.y);
		return balloon;
	}

	/**
	 * Handle a tap event.
	 * 
	 * @param index
	 *            the position of the item.
	 * 
	 * @return true
	 */
	abstract protected boolean onTap(int index);

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		canvas.drawBitmap(this.bitmap, this.matrix, null);
	}

	final protected void createOverlayBitmapsAndCanvas(int width, int height) {
		this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		this.shaddowBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		this.bitmapWrapper = new Canvas();
	}

	/**
	 * Calculate if a given point is within the bounds of an item.
	 * 
	 * @param item
	 *            the item to test.
	 * @param marker
	 *            the marker of the item.
	 * @param hitX
	 *            the x-coordinate of the point.
	 * @param hitY
	 *            the y-coordinate of the point.
	 * @return true if the point is within the bounds of the item.
	 */
	protected boolean hitTest(OverlayItem item, Drawable marker, int hitX, int hitY) {
		Point eventPos = new Point(hitX, hitY);
		Point itemHitPosOnDisplay = calculateItemPostionRelativeToDisplay(item.getPoint());
		Point distance = Point.substract(eventPos, itemHitPosOnDisplay);
		if (marker == null) {
			marker = this.defaultMarker;
		}
		if (Math.abs(distance.x) < marker.getIntrinsicWidth() / 2
				&& Math.abs(distance.y) < marker.getIntrinsicHeight() / 2) {
			return true;
		}
		return false;
	}

	private Point calculateItemPostionRelativeToDisplay(GeoPoint itemPostion) {
		Point itemPixelPosition = calculateItemPoint(itemPostion);
		Point displayPixelPosition = calculateDisplayPoint(new GeoPoint(this.mapView.latitude,
				this.mapView.longitude));
		Point distance = Point.substract(itemPixelPosition, displayPixelPosition);
		return distance;
	}

	protected Matrix getMatrix() {
		return this.matrix;
	}

	final protected void prepareOverlayBitmap(MapView mapView) {
		// if (!hasDisplayPostionChanged()) {
		// return;
		// }
		saveDisplayPositionBeforeDrawing();
		drawItemsOnShaddowBitmap();
		saveDisplayPositionAfterDrawing();
		swapBitmapAndCorrectMatrix(this.displayPositonBeforeDrawing,
				this.displayPositonAfterDrawing);
		notifyMapViewToRedraw();
	}

	private boolean hasDisplayPostionChanged() {
		Point currentPositon = calculateDisplayPoint(new GeoPoint(this.mapView.latitude,
				this.mapView.longitude));
		if (displayPositonAfterDrawing == null) {
			return true;
		}
		return (!this.displayPositonAfterDrawing.equals(currentPositon));
	}

	private void saveDisplayPositionBeforeDrawing() {
		this.displayPositonBeforeDrawing = calculateDisplayPoint(new GeoPoint(
				this.mapView.latitude, this.mapView.longitude));
	}

	private void saveDisplayPositionAfterDrawing() {
		this.displayPositonAfterDrawing = calculateDisplayPoint(new GeoPoint(
				this.mapView.latitude, this.mapView.longitude));
	}

	private void drawItemsOnShaddowBitmap() {
		this.shaddowBitmap.eraseColor(Color.TRANSPARENT);
		this.bitmapWrapper.setBitmap(this.shaddowBitmap);
		for (int i = 0; i < size(); i++) {
			drawItem(createItem(i));
		}
	}

	private void drawItem(OverlayItem item) {
		if (hasValidDisplayPosition(item)) {
			this.itemPixelPositon = item.posOnDisplay;
		} else {
			item.posOnDisplay = calculateItemPoint(item.getPoint());
			this.itemPixelPositon = item.posOnDisplay;
			item.zoomLevel = this.mapView.zoomLevel;
		}
		this.itemPosOnDisplay = Point.substract(this.itemPixelPositon,
				this.displayPositonBeforeDrawing);
		setCostumOrDeaultItemMarker(item);
		if (isItemOnDisplay(this.itemPosOnDisplay)) {
			boundCenter(this.itemMarker, this.itemPosOnDisplay).draw(this.bitmapWrapper);
		}
	}

	private boolean hasValidDisplayPosition(OverlayItem item) {
		boolean displayPositionValid = true;
		displayPositionValid &= (this.mapView.zoomLevel == item.zoomLevel);
		return displayPositionValid;
	}

	private void setCostumOrDeaultItemMarker(OverlayItem item) {
		if (item.getMarker() == null) {
			this.itemMarker = this.defaultMarker;
			item.setMarker(this.defaultMarker, 0);
		} else {
			this.itemMarker = item.getMarker();
		}
	}

	private void swapBitmapAndCorrectMatrix(Point displayPosBefore, Point displayPosAfter) {
		synchronized (this.matrix) {
			this.matrix.reset();
			Point diff = Point.substract(displayPosBefore, displayPosAfter);
			this.matrix.postTranslate(diff.x, diff.y);
			// swap the two MapViewBitmaps
			this.tempBitmapForSwap = this.bitmap;
			this.bitmap = this.shaddowBitmap;
			this.shaddowBitmap = this.tempBitmapForSwap;
		}
	}

	private void notifyMapViewToRedraw() {
		this.mapView.postInvalidate();
	}

	private boolean isItemOnDisplay(Point itemPos) {
		boolean isOnDisplay = true;
		isOnDisplay &= itemPos.x > 0;
		isOnDisplay &= itemPos.x < this.bitmap.getWidth();
		isOnDisplay &= itemPos.y > 0;
		isOnDisplay &= itemPos.y < this.bitmap.getHeight();
		return isOnDisplay;
	}

	private Point calculateItemPoint(GeoPoint geoPoint) {
		return new Point((float) MercatorProjection.longitudeToPixelX(geoPoint.getLongitude(),
				this.mapView.zoomLevel), (float) MercatorProjection.latitudeToPixelY(geoPoint
				.getLatitude(), this.mapView.zoomLevel));
	}

	private Point calculateDisplayPoint(GeoPoint geoPoint) {
		return new Point((float) MercatorProjection.longitudeToPixelX(geoPoint.getLongitude(),
				this.mapView.zoomLevel)
				- this.mapView.getWidth() / 2, (float) MercatorProjection.latitudeToPixelY(
				geoPoint.getLatitude(), this.mapView.zoomLevel)
				- this.mapView.getHeight() / 2);
	}
	
	/**
	 * Return true if mapview is set.
	 * 
	 * @return true if mapview is set.
	 */
	protected boolean isMapViewSet() {
		boolean ready = false;
		if (this.mapView == null)
			ready = false;
		else {
			ready = true;
		}
		return ready;
	}	
	
	public final void run() {
		while (!thread.isInterrupted()) {
			synchronized (this) {
				try {
					this.isReady = true;
					this.wait();
				} catch (InterruptedException e) {
					thread.interrupt();
				}
			}
			this.isReady = false;
			if (thread.isInterrupted()) {
				break;
			}
			prepareOverlayBitmap(this.mapView);
		}
		if (this.bmp != null)
			this.bmp.recycle();
	}
}
