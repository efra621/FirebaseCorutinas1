package com.gratum.firebasecorutinas1.firebase.cloudfirestore

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

    suspend fun update(colletionPath: String, documentPath: String, map: MutableMap<String, Any>): Void{

        return suspendCoroutine { continuation ->

            Firebase.firestore.collection(colletionPath).document(documentPath).update(map)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun delete(colletionPath: String,documentPath: String):Void{

        return suspendCoroutine {continuation ->

            Firebase.firestore.collection(colletionPath).document(documentPath).delete()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }
    }

}