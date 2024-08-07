package com.example.clickify.AccessibilityService;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import androidx.annotation.RequiresApi;
import android.widget.Toast;
import com.example.clickify.SessionManager.SessionManager;

public class AutoClickService extends AccessibilityService {

    private static final int NUMBER_OF_CLICKS = 50;
    private SessionManager sm;

    @Override
    public void onCreate() {
        super.onCreate();
        sm = new SessionManager(this);
        int x = sm.getXAxis();
        int y = sm.getYAxis();
        long delayMillis = 500; // Default delay for clicks

        // Display the parameters in a Toast
        Toast.makeText(this, "x=" + x + ", y=" + y + ", delayMillis=" + delayMillis, Toast.LENGTH_LONG).show();

        // Perform multiple clicks
        performMultipleClicks(x, y, delayMillis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow != null) {
                AccessibilityNodeInfo targetNode = findTargetNode(rootInActiveWindow);
                if (targetNode != null) {
                    performClick(targetNode);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Handle service interruption
    }

    private AccessibilityNodeInfo findTargetNode(AccessibilityNodeInfo root) {
        // Implement your logic to find the target node here
        // For example, you can use recursive search or specific criteria
        // Return the node that you want to click
        return null; // Replace with your implementation
    }


    private void performClick(AccessibilityNodeInfo node) {
        if (node != null && node.isEnabled() && node.isClickable())
        {
            AccessibilityAction clickAction = AccessibilityAction.ACTION_CLICK;
            node.performAction(clickAction.getId());
        }
    }

    private void performMultipleClicks(int x, int y, long delayMillis) {
        for (int i = 0; i < NUMBER_OF_CLICKS; i++) {
            final int clickIndex = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.d("AutoClickService", "Performing click " + (clickIndex + 1));
                AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                if (rootInActiveWindow != null) {
                    AccessibilityNodeInfo targetNode = findTargetNode(rootInActiveWindow);
                    if (targetNode != null) {
                        performClick(targetNode);
                    }
                }
            }, i * delayMillis);
        }
    }
}
