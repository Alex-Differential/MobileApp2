package com.example.lab2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_request.*
import kotlinx.android.synthetic.main.activity_work_with_db.*
import java.lang.Exception

class RequestActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        var helper = RegionDBHelper(applicationContext)
        var db = helper.readableDatabase
        val databaseHandler : DataBaseHandler = DataBaseHandler(this)

        val status = databaseHandler.qwe()
        viewEvrgPopul.setText(status.toString()+" людей")
        //Toast.makeText(applicationContext, status, Toast.LENGTH_LONG).show()


        btn_regions_request.setOnClickListener{
            try{
                val populationRegion = editNumPopulRegion.text.toString().toInt()
                if(populationRegion.toString() != ""){
                    setupListofDataIntoRecyclerView(populationRegion)
                }
            }catch(e:Exception){
                Toast.makeText(applicationContext, "Поле має бути заповнено!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getRegionsList(Population:Int): ArrayList<Region> {

        val databaseHandler: DataBaseHandler = DataBaseHandler(this)
        val regList: ArrayList<Region> = databaseHandler.viewRegion(Population)

        return regList
    }

    private fun setupListofDataIntoRecyclerView(Population:Int) {

        if (getRegionsList(Population).size > 0) {

            rvRegionsList.visibility = View.VISIBLE

            rvRegionsList.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getRegionsList(Population))
            rvRegionsList.adapter = itemAdapter
        } else {

            rvRegionsList.visibility = View.GONE
        }
    }
}