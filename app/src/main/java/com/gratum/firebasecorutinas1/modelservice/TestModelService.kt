package com.gratum.firebasecorutinas1.modelservice

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.core.OrderBy
import com.gratum.firebasecorutinas1.firebase.cloudfirestore.CloudFileStoreWrapper
import com.gratum.firebasecorutinas1.model.MemberModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TestModelService {

    suspend fun fetch(
        orderBy: String,
        offset: DocumentSnapshot? = null,
        limit:Long = 1,
        tag: String = ""
    ): QuerySnapshot = withContext(Dispatchers.IO){

        return@withContext CloudFileStoreWrapper.select(
            colletionPath = MemberModel.CLOUD_FIRE_STORE_PATH,
            orderBy = orderBy,
            offset = offset,
            limit = limit,
            conditionMap = if (tag == ""){
                null
            }else{
                mutableMapOf<String, Any>().apply {
                    put(MemberModel.ID_KEY, tag) //////////////////////5555555555555555
                }
            }
        )

    }

}