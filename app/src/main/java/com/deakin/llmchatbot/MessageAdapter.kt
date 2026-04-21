package com.deakin.llmchatbot

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textSource: TextView = itemView.findViewById(R.id.textSource)
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)
        val parentLayout: LinearLayout = itemView as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.textMessage.text = message.text
        holder.textTimestamp.text = message.timestamp

        if (message.isUser) {
            holder.parentLayout.gravity = Gravity.END
            holder.textMessage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4A90D9"))
            holder.textMessage.setTextColor(Color.WHITE)
            
            val paramsTimestamp = holder.textTimestamp.layoutParams as LinearLayout.LayoutParams
            paramsTimestamp.gravity = Gravity.END
            holder.textTimestamp.layoutParams = paramsTimestamp
            
            // Hide AI Source label for user message
            holder.textSource.visibility = View.GONE
            
        } else {
            holder.parentLayout.gravity = Gravity.START
            
            if (message.text == "Typing...") {
                holder.textMessage.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
                holder.textMessage.setTextColor(Color.parseColor("#FF9800"))
                holder.textSource.visibility = View.GONE
            } else {
                holder.textMessage.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
                holder.textMessage.setTextColor(Color.BLACK)
                
                // Show AI Source label
                if (message.aiSource != null) {
                    holder.textSource.text = message.aiSource
                    holder.textSource.visibility = View.VISIBLE
                    
                    val paramsSource = holder.textSource.layoutParams as LinearLayout.LayoutParams
                    paramsSource.gravity = Gravity.START
                    holder.textSource.layoutParams = paramsSource
                } else {
                    holder.textSource.visibility = View.GONE
                }
            }
            
            val paramsTimestamp = holder.textTimestamp.layoutParams as LinearLayout.LayoutParams
            paramsTimestamp.gravity = Gravity.START
            holder.textTimestamp.layoutParams = paramsTimestamp
        }
    }

    override fun getItemCount(): Int = messages.size
}
