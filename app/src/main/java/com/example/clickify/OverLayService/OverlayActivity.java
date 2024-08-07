package com.example.clickify.OverLayService;

import static androidx.core.content.ContextCompat.startActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.clickify.AdminDashBoard.AdminDashBoard;
import com.example.clickify.MyAccessibilityService;
import com.example.clickify.Login.LoginScreen;
import com.example.clickify.R;
import com.example.clickify.SessionManager.SessionManager;

public class OverlayActivity extends AppCompatActivity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1001;
    private static final int REQUEST_ACCESSIBILITY_PERMISSION = 1002;
    Button btnEnable;
    ImageView home_ic, logout_ic;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_activty);

        btnEnable = findViewById(R.id.btnEnable);
        home_ic = findViewById(R.id.ic_home);
        logout_ic = findViewById(R.id.logout_ic);
        sm = new SessionManager(this);

        checkIfAdmin();

        home_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OverlayActivity.this, AdminDashBoard.class));
                finish();
            }
        });

        logout_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OverlayActivity.this, LoginScreen.class));
                stopService(new Intent(OverlayActivity.this, OverlayService.class));
                stopService(new Intent(OverlayActivity.this, OverlayPointerService.class));
                sm.logoutUser();
                finish();
            }
        });

        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkIfAdmin() {
        Intent intent = getIntent();
        String receivedText = intent.getStringExtra("intent_text");
        if (receivedText != null && receivedText.equals("admin")) {
            home_ic.setVisibility(View.VISIBLE);
        } else {
            home_ic.setVisibility(View.GONE);
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        } else if (!isAccessibilityServiceEnabled(MyAccessibilityService.class)) {
            requestAccessibilityPermission();
        } else {
            startSomeService();
        }
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
    }

    private void requestAccessibilityPermission() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                checkPermissions();
            } else {
                showPermissionRequiredToast("Overlay");
            }
        } else if (requestCode == REQUEST_ACCESSIBILITY_PERMISSION) {
            // We need to recheck permissions when the user returns from the accessibility settings
            checkPermissions();
        }
    }

    private boolean isAccessibilityServiceEnabled(Class<? extends AccessibilityService> service) {
        String enabledServices = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServices != null) {
            for (String enabledService : enabledServices.split(":")) {
                ComponentName enabledServiceComponent = ComponentName.unflattenFromString(enabledService);
                if (enabledServiceComponent != null && enabledServiceComponent.getClassName().equals(service.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showPermissionRequiredToast(String permission) {
        Toast.makeText(this, permission + " permission is required for this app to work.", Toast.LENGTH_LONG).show();
    }

    private void startSomeService() {
        startService(new Intent(this, OverlayService.class));
        startService(new Intent(this, OverlayPointerService.class));
        // MyAccessibilityService should be started automatically by the system when enabled
    }
}
