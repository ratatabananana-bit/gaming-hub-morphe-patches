package app.ratbanana.patches.gaminghub.account

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import com.android.tools.smali.dexlib2.AccessFlags

internal const val ONBOARDING_STATUS_CLASS = "Lcom/samsung/android/game/gamehome/app/oobe/Status;"

/**
 * `StartViewModel.getOobeStatus(context: Context, isRetry: Boolean): Status` (suspend)
 *
 * Decides the first screen at startup: the mandatory sign-in screen, the welcome
 * (terms) screen, an error screen, or home. Every path except home needs a
 * Samsung account that a re-signed APK can never see.
 */
internal object GetOnboardingStatusFingerprint : Fingerprint(
    definingClass = "Lcom/samsung/android/game/gamehome/app/StartViewModel;",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Landroid/content/Context;", "Z", "L"),
    filters = listOf(
        fieldAccess(
            definingClass = ONBOARDING_STATUS_CLASS,
            name = "NEED_SIGN_IN",
        ),
    ),
)
