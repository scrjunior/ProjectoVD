package com.example.projectovd;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;

// ProdutoVendido.java

public class ProdutoVendido {
    private String dataVenda;
    private String nomeProduto;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    public ProdutoVendido(String dataVenda, String nomeProduto, int quantidade,
                          double precoUnitario, double subtotal) {
        this.dataVenda = dataVenda;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    // Getters
    public String getDataVenda() { return dataVenda; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public double getSubtotal() { return subtotal; }
}
