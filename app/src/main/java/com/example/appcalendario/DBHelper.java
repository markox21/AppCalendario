package com.example.appcalendario;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "time TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertNote(String title, String description, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        db.insert("notes", null, values);
        db.close();
    }


    @SuppressLint("Range")
    public List<Note> getNotesByDate(String date) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE date=?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex("id")));
                note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                note.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    // Otros métodos para actualizar y eliminar notas según sea necesario
    public ArrayList<String> getNotasPorFecha(String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> notas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT title, description, time FROM notes WHERE date = ?", new String[]{fecha});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String titulo = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String descripcion = cursor.getString(cursor.getColumnIndex("description"));
                @SuppressLint("Range") String hora = cursor.getString(cursor.getColumnIndex("time"));
                String nota = "TITULO: " + titulo + " - DESCRIPCION: " + descripcion + " - HORA: " + hora;
                notas.add(nota);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notas;
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("notes", "id=?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void updateNote(int noteId, String newTitle, String newDescription, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("description", newDescription);
        values.put("time", newTime);
        db.update("notes", values, "id = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }
}
