package app.ratbanana.patches.gaminghub.games

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import app.ratbanana.patches.gaminghub.shared.Constants.COMPATIBILITY_GAMING_HUB
import app.ratbanana.patches.gaminghub.shared.Constants.EXTENSION_PACKAGE
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
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
        GetGosPackageNamesFingerprint.let { fingerprint ->
            val matches = fingerprint.instructionMatches
            val resultListRegister = matches[1].getInstruction<FiveRegisterInstruction>().registerC
            val isEmptyRegister = matches[2].getInstruction<OneRegisterInstruction>().registerA

            // Only reached when GOS returned no games. The list is still empty here,
            // and the isEmpty() result register is free until the original code reuses it.
            fingerprint.method.addInstructionsWithLabels(
                matches[3].index + 1,
                """
                    invoke-static { v$resultListRegister }, $EXTENSION_CLASS->addInstalledGames(Ljava/util/List;)Z
                    move-result v$isEmptyRegister
                    if-eqz v$isEmptyRegister, :gos_failed
                    return-object v$resultListRegister
                    :gos_failed
                    nop
                """,
            )
        }
    }
}
