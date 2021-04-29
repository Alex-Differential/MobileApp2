package com.example.lab2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_request.*
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter(val context: Context, val items: ArrayList<Region>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_row,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)

        holder.tvName.text = item.name
        holder.tvPopulation.text = item.population.toString()
        holder.tvSquare.text = item.square.toString()
        holder.tvRegCenter.text = item.regCenter

        holder.tvPopulation.setText(holder.tvPopulation.text.toString() +" осіб")
        //holder.tvSquare.setText(holder.tvSquare.text.toString() +" км2")

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.colorLightGray
                    )
            )
        } else {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        }

        holder.ivEdit.setOnClickListener { view ->
            if (context is WorkWithDbActivity) {
                context.updateRecordDialog(item)
            }
        }

        holder.ivDelete.setOnClickListener { view ->

            if (context is WorkWithDbActivity) {
                context.deleteRecordAlertDialog(item)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val llMain = view.llMain
        val tvName = view.tvName
        val tvPopulation = view.tvPopulation
        val tvSquare = view.tvSquare
        val tvRegCenter = view.tvRegCenter
        val ivEdit = view.ivEdit
        val ivDelete = view.ivDelete
    }

}