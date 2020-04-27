package com.example.testcreator.Common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.util.concurrent.TimeUnit;

public class Utils {
    public static String getExtension(Uri imgUri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(imgUri));
    }

    /**
     * Конвертирует миллисекунды в нормальное представление времени.
     *
     * @param duration Продолжительность в миллисекундах.
     * @return чч:мм:сс (если чч == 0, то часы опускаются).
     */
    public static String convertToNormalForm(Long duration) {
        long millis = duration;
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder resTime = new StringBuilder();
        if (hours != 0) {
            resTime.append(String.format("%02d:", hours));
        }
        resTime.append(String.format("%02d:%02d", minutes, seconds));
        return resTime.toString();
    }

    public static String getNameCategoryByID(int categoryID) {
        for (int i = 0; i < Common.categoryLst.size(); i++) {
            if (Common.categoryLst.get(i).getId() == categoryID) {
                return Common.categoryLst.get(i).getName();
            }
        }
        return "";
    }
}
