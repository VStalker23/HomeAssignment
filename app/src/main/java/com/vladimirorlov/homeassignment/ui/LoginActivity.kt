package com.vladimirorlov.homeassignment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.vladimirorlov.homeassignment.R
import com.vladimirorlov.homeassignment.model.FirebaseManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    private lateinit var number: String
    private lateinit var phoneNumber: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val Login = findViewById<Button>(R.id.loginBtn)

        Login.setOnClickListener {
            login()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val intent = Intent(applicationContext, Verify::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("resendToken", token)
                intent.putExtra("phoneNumber", number)
                startActivity(intent)
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val mobileNumber = findViewById<EditText>(R.id.phoneNumber)
        number = mobileNumber.text.toString().trim()

        if (number.isNotEmpty()) {
            number = "+972$number"
            checkIfContactIsAlreadySignedIn(number)
        } else {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfContactIsAlreadySignedIn(phoneNumber: String) {
        val contactsRef = FirebaseManager.getInstance(this).db.collection("contacts")

        contactsRef.document(phoneNumber).get().addOnCompleteListener { snapshot ->
            if (snapshot.result.exists()) {
                Toast.makeText(applicationContext, "Welcome back", Toast.LENGTH_SHORT).show()
                val intent = Intent(this ,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                sendVerificationCode(number)
            }
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}