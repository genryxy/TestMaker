package com.example.testcreator.Common;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.testcreator.Model.QuestionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class Utils {
    /**
     * Массив, чтобы следить за количеством отмеченных вариантов ответов
     * при составлении ответов в режиме OneAnswer.
     */
    public static boolean[] selectedAnswer = new boolean[QuestionModel.NUMBER_ANSWER];

    static {
        Arrays.fill(selectedAnswer, false);
    }

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

    public static AlertDialog showLoadingDialog(Context context) {
        AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setMessage("loading ...")
                .setCancelable(false)
                .build();

        if (!dialog.isShowing()) {
            dialog.show();
        }
        return dialog;
    }
}
