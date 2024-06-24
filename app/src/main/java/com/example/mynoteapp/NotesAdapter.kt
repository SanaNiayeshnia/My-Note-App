import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mynoteapp.Note
import com.example.mynoteapp.R

class NotesAdapter(private val onNoteClick: (Note) -> Unit) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val notes = mutableListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size

    fun submitList(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)

        init {
            itemView.setOnClickListener {
                onNoteClick(notes[adapterPosition])
            }
        }

        fun bind(note: Note) {
            // Bind data to views
            textViewTitle.text = note.title
            textViewContent.text = if (note.content.length > 50) {
                "${note.content.substring(0, 50)}..." // Display first 50 characters with ellipsis
            } else {
                note.content // Display full content if it's less than or equal to 50 characters
            }
            textViewTimestamp.text = note.timestamp

            // Apply background drawable to the item view
            itemView.setBackgroundResource(R.drawable.note_item_background)
        }
    }
}
