package com.example.projectovd;

import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProdutosVendidosAdapter extends RecyclerView.Adapter<ProdutosVendidosAdapter.ViewHolder> {
    private List<ProdutoVendido> produtos;
    private NumberFormat formatoMoeda;

    public ProdutosVendidosAdapter(List<ProdutoVendido> produtos) {
        this.produtos = produtos;
        this.formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "MZ"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto_vendido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProdutoVendido produto = produtos.get(position);
        holder.textDataVenda.setText("Data: " + produto.getDataVenda());
        holder.textProduto.setText("Produto: " + produto.getNomeProduto());
        holder.textQuantidade.setText("Quantidade: " + produto.getQuantidade());
        holder.textPrecoUnitario.setText("Preço Unitário: " +
                formatoMoeda.format(produto.getPrecoUnitario()));
        holder.textSubtotal.setText("Total: " + formatoMoeda.format(produto.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDataVenda, textProduto, textQuantidade, textPrecoUnitario, textSubtotal;

        public ViewHolder(View itemView) {
            super(itemView);
            textDataVenda = itemView.findViewById(R.id.textDataVenda);
            textProduto = itemView.findViewById(R.id.textProduto);
            textQuantidade = itemView.findViewById(R.id.textQuantidade);
            textPrecoUnitario = itemView.findViewById(R.id.textPrecoUnitario);
            textSubtotal = itemView.findViewById(R.id.textSubtotal);
        }
    }
}