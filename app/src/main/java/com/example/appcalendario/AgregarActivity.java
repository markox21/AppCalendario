package com.example.appcalendario;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

public class AgregarActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDate, etTime;
    private Button btnSaveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        btnSaveNote = findViewById(R.id.btn_save_note);

        Intent intent = getIntent();
        String date = intent.getStringExtra("fecha");
        etDate.setText(date);

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String date = etDate.getText().toString();
                String time = etTime.getText().toString();


                DBHelper dbHelper = new DBHelper(AgregarActivity.this);

                dbHelper.insertNote(title, description, date, time);


                Toast.makeText(AgregarActivity.this, "Nota guardada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}