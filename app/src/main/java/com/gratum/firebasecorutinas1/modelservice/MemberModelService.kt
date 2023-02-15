package com.gratum.firebasecorutinas1.modelservice

import com.google.firebase.firestore.QuerySnapshot
import com.gratum.firebasecorutinas1.firebase.cloudfirestore.CloudFileStoreWrapper
import com.gratum.firebasecorutinas1.model.MemberModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MemberModelService {

    suspend fun register(memberModel: MemberModel): Void = withContext(Dispatchers.IO){
        return@withContext CloudFileStoreWrapper.replace(
            MemberModel.CLOUD_FIRE_STORE_PATH,
            memberModel.uuId, //uuId as document path of firebase fire store database
            memberModel.toDictionary()
        )
    }

    //input id and password to get member info from firebase fire store database
    //you can add something like jwt token
    //this example will use id and password

    suspend fun signIn(id: String, password: String): QuerySnapshot = withContext(Dispatchers.IO){

        val map = mutableMapOf<String, Any>()
        map[MemberModel.ID_KEY] = id
        map[MemberModel.PASSWORD_KEY] = password

        return@withContext CloudFileStoreWrapper.select(
            MemberModel.CLOUD_FIRE_STORE_PATH,
            map
            )
    }
}