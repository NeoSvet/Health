package ru.neosvet.health.data

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository : Repository {
    companion object {
        private const val COLLECTION_NAME = "health"
        private const val TIME = "time"
        private const val HIGH_PRESSURE = "highPressure"
        private const val LOW_PRESSURE = "lowPressure"
        private const val PULSE = "pulse"
    }

    private val db = Firebase.firestore

    override suspend fun getList(): List<HealthEntity> {
        return suspendCoroutine { cont ->
            db.collection(COLLECTION_NAME)
                .orderBy(TIME, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val list = getListFrom(result)
                    cont.resume(list)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun delete(id: String) {
        return suspendCoroutine { cont ->
            db.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .addOnSuccessListener {
                    cont.resume(Unit)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    private fun getListFrom(result: QuerySnapshot): List<HealthEntity> {
        val list = mutableListOf<HealthEntity>()
        for (doc in result) {
            val item = HealthEntity(
                id = doc.id,
                time = doc[TIME] as Long,
                highPressure = doc.getInt(HIGH_PRESSURE),
                lowPressure = doc.getInt(LOW_PRESSURE),
                pulse = doc.getInt(PULSE)
            )
            list.add(item)
        }
        return list
    }

    private fun QueryDocumentSnapshot.getInt(key: String): Int =
        (this[key] as Long).toInt()

    override suspend fun add(time: Long, highPressure: Int, lowPressure: Int, pulse: Int) {
        val item = hashMapOf(
            TIME to time,
            HIGH_PRESSURE to highPressure,
            LOW_PRESSURE to lowPressure,
            PULSE to pulse
        )
        return suspendCoroutine { cont ->
            db.collection(COLLECTION_NAME).add(item)
                .addOnSuccessListener {
                    cont.resume(Unit)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }
}