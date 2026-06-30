package com.mandoob.mena.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val value: String) {
    PENDING("جاري العمل"),
    DELIVERED("تم التسليم"),
    PARTIAL("التسليم الجزئى"),
    REJECTED_NO_FEE("رفض بدون مصاريف شحن"),
    REJECTED_WITH_FEE("رفض ودفع مصاريف شحن"),
    NO_ANSWER("لا يرد"),
    POSTPONED("مؤجل"),
    CANCELLED("لاغى");

    companion object {
        fun fromValue(value: String): OrderStatus? = entries.find { it.value == value }
    }
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val phoneNumber: String,
    val phoneNumber2: String? = null,
    val address: String,
    val amount: Double, // Positive = cash collected, 0 = no cash, Negative = refund/cash returned
    val commission: Double, // Amount earned by courier for this delivery
    val notes: String? = null,
    val status: String = OrderStatus.PENDING.value,
    val collectedAmount: Double? = null, // Used in STATUS_PARTIAL (التسليم الجزئي)
    val deliveryFeeAmount: Double? = null, // Used in STATUS_REJECTED_WITH_FEE (رفض ودفع مصاريف شحن)
    val isSequenceArranged: Boolean = false, // Sequence sorting toggle
    val sequenceNumber: Int = 0, // Order sequence index
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isCancelledOrPostponed(): Boolean {
        return status == OrderStatus.CANCELLED.value ||
               status == OrderStatus.REJECTED_NO_FEE.value ||
               status == OrderStatus.REJECTED_WITH_FEE.value ||
               status == OrderStatus.NO_ANSWER.value ||
               status == OrderStatus.POSTPONED.value ||
               status == OrderStatus.PARTIAL.value
    }
}
