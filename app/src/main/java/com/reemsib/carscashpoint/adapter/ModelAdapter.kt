package com.reemsib.carscashpoint.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.carscashpoint.R
import kotlinx.android.synthetic.main.model_item.view.*


class ModelAdapter(var activity: Activity, var mListener:RecyclerViewActionListener,var data: ArrayList<String>) :
    RecyclerView.Adapter<ModelAdapter.MyViewHolder>() {

  interface RecyclerViewActionListener {
    fun onViewClicked(clickedItemPosition: Int,model:String)
}

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var model = item.tv_model

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.model_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.model.text = data.get(position)
        val item = data[position]
       // holder.bind(item)
        holder.itemView.setOnClickListener {
          mListener.onViewClicked(position,item)
        }

    }


}
