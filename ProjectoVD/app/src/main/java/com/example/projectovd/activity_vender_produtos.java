package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.AdapterView;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.List;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.Locale;

public class activity_vender_produtos extends AppCompatActivity {

    private Spinner spinnerProdutos;
    private TextView labelPrecoUnitario;
    private EditText editTextQuantidade;
    private TextView labelTotal;
    private DBHelper dbHelper;
    private List<Object[]> listaProdutos;
    private NumberFormat formatoMoeda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_produtos);

        // Inicializa o formato de moeda para Real (R$)
        formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        // Inicializa as referências aos elementos do layout
        spinnerProdutos = findViewById(R.id.spinnerProdutos);
        labelPrecoUnitario = findViewById(R.id.labelPrecoUnitario);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        labelTotal = findViewById(R.id.labelTotal);

        // Inicializa o DBHelper
        dbHelper = new DBHelper(this);

        // Carrega os produtos no Spinner
        carregarProdutos();

        // Adiciona listener para quando um item for selecionado no Spinner
        spinnerProdutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listaProdutos != null && !listaProdutos.isEmpty()) {
                    Object[] produtoSelecionado = listaProdutos.get(position);
                    double preco = (double) produtoSelecionado[1];
                    labelPrecoUnitario.setText("Preço Unitário: " + formatoMoeda.format(preco));
                    atualizarTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                labelPrecoUnitario.setText("Preço Unitário: " + formatoMoeda.format(0));
            }
        });

        // Adiciona listener para quando a quantidade for alterada
        editTextQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                atualizarTotal();
            }
        });
    }

    private void carregarProdutos() {
        // Obtém a lista de produtos do banco de dados
        listaProdutos = dbHelper.obterTodosProdutos();

        // Cria uma lista apenas com os nomes dos produtos para o Spinner
        List<String> nomesProdutos = new ArrayList<>();
        for (Object[] produto : listaProdutos) {
            String nome = (String) produto[0];
            double preco = (double) produto[1];
            int quantidade = (int) produto[2];
            nomesProdutos.add(nome + " (Estoque: " + quantidade + ")");
        }

        // Cria e define o adapter para o Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomesProdutos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProdutos.setAdapter(adapter);
    }

    private void atualizarTotal() {
        if (listaProdutos.isEmpty()) return;

        try {
            int position = spinnerProdutos.getSelectedItemPosition();
            Object[] produtoSelecionado = listaProdutos.get(position);
            double precoUnitario = (double) produtoSelecionado[1];

            String quantidadeStr = editTextQuantidade.getText().toString();
            int quantidade = quantidadeStr.isEmpty() ? 0 : Integer.parseInt(quantidadeStr);

            double total = precoUnitario * quantidade;
            labelTotal.setText("Total: " + formatoMoeda.format(total));
        } catch (NumberFormatException e) {
            labelTotal.setText("Total: " + formatoMoeda.format(0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}