package com.example.projectovd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 2;

    // Nome da tabela
    public static final String TABLE_NAME = "produtos";

    public static final String TABLE_VENDAS = "vendas";
    public static final String TABLE_ITENS_VENDA = "itens_venda";

    // Colunas
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "nome";
    public static final String COLUMN_PRICE = "preco";
    public static final String COLUMN_QUANTITY = "quantidade";

    public static final String COLUMN_DATA_VENDA = "data_venda";
    public static final String COLUMN_TOTAL_VENDA = "total_venda";

    public static final String COLUMN_VENDA_ID = "venda_id";
    public static final String COLUMN_PRODUTO_ID = "produto_id";
    public static final String COLUMN_QUANTIDADE_VENDIDA = "quantidade_vendida";
    public static final String COLUMN_PRECO_UNITARIO = "preco_unitario";
    public static final String COLUMN_SUBTOTAL = "subtotal";

    // Query de criação da tabela
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_QUANTITY + " INTEGER);";

    private static final String TABLE_VENDAS_CREATE =
            "CREATE TABLE " + TABLE_VENDAS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATA_VENDA + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_TOTAL_VENDA + " REAL);";

    private static final String TABLE_ITENS_VENDA_CREATE =
            "CREATE TABLE " + TABLE_ITENS_VENDA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_VENDA_ID + " INTEGER, " +
                    COLUMN_PRODUTO_ID + " INTEGER, " +
                    COLUMN_QUANTIDADE_VENDIDA + " INTEGER, " +
                    COLUMN_PRECO_UNITARIO + " REAL, " +
                    COLUMN_SUBTOTAL + " REAL, " +
                    "FOREIGN KEY(" + COLUMN_VENDA_ID + ") REFERENCES " + TABLE_VENDAS + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_PRODUTO_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "));";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_VENDAS_CREATE);
        db.execSQL(TABLE_ITENS_VENDA_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_VENDAS_CREATE);
            db.execSQL(TABLE_ITENS_VENDA_CREATE);
        }
    }

    // Método para finalizar a venda
    public long finalizarVenda(List<ItemVenda> itensVenda) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Inserir a venda
            ContentValues vendasValues = new ContentValues();
            double totalVenda = 0;
            for (ItemVenda item : itensVenda) {
                totalVenda += item.getTotal();
            }
            vendasValues.put(COLUMN_TOTAL_VENDA, totalVenda);
            long vendaId = db.insert(TABLE_VENDAS, null, vendasValues);

            // Inserir os itens da venda e atualizar o estoque
            for (ItemVenda item : itensVenda) {
                // Inserir item da venda
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_VENDA_ID, vendaId);
                itemValues.put(COLUMN_PRODUTO_ID, item.getId());
                itemValues.put(COLUMN_QUANTIDADE_VENDIDA, item.getQuantidade());
                itemValues.put(COLUMN_PRECO_UNITARIO, item.getPrecoUnitario());
                itemValues.put(COLUMN_SUBTOTAL, item.getTotal());
                db.insert(TABLE_ITENS_VENDA, null, itemValues);

                // Atualizar estoque
                db.execSQL("UPDATE " + TABLE_NAME +
                                " SET " + COLUMN_QUANTITY + " = " + COLUMN_QUANTITY + " - ? " +
                                " WHERE " + COLUMN_ID + " = ?",
                        new String[]{String.valueOf(item.getQuantidade()), String.valueOf(item.getId())});
            }

            db.setTransactionSuccessful();
            return vendaId;
        } finally {
            db.endTransaction();
        }
    }

    // Método para inserir produto no banco de dados
    public boolean inserirProduto(String nome, double preco, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, nome);
        contentValues.put(COLUMN_PRICE, preco);
        contentValues.put(COLUMN_QUANTITY, quantidade);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Retorna true se a inserção for bem-sucedida
    }

    public List<Object[]> obterTodosProdutos() {
        List<Object[]> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String nome = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                double preco = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                int quantidade = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));

                Object[] produto = {nome, preco, quantidade, id};
                produtos.add(produto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return produtos;
    }

    public void apagarProduto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean atualizarProduto(int id, String nome, double preco, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, nome);
        contentValues.put(COLUMN_PRICE, preco);
        contentValues.put(COLUMN_QUANTITY, quantidade);

        int result = db.update(TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) });
        return result > 0;
    }

    // Adicione este método na classe DBHelper
    public List<ProdutoVendido> obterProdutosVendidos() {
        List<ProdutoVendido> produtosVendidos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT v." + COLUMN_DATA_VENDA + ", " +
                "p." + COLUMN_NAME + ", " +
                "iv." + COLUMN_QUANTIDADE_VENDIDA + ", " +
                "iv." + COLUMN_PRECO_UNITARIO + ", " +
                "iv." + COLUMN_SUBTOTAL +
                " FROM " + TABLE_VENDAS + " v" +
                " JOIN " + TABLE_ITENS_VENDA + " iv ON v." + COLUMN_ID + " = iv." + COLUMN_VENDA_ID +
                " JOIN " + TABLE_NAME + " p ON p." + COLUMN_ID + " = iv." + COLUMN_PRODUTO_ID +
                " ORDER BY v." + COLUMN_DATA_VENDA + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String dataVenda = cursor.getString(0);
                String nomeProduto = cursor.getString(1);
                int quantidade = cursor.getInt(2);
                double precoUnitario = cursor.getDouble(3);
                double subtotal = cursor.getDouble(4);

                ProdutoVendido produto = new ProdutoVendido(
                        dataVenda, nomeProduto, quantidade, precoUnitario, subtotal);
                produtosVendidos.add(produto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return produtosVendidos;
    }

}
