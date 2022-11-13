package com.vladimirorlov.homeassignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vladimirorlov.homeassignment.databinding.ActivityMainBinding
import com.vladimirorlov.homeassignment.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contactList = ArrayList<Contact>()
    private val firebaseUser = FirebaseAuth.getInstance()
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addUserToContacts()

        adapter = ContactsAdapter(
            contactList,
            onContactCardClick(),
            this
        )
        createSearchView()
        createRecyclerView()
        logout()
    }


    private fun createRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        getContact()
    }

    private fun createSearchView() {
        val searchView: SearchView = binding.searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterList(newText)
                }
                return true
            }

        })
    }

    private fun filterList(newText: String) {
        val filteredList: ArrayList<Contact> = arrayListOf()
        for (contact: Contact in contactList) {
            if (contact.name!!.lowercase().contains(newText.lowercase())) {
                filteredList.add(contact)
            }
        }
        if (filteredList.isEmpty()) {
            Snackbar.make(binding.activityMain, "No Contact found!", Snackbar.LENGTH_SHORT).show()
        } else {
            adapter.setFilteredList(filteredList)
        }
    }

    private fun onContactCardClick(): (contact: Contact) -> Unit = { contact ->

        if (!contactList.contains(contact)) {

            val uId = firebaseUser.currentUser!!.providerId
            Snackbar.make(binding.activityMain, uId, Snackbar.LENGTH_SHORT).show()

        } else {
            requestContactAppInvitation(contact)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun requestContactAppInvitation(contact: Contact) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "My app name")
        var strShareMessage = "Hey ${contact.name}!\nLet me recommend you this application\n\n"
        strShareMessage =
            strShareMessage + "https://play.google.com/store/apps/details?id=" + packageName
        i.putExtra(Intent.EXTRA_TEXT, strShareMessage)

        if (i.resolveActivity(
                packageManager
            ) == null
        ) {
            Snackbar.make(
                binding.activityMain,
                "Please install whatsapp first.",
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
        startActivity(Intent.createChooser(i, "Share via"))

    }

    @SuppressLint("Range")
    private fun getContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)
        }
        contactList.clear()
        val cursor: Cursor? = this.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ), null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        while (cursor!!.moveToNext()) {
            val contactName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val contactPhone =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val contactModel = Contact(contactName, contactPhone)
            contactList.add(contactModel)
        }
        cursor.close()
    }


    private fun addUserToContacts() {
        binding.addButton.setOnClickListener {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION)
            intent.type = ContactsContract.RawContacts.CONTENT_TYPE
            startActivity(intent)
        }
    }

    private fun logout() {
        binding.idLogout.setOnClickListener {
            firebaseUser.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}