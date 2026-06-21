package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Order
import com.example.data.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrderRepository

    val allOrders: StateFlow<List<Order>>

    private val _isSortingEnabled = MutableStateFlow(false)
    val isSortingEnabled: StateFlow<Boolean> = _isSortingEnabled.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val prefs = application.getSharedPreferences("courier_prefs", Context.MODE_PRIVATE)

    private val _captainName = MutableStateFlow(prefs.getString("captain_name", "كابتن مينا") ?: "كابتن مينا")
    val captainName: StateFlow<String> = _captainName.asStateFlow()

    private val _captainAvatar = MutableStateFlow(prefs.getString("captain_avatar", "default") ?: "default")
    val captainAvatar: StateFlow<String> = _captainAvatar.asStateFlow()

    private val _commissionCat1 = MutableStateFlow(prefs.getFloat("commission_cat1", 0.0f).toDouble())
    val commissionCat1: StateFlow<Double> = _commissionCat1.asStateFlow()

    private val _commissionCat2 = MutableStateFlow(prefs.getFloat("commission_cat2", 0.0f).toDouble())
    val commissionCat2: StateFlow<Double> = _commissionCat2.asStateFlow()

    private val _commissionCat3 = MutableStateFlow(prefs.getFloat("commission_cat3", 0.0f).toDouble())
    val commissionCat3: StateFlow<Double> = _commissionCat3.asStateFlow()

    fun updateCaptainInfo(name: String, avatar: String) {
        _captainName.value = name
        _captainAvatar.value = avatar
        prefs.edit()
            .putString("captain_name", name)
            .putString("captain_avatar", avatar)
            .apply()
    }

    fun updateCommissionRates(cat1: Double, cat2: Double, cat3: Double) {
        _commissionCat1.value = cat1
        _commissionCat2.value = cat2
        _commissionCat3.value = cat3
        prefs.edit()
            .putFloat("commission_cat1", cat1.toFloat())
            .putFloat("commission_cat2", cat2.toFloat())
            .putFloat("commission_cat3", cat3.toFloat())
            .apply()

        // Recalculate and update commissions for all stored non-pending orders in database
        viewModelScope.launch {
            try {
                val list = repository.allOrders.first()
                list.forEach { order ->
                    if (order.status != Order.STATUS_PENDING) {
                        val updated = order.copy(commission = getCommissionForStatus(order.status))
                        repository.updateOrder(updated)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCommissionForStatus(status: String): Double {
        return when (status) {
            Order.STATUS_DELIVERED, Order.STATUS_PARTIAL -> commissionCat1.value
            Order.STATUS_REJECTED_WITH_FEE -> commissionCat2.value
            Order.STATUS_REJECTED_NO_FEE -> commissionCat3.value
            else -> 0.0
        }
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = OrderRepository(database.orderDao())
        
        allOrders = repository.allOrders
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Pre-populate sample orders on first launch if the database is empty and we've never populated them
        viewModelScope.launch {
            val hasPopulated = prefs.getBoolean("has_populated_samples_v5", false)
            if (!hasPopulated) {
                populateSampleOrders()
                prefs.edit().putBoolean("has_populated_samples_v5", true).apply()
            }
        }
    }

    private suspend fun populateSampleOrders() {
        val samples = listOf(
            Order(
                clientName = "أحمد محمد عبد الله",
                phoneNumber = "01012345678",
                phoneNumber2 = "01287654321",
                address = "شارع التسعين، التجمع الخامس، القاهرة",
                amount = 450.0,
                commission = 30.0,
                notes = "الرجاء الاتصال قبل الوصول بنصف ساعة",
                status = Order.STATUS_PENDING
            ),
            Order(
                clientName = "مينا سمير جرجس",
                phoneNumber = "01123456789",
                address = "دوران شبرا، بجوار محطة المترو، القاهرة",
                amount = 250.0,
                commission = 25.0,
                notes = "الدفع كاش، تم التسليم بنجاح",
                status = Order.STATUS_DELIVERED
            ),
            Order(
                clientName = "سارة أحمد النجار",
                phoneNumber = "01512345678",
                address = "شارع جلال الدسوقي، وابور المياه، الإسكندرية",
                amount = -150.0, // Refund
                commission = 20.0,
                notes = "مرتجع شحنة ملابس مقاس غير مناسب",
                status = Order.STATUS_DELIVERED // Delivered refund (cash returned to client)
            ),
            Order(
                clientName = "محمد علي",
                phoneNumber = "01128669096",
                address = "سوهاج الجديد عند الموقف تومور",
                amount = 310.0,
                commission = 35.0,
                notes = "تسليم عاجل اليوم",
                status = Order.STATUS_PENDING
            ),
            Order(
                clientName = "مدام الدكتور مصطفى أبو الدهب",
                phoneNumber = "01092534399",
                address = "محافظة سوهاج / سوهاج الجديدة كمبوند ريتاج 1 / فيلا 25A",
                amount = 650.0,
                commission = 45.0,
                notes = "الدفع كاش عند المعاينة",
                status = Order.STATUS_PENDING
            ),
            Order(
                clientName = "كريم محمود عبد العزيز",
                phoneNumber = "01234567890",
                address = "المجاورة الثالثة، الشيخ زايد، الجيزة",
                amount = 800.0,
                commission = 40.0,
                notes = "العميل لم يقم بالرد على الهاتف متكرراً",
                status = Order.STATUS_CANCELLED
            )
        )
        repository.insertOrders(samples)
    }

    // Financial Metrics - Wallet collect sums
    val totalCashInWallet: StateFlow<Double> = allOrders.map { orders ->
        orders.sumOf { order ->
            when (order.status) {
                Order.STATUS_DELIVERED -> order.amount
                Order.STATUS_PARTIAL -> order.collectedAmount ?: 0.0
                Order.STATUS_REJECTED_WITH_FEE -> order.deliveryFeeAmount ?: 0.0
                else -> 0.0
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCommissions: StateFlow<Double> = allOrders.map { orders ->
        orders.filter { 
            it.status == Order.STATUS_DELIVERED || 
            it.status == Order.STATUS_PARTIAL || 
            it.status == Order.STATUS_REJECTED_WITH_FEE 
        }.sumOf { it.commission }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netRemittanceToOffice: StateFlow<Double> = allOrders.map { orders ->
        val cash = orders.sumOf { order ->
            when (order.status) {
                Order.STATUS_DELIVERED -> order.amount
                Order.STATUS_PARTIAL -> order.collectedAmount ?: 0.0
                Order.STATUS_REJECTED_WITH_FEE -> order.deliveryFeeAmount ?: 0.0
                else -> 0.0
            }
        }
        val comm = orders.filter { 
            it.status == Order.STATUS_DELIVERED || 
            it.status == Order.STATUS_PARTIAL || 
            it.status == Order.STATUS_REJECTED_WITH_FEE 
        }.sumOf { it.commission }
        cash - comm
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Routing progress
    val totalOrdersCount: StateFlow<Int> = allOrders.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount: StateFlow<Int> = allOrders.map { orders ->
        orders.count { it.status != Order.STATUS_PENDING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleSorting() {
        _isSortingEnabled.value = !_isSortingEnabled.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNewOrder(
        clientName: String,
        phoneNumber: String,
        phoneNumber2: String?,
        address: String,
        amount: Double,
        commission: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val order = Order(
                clientName = clientName,
                phoneNumber = formatEgyptianPhoneNumber(phoneNumber),
                phoneNumber2 = phoneNumber2?.let { formatEgyptianPhoneNumber(it) },
                address = address,
                amount = amount,
                commission = commission,
                notes = notes,
                status = Order.STATUS_PENDING
            )
            repository.insertOrder(order)
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            val currentList = allOrders.value
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    status = status,
                    collectedAmount = if (status == Order.STATUS_PARTIAL) order.amount else null,
                    deliveryFeeAmount = if (status == Order.STATUS_REJECTED_WITH_FEE) 25.0 else null,
                    commission = getCommissionForStatus(status)
                )
                repository.updateOrder(updated)
            }
        }
    }

    fun updateOrderStatusWithValues(orderId: Int, status: String, collectedAmount: Double? = null, deliveryFeeAmount: Double? = null) {
        viewModelScope.launch {
            val currentList = allOrders.value
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    status = status,
                    collectedAmount = collectedAmount,
                    deliveryFeeAmount = deliveryFeeAmount,
                    commission = getCommissionForStatus(status)
                )
                repository.updateOrder(updated)
            }
        }
    }

    fun updateOrderDetails(
        orderId: Int,
        clientName: String,
        phoneNumber: String,
        phoneNumber2: String?,
        address: String,
        amount: Double,
        commission: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val currentList = allOrders.value
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    clientName = clientName,
                    phoneNumber = formatEgyptianPhoneNumber(phoneNumber),
                    phoneNumber2 = phoneNumber2?.let { formatEgyptianPhoneNumber(it) },
                    address = address,
                    amount = amount,
                    commission = commission,
                    notes = notes
                )
                repository.updateOrder(updated)
            }
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            repository.deleteOrderById(orderId)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
            populateSampleOrders()
        }
    }

    fun clearItinerary() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // Parse Excel CSV import
    fun importOrdersFromCsv(csvText: String): Int {
        val lines = csvText.lines()
        if (lines.isEmpty()) return 0
        
        val newOrders = mutableListOf<Order>()
        var importedCount = 0

        val dataLines = lines.drop(1)
        for (line in dataLines) {
            if (line.isBlank()) continue
            
            val delimiters = if (line.contains(';')) ";" else if (line.contains('\t')) "\t" else ","
            val parts = line.split(delimiters).map { it.trim() }
            
            if (parts.size >= 4) {
                val name = parts[0]
                val phoneRaw = parts[1]
                val address = parts[2]
                val amountRaw = parts[3]
                
                if (name.isNotEmpty() && phoneRaw.isNotEmpty()) {
                    val cleanPhone = formatEgyptianPhoneNumber(phoneRaw)
                    val cleanAmountRaw = amountRaw.replace(Regex("[^0-9.-]"), "")
                    val amount = cleanAmountRaw.toDoubleOrNull() ?: 0.0
                    val commission = 25.0

                    newOrders.add(
                        Order(
                            clientName = name,
                            phoneNumber = cleanPhone,
                            address = address,
                            amount = amount,
                            commission = commission,
                            notes = "مستورد من إكسيل",
                            status = Order.STATUS_PENDING
                        )
                    )
                    importedCount++
                }
            }
        }

        if (newOrders.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertOrders(newOrders)
            }
        }
        return importedCount
    }

    // Normalizes or fixes phone numbers starting with '0' and having 11 digits
    private fun formatEgyptianPhoneNumber(rawPhone: String): String {
        var digits = rawPhone.filter { it.isDigit() }
        if (digits.isEmpty()) return rawPhone
        
        if (digits.startsWith("20") && digits.length > 11) {
            digits = digits.substring(2)
        }
        
        if (!digits.startsWith("0")) {
            digits = "0$digits"
        }
        
        return if (digits.length > 11) {
            digits.substring(0, 11)
        } else {
            digits
        }
    }
}
