package ru.wolfnord.task10;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText username, email, password, confirmPassword;
    Button add, view, update, delete;
    DatabaseHelper databaseHelper;
    PreferencesManager preferencesManager; // новый класс PreferencesManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);

        add = findViewById(R.id.add);
        view = findViewById(R.id.view);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);

        databaseHelper = new DatabaseHelper(this);
        preferencesManager = new PreferencesManager(this); // инициализация PreferencesManager

        add.setOnClickListener(v -> {
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseHelper.insertUser(username.getText().toString(),
                    email.getText().toString(),
                    pass,
                    confirmPass);
            preferencesManager.saveUsername(username.getText().toString()); // сохранение имени пользователя
        });

        view.setOnClickListener(v -> {
            Cursor res = databaseHelper.getUser(username.getText().toString());
            if(res.getCount() == 0) {
                showMessage("Ошибка", "Данных не найдено");
                return;
            }

            StringBuilder buffer = new StringBuilder();
            while (res.moveToNext()) {
                buffer.append("ID: ").append(res.getString(0)).append("\n");
                buffer.append("Имя: ").append(res.getString(1)).append("\n");
                buffer.append("Email: ").append(res.getString(2)).append("\n");
                buffer.append("Пароль: ").append(res.getString(3)).append("\n\n");
            }

            showMessage("Данные", buffer.toString());
        });

        update.setOnClickListener(v -> {
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseHelper.updateUser(username.getText().toString(),
                    email.getText().toString(),
                    pass,
                    confirmPass);
            preferencesManager.saveUsername(username.getText().toString()); // обновление имени пользователя
        });

        delete.setOnClickListener(v -> {
            databaseHelper.deleteUser(username.getText().toString());
            preferencesManager.deleteUsername(); // удаление имени пользователя
        });

        // Получение сохраненного имени пользователя при запуске приложения
        username.setText(preferencesManager.getUsername());
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
