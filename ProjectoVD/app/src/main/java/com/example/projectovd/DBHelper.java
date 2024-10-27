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
    private static final int DATABASE_VERSION = 1;

    // Nome da tabela
    public static final String TABLE_NAME = "produtos";

    // Colunas
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "nome";
    public static final String COLUMN_PRICE = "preco";
    public static final String COLUMN_QUANTITY = "quantidade";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualização do banco de dados (se necessário)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
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
