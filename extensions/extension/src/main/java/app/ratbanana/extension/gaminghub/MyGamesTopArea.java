package app.ratbanana.extension.gaminghub;

import android.view.View;
import android.view.ViewParent;

/**
 * Controls the top section of My games, which shows either Instant Plays history
 * or recently played games.
 */
@SuppressWarnings("unused")
public final class MyGamesTopArea {

    private MyGamesTopArea() {
    }

    /**
     * Injection point. Hides the section while it shows Instant Plays history.
     *
     * @param title Title view of the section.
     */
    public static void hide(View title) {
        setSectionVisibility(title, View.GONE);
    }

    /**
     * Injection point. Shows the section again while it shows recently played games.
     *
     * @param title Title view of the section.
     */
    public static void show(View title) {
        setSectionVisibility(title, View.VISIBLE);
    }

    private static void setSectionVisibility(View title, int visibility) {
        if (title == null) {
            return;
        }

        ViewParent section = title.getParent();
        if (section instanceof View) {
            ((View) section).setVisibility(visibility);
        }
    }
}
