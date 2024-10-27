package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class ActivityProdutosVendidos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProdutosVendidosAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_vendidos);

        recyclerView = findViewById(R.id.recyclerViewVendidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        List<ProdutoVendido> produtosVendidos = dbHelper.obterProdutosVendidos();

        adapter = new ProdutosVendidosAdapter(produtosVendidos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}