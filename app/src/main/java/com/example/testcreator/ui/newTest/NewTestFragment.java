package com.example.testcreator.ui.newTest;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.DBHelper.OnlineDBHelper;
import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.Interface.ThemesCallBack;
import com.example.testcreator.QuestionsCreatingActivity;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static android.app.Activity.RESULT_OK;


public class NewTestFragment extends Fragment implements FireBaseConnections {
    private final String TAG = "FAILURE NewTestFragment";

    private volatile Integer testsNumber;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button saveNameTestBtn;
    private EditText nameTestEdt;
    private Button chooseImageBtn;
    private ImageView imgView;
    private Uri imgUri;
    private String nameImage;
    private StringBuilder categoryName = new StringBuilder();
    private Spinner themesSpinner;
    private List<String> themesLst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountDownLatch downLatch = new CountDownLatch(1);
        new CountTests(downLatch);
        try {
            // Ждём, пока не произойдёт событие.
            downLatch.await();
        } catch (InterruptedException e) {
            Log.w("FAILURE", "Error CountDownLatch", e);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewTestViewModel newTestViewModel = ViewModelProviders
                .of(this).get(NewTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_name_test, container, false);
        final TextView nameTestTxt = root.findViewById(R.id.nameTestTxt);
        newTestViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                nameTestTxt.setText(s);
            }
        });
        findElementsViewById(root);
        themesSpinner = root.findViewById(R.id.themesSpinner);

        addSpinnerAdapter();
        themesSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener(categoryName));
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        return root;
    }

    private void addSpinnerAdapter() {
        new OnlineDBHelper(getContext(), FirebaseFirestore.getInstance())
                .readThemes(new ThemesCallBack() {
                    @Override
                    public void setThemesToAdapter(List<String> themes) {
                        themesLst = themes;
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, themes);
                        themesSpinner.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        saveNameTestBtnOnClickListen();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            imgView.setImageURI(imgUri);
        }
    }

    // Сжимает картинку, полученную во Uri. Возвращает изображение Bitmap.
//    private static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
//            throws FileNotFoundException {
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);
//
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//
//        while (true) {
//            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
//                break;
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
//    }

