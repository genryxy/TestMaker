package com.example.testcreator.ui.newTest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testcreator.FireBaseConnections;
import com.example.testcreator.Question;
import com.example.testcreator.QuestionsCreatingActivity;
import com.example.testcreator.R;
import com.example.testcreator.TestInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class NewTestFragment extends Fragment implements FireBaseConnections
{
    private volatile Integer testsNumber;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button saveNameTestBtn;
    private Button saveQuestionsNumberBtn;
    private EditText nameTestEdt;
    private EditText questionsNumberEdt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CountDownLatch downLatch = new CountDownLatch(1);
        new CountTests(downLatch);
        try
        {
            // Ждём, пока не произойдёт событие.
            downLatch.await();
        } catch (InterruptedException e)
        {
            Log.w("FAILURE", "Error CountDownLatch", e);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        NewTestViewModel newTestViewModel = ViewModelProviders
                .of(this).get(NewTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_name_test, container, false);
        final TextView nameTestTxt = root.findViewById(R.id.nameTestTxt);
        newTestViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                nameTestTxt.setText(s);
            }
        });
        findElementsViewById(root);
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        saveNameTestBtnOnClickListen();
        saveQuestionsNumberBtnOnClickListen();
    }

    /**
     * Связывает элементы из разметки XML с полями класса.
     *
     * @param root Представление для фрагмента пользовательского интерфейса.
     */
    private void findElementsViewById(View root)
    {
        nameTestEdt = root.findViewById(R.id.nameTestEdt);
        questionsNumberEdt = root.findViewById(R.id.questionsNumberEdt);
        saveNameTestBtn = root.findViewById(R.id.saveNameTestBtn);
        saveQuestionsNumberBtn = root.findViewById(R.id.saveQuestionsNumberBtn);
    }

    /**
     * Меняет Visibility элементов на экране.
     * Деактивирует поле ввода для названия теста.
     */
    private void changeElementsVisibility()
    {
        nameTestEdt.setEnabled(false);
        nameTestEdt.setFocusable(false);
        saveNameTestBtn.setVisibility(View.GONE);
        saveQuestionsNumberBtn.setVisibility(View.VISIBLE);
        questionsNumberEdt.setVisibility(View.VISIBLE);
    }

    /**
     * Обработчик события нажатие на кнопку для сохранения названия теста. Если
     * пользователь ввёл уникальное название теста, то добавляет название
     * в БД, иначе просит повторить ввод.
     */
    private void saveNameTestBtnOnClickListen()
    {
        saveNameTestBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CountDownLatch downLatch = new CountDownLatch(1);
                new CountTests(downLatch);
                try
                {
                    // Ждём, пока не произойдёт событие.
                    downLatch.await();
                } catch (InterruptedException e)
                {
                    Log.w("FAILURE", "Error CountDownLatch", e);
                    return;
                }

                db.collection("tests_names")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (nameTestEdt.getText().toString().length() == 0)
                                {
                                    nameTestEdt.setError("Небходимо заполнить это поле");
                                    nameTestEdt.requestFocus();
                                }
                                else if (task.isSuccessful())
                                {
                                    if (task.getResult().getDocuments().get(0).getData().values()
                                            .contains(nameTestEdt.getText().toString()))
                                    {
                                        nameTestEdt.setError("Тест с таким названием уже существует");
                                        nameTestEdt.requestFocus();
                                    } else if (testsNumber != null)
                                    {
                                        CountDownLatch downLatch2 = new CountDownLatch(1);
                                        new UpdateDataBase(downLatch2, nameTestEdt);
                                        try
                                        {
                                            // Ждём, пока не произойдёт событие.
                                            downLatch2.await();
                                            changeElementsVisibility();
                                        } catch (InterruptedException e)
                                        {
                                            Log.w("FAILURE", "Error CountDownLatch", e);
                                            return;
                                        }
                                    }
                                } else
                                {
                                    Log.w("FAILURE", "Error adding document", task.getException());
                                    Toast.makeText(getContext(), "Не удалось добавить", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Обработчик события нажатие на кнопку для сохранения количества вопросов в
     * тесте. Если пользователь ввёл положительное число, то открывает новую страницу.
     */
    private void saveQuestionsNumberBtnOnClickListen()
    {
        saveQuestionsNumberBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (questionsNumberEdt.getText().toString().length() == 0
                        || Integer.valueOf(questionsNumberEdt.getText().toString()) < 1)
                {
                    questionsNumberEdt.requestFocus();
                    questionsNumberEdt.setError("Необходимо указать положительное количество вопросов");
                } else
                {
                    Intent newIntent = new Intent(getActivity(), QuestionsCreatingActivity.class);
                    newIntent.putExtra("nameTestEdt", nameTestEdt.getText().toString());
                    newIntent.putExtra("questionsNumberEdt", questionsNumberEdt.getText().toString());
                    startActivity(newIntent);
                }
            }
        });
    }

    /**
     * Класс для обращения к FireBase в другом потоке.
     * Нужен, чтобы можно было отправить в режим ожидания основной
     * поток до тех пор, пока не произойдёт одно (или больше) событие.
     */
    class CountTests implements Runnable
    {
        CountDownLatch latch;

        /**
         * Конструктор класса.
         *
         * @param downLatch Экземпляр класса для снятия самоблокировки
         *                  пссле определенного числа событий.
         */
        CountTests(CountDownLatch downLatch)
        {
            latch = downLatch;
            new Thread(this).start();
        }

        public void run()
        {
            db.collection("tests")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot docSnapshot : task.getResult().getDocuments())
                                {
                                    if (docSnapshot.getId().equals("tests"))
                                    {
                                        testsNumber = Integer.valueOf(docSnapshot.getData().get("testsNumber").toString());
                                        //Toast.makeText(getActivity(), testsNumber.toString(), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            } else
                            {
                                Log.w("FAILURE", "Error adding document", task.getException());
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
    class UpdateDataBase implements Runnable
    {
        CountDownLatch latch;
        private EditText nameTestEdt;

        /**
         * Конструктор класса.
         *
         * @param downLatch Экземпляр класса для снятия самоблокировки
         *                  пссле определенного числа событий.
         */
        UpdateDataBase(CountDownLatch downLatch, EditText nameTestEdt)
        {
            latch = downLatch;
            this.nameTestEdt = nameTestEdt;
            new Thread(this).start();
        }

        public void run()
        {
            Map<String, String> data = new HashMap<>();
            data.put("name" + testsNumber, nameTestEdt.getText().toString());
            db.collection("tests_names").document("names")
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getActivity(), "Название добавлено", Toast.LENGTH_SHORT).show();
                            Map<String, Object> dataNumb = new HashMap<>();
                            dataNumb.put("testsNumber", testsNumber + 1);
                            db.collection("tests").document("tests")
                                    .update(dataNumb)
                                    .addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.w("FAILURE", "Error updating testsNumber", e);
                                        }
                                    });
                            //db.collection("tests")
                            //        .add()
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.w("FAILURE", "Error adding testName", e);
                            Toast.makeText(getActivity(), "Не удалось добавить", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Изещаем о том, что произошло событие.
            latch.countDown();
        }
    }
}