package com.deakin.llmchatbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private val messageList = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageDao: MessageDao
    private val geminiHelper = GeminiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val username = intent.getStringExtra("USERNAME") ?: "User"

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        recyclerView = findViewById(R.id.recyclerViewMessages)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)

        tvUsername.text = "Welcome, $username"

        adapter = MessageAdapter(messageList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Get Room DB
        messageDao = ChatDatabase.getDatabase(this).messageDao()

        lifecycleScope.launch {
            val savedMessages = messageDao.getAllMessages()
            messageList.addAll(savedMessages)
            adapter.notifyDataSetChanged()
            if (messageList.isNotEmpty()) {
                recyclerView.scrollToPosition(messageList.size - 1)
            }
        }

        btnSend.setOnClickListener {
            val userInput = etMessage.text.toString().trim()
            if (userInput.isEmpty()) return@setOnClickListener

            etMessage.text.clear()

            lifecycleScope.launch {
                // 1. Add User Message
                val userTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val userMessage = Message(
                    text = userInput, 
                    isUser = true, 
                    timestamp = userTime,
                    aiSource = null
                )
                messageDao.insertMessage(userMessage)
                messageList.add(userMessage)
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)

                // 2. Add "Typing..." Message
                val typingMessage = Message(
                    text = "Typing...", 
                    isUser = false, 
                    timestamp = "",
                    aiSource = null
                )
                messageList.add(typingMessage)
                val typingIndex = messageList.size - 1
                adapter.notifyItemInserted(typingIndex)
                recyclerView.scrollToPosition(typingIndex)

                // Disable UI while waiting
                btnSend.isEnabled = false
                etMessage.isEnabled = false

                // 3. Call Gemini
                val botResponse = geminiHelper.getResponse(userInput)

                // Enable UI
                btnSend.isEnabled = true
                etMessage.isEnabled = true

                // 4. Remove "Typing..."
                messageList.removeAt(typingIndex)
                adapter.notifyItemRemoved(typingIndex)

                // 5. Add AI Message
                val botTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val botMessage = Message(
                    text = botResponse.text, 
                    isUser = false, 
                    timestamp = botTime,
                    aiSource = botResponse.aiSource
                )
                messageDao.insertMessage(botMessage)
                messageList.add(botMessage)
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
            }
        }
    }
}
