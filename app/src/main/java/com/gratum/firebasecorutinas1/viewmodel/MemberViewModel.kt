package com.gratum.firebasecorutinas1.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.core.OrderBy
import com.gratum.firebasecorutinas1.firebase.storage.FirebaseStorageManager
import com.gratum.firebasecorutinas1.model.MemberModel
import com.gratum.firebasecorutinas1.modelservice.MemberModelService
import com.gratum.firebasecorutinas1.modelservice.TestModelService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class MemberViewModelState {

    data class RegisterSuccessfully(val memberModel: MemberModel) : MemberViewModelState()
    data class SingSuccessfully(val memberModel: MemberModel) : MemberViewModelState()
    data class Error(val message: String) : MemberViewModelState()

    data class UpdateSuccessfully(val memberModel: MemberModel) : MemberViewModelState()
    data class DeleteSuccessfully(val uuId: String) : MemberViewModelState()


    object Empty : MemberViewModelState()
    object Loading : MemberViewModelState()
    object None : MemberViewModelState()

    data class ReloadSuccessfully(val offset: DocumentSnapshot, val list: List<MemberModel>): MemberViewModelState()
    data class LoadMoreSuccessfully(val offset: DocumentSnapshot, val list: List<MemberModel>): MemberViewModelState()

}

class MemberViewModel : ViewModel() {

    private val _memberViewModelState =
        MutableStateFlow<MemberViewModelState>(MemberViewModelState.None)
    val memberViewModelState: StateFlow<MemberViewModelState> = _memberViewModelState

    //Añadimos la imgaen url para registrar la coleccion y el imageFileName para saber donde guardarlo
    fun register(imageUri: Uri, imageFileName: String, memberModel: MemberModel) =
        viewModelScope.launch {

            _memberViewModelState.value = MemberViewModelState.Loading

            try {
                coroutineScope {

                    val uploadImage = async {
                        MemberModelService.uploadImageFile(imageUri, imageFileName)
                    }
                    val imageUrl = uploadImage.await()

                    //image in your storage, the path is Member/xxxxxxxxxx.jpg
                    //keep the path, because when you want delete the image from storage, need the path
                    val imagePath = "${FirebaseStorageManager.MEMBER_IMAGE_FOLDER}$imageFileName"

                    //update image url and image path of the member model
                    memberModel.profileImageUrl = imageUrl
                    memberModel.profileImageFileCloudPath = imagePath

                    //then register
                    val register = async {
                        MemberModelService.register(memberModel)
                    }
                    register.await()
                    _memberViewModelState.value =
                        MemberViewModelState.RegisterSuccessfully(memberModel)
                }

            } catch (e: java.lang.Exception) {
                _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
            }
        }

    fun signIn(id: String, password: String) = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading
        try {
            coroutineScope {
                val signIn = async {
                    MemberModelService.signIn(id, password)
                }
                val querySnapshot = signIn.await()
                val list = mutableListOf<MutableMap<String, Any>>()
                for (document in querySnapshot.documents) {
                    list.add(document.data as MutableMap<String, Any>)
                }
                if (list.isEmpty()) {
                    _memberViewModelState.value = MemberViewModelState.Empty
                    return@coroutineScope
                }
                val memberModel = MemberModel()
                memberModel.parsing(list[0])

                _memberViewModelState.value = MemberViewModelState.SingSuccessfully(memberModel)
            }
        } catch (e: Exception) {
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
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            _memberViewModelState.value = MemberViewModelState.Error(e.message.toString())
        }

    }

    fun reload(orderBy: String, limit: Long = 1, tag: String = "") = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading

        try {

            coroutineScope {
                val reload = async {
                    TestModelService.fetch(orderBy, null, limit, tag)
                }
                val querySnapshot = reload.await()

                val list = mutableListOf<MutableMap<String, Any>>()

                for (document in querySnapshot.documents) {
                    list.add(document.data as MutableMap<String, Any>)
                }
                if (list.isEmpty()){
                    _memberViewModelState.value = MemberViewModelState.Empty
                }
                val testList = mutableListOf<MemberModel>()

                var testModel: MemberModel
                for (map in list){
                    testModel = MemberModel()
                    testModel.parsing(map)
                    testList.add(testModel)
                }
                //Documents last item as the offset flag
                _memberViewModelState.value= MemberViewModelState.ReloadSuccessfully(
                    querySnapshot.documents.last(),
                    testList
                )

            }

        } catch (e: Exception) {
            _memberViewModelState.value= MemberViewModelState.Error(e.message.toString())
        }
    }

    fun loadMore(orderBy: String,offset: DocumentSnapshot? = null, limit: Long = 1, tag: String = "") = viewModelScope.launch {

        _memberViewModelState.value = MemberViewModelState.Loading

        try {

            coroutineScope {
                val reload = async {
                    TestModelService.fetch(orderBy, offset, limit, tag)
                }
                val querySnapshot = reload.await()

                val list = mutableListOf<MutableMap<String, Any>>()

                for (document in querySnapshot.documents) {
                    list.add(document.data as MutableMap<String, Any>)
                }
                if (list.isEmpty()){
                    _memberViewModelState.value = MemberViewModelState.Empty
                }
                val testList = mutableListOf<MemberModel>()

                var testModel: MemberModel
                for (map in list){
                    testModel = MemberModel()
                    testModel.parsing(map)
                    testList.add(testModel)
                }
                //Documents last item as the offset flag
                _memberViewModelState.value= MemberViewModelState.LoadMoreSuccessfully(
                    querySnapshot.documents.last(),
                    testList
                )

            }

        } catch (e: Exception) {
            _memberViewModelState.value= MemberViewModelState.Error(e.message.toString())
        }
    }

}