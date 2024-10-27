package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.AdapterView;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Toast;

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
    private RecyclerView recyclerViewItens;
    private ItemVendaAdapter adapter;
    private List<ItemVenda> itensVenda;
    private Button buttonAdicionarItem;
    private Button buttonFinalizarVenda;
    private TextView labelTotalVenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_produtos);

        // Inicializa o formato de moeda para Real (R$)
        formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "MZ"));

        // Inicializa as referências aos elementos do layout
        spinnerProdutos = findViewById(R.id.spinnerProdutos);
        labelPrecoUnitario = findViewById(R.id.labelPrecoUnitario);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        labelTotal = findViewById(R.id.labelTotal);

        // Inicializa os novos componentes
        recyclerViewItens = findViewById(R.id.recyclerViewItens);
        buttonAdicionarItem = findViewById(R.id.buttonAdicionarItem);
        labelTotalVenda = findViewById(R.id.labelTotal);

        buttonFinalizarVenda = findViewById(R.id.buttonFinalizarVenda);
        buttonFinalizarVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarVenda();
            }
        });

        // Configura o RecyclerView
        itensVenda = new ArrayList<>();
        adapter = new ItemVendaAdapter(itensVenda);
        recyclerViewItens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItens.setAdapter(adapter);





        // Configura o botão Adicionar Item


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

        buttonAdicionarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarItem();
            }
        });

    }

    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos um item à venda", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            long vendaId = dbHelper.finalizarVenda(itensVenda);
            if (vendaId != -1) {
                Toast.makeText(this, "Venda finalizada com sucesso!", Toast.LENGTH_SHORT).show();
                // Limpar a lista de itens
                itensVenda.clear();
                adapter.notifyDataSetChanged();
                // Atualizar o total
                labelTotalVenda.setText("Total: " + formatoMoeda.format(0));
                // Recarregar produtos para atualizar o estoque mostrado no spinner
                carregarProdutos();
            } else {
                Toast.makeText(this, "Erro ao finalizar a venda", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao finalizar a venda: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void adicionarItem() {
        if (listaProdutos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantidadeStr = editTextQuantidade.getText().toString();
        if (quantidadeStr.isEmpty()) {
            Toast.makeText(this, "Digite uma quantidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantidade = Integer.parseInt(quantidadeStr);
        if (quantidade <= 0) {
            Toast.makeText(this, "Quantidade deve ser maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = spinnerProdutos.getSelectedItemPosition();
        Object[] produtoSelecionado = listaProdutos.get(position);

        int estoqueDisponivel = (int) produtoSelecionado[2];
        if (quantidade > estoqueDisponivel) {
            Toast.makeText(this, "Quantidade maior que o estoque disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = (int) produtoSelecionado[3];
        String nome = (String) produtoSelecionado[0];
        double preco = (double) produtoSelecionado[1];

        ItemVenda item = new ItemVenda(id, nome, preco, quantidade);
        adapter.adicionarItem(item);

        // Atualiza o total da venda
        double totalVenda = adapter.getTotalVenda();
        labelTotalVenda.setText("Total da Venda: " + formatoMoeda.format(totalVenda));

        // Limpa o campo de quantidade
        editTextQuantidade.setText("");

        Toast.makeText(this, "Item adicionado com sucesso", Toast.LENGTH_SHORT).show();
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