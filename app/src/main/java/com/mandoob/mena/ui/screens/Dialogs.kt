package com.mandoob.mena.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mandoob.mena.data.Order
import com.mandoob.mena.ui.theme.BluePrimary
import com.mandoob.mena.ui.theme.CancelledRed
import com.mandoob.mena.viewmodel.OrderViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ---------------- ADD NEW ORDER DIALOG ----------------
@Composable
fun AddOrderDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, phone2: String?, address: String, amount: Double, notes: String?) -> Unit
) {
    val BluePrimary = MaterialTheme.colorScheme.primary
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "إضافة أوردر جديد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Client Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل") },
                        modifier = Modifier.fillMaxWidth().testTag("add_name_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Phone numbers
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم تليفون العميل (أساسي)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_phone_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        label = { Text("رقم تليفون ثانى (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Address Input
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان بالتفصيل") },
                        modifier = Modifier.fillMaxWidth().testTag("add_address_field"),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Collection Amount input
                item {
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("مبلغ التحصيل (ج.م)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_amount_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Notes Input
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("الملاحظات") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Actions Button Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("إلغاء", color = CancelledRed)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                                    return@Button
                                }
                                val amt = amountValue.toDoubleOrNull() ?: 0.0

                                onSave(name, phone, phone2.ifBlank { null }, address, amt, notes.ifBlank { null })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("حفظ الأوردر", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- EDIT EXISTING ORDER DIALOG ----------------
@Composable
fun EditOrderDialog(
    order: Order,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, phone2: String?, address: String, amount: Double, notes: String?) -> Unit
) {
    val BluePrimary = MaterialTheme.colorScheme.primary
    var name by remember { mutableStateOf(order.clientName) }
    var phone by remember { mutableStateOf(order.phoneNumber) }
    var phone2 by remember { mutableStateOf(order.phoneNumber2 ?: "") }
    var address by remember { mutableStateOf(order.address) }
    var amountValue by remember { mutableStateOf(order.amount.toString()) }
    var notes by remember { mutableStateOf(order.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "تعديل بيانات الأوردر",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Client Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Phone numbers
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم تليفون العميل (أساسي)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        label = { Text("رقم تليفون ثانى (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Address Input
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان بالتفصيل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Collection Amount input
                item {
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("مبلغ التحصيل (ج.م)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Notes Input
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("الملاحظات") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = BluePrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }

                // Actions Button Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("إلغاء", color = CancelledRed)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                                    return@Button
                                }
                                val amt = amountValue.toDoubleOrNull() ?: 0.0

                                onSave(name, phone, phone2.ifBlank { null }, address, amt, notes.ifBlank { null })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تعديل وحفظ الأوردر", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- EXCEL IMPORT SHEET DIALOG ----------------
@Composable
fun ImportExcelDialog(
    viewModel: OrderViewModel,
    onDismiss: () -> Unit
) {
    val BluePrimary = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    var pastedText by remember { mutableStateOf("") }
    var tabIndex by remember { mutableStateOf(0) } // 0 -> pasted text, 1 -> pick Excel file

    // Launcher file pick specifically for .xlsx sheets
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.viewModelScope.launch {
                val count = viewModel.importOrdersFromExcelUri(context, uri)
                if (count >= 0) {
                    Toast.makeText(context, "تم استيراد $count أوردر بنجاح من شيت الإكسيل! 🎉", Toast.LENGTH_LONG).show()
                    onDismiss()
                } else {
                    Toast.makeText(context, "فشل قراءة ملف الإكسيل المرفق، تأكد أنه ملف .xlsx صحيح وبنفس نسق الأعمدة", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Default excel-paste template
    val demoTemplate = """اسم العميل,رقم الموبيل,عنوان العميل,قيمة التحصيل
مصطفى على جابر,0129887766,شارع النزهة مصر الجديدة,400
عبد الرحمن العوضي,113456789,ميدان الجيزة بجانب المحكمة,0
سليمان رزق مطر,0159012454,شارع جمال عبد الناصر الإسكندرية,-150"""

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "استيراد أوردرات من إكسيل",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Subtitle Instruction
                Text(
                    text = "يجب أن يحتوي شيت الإكسيل بالترتيب على:\nالعمود الأول: الاسم، ثم المحمول (١١ رقم)، ثم العنوان، ثم قيمة التحصيل.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dialog Tabs View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabIndex == 0) Color(0xFF1B5E20) else Color.Transparent)
                            .clickable { tabIndex = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "نسخ ولصق ورقة",
                            color = if (tabIndex == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabIndex == 1) Color(0xFF1B5E20) else Color.Transparent)
                            .clickable { tabIndex = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "اختيار شيت إكسل",
                            color = if (tabIndex == 1) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (tabIndex == 0) {
                    // Paste Area
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("انسخ جدول بياناتك أو الصقه هنا مباشرة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = pastedText,
                            onValueChange = { pastedText = it },
                            placeholder = { Text("مثال:\n$demoTemplate", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            singleLine = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = Color(0xFF1B5E20),
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )

                        // 1-tap Demo Loader
                        Text(
                            text = "💡 اضغط هنا لملء نموذج تجريبي جاهز وتجربة الاستيراد فوراً!",
                            fontSize = 11.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { pastedText = demoTemplate }
                                .padding(vertical = 4.dp)
                        )
                    }
                } else {
                    // Choose File Button (Filter for sheets)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصفح ملفات الهاتف (Excel)", color = Color.White)
                        }

                        Text(
                            "قم باختيار ملف شيت الإكسيل (.xlsx) الأصلي ليقوم التطبيق بقراءته واستخلاص البيانات وتأسيس خط التوجيه تلقائياً.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Confirm Actions bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = CancelledRed)
                    }
                    if (tabIndex == 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (pastedText.isBlank()) return@Button
                                viewModel.viewModelScope.launch {
                                    val count = viewModel.importOrdersFromCsv(pastedText)
                                    Toast.makeText(
                                        context,
                                        "تم استيراد $count أوردر من النص المنسوخ بنجاح! 🚀",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            enabled = pastedText.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ابدأ الاستيراد", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
