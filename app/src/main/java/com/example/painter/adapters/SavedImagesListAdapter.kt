package com.example.painter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.painter.R
import com.example.painter.custom_components.DrawBoardView
import com.example.painter.custom_components.SavedDrawing


class SavedImagesListAdapter(private var context: Context, private var savedDrawings: MutableList<SavedDrawing>): RecyclerView.Adapter<SavedImagesListAdapter.SavedImagesListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImagesListViewHolder {
        return SavedImagesListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved_images_list, parent, false))
    }

    override fun onBindViewHolder(holder: SavedImagesListViewHolder, position: Int) {

        val savedDrawing = savedDrawings[position]

        val canvasSize = savedDrawing.canvasSize

        holder.tvTitle.text = savedDrawing.title
        holder.tvSize.text = ("${canvasSize.width}x${canvasSize.height}")
        holder.tvDate.text = savedDrawing.date

        holder.viewBoardThumbnail.setIsDrawingEnabled(false)
        holder.viewBoardThumbnail.setCanvasSize(savedDrawing.canvasSize)
        holder.viewBoardThumbnail.setDrawing(savedDrawing.drawings)

    }

    override fun getItemCount(): Int {
        return savedDrawings.size
    }

    class SavedImagesListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title_saved_images_list_item)
        val viewBoardThumbnail: DrawBoardView = view.findViewById(R.id.view_draw_board)
        val tvSize: TextView = view.findViewById(R.id.tv_size_saved_images_list_item)
        val tvDate: TextView = view.findViewById(R.id.tv_date_saved_images_list_item)
    }

}