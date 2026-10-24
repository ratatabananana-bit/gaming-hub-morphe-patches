package app.ratbanana.extension.gaminghub;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Controls the top section of My games, which shows either Instant Plays history
 * or recently played games.
 */
@SuppressWarnings("unused")
public final class MyGamesTopArea {

    /**
     * One UI's collapsing app bar reserves an empty expanded title area behind the section.
     * It is taller than the Library header alone, so once the section is hidden it would
     * leave an empty strip below the header.
     */
    private static final String EXPANDED_TITLE_AREA_ID = "collapsing_appbar_title_layout_parent";

    /** How many parents up to search for the collapsing app bar. */
    private static final int MAX_ANCESTOR_DEPTH = 6;

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
        if (!(section instanceof View)) {
            return;
        }
        ((View) section).setVisibility(visibility);

        // The expanded title area is always empty here, so it only ever adds blank space.
        View expandedTitleArea = findInAncestors((View) section, EXPANDED_TITLE_AREA_ID);
        if (expandedTitleArea != null) {
            expandedTitleArea.setVisibility(visibility == View.GONE ? View.GONE : View.VISIBLE);
        }
    }

    /** Finds a view by id name in the closest ancestor that contains it. */
    private static View findInAncestors(View start, String idName) {
        Context context = start.getContext();
        int id = context.getResources().getIdentifier(idName, "id", context.getPackageName());
        if (id == 0) {
            return null;
        }

        ViewParent parent = start.getParent();
        for (int depth = 0; depth < MAX_ANCESTOR_DEPTH && parent instanceof ViewGroup; depth++) {
            View found = ((ViewGroup) parent).findViewById(id);
            if (found != null) {
                return found;
            }
            parent = parent.getParent();
        }
        return null;
    }
}
