package com.example.projectovd;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class activity_ver_produtos extends AppCompatActivity {

    ListView productsListView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_produtos);

        // Inicializar o ListView e o DBHelper
        productsListView = findViewById(R.id.productsListView);
        dbHelper = new DBHelper(this);

        // Recuperar os produtos do banco de dados
        List<String> produtos = dbHelper.obterTodosProdutos();

        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto registrado.", Toast.LENGTH_SHORT).show();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, produtos) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.BLACK); // Define a cor do texto aqui
                    return view;
                }
            };
            productsListView.setAdapter(adapter);
        }
    }
}
