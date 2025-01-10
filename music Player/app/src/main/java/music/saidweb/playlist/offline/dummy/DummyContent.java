package music.saidweb.playlist.offline.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();


    static {
        // Add some sample items.

        addItem(createDummyItem(1, "اغنية1", "mp3"));
        addItem(createDummyItem(2, "اغنية2", "mp3"));
        addItem(createDummyItem(3, "اغنية3", "mp3"));
        addItem(createDummyItem(4, "اغنية4", "mp3"));
        addItem(createDummyItem(5, "اغنية5", "mp3"));
        addItem(createDummyItem(6, "اغنية6", "mp3"));
        addItem(createDummyItem(7, "اغنية7", "mp3"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int number, String songNmae, String extension) {
        return new DummyItem("" + number, songNmae, extension);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
