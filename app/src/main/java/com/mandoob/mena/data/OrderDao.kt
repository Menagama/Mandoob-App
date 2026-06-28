package com.mandoob.mena.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY isSequenceArranged DESC, sequenceNumber ASC, id ASC")
    fun getAllOrders(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Int)

    @Query("DELETE FROM orders")
    suspend fun clearAll()
}
