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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements FireBaseConnections
{
    private EditText emailIdEdt;
    private EditText passwordEdt;
    private Button signInBtn;
    private TextView signUpTxt;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailIdEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.pswdEdt);
        signInBtn = findViewById(R.id.signInBtn);
        signUpTxt = findViewById(R.id.signUpTxt);

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)
                {
                    Toast.makeText(LoginActivity.this, "Вы вошли!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else
                {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, войдите в учетную запись", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signInBtn.setOnClickListener(new View.OnClickListener()
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
                    authFrbs.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                            LoginActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this,
                                                "Не удалось войти! Попробуйте снова", Toast.LENGTH_SHORT).show();
                                    } else
                                    {
                                        try
                                        {
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } catch (Exception e)
                                        {
                                            Toast.makeText(LoginActivity.this,
                                                    e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        authFrbs.addAuthStateListener(authStateListener);
    }
}
