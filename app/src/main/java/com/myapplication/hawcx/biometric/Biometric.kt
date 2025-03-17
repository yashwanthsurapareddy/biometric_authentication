package com.myapplication.hawcx.biometric

import com.myapplication.hawcx.AuthRequest
import com.myapplication.hawcx.RetrofitClient
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Base64
import java.util.concurrent.Executor
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class BiometricHelper(
    private val activity: FragmentActivity,
    private val userId: String,
    private val callback: (Boolean, String?) -> Unit
) {
    private val executor : Executor = ContextCompat.getMainExecutor(activity)
    fun authenticate() {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback(false, null)
                return
            }
            else -> {
                callback(false, null)
                return
            }
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Verify your identity")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult){
                    super.onAuthenticationSucceeded(result)
                    val token = generateSecureToken(userId)
                    callback(true, token)
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback(false, null)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback(false, null)
                }
            }
        )
        biometricPrompt.authenticate(promptInfo)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateSecureToken(userId: String): String {
        val deviceId = getDeviceId()
        val expires = System.currentTimeMillis() + 5 * 60 * 1000
        val json = JSONObject().apply{
            put("userId", userId)
            put("deviceId", deviceId)
            put("expires", expires)
        }
        val payload = json.toString()
        val signature = hmacSha256(payload, SECRET_KEY)
        return Base64.getEncoder().encodeToString("$payload.$signature".toByteArray())
    }
    private fun getDeviceId(): String{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Build.HARDWARE
        }
        else{
            Build.FINGERPRINT
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun hmacSha256(data: String, secret: String): String {
        val hmacSha256 = "HmacSHA256" // Hash based Message authentication code - Secure Hash Algorithm
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), hmacSha256)
        val mac = Mac.getInstance(hmacSha256)
        mac.init(secretKeySpec)
        return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
    }
    companion object {
        private const val SECRET_KEY = "secure-secret-key"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAuthRequest(email : String, token: String){
        val decoded = String(Base64.getDecoder().decode(token))
        val json = JSONObject(decoded.split(".")[0])

        val expires = json.getLong("expires")
        if (System.currentTimeMillis() > expires) {
            callback(false, "Token Expired!")
            return
        }
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = RetrofitClient.authService.authenticateUser(AuthRequest(email, token))
                if(response.isSuccessful){
                    val authResponse = response.body()
                    if(authResponse != null && authResponse.success){
                        callback(true, "Access Granted")
                    }
                    else{
                        callback(false, "Access Denied")
                    }
                }
                else{
                    callback(false, "Server Error")
                }
            }catch(e : Exception){
                callback(false, "API Error")
            }
        }
    }

}
