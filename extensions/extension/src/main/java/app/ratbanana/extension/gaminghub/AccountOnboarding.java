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

    private AccountOnboarding() {
    }

    /**
     * Injection point.
     *
     * @return Whether startup must skip sign-in and onboarding and open home directly.
     * False when a Samsung account is visible, so the original flow runs unchanged.
     */
    public static boolean shouldSkipOnboarding(Context context) {
        if (context == null) {
            return false;
        }

        try {
            return AccountManager.get(context).getAccountsByType(SAMSUNG_ACCOUNT_TYPE).length == 0;
        } catch (RuntimeException ex) {
            // Account lookup unavailable: the original flow would fail the same way, so skip it.
            return true;
        }
    }
}
