package app.ratbanana.patches.gaminghub.account

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * `isSignInRequired(context: Context, settingRepository: SettingRepository): Boolean`
 *
 * Decides at startup whether Gaming Hub must show the mandatory Samsung account sign-in screen.
 * It returns true when no Samsung account is visible to the app, which is always the case
 * for a re-signed APK, because Android only exposes the account to Samsung-signed apps.
 */
internal object IsSignInRequiredFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf("Landroid/content/Context;", "L"),
    filters = listOf(
        string("settingRepository"),
        // SamsungAccountUtil.isSignedIn(context)
        methodCall(
            definingClass = "Lcom/samsung/android/game/gamehome/account/utility/SamsungAccountUtil;",
            parameters = listOf("Landroid/content/Context;"),
            returnType = "Z",
        ),
    ),
)
