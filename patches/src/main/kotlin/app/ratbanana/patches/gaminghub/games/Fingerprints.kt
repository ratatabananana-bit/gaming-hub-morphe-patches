package app.ratbanana.patches.gaminghub.games

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * `IGosService.requestWithJson(command: String, param: JSONObject?): String?`
 *
 * Every request to Samsung's Game Optimizing Service (GOS) goes through this helper.
 * GOS only answers Samsung-signed callers, so for a re-signed APK every response fails.
 */
internal object GosRequestFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/String;",
    parameters = listOf(
        "Lcom/samsung/android/game/gos/IGosService;",
        "Ljava/lang/String;",
        "Lorg/json/JSONObject;",
    ),
    filters = listOf(
        string("command"),
    ),
)
