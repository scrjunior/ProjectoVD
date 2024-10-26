package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class activity_registrar_produtos extends AppCompatActivity {

    EditText productName, productPrice, productQuantity;
    Button saveProductButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_produtos);

        // Inicializando os campos e botão
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productQuantity = findViewById(R.id.productQuantity);
        saveProductButton = findViewById(R.id.saveProductButton);

        // Inicializando o DBHelper
        dbHelper = new DBHelper(this);

        // Configurando ação de clique no botão
        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pegando os valores dos campos
                String nome = productName.getText().toString();
                String precoString = productPrice.getText().toString();
                String quantidadeString = productQuantity.getText().toString();

                // Verificando se os campos estão preenchidos
                if (nome.isEmpty() || precoString.isEmpty() || quantidadeString.isEmpty()) {
                    Toast.makeText(activity_registrar_produtos.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertendo preço e quantidade
                double preco = Double.parseDouble(precoString);
                int quantidade = Integer.parseInt(quantidadeString);

                // Inserindo no banco de dados
                boolean sucesso = dbHelper.inserirProduto(nome, preco, quantidade);

                if (sucesso) {
                    Toast.makeText(activity_registrar_produtos.this, "Produto registrado com sucesso!", Toast.LENGTH_SHORT).show();
                    // Limpar campos
                    productName.setText("");
                    productPrice.setText("");
                    productQuantity.setText("");
                } else {
                    Toast.makeText(activity_registrar_produtos.this, "Erro ao registrar produto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
