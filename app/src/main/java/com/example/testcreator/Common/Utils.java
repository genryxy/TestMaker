package com.example.testcreator.Common;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.testcreator.Model.QuestionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

/**
 * Класс хранения методов, необходимых для выполнения различных
 * небольших подзадач.
 */
public class Utils {
    /**
     * Массив, чтобы следить за количеством отмеченных вариантов ответов
     * при составлении ответов в режиме OneAnswer.
     */
    public static boolean[] selectedAnswer = new boolean[QuestionModel.NUMBER_ANSWER];

    public static Uri imgQuestionUri;


    static {
        Arrays.fill(selectedAnswer, false);
    }

    /**
     * Метод для получения расширения переданного файла.
     *
     * @param imgUri  Унифицированный идентификатор ресурса. Здесь хранится
     *                идентификатор выбранной пользователем фотографии.
     * @param context Контекст класса, из которого происходит вызов метода.
     * @return Расширение файла.
     */
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

    /**
     * Метод для получения названия категории по ID категории.
     *
     * @param categoryID ID категории.
     * @return Строковое название категории.
     */
    public static String getNameCategoryByID(int categoryID) {
        for (int i = 0; i < Common.categoryLst.size(); i++) {
            if (Common.categoryLst.get(i).getId() == categoryID) {
                return Common.categoryLst.get(i).getName();
            }
        }
        return "";
    }

    /**
     * Метод для получения диалога, который будет показываться во время
     * загрузки данных из базы данных.
     *
     * @param context Контекст класса, из которого происходит вызов метода.
     * @return Диалог, который показывается во время загрузки.
     */
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

    /**
     * Метод для получения результата пользователя. В результате указано количество
     * правильных ответов пользователя относительно количества вопросов.
     *
     * @return Строка с результатом.
     */
    public static String getFinalResult() {
        return String.format("%d/%d", Common.rightAnswerCount, Common.questionLst.size());
    }

    /**
     * Метод для получения результата пользователя. В результате указано количество
     * набранных пользователем баллов относительно количества баллов,
     * которые можно было получить.
     *
     * @return Строка с результатом.
     */
    public static String getFinalPoint() {
        int totalPoint = 0;
        for (QuestionModel question : Common.questionLst) {
            totalPoint += question.getQuestionPoint();
        }
        return String.format("%d/%d", Common.userPointCount, totalPoint);
    }

    /**
     * Метод для получения информации о наличии подключении к интернету.
     *
     * @param context Контекст страницы, с которой вызывается метод.
     * @return true - есть подключение, false - нет подключения
     */
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }
}
