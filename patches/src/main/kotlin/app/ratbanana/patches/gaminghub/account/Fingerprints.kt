package app.ratbanana.patches.gaminghub.account

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/** Startup status enum. Its class name is kept, but its constant fields are obfuscated. */
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
        string("graduateChild getOobeStatus userStatus == AccountUserStatus.GRADUATE_CHILD_ACCOUNT"),
    ),
)
