package com.mandoob.mena.data

import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun insertOrder(order: Order) = orderDao.insertOrder(order)

    suspend fun insertOrders(orders: List<Order>) = orderDao.insertOrders(orders)

    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)
    
    suspend fun getOrderById(orderId: Int): Order? = orderDao.getOrderById(orderId)

    suspend fun deleteOrderById(orderId: Int) = orderDao.deleteOrderById(orderId)

    suspend fun clearAll() = orderDao.clearAll()
    
    fun getCommissionForStatus(status: String, cat1: Double, cat2: Double, cat3: Double): Double {
        return when (status) {
            OrderStatus.DELIVERED.value, OrderStatus.PARTIAL.value -> cat1
            OrderStatus.REJECTED_WITH_FEE.value -> cat2
            OrderStatus.REJECTED_NO_FEE.value -> cat3
            else -> 0.0
        }
    }

    suspend fun updateOrderStatusWithValues(
        orderId: Int, 
        status: String, 
        collectedAmount: Double?, 
        deliveryFeeAmount: Double?, 
        commission: Double
    ) {
        val order = getOrderById(orderId) ?: return
        val updated = order.copy(
            status = status,
            collectedAmount = collectedAmount,
            deliveryFeeAmount = deliveryFeeAmount,
            commission = commission,
            updatedAt = System.currentTimeMillis()
        )
        updateOrder(updated)
    }

    suspend fun updateOrderDetails(
        orderId: Int,
        clientName: String,
        phoneNumber: String,
        phoneNumber2: String?,
        address: String,
        amount: Double,
        commission: Double,
        notes: String?
    ) {
        val order = getOrderById(orderId) ?: return
        val updated = order.copy(
            clientName = clientName,
            phoneNumber = phoneNumber,
            phoneNumber2 = phoneNumber2,
            address = address,
            amount = amount,
            commission = commission,
            notes = notes,
            updatedAt = System.currentTimeMillis()
        )
        updateOrder(updated)
    }

    suspend fun updateOrderNotes(orderId: Int, notes: String?, courierNotes: String?) {
        val order = getOrderById(orderId) ?: return
        val updated = order.copy(
            notes = notes,
            courierNotes = courierNotes,
            updatedAt = System.currentTimeMillis()
        )
        updateOrder(updated)
    }

    suspend fun saveRouteSequence(orderedList: List<Order>) {
        val currentTime = System.currentTimeMillis()
        val updatedList = orderedList.mapIndexed { index, order ->
            order.copy(sequenceNumber = index, isSequenceArranged = true, updatedAt = currentTime)
        }
        updateOrders(updatedList)
    }
    
    suspend fun updateOrders(orders: List<Order>) {
        orderDao.updateOrders(orders)
    }
}
