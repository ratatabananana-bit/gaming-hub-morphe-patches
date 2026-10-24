package app.ratbanana.patches.gaminghub.games

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * `AidlGosManager.getPackageNameList(category)` coroutine body.
 *
 * Asks Samsung's Game Optimizing Service (GOS) which installed apps are games. GOS only
 * answers Samsung-signed callers, so for a re-signed APK the result is always empty.
 */
internal object GetGosPackageNamesFingerprint : Fingerprint(
    definingClass = "Lcom/samsung/android/game/gamehome/gos/AidlGosManager\$getPackageNameList\$2;",
    name = "invokeSuspend",
    returnType = "Ljava/lang/Object;",
    filters = listOf(
        string("get_package_names"),
        // resultList.isEmpty()
        methodCall(definingClass = "Ljava/util/Collection;", name = "isEmpty"),
        opcode(Opcode.MOVE_RESULT, MatchAfterImmediately()),
        opcode(Opcode.IF_EQZ, MatchAfterImmediately()),
        string("retList is null"),
    ),
)
