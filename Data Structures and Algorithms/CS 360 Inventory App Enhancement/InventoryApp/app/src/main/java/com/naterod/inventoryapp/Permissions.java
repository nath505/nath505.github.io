package com.naterod.inventoryapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    public static boolean hasPermissions(final Activity activity, final String permission,
                                         int rationaleMessageId, final int requestCode) {
        // check if permission is granted
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            // permission isn't granted, determine if permission rationale is shown
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // show why permission is needed
                showPermissionRationaleDialog(activity, rationaleMessageId, (dialog, which) -> {
                    // since permission was denied, request it another time
                    ActivityCompat.requestPermissions(activity, new String[] {permission}, requestCode);
                });
            }
            else {
                // request permission from user
                ActivityCompat.requestPermissions(activity, new String[] {permission}, requestCode);
            }
            // permission granted is always false the first time
            return false;
        }
        else {
            // permission is granted
            return true;
        }
    }

    private static void showPermissionRationaleDialog(Activity activity, int messageId,
                                                      DialogInterface.OnClickListener onClickListener) {
        // explain to user why permission is needed
        new AlertDialog.Builder(activity)
                .setTitle(R.string.permission_title)
                .setMessage(messageId)
                .setPositiveButton(R.string.ok_button, onClickListener)
                .create()
                .show();
    }
}
