package app.ratbanana.extension.gaminghub;

import android.accounts.AccountManager;
import android.content.Context;

/**
 * Lets a re-signed Gaming Hub start without a Samsung account.
 * <p>
 * Android only exposes the Samsung account to apps signed by Samsung, and Samsung Account
 * rejects token requests from any other signature. A patched Gaming Hub therefore can
 * never sign in, and its startup flow would loop on the mandatory sign-in screen.
 */
@SuppressWarnings("unused")
public final class AccountOnboarding {

    private static final String SAMSUNG_ACCOUNT_TYPE = "com.osp.app.signin";

    /** Startup status that opens the home screen. Enum names survive obfuscation, fields do not. */
    private static final String HOME_STATUS_NAME = "NEED_TO_GO_HOME";

    private AccountOnboarding() {
    }

    /**
     * Injection point.
     *
     * @param context     Context used to look up the Samsung account.
     * @param statusClass The startup status enum class.
     * @return The home startup status when account onboarding must be skipped,
     * or null to run the original flow, which happens whenever a Samsung account is visible.
     */
    public static Object getSkippedOnboardingStatus(Context context, Class<?> statusClass) {
        if (context == null || statusClass == null || !statusClass.isEnum()) {
            return null;
        }

        if (isSamsungAccountVisible(context)) {
            return null;
        }

        for (Object status : statusClass.getEnumConstants()) {
            if (HOME_STATUS_NAME.equals(((Enum<?>) status).name())) {
                return status;
            }
        }

        // Unknown app version: keep the original behavior rather than guessing a screen.
        return null;
    }

    private static boolean isSamsungAccountVisible(Context context) {
        try {
            return AccountManager.get(context).getAccountsByType(SAMSUNG_ACCOUNT_TYPE).length > 0;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
