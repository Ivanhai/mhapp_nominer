package com.ivanhai.mhcoin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import json.UserX

class UsersAdapter(private val users: List<UserX>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickname.text = "${position + 1}.${users[position].username} ${users[position].balance}"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nickname:TextView = itemView.findViewById(R.id.nickname)
    }
}
