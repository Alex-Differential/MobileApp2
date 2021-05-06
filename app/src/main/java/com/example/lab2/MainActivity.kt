package com.example.lab2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import  android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.info_task.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainInfoTaskBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.info_task, null);

            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Інформація про додаток")

            val mAlertDialog = mBuilder.show()
            mDialogView.okInfoTask.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        mainInfoOwnBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.info_own, null);

            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Інформація про автора")

            val mAlertDialog = mBuilder.show()
        }

        startApplication.setOnClickListener{
            val intent = Intent(this, WorkWithDbActivity::class.java)
            startActivity(intent)
        }

        goAddressBook.setOnClickListener{
            val intent = Intent(this, AddressBookActivity::class.java)
            startActivity(intent)
        }
        btn_go_to_map.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}