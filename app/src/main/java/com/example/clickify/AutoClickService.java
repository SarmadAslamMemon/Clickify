package com.example.clickify;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.clickify.SessionManager.SessionManager;

public class AutoClickService extends AccessibilityService {

    private static final String TAG = "AutoClickService";
    private SessionManager sm;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        sm = new SessionManager(this);
        boolean shouldClick = sm.getIsStarted();

      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              Log.w(TAG,"In Handler ");
              int x = sm.getXAxis(); // Assuming method to get X coordinate
              int y = sm.getYAxis(); // Assuming method to get Y coordinate
              simulateClicks(x, y, 3); // Simulate 10 clicks

 //             simulateTouchEvent(x,y);
          }
      },10000);




    }
//    private void simulateTouchEvent(int x, int y) {
//        new Thread(() -> {
//            try {
//                Instrumentation inst = new Instrumentation();
//                long downTime = SystemClock.uptimeMillis();
//                long eventTime = SystemClock.uptimeMillis();
//
//                // Log the coordinates and action
//                Log.d(TAG, "Simulating touch event at: (" + x + ", " + y + ")");
//
//                // Send down event
//                inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0));
//                Log.d(TAG, "Sent ACTION_DOWN at: (" + x + ", " + y + ")");
//
//                // Send up event
//                inst.sendPointerSync(MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0));
//                Log.d(TAG, "Sent ACTION_UP at: (" + x + ", " + y + ")");
//            } catch (Exception e) {
//                Log.e(TAG, "Error while simulating touch event", e);
//            }
//        }).start();
//    }


    private void simulateClicks(final int x, final int y, final int clickCount) {
        for (int i = 0; i < clickCount; i++) {
            Path clickPath = new Path();
            clickPath.moveTo(x, y);

            GestureDescription.StrokeDescription clickStroke = new GestureDescription.StrokeDescription(clickPath,
                    0, 50);
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(clickStroke);
            GestureDescription gesture = gestureBuilder.build();

            dispatchGesture(gesture, new GestureResultCallback() {
                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    Log.w(TAG,"Jarvis cancel");
                }

                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    Log.w(TAG,"Jarvis pass");
                }
            },null);

        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
        // Handle service interruption if needed
    }

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        // Setting necessary flags
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;


        setServiceInfo(info); // Apply the configuration

        Log.d(TAG, "Service connected and configured.");
    }

}















