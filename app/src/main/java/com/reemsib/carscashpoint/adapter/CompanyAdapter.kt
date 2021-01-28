package com.reemsib.carscashpoint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.orhanobut.hawk.Hawk
import com.reemsib.carscashpoint.R
import com.reemsib.carscashpoint.model.Company
import kotlinx.android.synthetic.main.company_row.view.*

open class CompanyAdapter (var context: Context, var companyList:ArrayList<Company>): BaseAdapter() {

    var mListener: OnItemClickListener?=null
    interface OnItemClickListener {

        fun onItemClick(id: Int,position: Int,name:String)
        fun onLongItemClick(view: View?, position: Int)
    }
    fun setOnItemClickListener (listener: OnItemClickListener) {
        mListener = listener
    }
    override fun getCount(): Int {
        return companyList.size
    }

    override fun getItem(position: Int): Any {
        return companyList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val company = this.companyList[position]
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val CompanyView = inflator.inflate(R.layout.company_row, null)


        Hawk.init(context).build()

        CompanyView.img_company.setImageResource(company.img)

        CompanyView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(company.id,position,company.name)

            }

        }
        return CompanyView
    }

}

//private fun ImageView.setImageBitmap(img: Int) {
//
//}
