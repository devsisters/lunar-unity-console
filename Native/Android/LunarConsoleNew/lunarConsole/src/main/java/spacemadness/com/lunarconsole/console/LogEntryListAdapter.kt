package spacemadness.com.lunarconsole.console

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import spacemadness.com.lunarconsole.R
import spacemadness.com.lunarconsole.ui.LogEntryStyle
import spacemadness.com.lunarconsole.extensions.setDrawables

class LogEntryListAdapter(
    private val dataSource: DataSource<LogEntry>
) : RecyclerView.Adapter<LogEntryListAdapter.ViewHolder>() {
    var onClickListener: ((entry: LogEntry, position: Int) -> Unit)? = null
    var onLongClickListener: ((entry: LogEntry, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lunar_console_layout_console_log_entry, parent, false)
        return ViewHolder(
            itemView = itemView,
            onClickListener = ::onClick,
            onLongClickListener = ::onLongClick
        )
    }

    override fun getItemCount(): Int = dataSource.getItemCount()

    fun getItem(position: Int) = dataSource.getItem(position)

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private fun onClick(position: Int) {
        onClickListener?.invoke(getItem(position), position)
    }

    private fun onLongClick(position: Int): Boolean {
        onLongClickListener?.invoke(getItem(position), position)
        return onLongClickListener != null
    }

    class ViewHolder(
        itemView: View,
        private val onClickListener: (position: Int) -> Unit,
        private val onLongClickListener: (position: Int) -> Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        private val layout: View = itemView.findViewById(R.id.lunar_console_log_entry_layout)
        private val messageView: TextView =
            itemView.findViewById(R.id.lunar_console_log_entry_message)
        private val collapsedCountView: TextView =
            itemView.findViewById(R.id.lunar_console_log_collapsed_count)
        private val context = itemView.context

        init {
            itemView.setOnClickListener {
                onClickListener(adapterPosition)
            }
            itemView.setOnLongClickListener {
                onLongClickListener(adapterPosition)
            }
        }

        fun bind(entry: LogEntry, position: Int) {
            val style = LogEntryStyle.of(entry.type)
            layout.setBackgroundColor(getBackgroundColor(context, position))
            messageView.setDrawables(left = style.getIcon(context))
            messageView.text = entry.message

            if (entry is CollapsedLogEntry && entry.count > 1) {
                collapsedCountView.visibility = View.VISIBLE
                collapsedCountView.text = entry.count.toString()
            } else {
                collapsedCountView.visibility = View.GONE
            }
        }

        companion object {
            private fun getBackgroundColor(context: Context, position: Int): Int {
                val colorId = if (position % 2 == 0)
                    R.color.lunar_console_color_cell_background_dark else
                    R.color.lunar_console_color_cell_background_light

                @Suppress("DEPRECATION")
                return context.resources.getColor(colorId)
            }
        }
    }
}