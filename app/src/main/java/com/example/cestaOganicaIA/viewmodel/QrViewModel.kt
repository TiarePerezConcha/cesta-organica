package com.example.cestaOganicaIA.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cestaOganicaIA.data.model.QrResult

class QrViewModel : ViewModel() {
    private val _qrResult = MutableLiveData<QrResult?>()
    val qrResult: LiveData<QrResult?> = _qrResult

    fun onQrDetected(content: String) { _qrResult.value = QrResult(content) }
    fun clearResult() { _qrResult.value = null }
}
