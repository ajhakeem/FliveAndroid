package co.fanstories.android.pages;

import android.provider.BaseColumns;

/**
 * Created by mohsal on 7/3/17.
 */

public final class PagesContract {
    private PagesContract() {}

    public static class PagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "pages";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PAGE_ID = "page_id";
    }
}
