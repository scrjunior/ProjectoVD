package com.example.projectovd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button registerProductButton, sellProductButton, viewProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Encontrar os botões
        registerProductButton = findViewById(R.id.registerProductButton);
        sellProductButton = findViewById(R.id.sellProductButton);
        viewProductButton = findViewById(R.id.viewProductButton);

        // Ação para o botão "Registrar Produtos"
        registerProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a Activity RegistrarProdutosActivity
                Intent intent = new Intent(HomeActivity.this, activity_registrar_produtos.class);
                startActivity(intent);
            }
        });

        viewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a Activity VerProdutos
                Intent intent = new Intent(HomeActivity.this, activity_ver_produtos.class);
                startActivity(intent);
            }
        });

        // Ação para o botão "Vender Produtos" (você pode adicionar aqui sua ação)
        sellProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, activity_vender_produtos.class);
                startActivity(intent);
            }
        });
    }
}
