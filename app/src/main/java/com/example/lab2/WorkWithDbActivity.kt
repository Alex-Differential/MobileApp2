   package com.example.lab2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_work_with_db.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.item_row.*

   class WorkWithDbActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_with_db)

        var helper = RegionDBHelper(applicationContext)
        var db = helper.readableDatabase
        var rs = db.rawQuery("SELECT * FROM REGION", null)

        setupListofDataIntoRecyclerView()
        //ivDelete.visibility =View.VISIBLE
        saveDBbtn.setOnClickListener{
            if(editNameRegion.text.toString() != "" && editPopulation.text.toString() != "" && editSquare.text.toString() != "" && editRegCenter.text.toString() != "" ){
                addRecord()
                setupListofDataIntoRecyclerView()
            }
            else{
                Toast.makeText(applicationContext, "Всі поля мають бути заповнені!",Toast.LENGTH_LONG).show()
            }

        }
        btnWorkWithDB.setOnClickListener{
            val intent = Intent(this, RequestActivity::class.java)
            startActivity(intent)
        }
        btn_exit_work_db.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private fun addRecord(){
        val name = editNameRegion.text.toString()
        val population = editPopulation.text.toString().toInt()
        val square = editSquare.text.toString().toDouble()
        val regcenter = editRegCenter.text.toString()

        val databaseHandler : DataBaseHandler = DataBaseHandler(this)
        if(!name.isEmpty() && regcenter.isNotEmpty()){
            val status = databaseHandler.addRegion(Region(0,name,population,square,regcenter))
            if(status > -1){
                Toast.makeText(applicationContext, "Запис успішно збережено",Toast.LENGTH_LONG).show()
                editNameRegion.text.clear()
                editPopulation.text.clear()
                editSquare.text.clear()
                editRegCenter.text.clear()
            }
        }
        else{
            Toast.makeText(applicationContext, "NIT",Toast.LENGTH_LONG).show()
        }
    }

       private fun getItemsList(): ArrayList<Region> {

           val databaseHandler: DataBaseHandler = DataBaseHandler(this)
           val regList: ArrayList<Region> = databaseHandler.viewEmployee()

           return regList
       }

       private fun setupListofDataIntoRecyclerView() {

           if (getItemsList().size > 0) {

               rvItemsList.visibility = View.VISIBLE

               rvItemsList.layoutManager = LinearLayoutManager(this)
               val itemAdapter = ItemAdapter(this, getItemsList())
               rvItemsList.adapter = itemAdapter
           } else {

               rvItemsList.visibility = View.GONE
           }
       }

       fun updateRecordDialog(regModelClass: Region) {
           val updateDialog = Dialog(this, R.style.Theme_Dialog)
           updateDialog.setCancelable(false)

           updateDialog.setContentView(R.layout.dialog_update)

           updateDialog.etUpdateName.setText(regModelClass.name)
           updateDialog.etUpdateRegCenter.setText(regModelClass.regCenter)
           updateDialog.etUpdateSquare.setText(regModelClass.square.toString())
           updateDialog.etUpdatePopulation.setText(regModelClass.population.toString())

           updateDialog.tvUpdate.setOnClickListener(View.OnClickListener {

               val name = updateDialog.etUpdateName.text.toString()
               val regCenter = updateDialog.etUpdateRegCenter.text.toString()
               val population = updateDialog.etUpdatePopulation.text.toString().toInt()
               val square = updateDialog.etUpdateSquare.text.toString().toDouble()

               val databaseHandler: DataBaseHandler = DataBaseHandler(this)

               if (!name.isEmpty() && !regCenter.isEmpty()) {
                   val status =
                           databaseHandler.updateEmployee(Region(regModelClass.id, name, population,square,regCenter))
                   if (status > -1) {
                       Toast.makeText(applicationContext, "Регіон оновлено", Toast.LENGTH_LONG).show()

                       setupListofDataIntoRecyclerView()

                       updateDialog.dismiss()
                   }
               } else {
                   Toast.makeText(
                           applicationContext,
                           "Назва області та її центр мають бути обов'язково!",
                           Toast.LENGTH_LONG
                   ).show()
               }
           })
           updateDialog.tvCancel.setOnClickListener(View.OnClickListener {
               updateDialog.dismiss()
           })

           updateDialog.show()
       }

       fun deleteRecordAlertDialog(regModelClass: Region) {
           val builder = AlertDialog.Builder(this)
           builder.setTitle("Видалення запису")

           builder.setMessage("Ви дійсно хочете видалити ${regModelClass.name} область?")
           builder.setIcon(android.R.drawable.ic_dialog_alert)

           builder.setPositiveButton("Так") { dialogInterface, which ->

               //creating the instance of DatabaseHandler class
               val databaseHandler: DataBaseHandler = DataBaseHandler(this)
               //calling the deleteEmployee method of DatabaseHandler class to delete record
               val status = databaseHandler.deleteRegion(Region(regModelClass.id, regModelClass.name, regModelClass.population, regModelClass.square,regModelClass.regCenter))
               if (status > -1) {
                   Toast.makeText(
                           applicationContext,
                           "Запис успішно видалено",
                           Toast.LENGTH_LONG
                   ).show()

                   setupListofDataIntoRecyclerView()
               }
               dialogInterface.dismiss()
           }

           builder.setNegativeButton("Ніт") { dialogInterface, which ->
               dialogInterface.dismiss()
           }

           val alertDialog: AlertDialog = builder.create()
           alertDialog.setCancelable(false)
           alertDialog.show()
       }
}