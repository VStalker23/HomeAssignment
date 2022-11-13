package com.vladimirorlov.homeassignment.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vladimirorlov.homeassignment.R
import com.vladimirorlov.homeassignment.databinding.ContactsListItemBinding
import com.vladimirorlov.homeassignment.model.Contact

class ContactsAdapter(

    private var contactList: ArrayList<Contact>,
    private val onContactCardClick: (contact: Contact) -> Unit,
    val context: Context
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(){

    class ViewHolder(
        binding: ContactsListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val colors : ArrayList<Int> =
            arrayListOf(R.color.black, R.color.purple_200, R.color.purple_500, R.color.teal_700)
        val contactCard: CardView = itemView.findViewById(R.id.contact_card)
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactNumber : TextView = itemView.findViewById(R.id.contact_number)
        val contactImage : ImageView = itemView.findViewById(R.id.contact_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ContactsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contact = contactList[position]

        holder.contactName.text = contact.name
        holder.contactNumber.text = contact.phoneNumber

        val contactImage = holder.colors

        holder.contactImage.setImageResource(contactImage.random())

        holder.contactCard.setOnClickListener {
            onContactCardClick(contact)
        }
    }

    fun setFilteredList(filteredList : ArrayList<Contact>) {
        this.contactList = filteredList
        notifyDataSetChanged()
    }

    fun heyAdapterPleaseUpdateTheView(contactList: ArrayList<Contact>) {
        contactList.clear()
        contactList.addAll(contactList)
        notifyDataSetChanged()
    }

}