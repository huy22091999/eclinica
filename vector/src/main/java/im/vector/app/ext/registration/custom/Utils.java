package im.vector.app.ext.registration.custom;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

public class Utils {
    public static String getContentType(Uri uri, @NonNull Context context) {
        String type = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                type = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
