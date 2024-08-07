package com.example.clickify;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.Nullable;

import com.example.clickify.SessionManager.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    // SharedPreferences configuration for user control (optional)
    private static final String PREF_FILE_NAME = "your_pref_file";
    private static final String PREF_KEY_SERVICE_ACTIVE = "service_active";
    private static final int CLICK_DELAY = 1000; // Time between clicks in milliseconds
    private static final String TARGET_PACKAGE_NAME = "com.whatsapp"; // WhatsApp package name
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private int targetX =540; // Replace with desired X coordinate
    private int targetY = 960; // Replace with desired Y coordinate

    private Handler handler;

    @SuppressLint("StaticFieldLeak") // Handler is managed within the service
    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Start clicks only on a specific event type
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && isServiceActive()) {
            String packageName = (String) event.getPackageName();
            Log.d(TAG, "Window state changed: " + packageName);
            if (TARGET_PACKAGE_NAME.equals(packageName)) {
                // Add a delay to ensure the window is fully loaded
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performClicks(event);
                    }
                }, 2000); // Adjust delay as needed
            }
        }
    }

    private boolean isServiceActive() {
        return new SessionManager(MyAccessibilityService.this).getIsStarted();
    }

    private void performClicks(AccessibilityEvent event) {
        // Ensure accessibility service has permission to click
        if (!hasPermissionToClick()) {
            Log.w(TAG, "Accessibility service does not have permission to click. Please grant permission in settings.");
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1; i++) {
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AccessibilityNodeInfo rootNode = getAccessibilityNodeInfo();
                                Log.w(TAG, "Check Test before root node ");
                                if (rootNode != null) {
                                    Log.i(TAG, "Check Test in root node");
                                    clickAtCoordinates(rootNode, targetX, targetY);
                                } else {
                                    Log.d(TAG, "rootNode is null");
                                }
                            } catch (IllegalStateException e) {
                                Log.e(TAG, "IllegalStateException while accessing rootNode", e);
                            } catch (Exception e) {
                                Log.e(TAG, "Exception in rootNode handling", e);
                            }
                        }

                        private @Nullable AccessibilityNodeInfo getAccessibilityNodeInfo() {
                            List<AccessibilityWindowInfo> windows = getWindows();
                            AccessibilityNodeInfo rootNode = null;

                            // Prioritize the first window if available
                            if (windows != null && !windows.isEmpty()) {
                                rootNode = windows.get(0).getRoot();
                            }

                            // If rootNode is still null, try using the event source
                            if (rootNode == null && event != null) {
                                rootNode = event.getSource();
                            }
                            return rootNode;
                        }
                    }, 3000); // 100ms delay

                    try {
                        Thread.sleep(CLICK_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void clickAtCoordinates(AccessibilityNodeInfo rootNode, int x, int y) {
        Rect bounds = new Rect();
        rootNode.getBoundsInScreen(bounds);
        Log.i(TAG,"Check Test  Not null before root node  ");
        if (bounds.contains(x, y)) {
            Log.w(TAG,"Check Test  Not null bound  ");
            if (rootNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                Log.i(TAG, "Click successful at (" + x + ", " + y + ")");
            } else {
                Log.w(TAG, "Click failed at (" + x + ", " + y + ")");
            }
        } else {
            Log.d(TAG, "Target coordinates (" + x + ", " + y + ") not found on screen.");
        }
    }

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED; // Can be left here if needed for other functionalities
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        }
        setServiceInfo(info);
        Log.d(TAG, "Service connected and configured.");
    }

    @Override
    public void onInterrupt() {
        // Handle service interruption if needed (e.g., stop clicks)
        getHandler().removeCallbacksAndMessages(null);
        Log.d(TAG, "Service interrupted.");
    }

    private boolean hasPermissionToClick() {
        // Check if the service has the CAN_PERFORM_GESTURES permission
        // Replace with your actual permission check based on API level
        return true; // Placeholder for actual permission check
    }
}




//package com.example.clickify;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.graphics.Rect;
//import android.os.Build;
//import android.util.Log;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//public class MyAccessibilityService extends AccessibilityService {
//
//    private static final int CLICK_COUNT = 100;
//    private int currentClickCount = 0;
//    private int targetX = 100; // Replace with desired X coordinate
//    private int targetY = 200; // Replace with desired Y coordinate
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//            if (rootNode != null) {
//                AccessibilityNodeInfo targetNode = findNodeAtCoordinates(rootNode, targetX, targetY);
//                if (targetNode != null && currentClickCount < CLICK_COUNT) {
//                    if (targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
//                        Log.d("MyAccessibilityService", "Click performed at: (" + targetX + ", " + targetY + ")");
//                        currentClickCount++;
//                    } else {
//                        Log.e("MyAccessibilityService", "Failed to perform click at: (" + targetX + ", " + targetY + ")");
//                    }
//                }
//            } else {
//                Log.w("MyAccessibilityService", "Root node is null");
//            }
//        }
//    }
//
//    private AccessibilityNodeInfo findNodeAtCoordinates(AccessibilityNodeInfo node, int x, int y) {
//        if (node == null) {
//            return null;
//        }
//
//        Rect rect = new Rect();
//        node.getBoundsInScreen(rect);
//        if (rect.contains(x, y)) {
//            return node;
//        }
//
//        for (int i = 0; i < node.getChildCount(); i++) {
//            AccessibilityNodeInfo child = node.getChild(i);
//            if (child != null) {
//                AccessibilityNodeInfo foundNode = findNodeAtCoordinates(child, x, y);
//                if (foundNode != null) {
//                    return foundNode;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public void onServiceConnected() {
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;;
//        }
//        setServiceInfo(info);
//        Log.d("MyAccessibilityService", "Service connected and configured.");
//    }
//
//    @Override
//    public void onInterrupt() {
//        Log.d("MyAccessibilityService", "Service interrupted.");
//    }
//}
