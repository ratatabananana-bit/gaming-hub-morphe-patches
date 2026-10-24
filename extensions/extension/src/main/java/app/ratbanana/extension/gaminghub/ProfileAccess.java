package app.ratbanana.extension.gaminghub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Opens the gamer profile, which holds the play history, without a Samsung account.
 */
@SuppressWarnings("unused")
public final class ProfileAccess {

    private static final String PROFILE_ACTIVITY =
            "com.samsung.android.game.gamehome.app.profile.ProfileActivity";

    private ProfileAccess() {
    }

    /**
     * Injection point. Replaces the sign-in prompt the profile button shows without an account.
     *
     * @param fragment The home screen fragment that owns the profile button.
     */
    public static void openProfile(Object fragment) {
        try {
            Context context = (Context) fragment.getClass().getMethod("getContext").invoke(fragment);
            if (context == null) {
                return;
            }
            context.startActivity(new Intent(context, Class.forName(PROFILE_ACTIVITY)));
        } catch (ReflectiveOperationException | RuntimeException ex) {
            Log.e(SamsungAccount.TAG, "Failed to open the gamer profile", ex);
        }
    }
}
