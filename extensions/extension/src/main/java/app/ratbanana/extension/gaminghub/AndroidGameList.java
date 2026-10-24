package app.ratbanana.extension.gaminghub;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * Replaces the game list of Samsung's Game Optimizing Service (GOS) with Android's own
 * app categories. GOS answers only Samsung-signed callers, so a re-signed Gaming Hub
 * would otherwise never see any installed game.
 */
@SuppressWarnings("unused")
public final class AndroidGameList {

    private static final String TAG = "GamingHubPatches";

    private AndroidGameList() {
    }

    /**
     * Injection point. Called only after GOS returned no games.
     *
     * @param packageNames Empty list to fill with the package names of installed games.
     * @return Whether any game was added.
     */
    public static boolean addInstalledGames(List<String> packageNames) {
        if (packageNames == null) {
            return false;
        }

        Context context = currentApplication();
        if (context == null) {
            return false;
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            String ownPackageName = context.getPackageName();

            for (ApplicationInfo app : packageManager.getInstalledApplications(0)) {
                if (!app.packageName.equals(ownPackageName)
                        && isGame(app)
                        && packageManager.getLaunchIntentForPackage(app.packageName) != null) {
                    packageNames.add(app.packageName);
                }
            }
        } catch (RuntimeException ex) {
            Log.e(TAG, "Failed to list installed games", ex);
            packageNames.clear();
            return false;
        }

        return !packageNames.isEmpty();
    }

    @SuppressWarnings("deprecation")
    private static boolean isGame(ApplicationInfo app) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && app.category == ApplicationInfo.CATEGORY_GAME) {
            return true;
        }
        return (app.flags & ApplicationInfo.FLAG_IS_GAME) != 0;
    }

    @SuppressLint("PrivateApi")
    private static Context currentApplication() {
        try {
            return (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            Log.e(TAG, "Failed to get the application context", ex);
            return null;
        }
    }
}
