package com.example.jankenteamb.utils

import com.example.jankenteamb.model.RepositoryResult
import com.google.android.gms.tasks.Task

import kotlinx.coroutines.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine as suspendCancellableCoroutine1

object FirebaseTaskExtension{
    // Task Extension
    suspend fun <T> Task<T>.awaits(): RepositoryResult<T> {
        if (isComplete) {
            val e = exception
            return if (e == null) {
                if (isCanceled) {
                    RepositoryResult.Canceled(CancellationException("Task $this was cancelled normally."))
                } else {
                    @Suppress("UNCHECKED_CAST")
                    RepositoryResult.Success(result as T)
                }
            } else {
                RepositoryResult.Error(e)
            }
        }

        return suspendCancellableCoroutine1 { cont ->
            addOnCompleteListener {
                val e = exception
                if (e == null) {
                    @Suppress("UNCHECKED_CAST")
                    if (isCanceled) cont.cancel() else  cont.resume(RepositoryResult.Success(result as T))
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }
}

