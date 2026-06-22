package com.mandoob.mena.data

import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun insertOrder(order: Order) = orderDao.insertOrder(order)

    suspend fun insertOrders(orders: List<Order>) = orderDao.insertOrders(orders)

    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)

    suspend fun updateOrderStatus(orderId: Int, status: String) = orderDao.updateOrderStatus(orderId, status)

    suspend fun deleteOrderById(orderId: Int) = orderDao.deleteOrderById(orderId)

    suspend fun clearAll() = orderDao.clearAll()
}
