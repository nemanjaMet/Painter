package com.example.painter.adapters

import android.content.Context
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.painter.R
import com.example.painter.custom_components.DrawBoardView
import com.example.painter.helpers.PainterManager
import com.example.painter.models.CanvasSize
import com.example.painter.models.SavedDrawing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SavedImagesListAdapter(private var context: Context, private var savedDrawings: MutableList<SavedDrawing>, private var listener: SavedImagesListAdapterInterface? = null): RecyclerView.Adapter<SavedImagesListAdapter.SavedImagesListViewHolder>() {

    interface SavedImagesListAdapterInterface {
        fun onExportImageClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImagesListViewHolder {
        return SavedImagesListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved_images_list, parent, false))
    }

    override fun onBindViewHolder(holder: SavedImagesListViewHolder, position: Int) {

        val savedDrawing = savedDrawings[position]

        val canvasSize = savedDrawing.canvasSize

        holder.tvTitle.text = savedDrawing.title
        holder.tvSize.text = ("${canvasSize.width}x${canvasSize.height}")
        holder.tvDate.text = savedDrawing.date

        //holder.viewBoardThumbnail.layoutParams = getLayoutParamsForThumbnail(canvasSize)

        holder.viewBoardThumbnail.setIsDrawingEnabled(false)
        //holder.viewBoardThumbnail.setCanvasSize(savedDrawing.canvasSize)

//        if (position <= 0)
        //holder.viewBoardThumbnail.setDrawing(savedDrawing.drawings)
        //if (position == 0)
        //holder.ivDrawBoard.setImageBitmap(PainterManager.createBitmap(Size(100,100), savedDrawing.drawings))

        // .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true) / /.apply(RequestOptions.centerCropTransform())
        holder.viewBoardThumbnail.visibility = View.GONE
        Glide.with(context).load(savedDrawing.thumbnailPath).apply(RequestOptions.centerCropTransform()).centerCrop().into(holder.ivDrawBoard)

        holder.clHolder.setOnLongClickListener {

            showPopupMenu(holder.ivDrawBoard, position)

            true
        }

    }

    private fun getLayoutParamsForThumbnail(canvasSize: CanvasSize): ViewGroup.LayoutParams {

        val layoutParams = ConstraintLayout.LayoutParams(0, 0)
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        layoutParams.dimensionRatio = "${canvasSize.width}:${canvasSize.height}"

        return layoutParams
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.menu_saved_item_list_item)
        popup.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.btn_export -> {
                    // zato sto nam je redosled u listi koji prikazujemo obrnut
                    val reversedPosition = itemCount - 1 - position
                    listener?.onExportImageClick(reversedPosition)
                }
            }
            true
        }
        popup.show()
    }

    fun refreshList(savedDrawings: MutableList<SavedDrawing>) {
        this.savedDrawings.clear()

        this.savedDrawings.addAll(savedDrawings)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return savedDrawings.size
    }

    class SavedImagesListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clHolder: ConstraintLayout = view.findViewById(R.id.cl_holder_saved_image_list)
        val tvTitle: TextView = view.findViewById(R.id.tv_title_saved_images_list_item)
        val viewBoardThumbnail: DrawBoardView = view.findViewById(R.id.view_draw_board)
        val ivDrawBoard: ImageView = view.findViewById(R.id.iv_draw_board)
        val tvSize: TextView = view.findViewById(R.id.tv_size_saved_images_list_item)
        val tvDate: TextView = view.findViewById(R.id.tv_date_saved_images_list_item)
    }

}