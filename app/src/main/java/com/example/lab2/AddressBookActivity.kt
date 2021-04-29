package com.example.lab2

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.contact_child.view.*

class AddressBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)

        contact_list.layoutManager = LinearLayoutManager(this)

        btn_read_contact.setOnClickListener{
            val contactList : MutableList<ContactDTO> = ArrayList()
            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null)
            if (contacts != null) {
                while(contacts.moveToNext()){
                    val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    number = number.replace(" ","")
                    val photo_uri = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    val obj = ContactDTO()

                    obj.name = name
                    obj.number = number

                    if(photo_uri != null){
                        obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photo_uri))
                    }
                    contactList.add(obj)
                }
                contact_list.adapter = ContactAdapter(contactList, this)
                contacts.close()
            }
        }

        btn_get_cryt_contact.setOnClickListener{
            val num = editPhoneNum.text.toString()
            val contactList : MutableList<ContactDTO> = ArrayList()
            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null)
            if (contacts != null && num != "" ) {
                while(contacts.moveToNext()){
                    val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    number = number.replace(" ","")
                    val num1 = num.removePrefix("+38")
                    val number1 = number.removePrefix("+38")
                    val photo_uri = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    val obj = ContactDTO()

                    if(number.startsWith(num) || number.startsWith(num1) || number1.startsWith(num)){
                        obj.name = name
                        obj.number = number

                        if(photo_uri != null){
                            obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photo_uri))
                        }
                        contactList.add(obj)
                    }
                }
                contact_list.adapter = ContactAdapter(contactList, this)
                contacts.close()
            }
            else{
                Toast.makeText(applicationContext, "Введіть, будь ласка, перші цифри номеру!", Toast.LENGTH_SHORT).show()
            }
            editPhoneNum.setText("")
        }
    }

    class ContactAdapter(items : List<ContactDTO>,ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].number
            if (list[position].image != null)
                holder.profile.setImageBitmap(list[position].image)
            else
                holder.profile.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.ic_launcher_round
                    )
                )
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ContactAdapter.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.contact_child, parent, false)
            )
        }


        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name = v.tv_name!!
            val number = v.tv_number!!
            val profile = v.iv_profile!!
        }
    }
}