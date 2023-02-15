package com.gratum.firebasecorutinas1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratum.firebasecorutinas1.model.MemberModel
import com.gratum.firebasecorutinas1.modelservice.MemberModelService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class MemberViewModelState{

    data class RegisterSuccessfully(val memberModel: MemberModel): MemberViewModelState()
    data class SingSuccessfully(val memberModel: MemberModel): MemberViewModelState()
    data class Error(val message: String): MemberViewModelState()

    data class UpdateSuccessfully(val memberModel: MemberModel): MemberViewModelState()
    data class DeleteSuccessfully(val uuId: String): MemberViewModelState()


    object Empty: MemberViewModelState()
    object Loading: MemberViewModelState()
    object None: MemberViewModelState()

}

class MemberViewModel :ViewModel(){

    private val _memberViewModelState = MutableStateFlow<MemberViewModelState>(MemberViewModelState.None)
    val memberViewModelState : StateFlow<MemberViewModelState> = _memberViewModelState

    fun register(memberModel: MemberModel) = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading

        try {
            val register = async {
                MemberModelService.register(memberModel)
            }
            register.await()
            _memberViewModelState.value = MemberViewModelState.RegisterSuccessfully(memberModel)
        }catch (e: java.lang.Exception){
            _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
        }
    }

    fun signIn(id:String, password: String) = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading
        try {
            coroutineScope {
                val signIn = async {
                    MemberModelService.signIn(id,password)
                }
                val querySnapshot = signIn.await()
                val list = mutableListOf<MutableMap<String,Any>>()
                for (document in querySnapshot.documents){
                    list.add(document.data as MutableMap<String, Any>)
                }
                if (list.isEmpty()){
                    _memberViewModelState.value = MemberViewModelState.Empty
                    return@coroutineScope
                }
                val memberModel = MemberModel()
                memberModel.parsing(list[0])

                _memberViewModelState.value = MemberViewModelState.SingSuccessfully(memberModel)
            }
        }catch (e:Exception){
            _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
        }
    }

    fun modify(memberModel: MemberModel) = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading

        try {
            coroutineScope {
                val modify = async {
                    MemberModelService.modify(memberModel)
                }
                modify.await()
                _memberViewModelState.value = MemberViewModelState.UpdateSuccessfully(memberModel)
            }
        }catch (e:Exception){
            _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
        }
    }

    fun delete(uuId: String) = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading

        try {
            coroutineScope {
                val delete = async {
                    MemberModelService.delete(uuId)
                }
            }
        }catch (e: Exception){
            _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
        }

    }

}