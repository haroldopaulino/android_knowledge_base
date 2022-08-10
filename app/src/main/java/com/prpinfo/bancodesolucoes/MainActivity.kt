package com.prpinfo.bancodesolucoes

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val taskList: MutableList<String> = mutableListOf()
    private val adapter by lazy { makeAdapter(taskList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2
        super.onCreate(savedInstanceState)
        // 3
        setContentView(R.layout.activity_main)

        // 4
        //taskListView.adapter = adapter

        // 5
        //taskListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }
    }

    // 6
    fun addTaskClicked(view: View) {

    }

    // 7
    private fun makeAdapter(list: List<String>): ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
}