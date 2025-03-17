package com.myapplication.hawcx

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.myapplication.hawcx.biometric.BiometricHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun EmailUI(){
    var email by remember{mutableStateOf("")}
    var emailError by remember{mutableStateOf(false)}
    var authSuccess by remember{mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    var message by remember{mutableStateOf("")}
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    if(activity == null){
        return
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hawcx),
                contentDescription = "Hawcx",
                tint = Color.Unspecified,
                modifier = Modifier.size(130.dp)
                    .clip(RoundedCornerShape(25.dp))

            )
            Text(
                text = "Hawcx",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(55.dp))
            OutlinedTextField(value = email,
                onValueChange = {email = it
                    emailError = !isValidEmail(it)},
                isError = emailError,
                label = {Text("Email")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedIndicatorColor =  if(emailError) Color.Red else Color.Black,
                    unfocusedIndicatorColor =  if(emailError) Color.Red else Color.Gray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    errorContainerColor = Color.Transparent,
                    errorIndicatorColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (emailError) {
                Text(
                    text = "Invalid email format",
                    color = Color.Red,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = {
                message = "Starting biometric authentication..."
                BiometricHelper(activity, email) { success, token ->
                    authSuccess = success
                    message = if (success && token != null) {
                        "Authentication Successful, Token: $token"
                    } else {
                        "Authentication Failed!"
                    }
                }.authenticate()
                /*BiometricHelper(activity, email){success, response ->
                    message = response?: "Authentication Failed"
                }.authenticate()
                */
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = !emailError && email.isNotEmpty()){
                Text(
                    text = if(authSuccess) "Authenticated!" else "Continue",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = if(authSuccess) Color.Green else Color.Red,
                fontSize = 13.sp
            )
        }
    }
}
fun isValidEmail(email: String): Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
@Composable
@Preview(showBackground = true)
fun EmailUIPreview(){
    EmailUI()
}