//    private Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     *
     * @param root Представление для фрагмента пользовательского интерфейса.
     */
    private void findElementsViewById(View root) {
        nameTestEdt = root.findViewById(R.id.nameTestEdt);
        saveNameTestBtn = root.findViewById(R.id.saveNameTestBtn);
        chooseImageBtn = root.findViewById(R.id.chooseImageBtn);
        imgView = root.findViewById(R.id.imgView);
    }

    private void uploadImage(String nameImage) {
        StorageReference childRef = storageRef.child(nameImage);
        childRef.putFile(imgUri)
                //try {
                //childRef.putFile(getImageUri(getContext(), decodeUri(getContext(), imgUri, 100)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Изображение загружено", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "Error CountDownLatch", exception);
                        Toast.makeText(getActivity(), "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
                    }
                });
        //} catch (FileNotFoundException e) {
        //    Toast.makeText(getActivity(), "Не удалось загрузить картинку", Toast.LENGTH_SHORT).show();
        //}
    }

    private String getExtension(Uri imgUri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(imgUri));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     * Обработчик события нажатие на кнопку для сохранения названия теста. Если
     * пользователь ввёл уникальное название теста, то добавляет название
     * в БД, иначе просит повторить ввод.
     */
    private void saveNameTestBtnOnClickListen() {
        saveNameTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если пользователь выбрал фотографию.
                if (imgUri != null) {
                    nameImage = System.currentTimeMillis() + "." + getExtension(imgUri);
                    uploadImage(nameImage);
                }
                CountDownLatch downLatch = new CountDownLatch(1);
                new CountTests(downLatch);
                try {
                    // Ждём, пока не произойдёт событие.
                    downLatch.await();
                } catch (InterruptedException e) {
                    Log.w(TAG, "Error CountDownLatch", e);
                    return;
                }

                db.collection("tests")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (nameTestEdt.getText().toString().length() == 0) {
                            nameTestEdt.setError("Небходимо заполнить это поле");
                            nameTestEdt.requestFocus();
                        } else if (task.isSuccessful()) {
                            for (DocumentSnapshot docSnapshot : task.getResult().getDocuments()) {
                                if (docSnapshot.getId().equals("tests_names")) {
                                    if (docSnapshot.getData().values()
                                            .contains(nameTestEdt.getText().toString())) {
                                        nameTestEdt.setError("Тест с таким названием уже существует");
                                        nameTestEdt.requestFocus();
                                    } else if (testsNumber != null) {
                                        CountDownLatch downLatch2 = new CountDownLatch(1);
                                        new UpdateDataBase(downLatch2, nameTestEdt);
                                        try {
                                            // Ждём, пока не произойдёт событие.
                                            downLatch2.await();
                                        } catch (InterruptedException e) {
                                            Log.w(TAG, "Error CountDownLatch", e);
                                            return;
                                        }
                                        Intent newIntent = new Intent(getActivity(), QuestionsCreatingActivity.class);
                                        newIntent.putExtra("nameTestEdt", nameTestEdt.getText().toString());
                                        newIntent.putExtra("categoryName", categoryName.toString());
                                        newIntent.putExtra("keyNameTestEdt", Integer.valueOf(testsNumber + 1));
                                        newIntent.putExtra("nameImage", nameImage);
//                                        Toast.makeText(getContext(), "name" + testsNumber, Toast.LENGTH_SHORT).show();
                                        startActivity(newIntent);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Log.w(TAG, "Error adding document", task.getException());
                            Toast.makeText(getContext(), "Не удалось добавить", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Класс для обращения к FireBase в другом потоке.
     * Нужен, чтобы можно было отправить в режим ожидания основной
     * поток до тех пор, пока не произойдёт одно (или больше) событие.
     */
    class CountTests implements Runnable {
        CountDownLatch latch;

        /**
         * Конструктор класса.
         *
         * @param downLatch Экземпляр класса для снятия самоблокировки
         *                  пссле определенного числа событий.
         */
        CountTests(CountDownLatch downLatch) {
            latch = downLatch;
            new Thread(this).start();
        }

        public void run() {
            // Считываем количество тестов в БД для создания ключа.
            db.collection("tests")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot docSnapshot : task.getResult().getDocuments()) {
                            if (docSnapshot.getId().equals("tests")) {
                                testsNumber = Integer.valueOf(docSnapshot.getData().get("testsNumber").toString());
                                //Toast.makeText(getActivity(), testsNumber.toString(), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    } else {
                        Log.w(TAG, "Error adding document", task.getException());
                        //Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Изещаем о том, что произошло событие.
            latch.countDown();
        }
    }

    /**
     * Класс для обращения к FireBase в другом потоке.
     * Нужен, чтобы можно было отправить в режим ожидания основной
     * поток до тех пор, пока не произойдёт одно (или больше) событие.
     */
    class UpdateDataBase implements Runnable {
        CountDownLatch latch;
        private EditText nameTestEdt;

        /**
         * Конструктор класса.
         *
         * @param downLatch Экземпляр класса для снятия самоблокировки
         *                  пссле определенного числа событий.
         */
        UpdateDataBase(CountDownLatch downLatch, EditText nameTestEdt) {
            latch = downLatch;
            this.nameTestEdt = nameTestEdt;
            new Thread(this).start();
        }

        public void run() {
            Map<String, String> data = new HashMap<>();
            data.put("name" + testsNumber, nameTestEdt.getText().toString());
            db.collection("tests").document("tests_names")
                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Название добавлено", Toast.LENGTH_SHORT).show();
                    Map<String, Object> dataNumb = new HashMap<>();
                    dataNumb.put("testsNumber", testsNumber + 1);
                    db.collection("tests").document("tests")
                            .update(dataNumb)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating testsNumber", e);
                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding testName", e);
                            Toast.makeText(getActivity(), "Не удалось добавить", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Изещаем о том, что произошло событие.
            latch.countDown();
        }
    }
}