package com.mandoob.mena.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Order::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Safe Migration from version 1 to 2 to prevent destructive wipes
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // If any schema adjustments were made in version 2, apply here.
                // Room will execute this block during upgrade from version 1, keeping user data safe!
            }
        }

        // Safe migration from version 2 to 3 to add courierNotes column
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE orders ADD COLUMN courierNotes TEXT DEFAULT NULL")
            }
        }

        // Safe migration from version 3 to 4 to add updatedAt column
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE orders ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE orders SET updatedAt = createdAt")
            }
        }

        // Safe migration from version 4 to 5 to remove courierNotes column
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS orders_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        clientName TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        phoneNumber2 TEXT,
                        address TEXT NOT NULL,
                        amount REAL NOT NULL,
                        commission REAL NOT NULL,
                        notes TEXT,
                        status TEXT NOT NULL,
                        collectedAmount REAL,
                        deliveryFeeAmount REAL,
                        isSequenceArranged INTEGER NOT NULL,
                        sequenceNumber INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
                
                db.execSQL("""
                    INSERT INTO orders_new (id, clientName, phoneNumber, phoneNumber2, address, amount, commission, notes, status, collectedAmount, deliveryFeeAmount, isSequenceArranged, sequenceNumber, createdAt, updatedAt)
                    SELECT id, clientName, phoneNumber, phoneNumber2, address, amount, commission, notes, status, collectedAmount, deliveryFeeAmount, isSequenceArranged, sequenceNumber, createdAt, updatedAt FROM orders
                """.trimIndent())
                
                db.execSQL("DROP TABLE orders")
                db.execSQL("ALTER TABLE orders_new RENAME TO orders")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "courier_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
