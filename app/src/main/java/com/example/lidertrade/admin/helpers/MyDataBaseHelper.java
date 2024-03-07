package com.example.lidertrade.admin.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "LiderTrade.db";
    private static final int DATABASE_VERSION = 1;
//    SUBCATEGORY
    private static final String CATEGORY_COLUMN_ID = "_id";
    private static final String CATEGORY_TABLE_NAME = "Categories";
    private static final String CATEGORY_COLUMN_NAME = "categoryName";
    //    SUBCATEGORY
    private static final String BRAND_COLUMN_ID = "_id";
    private static final String BRAND_TABLE_NAME = "Brands";
    private static final String BRAND_COLUMN_NAME = "brandName";

//    SUBCATEGORY
    private static final String SUBCATEGORY_COLUMN_ID = "_id";
    private static final String SUBCATEGORY_TABLE_NAME = "subCategories";
    private static final String SUBCATEGORY_COLUMN_NAME = "subcategoryName";
    private static final String SUBCATEGORY_COLUMN_CATEGORY_ID = "categoryId";


//PRODUCT
    private static final String COLUMN_ID = "_id";
    private static final String PRODUCT_TABLE_NAME = "products";
    private static final String PRODUCT_COLUMN_PRICE = "soldPrice";
    private static final String PRODUCT_COLUMN_PRODUCT_NAME = "productName";

//CREATE DATABSE
    public MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sDb) {
        String brandQuery =  "CREATE TABLE " + BRAND_TABLE_NAME +
                " (" + BRAND_COLUMN_ID + " TEXT UNIQUE, " +
                BRAND_COLUMN_NAME + " TEXT UNIQUE);";
        String categoryQuery =  "CREATE TABLE " + CATEGORY_TABLE_NAME +
                " (" + CATEGORY_COLUMN_ID + " TEXT UNIQUE, " +
                CATEGORY_COLUMN_NAME + " TEXT UNIQUE);";
        String productQuery =  "CREATE TABLE " + PRODUCT_TABLE_NAME +
                        " (" + COLUMN_ID + " TEXT UNIQUE, " +
                PRODUCT_COLUMN_PRODUCT_NAME + " TEXT , " +
                PRODUCT_COLUMN_PRICE + " INTEGER);";
        String subcategoryQuery =  "CREATE TABLE " + SUBCATEGORY_TABLE_NAME +
                " (" + SUBCATEGORY_COLUMN_ID + " TEXT UNIQUE, " +
                SUBCATEGORY_COLUMN_NAME + " TEXT UNIQUE, " +
                SUBCATEGORY_COLUMN_CATEGORY_ID + " TEXT);";

        sDb.execSQL(categoryQuery);
        sDb.execSQL(brandQuery);
        sDb.execSQL(productQuery);
        sDb.execSQL(subcategoryQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sDb, int i, int i1) {
        sDb.execSQL("DROP TABLE IF EXISTS "+ PRODUCT_TABLE_NAME);
        sDb.execSQL("DROP TABLE IF EXISTS "+ SUBCATEGORY_TABLE_NAME);
        sDb.execSQL("DROP TABLE IF EXISTS "+ CATEGORY_TABLE_NAME);
        sDb.execSQL("DROP TABLE IF EXISTS "+ BRAND_TABLE_NAME);
        onCreate(sDb);
    }

//----------------------BRANDS-----------------------------//
//CREATE NEW CATEGORIES
    public void addNewBrand(String brandId, String brandName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BRAND_COLUMN_ID, brandId);
        cv.put(BRAND_COLUMN_NAME, brandName);
        long result = db.insert(BRAND_TABLE_NAME, null, cv);
        if (result == -1){
        }
    }
//READ CATEGORIES
    public Cursor readAllBrands(){
        String query = "SELECT * FROM " + BRAND_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
//UPDATE CATEGORIES
    public void updateBrandData(String row_id, String brandName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BRAND_COLUMN_NAME, brandName);

        long result = db.update(BRAND_TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1){
        }
    }
