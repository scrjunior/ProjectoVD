package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class activity_editar_produto extends AppCompatActivity {

    EditText editProductName, editProductPrice, editProductQuantity;
    Button updateProductButton;
    DBHelper dbHelper;
    int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        // Inicializando os campos e botão
        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        editProductQuantity = findViewById(R.id.editProductQuantity);
        updateProductButton = findViewById(R.id.updateProductButton);

        // Inicializando o DBHelper
        dbHelper = new DBHelper(this);

        // Recuperando os dados do produto
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productId = extras.getInt("id");
            editProductName.setText(extras.getString("nome"));
            editProductPrice.setText(String.valueOf(extras.getDouble("preco")));
            editProductQuantity.setText(String.valueOf(extras.getInt("quantidade")));
        }

        // Configurando ação de clique no botão
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pegando os valores dos campos
                String nome = editProductName.getText().toString();
                String precoString = editProductPrice.getText().toString();
                String quantidadeString = editProductQuantity.getText().toString();

                // Verificando se os campos estão preenchidos
                if (nome.isEmpty() || precoString.isEmpty() || quantidadeString.isEmpty()) {
                    Toast.makeText(activity_editar_produto.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertendo preço e quantidade
                double preco = Double.parseDouble(precoString);
                int quantidade = Integer.parseInt(quantidadeString);

                // Atualizando no banco de dados
                boolean sucesso = dbHelper.atualizarProduto(productId, nome, preco, quantidade);

                if (sucesso) {
                    Toast.makeText(activity_editar_produto.this, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a activity e volta para a lista
                } else {
                    Toast.makeText(activity_editar_produto.this, "Erro ao atualizar produto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}