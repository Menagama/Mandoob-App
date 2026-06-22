package com.mandoob.mena.util

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.util.zip.ZipInputStream

object ExcelReader {
    fun readXlsx(inputStream: InputStream): List<List<String>> {
        val sharedStrings = mutableListOf<String>()
        var sheetBytes: ByteArray? = null
        var sharedStringsBytes: ByteArray? = null

        try {
            val zip = ZipInputStream(inputStream)
            var entry = zip.nextEntry
            while (entry != null) {
                val name = entry.name
                if (name == "xl/sharedStrings.xml") {
                    sharedStringsBytes = zip.readBytes()
                } else if (name == "xl/worksheets/sheet1.xml") {
                    sheetBytes = zip.readBytes()
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
            zip.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        // 1. Parse sharedStrings.xml
        if (sharedStringsBytes != null) {
            try {
                val parser = Xml.newPullParser()
                parser.setInput(sharedStringsBytes.inputStream(), "UTF-8")
                var eventType = parser.eventType
                var inT = false
                val currentString = StringBuilder()
                
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tag = parser.name
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (tag == "t") {
                                inT = true
                                currentString.setLength(0)
                            }
                        }
                        XmlPullParser.TEXT -> {
                            if (inT) {
                                currentString.append(parser.text)
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (tag == "t") {
                                inT = false
                            } else if (tag == "si") {
                                sharedStrings.add(currentString.toString())
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Parse sheet1.xml
        val result = mutableListOf<MutableList<String>>()
        if (sheetBytes != null) {
            try {
                val parser = Xml.newPullParser()
                parser.setInput(sheetBytes.inputStream(), "UTF-8")
                var eventType = parser.eventType
                
                var currentCellRef = ""
                var currentCellType = ""
                val currentCellValue = StringBuilder()
                var currentRowIndex = -1
                var currentRowCells = mutableMapOf<Int, String>() // ColIndex to Value
                
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tag = parser.name
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (tag == "row") {
                                val rAttr = parser.getAttributeValue(null, "r")
                                currentRowIndex = rAttr?.toIntOrNull() ?: (currentRowIndex + 1)
                                currentRowCells = mutableMapOf()
                            } else if (tag == "c") {
                                currentCellRef = parser.getAttributeValue(null, "r") ?: ""
                                currentCellType = parser.getAttributeValue(null, "t") ?: ""
                                currentCellValue.setLength(0)
                            }
                        }
                        XmlPullParser.TEXT -> {
                            currentCellValue.append(parser.text)
                        }
                        XmlPullParser.END_TAG -> {
                            if (tag == "c") {
                                val rawVal = currentCellValue.toString()
                                val colIndex = cellRefToColIndex(currentCellRef)
                                if (colIndex >= 0) {
                                    val finalVal = if (currentCellType == "s") {
                                        val idx = rawVal.toIntOrNull()
                                        if (idx != null && idx >= 0 && idx < sharedStrings.size) {
                                            sharedStrings[idx]
                                        } else {
                                            rawVal
                                        }
                                    } else {
                                        rawVal
                                    }
                                    currentRowCells[colIndex] = finalVal
                                }
                            } else if (tag == "row") {
                                if (currentRowCells.isNotEmpty()) {
                                    val maxCol = currentRowCells.keys.maxOrNull() ?: -1
                                    val rowList = mutableListOf<String>()
                                    for (i in 0..maxCol) {
                                        rowList.add(currentRowCells[i] ?: "")
                                    }
                                    result.add(rowList)
                                }
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun cellRefToColIndex(ref: String): Int {
        if (ref.isEmpty()) return -1
        var colStr = ""
        for (char in ref) {
            if (char.isLetter()) {
                colStr += char.uppercaseChar()
            } else {
                break
            }
        }
        if (colStr.isEmpty()) return -1
        var idx = 0
        for (i in 0 until colStr.length) {
            idx = idx * 26 + (colStr[i] - 'A' + 1)
        }
        return idx - 1
    }
}
