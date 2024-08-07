package com.example.clickify.OverLayService;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.clickify.R;
import com.example.clickify.SessionManager.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Objects;

public class OverlayService extends Service {

    private static final String TAG = "OverlayService";
    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;
    private SessionManager sm;
    private EditText clickSpeedEdt, timeIntervalEdt, continueTimeEdt;
    private boolean isViewAttached = false;
    private OverlayPointerService overlayPointerService;
    private boolean isBound = false;


    @Override
    public void onCreate() {
        super.onCreate();
        sm = new SessionManager(this);
        setupOverlayLayout();
        setupOverlayTouchHandling();
        setupIconClickListeners();
    }



    private void setupOverlayLayout() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.over_lay_layout, null);
        LinearLayout linearOverlay = overlayView.findViewById(R.id.linearOverlayMenu);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = 1;
        params.y = 0;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(overlayView, params);
        isViewAttached = true;
    }

    private void setupOverlayTouchHandling() {
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private int offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        offsetX = (int) event.getRawX() - params.x;
                        offsetY = (int) event.getRawY() - params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = (int) (event.getRawX() - offsetX);
                        params.y = (int) (event.getRawY() - offsetY);
                        windowManager.updateViewLayout(overlayView, params);
                        break;
                }
                return true;
            }
        });
    }

    private void setupIconClickListeners() {
        ImageView startIcon = overlayView.findViewById(R.id.start_icon);
        ImageView stopIcon = overlayView.findViewById(R.id.stop_icon);
        ImageView settingsIcon = overlayView.findViewById(R.id.settings_icon);

        startIcon.setOnClickListener(v -> {
            sm.isStarted(true);
            if (isBound) {
//                overlayPointerService.simulateClickAtPointer(sm.getXAxis(), sm.getYAxis());
            }
        });

        stopIcon.setOnClickListener(v -> {
            if (isViewAttached) {
                windowManager.removeView(overlayView);
                isViewAttached = false;
            }
            stopSelf();
        });

        settingsIcon.setOnClickListener(v -> showSettingsDialog());
    }

    private void showSettingsDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(overlayView.getContext(), com.google.android.material.R.style.AlertDialog_AppCompat);

            LayoutInflater inflater = LayoutInflater.from(contextThemeWrapper);
            View settingsView = inflater.inflate(R.layout.setting_layout, null);
            intializeSettingView(settingsView);

            if (sm.getClickSpeed() != null && sm.getTimeInterval() != null && !sm.getContinueTime().isEmpty()) {
                continueTimeEdt.setText(sm.getContinueTime());
                clickSpeedEdt.setText(sm.getClickSpeed());
                timeIntervalEdt.setText(sm.getTimeInterval());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper);
            builder.setView(settingsView)
                    .setCancelable(false)
                    .setPositiveButton("Save", null)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            }

            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                clickSpeedEdt = settingsView.findViewById(R.id.editTextClickSpeed);
                timeIntervalEdt = settingsView.findViewById(R.id.time_Interval);
                continueTimeEdt = settingsView.findViewById(R.id.editTextContinueTime);

                String speed = clickSpeedEdt.getText().toString().trim();
                String time = timeIntervalEdt.getText().toString().trim();
                String continueTime = continueTimeEdt.getText().toString().trim();

                if (speed.isEmpty()) {
                    clickSpeedEdt.setError("Required!");
                } else if (time.isEmpty()) {
                    timeIntervalEdt.setError("Required");
                } else if (continueTime.isEmpty()) {
                    continueTimeEdt.setError("Required");
                } else {
                    sm.setClickSetting(speed, time, continueTime);
                    Toast.makeText(contextThemeWrapper, "Saved Setting!", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        });
    }

    private void intializeSettingView(View settingsView) {
        clickSpeedEdt = settingsView.findViewById(R.id.editTextClickSpeed);
        timeIntervalEdt = settingsView.findViewById(R.id.time_Interval);
        continueTimeEdt = settingsView.findViewById(R.id.editTextContinueTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isViewAttached && windowManager != null) {
            try {
                windowManager.removeViewImmediate(overlayView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isViewAttached = false;
        }
        if (isBound) {
            isBound = false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


