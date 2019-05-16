package snetproject.com.main.map;

/**
 * Created by overm on 2018-11-20.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import snetproject.com.R;

public class MarkerManager {

    Activity activity;
    ImageView my_marker;
    View marker_root_view;

    public MarkerManager(Activity activity) {
        this.activity = activity;
    }

    public void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(activity).inflate(R.layout.layout_marker_custom, null);
        my_marker = (ImageView) marker_root_view.findViewById(R.id.img_marker_icon);
    }

    public Marker changeMyMarker(LatLng position, int imageResource, GoogleMap mMap) {
        my_marker.setImageResource(imageResource);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(activity, marker_root_view)));
        return mMap.addMarker(markerOptions);
    }

    public Marker changeOtherMarker(LatLng position, int imageResource, GoogleMap mMap) {
        my_marker.setImageResource(imageResource);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(activity, marker_root_view)));
        return mMap.addMarker(markerOptions);
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

}