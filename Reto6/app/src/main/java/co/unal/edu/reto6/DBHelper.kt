package co.unal.edu.reto6

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_NAME = "MyDBCompanies.db"
        const val COMPANIES_TABLE_NAME = "companies"
        const val COMPANIES_COLUMN_ID = "id"
        const val COMPANIES_COLUMN_NAME = "name"
        const val COMPANIES_COLUMN_URL = "url"
        const val COMPANIES_COLUMN_PHONE = "phone"
        const val COMPANIES_COLUMN_EMAIL = "email"
        const val COMPANIES_COLUMN_PRODUCTS_SERVICES = "products"
        const val COMPANIES_COLUMN_CLASSIFICATION = "classification"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table companies " +
                    "(id integer primary key,name text,url text,phone text,email text, products text, classification text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS companies")
        onCreate(db)
    }

    fun insertCompany(name: String?, url: String?, phone: String?, email: String?, products: String?, classification: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("name", name)
        contentValues.put("url", url)
        contentValues.put("phone", phone)
        contentValues.put("email", email)
        contentValues.put("products", products)
        contentValues.put("classification", classification)
        db.insert("companies", null, contentValues)
        return true
    }

    fun getData(id: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("select * from companies where id=$id", null)
    }

    fun numberOfRows(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, COMPANIES_TABLE_NAME).toInt()
    }

    fun updateCompany(id: Int?, name: String?, url: String?, phone: String?, email: String?, products: String?, classification: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("name", name)
        contentValues.put("url", url)
        contentValues.put("phone", phone)
        contentValues.put("email", email)
        contentValues.put("products", products)
        contentValues.put("classification", classification)
        db.update("companies", contentValues, "id = ? ", arrayOf((id!!).toString()))
        return true
    }

    fun deleteCompany(id: Int?): Int? {
        val db = this.writableDatabase
        return db.delete("companies","id = ? ", arrayOf((id!!).toString()))
    }

    fun getAllCompanies(): ArrayList<String> {
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_NAME)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun getAllCompaniesIds():ArrayList<String>{
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_ID)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun getCompaniesClas(clas : String):ArrayList<String>{
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies where classification='$clas'", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_NAME)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun getCompaniesClasIds(clas : String):ArrayList<String>{
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies where classification='$clas'", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_ID)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun getByName(name : CharSequence): ArrayList<String> {
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies where name like '%$name%'", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_NAME)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun getByNameIds(name : CharSequence): ArrayList<String> {
        val arrayList = ArrayList<String>()

        val db = this.readableDatabase
        val res = db.rawQuery("select * from companies where name like '%$name%'", null)
        res.moveToFirst()
        while (!res.isAfterLast) {
            arrayList.add(res.getString(res.getColumnIndexOrThrow(COMPANIES_COLUMN_ID)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

}