//package com.example.clickify;
//
//import android.accessibilityservice.AccessibilityService;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import com.example.clickify.SessionManager.SessionManager;
//
//public class AutoClickService extends AccessibilityService {
//
//    private static final String TAG = "AutoClickService";
//    private boolean shouldClick = false;
//    private int clickCount = 0;
//    private int pointerViewId = 234234; // Replace with actual resource ID
//
//    private Handler handler = new Handler(Looper.getMainLooper());
//    private Runnable clickRunnable;
//    private SessionManager sm;
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        sm = new SessionManager(this);
//        shouldClick = sm.getIsStarted();
//        Log.d(TAG, "onAccessibilityEvent: shouldClick = " + shouldClick);
//        if (shouldClick && clickCount < 50) {
//            // Find the pointer view
//            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//            Log.d(TAG, "Root node retrieved: " + rootNode);
//            if (rootNode != null) {
//                AccessibilityNodeInfo pointerViewNode = findPointerView(rootNode);
//                Log.d(TAG, "Pointer view node found: " + pointerViewNode);
//
//                if (pointerViewNode != null) {
//                    startClicking();
//                }
//            }
//        }
//    }
//
//    private AccessibilityNodeInfo findPointerView(AccessibilityNodeInfo rootNode) {
//        if (rootNode == null) {
//            Log.d(TAG, "Root node is null");
//            return null;
//        }
//
//        // Check if the root node is the target view based on window type and criteria
//        if (rootNode.getWindow() != null) {
//            int windowType = rootNode.getWindow().getType();
//            if (windowType == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
//                if (matchesTargetCriteria(rootNode)) {
//                    Log.e(TAG,"Foundddd");
//                    return rootNode;
//                }
//            }
//        }
//
//        // Recursively search for the pointer view in child nodes
//        for (int i = 0; i < rootNode.getChildCount(); i++) {
//            AccessibilityNodeInfo child = rootNode.getChild(i);
//            Log.d(TAG, "Checking child node: " + i);
//            AccessibilityNodeInfo foundView = findPointerView(child);
//            if (foundView != null) {
//                return foundView;
//            }
//        }
//        Log.d(TAG, "Pointer view node not found");
//        return null;
//    }
//
//    private boolean matchesTargetCriteria(AccessibilityNodeInfo node) {
//        // Assuming `pointerViewId` is an ID of a view in the layout
//        return node.getViewIdResourceName() != null && node.getViewIdResourceName().equals(getResources().getResourceName(R.id.pointerViewId));
//    }
//
//    private void startClicking() {
//        Log.d(TAG, "Starting clicking process");
//        clickRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (clickCount < 50) {
//                    Log.d(TAG, "Clicking: " + clickCount);
//                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK); // Simulate a click
//                    clickCount++;
//                    handler.postDelayed(this, 1000); // Delay next click
//                } else {
//                    Log.d(TAG, "Clicking finished after 50 clicks");
//                }
//            }
//        };
//        handler.postDelayed(clickRunnable, 0);
//    }
//
//    @Override
//    public void onInterrupt() {
//        Log.d(TAG, "Service interrupted");
//        // Handle interruptions
//        if (clickRunnable != null) {
//            handler.removeCallbacks(clickRunnable);
//            Log.d(TAG, "Click runnable callbacks removed");
//        }
//    }
//}
//
//
//
//
////
////
////
////import android.accessibilityservice.AccessibilityService;
////import android.accessibilityservice.AccessibilityServiceInfo;
////import android.annotation.SuppressLint;
////import android.graphics.Rect;
////import android.os.Build;
////import android.os.Handler;
////import android.os.Looper;
////import android.util.Log;
////import android.view.accessibility.AccessibilityEvent;
////import android.view.accessibility.AccessibilityNodeInfo;
////import android.view.accessibility.AccessibilityWindowInfo;
////
////import androidx.annotation.Nullable;
////
////import com.example.clickify.SessionManager.SessionManager;
////
////import java.util.List;
////import java.util.concurrent.ExecutorService;
////import java.util.concurrent.Executors;
////
////public class MyAccessibilityService extends AccessibilityService {
////
////    private static final String TAG = "MyAccessibilityService";
////
////    // SharedPreferences configuration for user control (optional)
////    private static final String PREF_FILE_NAME = "your_pref_file";
////    private static final String PREF_KEY_SERVICE_ACTIVE = "service_active";
////    private static final int CLICK_DELAY = 1000; // Time between clicks in milliseconds
////    private static final String TARGET_PACKAGE_NAME = "com.whatsapp"; // WhatsApp package name
////    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
////
////    private int targetX =540; // Replace with desired X coordinate
////    private int targetY = 960; // Replace with desired Y coordinate
////
////    private Handler handler;
////
////    @SuppressLint("StaticFieldLeak") // Handler is managed within the service
////    private Handler getHandler() {
////        if (handler == null) {
////            handler = new Handler(Looper.getMainLooper());
////        }
////        return handler;
////    }
////
////    @Override
////    public void onAccessibilityEvent(AccessibilityEvent event) {
////        // Start clicks only on a specific event type
////        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && isServiceActive()) {
////            String packageName = (String) event.getPackageName();
////            Log.d(TAG, "Window state changed: " + packageName);
////            if (TARGET_PACKAGE_NAME.equals(packageName)) {
////                // Add a delay to ensure the window is fully loaded
////                getHandler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        performClicks(event);
////                    }
////                }, 2000); // Adjust delay as needed
////            }
////        }
////    }
////
////    private boolean isServiceActive() {
////        return new SessionManager(MyAccessibilityService.this).getIsStarted();
////    }
////
////    private void performClicks(AccessibilityEvent event) {
////        // Ensure accessibility service has permission to click
////        if (!hasPermissionToClick()) {
////            Log.w(TAG, "Accessibility service does not have permission to click. Please grant permission in settings.");
////            return;
////        }
////
////        executorService.execute(new Runnable() {
////            @Override
////            public void run() {
////                for (int i = 0; i < 1; i++) {
////                    new Handler(getMainLooper()).postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            try {
////                                AccessibilityNodeInfo rootNode = getAccessibilityNodeInfo();
////                                Log.w(TAG, "Check Test before root node ");
////                                if (rootNode != null) {
////                                    Log.i(TAG, "Check Test in root node");
////                                    clickAtCoordinates(rootNode, targetX, targetY);
////                                } else {
////                                    Log.d(TAG, "rootNode is null");
////                                }
////                            } catch (IllegalStateException e) {
////                                Log.e(TAG, "IllegalStateException while accessing rootNode", e);
////                            } catch (Exception e) {
////                                Log.e(TAG, "Exception in rootNode handling", e);
////                            }
////                        }
////
////                        private @Nullable AccessibilityNodeInfo getAccessibilityNodeInfo() {
////                            List<AccessibilityWindowInfo> windows = getWindows();
////                            AccessibilityNodeInfo rootNode = null;
////
////                            // Prioritize the first window if available
////                            if (windows != null && !windows.isEmpty()) {
////                                rootNode = windows.get(0).getRoot();
////                            }
////
////                            // If rootNode is still null, try using the event source
////                            if (rootNode == null && event != null) {
////                                rootNode = event.getSource();
////                            }
////                            return rootNode;
////                        }
////                    }, 3000); // 100ms delay
////
////                    try {
////                        Thread.sleep(CLICK_DELAY);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        });
////
////    }
////
////    private void clickAtCoordinates(AccessibilityNodeInfo rootNode, int x, int y) {
////        Rect bounds = new Rect();
////        rootNode.getBoundsInScreen(bounds);
////        Log.i(TAG,"Check Test  Not null before root node  ");
////        if (bounds.contains(x, y)) {
////            Log.w(TAG,"Check Test  Not null bound  ");
////            if (rootNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
////                Log.i(TAG, "Click successful at (" + x + ", " + y + ")");
////            } else {
////                Log.w(TAG, "Click failed at (" + x + ", " + y + ")");
////            }
////        } else {
////            Log.d(TAG, "Target coordinates (" + x + ", " + y + ") not found on screen.");
////        }
////    }
////
////    @Override
////    public void onServiceConnected() {
////        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
////        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED; // Can be left here if needed for other functionalities
////        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
////        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
////        }
////        setServiceInfo(info);
////        Log.d(TAG, "Service connected and configured.");
////    }
////
////    @Override
////    public void onInterrupt() {
////        // Handle service interruption if needed (e.g., stop clicks)
////        getHandler().removeCallbacksAndMessages(null);
////        Log.d(TAG, "Service interrupted.");
////    }
////
////    private boolean hasPermissionToClick() {
////        // Check if the service has the CAN_PERFORM_GESTURES permission
////        // Replace with your actual permission check based on API level
////        return true; // Placeholder for actual permission check
////    }
////}
////
//
//
//
////package com.example.clickify;
////
////import android.accessibilityservice.AccessibilityService;
////import android.accessibilityservice.AccessibilityServiceInfo;
////import android.graphics.Rect;
////import android.os.Build;
////import android.util.Log;
////import android.view.accessibility.AccessibilityEvent;
////import android.view.accessibility.AccessibilityNodeInfo;
////
////public class MyAccessibilityService extends AccessibilityService {
////
////    private static final int CLICK_COUNT = 100;
////    private int currentClickCount = 0;
////    private int targetX = 100; // Replace with desired X coordinate
////    private int targetY = 200; // Replace with desired Y coordinate
////
////    @Override
////    public void onAccessibilityEvent(AccessibilityEvent event) {
////        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
////            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
////            if (rootNode != null) {
////                AccessibilityNodeInfo targetNode = findNodeAtCoordinates(rootNode, targetX, targetY);
////                if (targetNode != null && currentClickCount < CLICK_COUNT) {
////                    if (targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
////                        Log.d("MyAccessibilityService", "Click performed at: (" + targetX + ", " + targetY + ")");
////                        currentClickCount++;
////                    } else {
////                        Log.e("MyAccessibilityService", "Failed to perform click at: (" + targetX + ", " + targetY + ")");
////                    }
////                }
////            } else {
////                Log.w("MyAccessibilityService", "Root node is null");
////            }
////        }
////    }
////
////    private AccessibilityNodeInfo findNodeAtCoordinates(AccessibilityNodeInfo node, int x, int y) {
////        if (node == null) {
////            return null;
////        }
////
////        Rect rect = new Rect();
////        node.getBoundsInScreen(rect);
////        if (rect.contains(x, y)) {
////            return node;
////        }
////
////        for (int i = 0; i < node.getChildCount(); i++) {
////            AccessibilityNodeInfo child = node.getChild(i);
////            if (child != null) {
////                AccessibilityNodeInfo foundNode = findNodeAtCoordinates(child, x, y);
////                if (foundNode != null) {
////                    return foundNode;
////                }
////            }
////        }
////
////        return null;
////    }
////
////    @Override
////    public void onServiceConnected() {
////        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
////        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
////        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
////        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;;
////        }
////        setServiceInfo(info);
////        Log.d("MyAccessibilityService", "Service connected and configured.");
////    }
////
////    @Override
////    public void onInterrupt() {
////        Log.d("MyAccessibilityService", "Service interrupted.");
////    }
////}
