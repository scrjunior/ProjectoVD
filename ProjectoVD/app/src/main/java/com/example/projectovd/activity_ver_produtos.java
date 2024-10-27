package com.example.projectovd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class activity_ver_produtos extends AppCompatActivity {

    ListView productsListView;
    DBHelper dbHelper;
    ProdutoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_produtos);

        productsListView = findViewById(R.id.productsListView);
        dbHelper = new DBHelper(this);

        atualizarListaProdutos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarListaProdutos();
    }

    private void atualizarListaProdutos() {
        List<Object[]> produtos = dbHelper.obterTodosProdutos();

        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto registrado.", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new ProdutoAdapter(produtos);
            productsListView.setAdapter(adapter);
        }
    }

    private class ProdutoAdapter extends ArrayAdapter<Object[]> {
        public ProdutoAdapter(List<Object[]> produtos) {
            super(activity_ver_produtos.this, R.layout.list_item_product, produtos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_product, parent, false);
            }

            Object[] produto = getItem(position);
            String nome = (String) produto[0];
            double preco = (double) produto[1];
            int quantidade = (int) produto[2];
            int id = (int) produto[3]; // Você precisará adicionar o ID no DBHelper

            TextView textNome = convertView.findViewById(R.id.textNome);
            TextView textPreco = convertView.findViewById(R.id.textPreco);
            TextView textQuantidade = convertView.findViewById(R.id.textQuantidade);
            Button btnEditar = convertView.findViewById(R.id.btnEditar);
            Button btnApagar = convertView.findViewById(R.id.btnApagar);

            textNome.setText(nome);
            textPreco.setText(String.format("Preço: MZN %.2f", preco));
            textQuantidade.setText("Quantidade: " + quantidade);

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity_ver_produtos.this, activity_editar_produto.class);
                    intent.putExtra("id", id);
                    intent.putExtra("nome", nome);
                    intent.putExtra("preco", preco);
                    intent.putExtra("quantidade", quantidade);
                    startActivity(intent);
                }
            });

            btnApagar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aqui você implementará a lógica para apagar o produto
                    dbHelper.apagarProduto(id);
                    atualizarListaProdutos();
                    Toast.makeText(getContext(), "Produto apagado: " + nome, Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}