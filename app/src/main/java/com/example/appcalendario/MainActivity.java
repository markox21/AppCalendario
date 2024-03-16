package com.example.appcalendario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private FloatingActionButton fabAddNote;
    private String fechaSeleccionada;

    private RecyclerView recyclerViewNotas;
    private NotasAdapter notasAdapter;
    private ArrayList<Note> notasList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale.setDefault(new Locale("es"));
        dbHelper = new DBHelper(this);

        calendarView = findViewById(R.id.calendario);
        fabAddNote = findViewById(R.id.crearNota);
        recyclerViewNotas = findViewById(R.id.recyclerViewNotas);

        recyclerViewNotas.setLayoutManager(new LinearLayoutManager(this));
        notasList = new ArrayList<>();
        notasAdapter = new NotasAdapter(notasList, new NotasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Mostrar un modal con la descripción completa de la nota
                mostrarModalNota(note);
            }
        });
        recyclerViewNotas.setAdapter(notasAdapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Actualizar la fecha seleccionada cuando se cambia la selección en el calendario
                month = month + 1; // Ajustar el mes ya que comienza desde 0
                fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month, year);
                cargarNotasPorFecha(fechaSeleccionada);
            }
        });

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir AgregarActivity sin verificar si se ha seleccionado una fecha
                Intent intent = new Intent(MainActivity.this, AgregarActivity.class);
                intent.putExtra("fecha", fechaSeleccionada);
                startActivity(intent);
            }
        });
    }

    private void cargarNotasPorFecha(String fecha) {
        notasList.clear();
        notasList.addAll(dbHelper.getNotesByDate(fecha));
        notasAdapter.notifyDataSetChanged();
    }

    private void mostrarModalNota(final Note note) {
        // Crear un AlertDialog para mostrar la descripción y la hora de la nota
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nota: " + note.getTitle());

        String message = "Descripción: " + note.getDescription() + "\nHora: " + note.getTime();
        builder.setMessage(message);

        // Agregar un botón de "Cerrar" al AlertDialog
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cerrar el AlertDialog al hacer clic en el botón "Cerrar"
            }
        });

        // Agregar un botón de "Editar" al AlertDialog
        builder.setNeutralButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Crear un AlertDialog para editar la nota
                AlertDialog.Builder editBuilder = new AlertDialog.Builder(MainActivity.this);
                editBuilder.setTitle("Editar Nota");

                // Inflar el layout dialog_edit_note.xml
                View editView = getLayoutInflater().inflate(R.layout.dialog_edit_note, null);
                editBuilder.setView(editView);

                // Obtener referencias a los EditTexts y botones en el layout
                final EditText editTextTitulo = editView.findViewById(R.id.editTextTitulo);
                final EditText editTextDescripcion = editView.findViewById(R.id.editTextDescripcion);
                final EditText editTextHora = editView.findViewById(R.id.editTextHora);
                Button btnGuardar = editView.findViewById(R.id.btnGuardar);
                Button btnEliminar = editView.findViewById(R.id.btnEliminar);

                // Mostrar los detalles de la nota en los EditTexts
                editTextTitulo.setText(note.getTitle());
                editTextDescripcion.setText(note.getDescription());
                editTextHora.setText(note.getTime());

                // Configurar el botón para guardar los cambios en la nota
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtener los nuevos valores del título, descripción y hora
                        String nuevoTitulo = editTextTitulo.getText().toString();
                        String nuevaDescripcion = editTextDescripcion.getText().toString();
                        String nuevaHora = editTextHora.getText().toString();

                        // Actualizar la nota en la base de datos
                        dbHelper.updateNote(note.getId(), nuevoTitulo, nuevaDescripcion, nuevaHora);

                        // Actualizar la lista de notas y cerrar el diálogo
                        cargarNotasPorFecha(fechaSeleccionada);
                        dialog.dismiss();
                    }
                });

                // Configurar el botón para eliminar la nota
                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Implementar la lógica para borrar la nota
                        dbHelper.deleteNote(note.getId());
                        // Actualizar la lista de notas y cerrar el diálogo
                        cargarNotasPorFecha(fechaSeleccionada);
                        dialog.dismiss();
                    }
                });

                // Mostrar el AlertDialog de edición de la nota
                AlertDialog editDialog = editBuilder.create();
                editDialog.show();
            }
        });

        // Agregar un botón de "Borrar" al AlertDialog original
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implementar la lógica para borrar la nota
                dbHelper.deleteNote(note.getId());
                // Actualizar la lista de notas y cerrar el diálogo
                cargarNotasPorFecha(fechaSeleccionada);
                dialog.dismiss();
            }
        });

        // Mostrar el AlertDialog original
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}