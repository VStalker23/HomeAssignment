package com.vladimirorlov.homeassignment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.vladimirorlov.homeassignment.R
import com.vladimirorlov.homeassignment.databinding.ActivityVerifyBinding
import com.vladimirorlov.homeassignment.model.Contact
import com.vladimirorlov.homeassignment.model.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class Verify : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityVerifyBinding
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OTP = intent.getStringExtra("storedVerificationId").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        auth = FirebaseAuth.getInstance()

        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        val verify = findViewById<Button>(R.id.verifyBtn)
        val otpGiven = findViewById<EditText>(R.id.id_otp)


        verify.setOnClickListener {
            val otp = otpGiven.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                val contact = Contact(phoneNumber)
                FirebaseManager.getInstance(this).addContactToDatabase(contact)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}


