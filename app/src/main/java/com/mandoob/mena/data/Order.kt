package com.mandoob.mena.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val courierNotes: String? = null,
    val status: String = STATUS_PENDING,
    val collectedAmount: Double? = null, // Used in STATUS_PARTIAL (التسليم الجزئي)
    val deliveryFeeAmount: Double? = null, // Used in STATUS_REJECTED_WITH_FEE (رفض ودفع مصاريف شحن)
    val isSequenceArranged: Boolean = false, // Sequence sorting toggle
    val sequenceNumber: Int = 0, // Order sequence index
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isCancelledOrPostponed(): Boolean {
        return status == STATUS_CANCELLED ||
               status == STATUS_REJECTED_NO_FEE ||
               status == STATUS_REJECTED_WITH_FEE ||
               status == STATUS_NO_ANSWER ||
               status == STATUS_POSTPONED
    }

    companion object {
        const val STATUS_PENDING = "جاري العمل"
        const val STATUS_DELIVERED = "تم التسليم"
        const val STATUS_PARTIAL = "التسليم الجزئى"
        const val STATUS_REJECTED_NO_FEE = "رفض بدون مصاريف شحن"
        const val STATUS_REJECTED_WITH_FEE = "رفض ودفع مصاريف شحن"
        const val STATUS_NO_ANSWER = "لا يرد"
        const val STATUS_POSTPONED = "مؤجل"
        const val STATUS_CANCELLED = "لاغى"
    }
}
