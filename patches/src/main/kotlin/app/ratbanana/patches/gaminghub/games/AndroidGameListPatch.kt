package app.ratbanana.patches.gaminghub.games

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.ratbanana.patches.gaminghub.shared.Constants.COMPATIBILITY_GAMING_HUB
import app.ratbanana.patches.gaminghub.shared.Constants.EXTENSION_PACKAGE
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/AndroidGameList;"

@Suppress("unused")
val androidGameListPatch = bytecodePatch(
    name = "Use Android game list",
    description = "Fills My games with installed apps that Android marks as games. " +
        "Required for any patched Gaming Hub, because Samsung's game service refuses " +
        "apps that are not signed by Samsung.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_GAMING_HUB)

    extendWith("extensions/extension.mpe")

    execute {
        GosRequestFingerprint.method.apply {
            val returnIndex = instructions.indexOfLast { it.opcode == Opcode.RETURN_OBJECT }
            if (returnIndex < 0) throw PatchException("GOS request helper has no return")
            val responseRegister = instructions[returnIndex].let { it as OneRegisterInstruction }.registerA

            // The command and parameter registers (p1, p2) still hold the request at the return.
            addInstructions(
                returnIndex,
                """
                    invoke-static { p1, p2, v$responseRegister }, $EXTENSION_CLASS->replaceGosResponse(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$responseRegister
                """,
            )
        }
    }
}
