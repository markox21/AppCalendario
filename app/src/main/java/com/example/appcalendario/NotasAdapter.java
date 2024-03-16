package com.example.appcalendario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.NotaViewHolder> {

    private ArrayList<Note> notasList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public NotasAdapter(ArrayList<Note> notasList, OnItemClickListener listener) {
        this.notasList = notasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Note nota = notasList.get(position);
        holder.bind(nota, listener);
    }

    @Override
    public int getItemCount() {
        return notasList.size();
    }

    public static class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView, horaTextView;
        LinearLayout notaLayout; // Agregado para el contenedor de la nota

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            notaLayout = itemView.findViewById(R.id.notaLayout); //
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            horaTextView = itemView.findViewById(R.id.horaTextView);

        }

        public void bind(final Note nota, final OnItemClickListener listener) {
            tituloTextView.setText(nota.getTitle());
            horaTextView.setText(nota.getTime());

            // Configurar el OnClickListener en el contenedor de la nota
            notaLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(nota);
                }
            });
        }
    }
}