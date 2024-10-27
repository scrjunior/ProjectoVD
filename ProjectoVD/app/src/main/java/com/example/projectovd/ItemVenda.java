package com.example.projectovd;

public class ItemVenda {
    private int id;
    private String nome;
    private double precoUnitario;
    private int quantidade;
    private double total;

    public ItemVenda(int id, String nome, double precoUnitario, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.total = precoUnitario * quantidade;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }
    public double getTotal() { return total; }
}