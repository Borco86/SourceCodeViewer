package com.tomaszborejko.sourcecodeviewer.database;

import android.provider.BaseColumns;

/**
 * Created by Borco on 2017-05-25.
 */

public class SourceCodesTableContract implements BaseColumns {
    public static final String TABLE_NAME = "SOURCE_CODES";
    public static final String COLUMN_WEBSITE_URL = "WEBSITE_URL";
    public static final String COLUMN_SOURCE_CODE = "SOURCE_CODE";
}
