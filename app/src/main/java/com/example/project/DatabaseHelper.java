package com.example.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.mapsforge.core.model.LatLong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "kyrgyzstan.db";
    private final String DB_PATH;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        // Правильный путь к базе данных приложения
        File dbFile = context.getDatabasePath(DB_NAME);
        this.DB_PATH = dbFile.getParent() + "/";
        copyDatabaseIfMissing();
    }

    private void copyDatabaseIfMissing() {
        // Получаем правильный путь к базе
        File dbFile = myContext.getDatabasePath(DB_NAME);

        // Если файла нет ИЛИ он весит очень мало (пустая база весит обычно 8-16 КБ)
        if (!dbFile.exists() || dbFile.length() < 100) {
            Log.d("DB", "Файл базы отсутствует или пуст. Начинаю копирование...");

            // Создаем папку /databases/, если её нет
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            try (InputStream input = myContext.getAssets().open(DB_NAME);
                 OutputStream output = new FileOutputStream(dbFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                Log.d("DB", "Копирование завершено успешно.");
            } catch (IOException e) {
                Log.e("DB", "Ошибка при копировании: " + e.getMessage());
            }
        }
    }

    public LatLong getCoordinates(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String input = query.trim();
        LatLong result = null;
        Cursor cursor = null;

        // 1. Быстрая проверка на координаты (как и раньше)
        if (input.matches("^-?\\d+(\\.\\d+)? kinship*,?\\s+-?\\d+(\\.\\d+)?$".replace(" kinship*", ""))) {
            try {
                String[] parts = input.split("[,\\s]+");
                return new LatLong(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            } catch (Exception ignored) {}
        }

        try {
            String wildCard = "%" + input + "%";

            // SQL с весовой сортировкой
            String sql = "SELECT latitude, longitude, " +
                    // Назначаем веса: чем меньше число, тем выше приоритет
                    "(CASE " +
                    "  WHEN name LIKE ? THEN 1 " +      // Точное имя — самый высокий приоритет
                    "  WHEN street LIKE ? THEN 2 " +    // Улица — второй
                    "  WHEN locality LIKE ? THEN 3 " +  // Город — третий
                    "  ELSE 4 END) as priority " +
                    "FROM addresses WHERE " +
                    "name LIKE ? OR street LIKE ? OR locality LIKE ? " +
                    "ORDER BY priority ASC LIMIT 1";

            // Передаем параметры: 3 для CASE и 3 для WHERE
            String[] params = {wildCard, wildCard, wildCard, wildCard, wildCard, wildCard};
            cursor = db.rawQuery(sql, params);

            if (cursor.moveToFirst()) {
                result = new LatLong(cursor.getDouble(0), cursor.getDouble(1));
            }
        } catch (Exception e) {
            Log.e("DB", "Search error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}