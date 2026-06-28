package com.mandoob.mena.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mandoob.mena.data.AppDatabase
import com.mandoob.mena.data.Order
import com.mandoob.mena.data.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.SharedPreferencesMigration

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "courier_preferences",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, "courier_prefs"))
    }
)

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrderRepository
    private val routeMutex = Mutex()

    val allOrders: StateFlow<List<Order>>

    private val _isSortingEnabled = MutableStateFlow(false)
    val isSortingEnabled: StateFlow<Boolean> = _isSortingEnabled.asStateFlow()

    private val _isFastMoveEnabled = MutableStateFlow(false)
    val isFastMoveEnabled: StateFlow<Boolean> = _isFastMoveEnabled.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val dataStore = application.dataStore

    companion object {
        val Factory: androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras
            ): T {
                val application = checkNotNull(extras[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return OrderViewModel(application) as T
            }
        }

        private val THEME_KEY = stringPreferencesKey("app_theme_settings")
        private val CAPTAIN_NAME_KEY = stringPreferencesKey("captain_name")
        private val CAPTAIN_AVATAR_KEY = stringPreferencesKey("captain_avatar")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")
        private val CAT1_STR_KEY = stringPreferencesKey("commission_cat1")
        private val CAT1_FLT_KEY = floatPreferencesKey("commission_cat1_float")
        private val CAT2_STR_KEY = stringPreferencesKey("commission_cat2")
        private val CAT2_FLT_KEY = floatPreferencesKey("commission_cat2_float")
        private val CAT3_STR_KEY = stringPreferencesKey("commission_cat3")
        private val CAT3_FLT_KEY = floatPreferencesKey("commission_cat3_float")
    }

    val appThemeSettings: StateFlow<String> = dataStore.data
        .map { it[THEME_KEY] ?: "system" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "system")

    fun updateAppTheme(theme: String) {
        viewModelScope.launch {
            dataStore.edit { it[THEME_KEY] = theme }
        }
    }

    fun setAppThemeRuntime(theme: String) {
        updateAppTheme(theme)
    }

    val captainName: StateFlow<String> = dataStore.data
        .map { it[CAPTAIN_NAME_KEY] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val captainAvatar: StateFlow<String> = dataStore.data
        .map { it[CAPTAIN_AVATAR_KEY] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val isFirstLaunch: StateFlow<Boolean> = dataStore.data
        .map { it[FIRST_LAUNCH_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { it[FIRST_LAUNCH_KEY] = false }
        }
    }

    private fun getCommissionFallback(pref: Preferences, strKey: Preferences.Key<String>, fltKey: Preferences.Key<Float>): Double {
        return try {
            pref[strKey]?.toDoubleOrNull()
        } catch (e: ClassCastException) {
            null
        } ?: try {
            pref[fltKey]?.toDouble()
        } catch (e: ClassCastException) {
            null
        } ?: 0.0
    }

    val commissionCat1: StateFlow<Double> = dataStore.data
        .map { getCommissionFallback(it, CAT1_STR_KEY, CAT1_FLT_KEY) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val commissionCat2: StateFlow<Double> = dataStore.data
        .map { getCommissionFallback(it, CAT2_STR_KEY, CAT2_FLT_KEY) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val commissionCat3: StateFlow<Double> = dataStore.data
        .map { getCommissionFallback(it, CAT3_STR_KEY, CAT3_FLT_KEY) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun updateCaptainInfo(name: String, avatar: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[CAPTAIN_NAME_KEY] = name
                it[CAPTAIN_AVATAR_KEY] = avatar
            }
        }
    }

    fun updateCommissionRates(cat1: Double, cat2: Double, cat3: Double) {
        viewModelScope.launch {
            dataStore.edit {
                it[CAT1_STR_KEY] = cat1.toString()
                it[CAT2_STR_KEY] = cat2.toString()
                it[CAT3_STR_KEY] = cat3.toString()
            }

            // Recalculate and update commissions for all stored non-pending orders in database
            try {
                val list = repository.allOrders.first()
                list.forEach { order ->
                    if (order.status != Order.STATUS_PENDING) {
                        val newCommission = when (order.status) {
                            Order.STATUS_DELIVERED, Order.STATUS_PARTIAL -> cat1
                            Order.STATUS_REJECTED_WITH_FEE -> cat2
                            Order.STATUS_REJECTED_NO_FEE -> cat3
                            else -> 0.0
                        }
                        val updated = order.copy(commission = newCommission)
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
            it.status == Order.STATUS_REJECTED_WITH_FEE ||
            it.status == Order.STATUS_REJECTED_NO_FEE
        }.sumOf { it.commission }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netRemittanceToOffice: StateFlow<Double> = combine(totalCashInWallet, totalCommissions) { cash, comm ->
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
        if (!_isSortingEnabled.value) {
            _isFastMoveEnabled.value = false
        }
    }

    fun toggleFastMove() {
        _isFastMoveEnabled.value = !_isFastMoveEnabled.value
    }

    fun moveOrder(orderId: Int, up: Boolean, isFast: Boolean, currentFilteredList: List<Order>) {
        viewModelScope.launch {
            routeMutex.withLock {
                val latestOrdersFromDb = repository.allOrders.first()
                val latestOrdersMap = latestOrdersFromDb.associateBy { it.id }
                val listToUpdate = currentFilteredList.mapNotNull { latestOrdersMap[it.id] }.toMutableList()
                
                val index = listToUpdate.indexOfFirst { it.id == orderId }
                if (index == -1) return@withLock

                if (isFast) {
                    if (up) {
                        if (index > 0) {
                            val item = listToUpdate.removeAt(index)
                            listToUpdate.add(0, item)
                        }
                    } else {
                        if (index < listToUpdate.size - 1) {
                            val item = listToUpdate.removeAt(index)
                            listToUpdate.add(item)
                        }
                    }
                } else {
                    if (up) {
                        if (index > 0) {
                            val temp = listToUpdate[index]
                            listToUpdate[index] = listToUpdate[index - 1]
                            listToUpdate[index - 1] = temp
                        }
                    } else {
                        if (index < listToUpdate.size - 1) {
                            val temp = listToUpdate[index]
                            listToUpdate[index] = listToUpdate[index + 1]
                            listToUpdate[index + 1] = temp
                        }
                    }
                }

                val currentTime = System.currentTimeMillis()
                listToUpdate.forEachIndexed { i, ord ->
                    val updated = ord.copy(sequenceNumber = i, isSequenceArranged = true, updatedAt = currentTime)
                    repository.updateOrder(updated)
                }
            }
        }
    }

    fun saveRouteSequence(orderedList: List<Order>) {
        viewModelScope.launch {
            routeMutex.withLock {
                val currentTime = System.currentTimeMillis()
                orderedList.forEachIndexed { index, order ->
                    val updated = order.copy(sequenceNumber = index, isSequenceArranged = true, updatedAt = currentTime)
                    repository.updateOrder(updated)
                }
            }
        }
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
            val currentList = repository.allOrders.first()
            val anyArranged = currentList.any { it.isSequenceArranged }
            val maxSeq = currentList.maxOfOrNull { it.sequenceNumber } ?: 0

            val order = Order(
                clientName = clientName,
                phoneNumber = formatEgyptianPhoneNumber(phoneNumber),
                phoneNumber2 = phoneNumber2?.let { formatEgyptianPhoneNumber(it) },
                address = address,
                amount = amount,
                commission = commission,
                notes = notes,
                status = Order.STATUS_PENDING,
                isSequenceArranged = anyArranged,
                sequenceNumber = if (anyArranged) maxSeq + 1 else 0
            )
            repository.insertOrder(order)
        }
    }

    fun updateOrderStatusWithValues(orderId: Int, status: String, collectedAmount: Double? = null, deliveryFeeAmount: Double? = null) {
        viewModelScope.launch {
            val currentList = repository.allOrders.first()
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    status = status,
                    collectedAmount = collectedAmount,
                    deliveryFeeAmount = deliveryFeeAmount,
                    commission = getCommissionForStatus(status),
                    updatedAt = System.currentTimeMillis()
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
            val currentList = repository.allOrders.first()
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    clientName = clientName,
                    phoneNumber = formatEgyptianPhoneNumber(phoneNumber),
                    phoneNumber2 = phoneNumber2?.let { formatEgyptianPhoneNumber(it) },
                    address = address,
                    amount = amount,
                    commission = commission,
                    notes = notes,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateOrder(updated)
            }
        }
    }

    fun updateOrderNotes(orderId: Int, notes: String?, courierNotes: String?) {
        viewModelScope.launch {
            val currentList = repository.allOrders.first()
            val order = currentList.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(
                    notes = notes,
                    courierNotes = courierNotes,
                    updatedAt = System.currentTimeMillis()
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

    fun clearItinerary() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // Parse Excel CSV import
    suspend fun importOrdersFromCsv(csvText: String): Int = withContext(Dispatchers.IO) {
        val lines = csvText.lines()
        if (lines.isEmpty()) return@withContext 0
        
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
                    val commission = commissionCat1.value

                    newOrders.add(
                        Order(
                            clientName = name,
                            phoneNumber = cleanPhone,
                            address = address,
                            amount = amount,
                            commission = commission,
                            notes = "",
                            status = Order.STATUS_PENDING
                        )
                    )
                    importedCount++
                }
            }
        }

        if (newOrders.isNotEmpty()) {
            val currentList = repository.allOrders.first()
            val anyArranged = currentList.any { it.isSequenceArranged }
            var maxSeq = currentList.maxOfOrNull { it.sequenceNumber } ?: 0

            val updatedNewOrders = if (anyArranged) {
                newOrders.map { ord ->
                    maxSeq++
                    ord.copy(isSequenceArranged = true, sequenceNumber = maxSeq)
                }
            } else {
                newOrders
            }
            repository.insertOrders(updatedNewOrders)
        }
        importedCount
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

    // Import from Excel Sheet Uri using our clean custom parser
    suspend fun importOrdersFromExcelUri(context: Context, uri: Uri): Int = withContext(Dispatchers.IO) {
        val newOrders = mutableListOf<Order>()
        var importedCount = 0

        try {
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val rows = com.mandoob.mena.util.ExcelReader.readXlsx(inputStream)
                if (rows.isEmpty()) return@withContext 0

                val dataRows = rows.drop(1)
                for (row in dataRows) {
                    if (row.size >= 4) {
                        val name = row[0].trim()
                        val phoneRaw = row[1].trim()
                        val address = row[2].trim()
                        val amountRaw = row[3].trim()

                        if (name.isNotEmpty() && phoneRaw.isNotEmpty()) {
                            val cleanPhone = formatEgyptianPhoneNumber(phoneRaw)
                            val cleanAmountRaw = amountRaw.replace(Regex("[^0-9.-]"), "")
                            val amount = cleanAmountRaw.toDoubleOrNull() ?: 0.0
                            val commission = commissionCat1.value

                            newOrders.add(
                                Order(
                                    clientName = name,
                                    phoneNumber = cleanPhone,
                                    address = address,
                                    amount = amount,
                                    commission = commission,
                                    notes = "",
                                    status = Order.STATUS_PENDING
                                )
                            )
                            importedCount++
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return@withContext -1
        }

        if (newOrders.isNotEmpty()) {
            val currentList = repository.allOrders.first()
            val anyArranged = currentList.any { it.isSequenceArranged }
            var maxSeq = currentList.maxOfOrNull { it.sequenceNumber } ?: 0

            val updatedNewOrders = if (anyArranged) {
                newOrders.map { ord ->
                    maxSeq++
                    ord.copy(isSequenceArranged = true, sequenceNumber = maxSeq)
                }
            } else {
                newOrders
            }
            repository.insertOrders(updatedNewOrders)
        }
        importedCount
    }
}