//CHECK EXIST CATEGORIES
    public boolean existBrandId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + BRAND_TABLE_NAME + " WHERE _id = ?",
                new String[]{id}
        );
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }
//DELETE CATEGORIES
    public void deleteOneBrand(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(BRAND_TABLE_NAME,"_id=?", new String[]{row_id});
        if (result == -1){
        }
    }

    //----------------------CATEGORIES-----------------------------//
//CREATE NEW CATEGORIES
    public void addNewCategory(String categoryId, String categoryName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN_ID, categoryId);
        cv.put(CATEGORY_COLUMN_NAME, categoryName);
        long result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        if (result == -1){
        }
    }
//READ CATEGORIES
    public Cursor readAllCategories(){
        String query = "SELECT * FROM " + CATEGORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
//UPDATE CATEGORIES
    public void updateCategoryData(String row_id, String categoryName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN_NAME, categoryName);

        long result = db.update(CATEGORY_TABLE_NAME, cv, "_id=?", new String[]{row_id});

    }

//CHECK EXIST CATEGORIES
    public boolean existCategoryId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + CATEGORY_TABLE_NAME + " WHERE _id = ?",
                new String[]{id}
        );
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }
//DELETE CATEGORIES
    public void deleteOneCategory(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CATEGORY_TABLE_NAME,"_id=?", new String[]{row_id});

    }


//----------------------SUBCATEGORIES-----------------------------//
//CREATE NEW SUbCategory
    public void addNewSubcategory(String subcategoryId, String subcategoryName, String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(SUBCATEGORY_COLUMN_ID, subcategoryId);
        cv.put(SUBCATEGORY_COLUMN_NAME, subcategoryName);
        cv.put(SUBCATEGORY_COLUMN_CATEGORY_ID, categoryId);
        long result = db.insert(SUBCATEGORY_TABLE_NAME, null, cv);

    }
//READ SUBCATEGORIES
    public Cursor readAllSubcategories(){
        String query = "SELECT * FROM " + SUBCATEGORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
//UPDATE SUBCATEGORIES
    public void updateSubCategoryData(String row_id, String subcategoryName,String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(SUBCATEGORY_COLUMN_NAME, subcategoryName);
        cv.put(SUBCATEGORY_COLUMN_CATEGORY_ID, categoryId);

        long result = db.update(SUBCATEGORY_TABLE_NAME, cv, "_id=?", new String[]{row_id});

    }

//CHECK EXIST SUBCATEGORY
    public boolean existSubCategoryId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + SUBCATEGORY_TABLE_NAME + " WHERE _id = ?",
                new String[]{id}
        );
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }
//DELETE SUBCATEGORY
    public void deleteOneSubcategory(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(SUBCATEGORY_TABLE_NAME,"_id=?", new String[]{row_id});

    }


//----------------------PRODUCTS-----------------------------//

//    CHECK EXIST PRODUCT
    public boolean existProductId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + PRODUCT_TABLE_NAME + " WHERE _id = ?",
                new String[]{id}
        );
        boolean result = c.getCount() > 0;
        c.close();
        return result;
    }
//CREATE PRODUCT
    public void addNewProduct(String productId, String productName, int productPrice){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, productId);
        cv.put(PRODUCT_COLUMN_PRODUCT_NAME, productName);
        cv.put(PRODUCT_COLUMN_PRICE, productPrice);
        long result = db.insert(PRODUCT_TABLE_NAME, null, cv);

    }
//READ PRODUCT
    public Cursor readAllProducts(){
     String query = "SELECT * FROM " + PRODUCT_TABLE_NAME;
     SQLiteDatabase db = this.getReadableDatabase();

     Cursor cursor = null;
     if(db != null){
         cursor = db.rawQuery(query,null);
     }
     return cursor;
    }
//UPDATE PRODUCT
    public void updateProductData(String row_id, String productName, int price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PRODUCT_COLUMN_PRODUCT_NAME, productName);
        cv.put(PRODUCT_COLUMN_PRICE, price);

        long result = db.update(PRODUCT_TABLE_NAME, cv, "_id=?", new String[]{row_id});

    }
//DELETE PRODUCT
    public void deleteOneProduct(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(PRODUCT_TABLE_NAME,"_id=?", new String[]{row_id});

    }
}
