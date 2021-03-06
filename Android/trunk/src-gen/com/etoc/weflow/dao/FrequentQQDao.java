package com.etoc.weflow.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.etoc.weflow.dao.FrequentQQ;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table FREQUENT_QQ.
*/
public class FrequentQQDao extends AbstractDao<FrequentQQ, String> {

    public static final String TABLENAME = "FREQUENT_QQ";

    /**
     * Properties of entity FrequentQQ.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Qq_num = new Property(0, String.class, "qq_num", true, "QQ_NUM");
    };


    public FrequentQQDao(DaoConfig config) {
        super(config);
    }
    
    public FrequentQQDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FREQUENT_QQ' (" + //
                "'QQ_NUM' TEXT PRIMARY KEY NOT NULL );"); // 0: qq_num
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FREQUENT_QQ'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, FrequentQQ entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getQq_num());
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public FrequentQQ readEntity(Cursor cursor, int offset) {
        FrequentQQ entity = new FrequentQQ( //
            cursor.getString(offset + 0) // qq_num
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, FrequentQQ entity, int offset) {
        entity.setQq_num(cursor.getString(offset + 0));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(FrequentQQ entity, long rowId) {
        return entity.getQq_num();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(FrequentQQ entity) {
        if(entity != null) {
            return entity.getQq_num();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
