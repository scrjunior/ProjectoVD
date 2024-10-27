package com.example.projectovd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ItemVendaAdapter extends RecyclerView.Adapter<ItemVendaAdapter.ViewHolder> {
    private List<ItemVenda> itens;
    private NumberFormat formatoMoeda;

    public ItemVendaAdapter(List<ItemVenda> itens) {
        this.itens = itens;
        this.formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venda_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemVenda item = itens.get(position);
        holder.textNome.setText(item.getNome());
        holder.textQuantidade.setText("Qtd: " + item.getQuantidade());
        holder.textPrecoUnitario.setText("Preço: " + formatoMoeda.format(item.getPrecoUnitario()));
        holder.textTotal.setText("Total: " + formatoMoeda.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        notifyItemInserted(itens.size() - 1);
    }

    public double getTotalVenda() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getTotal();
        }
        return total;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQuantidade, textPrecoUnitario, textTotal;

        ViewHolder(View view) {
            super(view);
            textNome = view.findViewById(R.id.textNome);
            textQuantidade = view.findViewById(R.id.textQuantidade);
            textPrecoUnitario = view.findViewById(R.id.textPrecoUnitario);
            textTotal = view.findViewById(R.id.textTotal);
        }
    }
}