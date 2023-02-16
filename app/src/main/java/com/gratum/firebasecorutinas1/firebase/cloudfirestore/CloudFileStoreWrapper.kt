package com.gratum.firebasecorutinas1.firebase.cloudfirestore

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//Creamos el contenedor de la base de datos
object CloudFileStoreWrapper {

    //Suspend se ocupan de las tareas de larga duracion
    //Pausa la ejecucion de una corutina actual y guarda las variables locales
    suspend fun replace(
        colletionPath: String,
        documentPath: String,
        map: MutableMap<String, Any>
    ): Void {

        //Corutina encarcagada de subir el mapa cargado a firebase
        return suspendCoroutine {
            Firebase.firestore.collection(colletionPath).document(documentPath).set(map)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
    }

    //
    suspend fun select(
        colletionPath: String,
        conditionMap: MutableMap<String, Any>? = null,
        limit: Long = 1
    ): QuerySnapshot {

        return suspendCoroutine { continuation ->
            val collectionReferece = Firebase.firestore.collection(colletionPath)

            //select one default
            var query = collectionReferece.limit(limit)

            conditionMap?.let {
                it.forEach { map ->
                    query = collectionReferece.whereEqualTo(map.key, map.value)
                }
            }

            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun select(
        colletionPath: String,
        conditionMap: MutableMap<String, Any>? = null,
        orderBy: String? = null,
        offset: DocumentSnapshot? = null,
        limit: Long = 1
    ): QuerySnapshot {

        return suspendCoroutine { continuation ->
            val collectionReferece = Firebase.firestore.collection(colletionPath)

            //Create query
            //the order mus be 1-order by, 2-offset, 3-limit, 4-condition
            //dont change the query order
            var query: Query? = null

            //1 order by
            orderBy?.let {
                //we will order by create date , desc
                query = collectionReferece.orderBy(it, Query.Direction.DESCENDING)
            }
            //2 offset
            orderBy?.let { offset ->
                //check query is null or not
                query?.let {
                    query = it.startAfter(offset)
                } ?: kotlin.run {
                    query = collectionReferece.limit(limit)
                }
            }

            // 3 limit
            query?.let {
                query = it.limit(limit)

            } ?: kotlin.run {
                query = collectionReferece.limit(limit)
            }

            // 4 limit
            // this condition may ask to create a index
            // we will create index when we test it
            conditionMap?.let {
                it.forEach { map ->
                    //codition map example id = xxxx, password = xxxx
                    query = collectionReferece.whereEqualTo(map.key, map.value)
                }
            }

            conditionMap?.let {
                it.forEach { map ->
                    query = collectionReferece.whereEqualTo(map.key, map.value)
                }
            }

            query?.get()
                ?.addOnSuccessListener {
                    continuation.resume(it)
                }
                ?.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun update(
        colletionPath: String,
        documentPath: String,
        map: MutableMap<String, Any>
    ): Void {

        return suspendCoroutine { continuation ->

            Firebase.firestore.collection(colletionPath).document(documentPath).update(map)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun delete(colletionPath: String, documentPath: String): Void {

        return suspendCoroutine { continuation ->

            Firebase.firestore.collection(colletionPath).document(documentPath).delete()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

}