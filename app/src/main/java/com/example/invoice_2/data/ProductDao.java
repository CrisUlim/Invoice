package com.example.invoice_2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    Product getProductByBarcode(String barcode);

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
} 