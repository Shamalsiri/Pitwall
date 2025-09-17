package com.ssiriwardana.pitwall.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssiriwardana.pitwall.domain.usecase.GetAllDriversUseCase
import com.ssiriwardana.pitwall.domain.usecase.GetDriverDetailsUseCase

class DriverViewModelFactory(
    private val getAllDriversUseCase: GetAllDriversUseCase,
    private val getDriverDetailsUseCase: GetDriverDetailsUseCase
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverViewModel::class.java)) {
            return DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase) as T
        }
        throw IllegalArgumentException("Viewmodel class unknown")
    }
}