package com.example.clickify.OverLayService;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.clickify.R;
import com.example.clickify.SessionManager.SessionManager;

public class OverlayPointerService extends Service {

    private WindowManager windowManager;
    private View pointerView;
    private WindowManager.LayoutParams params;
    private int screenWidth;
    private int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a window manager instance
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        pointerView = new View(this);

        // Set drawable resource to the ImageView
        pointerView.setBackgroundResource(R.drawable.pointer_svg_repo);

        // Calculate pixel size from dp to pixels
        float scale = getResources().getDisplayMetrics().density;
        int sizeInDp = 70;  // Assuming the XML layout's width and height are 70dp
        int sizeInPixels = (int) (sizeInDp * scale + 0.5f);

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Set up layout params
        params = new WindowManager.LayoutParams(
                sizeInPixels,
                sizeInPixels,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        // Default position at center
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 300;
        params.y = 300;

        // Add the view to the window manager
        windowManager.addView(pointerView, params);

        pointerView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Remember the initial position and touch point
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Calculate new position
                        int newX = initialX + (int) (event.getRawX() - initialTouchX);
                        int newY = initialY + (int) (event.getRawY() - initialTouchY);

                        // Ensure new position is within screen bounds
                        newX = Math.max(0, newX); // Ensure X is non-negative
                        newY = Math.max(0, newY); // Ensure Y is non-negative
                        newX = Math.min(screenWidth - pointerView.getWidth(), newX); // Ensure X is within screen width
                        newY = Math.min(screenHeight - pointerView.getHeight(), newY); // Ensure Y is within screen height

                        // Update view position
                        params.x = newX;
                        params.y = newY;
                        windowManager.updateViewLayout(pointerView, params);

                        Log.d("AutoClickService","X axis : "+newX+"Y Axis : "+newY);
                        // Send the updated coordinates to OverlayService
                        savePointerCoordinates(newX, newY);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void savePointerCoordinates(int x, int y) {
        new SessionManager(OverlayPointerService.this).savePointerCoordinates(x,y);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pointerView != null && windowManager != null) {
            windowManager.removeView(pointerView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
