package com.stephenbrough.jetpack_learning.amiibo_list_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.Amiibo
import com.stephenbrough.jetpack_learning.domain.AmiiboRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AmiiboListViewModel @Inject constructor(
    private val amiiboRepository: AmiiboRepository
) : ViewModel() {

    private var _amiibos = MutableStateFlow<List<Amiibo>>(emptyList())
    val amiibos = _amiibos
        .onStart {
            val amiibos = amiiboRepository.getAmiiboList()
            _amiibos.update { amiibos.amiibo }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


}