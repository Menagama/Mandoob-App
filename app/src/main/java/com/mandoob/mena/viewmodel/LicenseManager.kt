package com.mandoob.mena.viewmodel

import android.content.Context
import android.provider.Settings
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class LicenseStatus {
    object Loading : LicenseStatus()
    object Active : LicenseStatus()
    object Expired : LicenseStatus()
    object NotFound : LicenseStatus()
}

object LicenseManager {
    suspend fun checkLicense(deviceId: String): LicenseStatus = suspendCancellableCoroutine { continuation ->
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("licenses").document(deviceId).get()
                .addOnSuccessListener { document ->
                    if (continuation.isActive) {
                        if (document != null && document.exists()) {
                            val expiryTimestamp = document.getTimestamp("expiryDate")
                            if (expiryTimestamp != null) {
                                val expiryDate = expiryTimestamp.toDate()
                                val currentDate = java.util.Date()
                                if (expiryDate.after(currentDate)) {
                                    continuation.resume(LicenseStatus.Active)
                                } else {
                                    continuation.resume(LicenseStatus.Expired)
                                }
                            } else {
                                continuation.resume(LicenseStatus.NotFound)
                            }
                        } else {
                            continuation.resume(LicenseStatus.NotFound)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        exception.printStackTrace()
                        continuation.resume(LicenseStatus.NotFound)
                    }
                }
        } catch (e: Exception) {
            if (continuation.isActive) {
                e.printStackTrace()
                continuation.resume(LicenseStatus.NotFound)
            }
        }
    }

    fun getOrCreateDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }
}
