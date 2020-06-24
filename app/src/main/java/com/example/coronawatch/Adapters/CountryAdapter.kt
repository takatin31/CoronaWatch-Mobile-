package com.example.coronawatch.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.blongho.country_data.World
import com.example.coronawatch.Activities.CountriesActivity
import com.example.coronawatch.Activities.StatsActivity
import com.example.coronawatch.DataClasses.Country
import com.example.coronawatch.R
import de.hdodenhof.circleimageview.CircleImageView

class CountryAdapter(val activity : CountriesActivity, val list : MutableList<Country>) : RecyclerView.Adapter<CountryAdapter.WordViewHolder>(){
    class WordViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val countryTitleView = v.findViewById<TextView>(R.id.countryTitleView)
        val itemLayout = v.findViewById<CardView>(R.id.countryItemLayout)
        val countryFlag = v.findViewById<CircleImageView>(R.id.countryFlagView)
        val countryNbrCases = v.findViewById<TextView>(R.id.countryNbrCasesView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(LayoutInflater.from(activity).inflate(R.layout.country_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val pays = list[position]
        holder.countryTitleView.text = pays.countryName
        holder.countryNbrCases.text = pays.nbrCas.toString()
        holder.countryFlag.setImageResource(World.getFlagOf(pays.countryCode))

        holder.itemLayout.setOnClickListener {

            val intent = Intent(activity, StatsActivity::class.java)
            intent.putExtra("isCountry", true)
            intent.putExtra("countryCode", pays.countryCode)
            intent.putExtra("countryName", pays.countryName)
            activity.startActivity(intent)
        }


    }


}