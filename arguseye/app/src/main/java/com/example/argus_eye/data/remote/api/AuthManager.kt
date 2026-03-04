package com.example.argus_eye.data.remote.api

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.*

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(serverClientId: String): Boolean {
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d("AuthManager", "Requesting Google credential...")
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            // Use the constant string for comparison to avoid metadata mismatch issues
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Log.d("AuthManager", "Received Google ID token. Signing into Firebase...")
                
                // Manually create the credential from the result data
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                Log.d("AuthManager", "Firebase Sign-In successful.")
                true
            } else {
                Log.e("AuthManager", "Received unexpected credential type: ${credential.type}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Google Sign-In error", e)
            false
        }
    }

    suspend fun signInWithGithub(activity: Activity): Boolean {
        return try {
            val provider = OAuthProvider.newBuilder("github.com")
            provider.scopes = listOf("user:email")
            
            auth.startActivityForSignInWithProvider(activity, provider.build()).await()
            true
        } catch (e: Exception) {
            Log.e("AuthManager", "Github Sign-In error", e)
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser
}
