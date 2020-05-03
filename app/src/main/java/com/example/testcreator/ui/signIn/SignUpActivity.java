package com.example.testcreator.ui.signIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testcreator.Interface.FireBaseConnections;
import com.example.testcreator.MainActivity;
import com.example.testcreator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignUpActivity extends AppCompatActivity implements FireBaseConnections
{
    private EditText emailIdEdt;
    private EditText passwordEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailIdEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.pswdEdt);
        Button signUpBtn = findViewById(R.id.signUpBtn);
        TextView signInTxt = findViewById(R.id.signInTxt);

        signUpBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailIdEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                if (email.isEmpty() & password.isEmpty())
                {
                    emailIdEdt.setError("Пожалуйста, введите Email");
                    passwordEdt.setError("Пожалуйста, введите пароль");
                    emailIdEdt.requestFocus();
                } else if (email.isEmpty())
                {
                    emailIdEdt.setError("Пожалуйста, введите Email");
                    emailIdEdt.requestFocus();
                } else if (password.isEmpty() || email.length() < 6)
                {
                    passwordEdt.setError("Пожалуйста, введите пароль длиной не менее 6 символов");
                    passwordEdt.requestFocus();
                } else
                {
                    authFrbs.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            SignUpActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful())
                                    {
                                        Toast.makeText(SignUpActivity.this,
                                                "Не удалось зарегистрироваться! Попробуйте снова", Toast.LENGTH_SHORT).show();
                                    } else
                                    {
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    }
                                }
                            });
                }
            }
        });

        signInTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}
