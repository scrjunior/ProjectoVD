package com.example.projectovd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 1;

    // Nome da tabela
    public static final String TABLE_NAME = "produtos";

    // Colunas
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "nome";
    public static final String COLUMN_PRICE = "preco";
    public static final String COLUMN_QUANTITY = "quantidade";

    public static final String TABLE_VENDAS = "vendas";
    public static final String COLUMN_VENDA_ID = "venda_id";
    public static final String COLUMN_PRODUTO_ID = "produto_id";
    public static final String COLUMN_QUANTIDADE_VENDIDA = "quantidade_vendida";
    public static final String COLUMN_PRECO_UNITARIO = "preco_unitario";
    public static final String COLUMN_DATA_VENDA = "data_venda";

    private static final String TABLE_VENDAS_CREATE =
            "CREATE TABLE " + TABLE_VENDAS + " (" +
                    COLUMN_VENDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUTO_ID + " INTEGER, " +
                    COLUMN_QUANTIDADE_VENDIDA + " INTEGER, " +
                    COLUMN_PRECO_UNITARIO + " REAL, " +
                    COLUMN_DATA_VENDA + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_PRODUTO_ID + ") REFERENCES " +
                    TABLE_NAME + "(" + COLUMN_ID + "));";


    // Query de criação da tabela
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_QUANTITY + " INTEGER);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_VENDAS_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualização do banco de dados (se necessário)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean registrarVenda(List<ItemVenda> itensVenda) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String dataAtual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).format(new Date());

            // Para cada item da venda
            for (ItemVenda item : itensVenda) {
                // Registra a venda
                ContentValues valuesVenda = new ContentValues();
                valuesVenda.put(COLUMN_PRODUTO_ID, item.getId());
                valuesVenda.put(COLUMN_QUANTIDADE_VENDIDA, item.getQuantidade());
                valuesVenda.put(COLUMN_PRECO_UNITARIO, item.getPrecoUnitario());
                valuesVenda.put(COLUMN_DATA_VENDA, dataAtual);

                long resultVenda = db.insert(TABLE_VENDAS, null, valuesVenda);
                if (resultVenda == -1) throw new Exception("Erro ao registrar venda");

                // Atualiza o estoque
                String query = "UPDATE " + TABLE_NAME +
                        " SET " + COLUMN_QUANTITY + " = " + COLUMN_QUANTITY +
                        " - ? WHERE " + COLUMN_ID + " = ?";

                db.execSQL(query, new Object[]{item.getQuantidade(), item.getId()});
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

}
