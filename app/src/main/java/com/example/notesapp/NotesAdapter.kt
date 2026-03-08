package com.example.notesapp


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemNoteBinding
import com.example.notesapp.enteties.Note


class NotesAdapter (
    private val onItemClick : (Note)  -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> (){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {

        holder.bind(visibleList[position])


    }

    override fun getItemCount(): Int = visibleList.size




    private val fullList = mutableListOf<Note>()
    private val visibleList = mutableListOf<Note>()

    fun submitAll(items: List<Note>) {
        fullList.clear()
        fullList.addAll(items)
        visibleList.clear()
        visibleList.addAll(items)
        notifyDataSetChanged()
    }

    fun filter(queryRaw: String) {

        val q = queryRaw.trim().lowercase()
        visibleList.clear()

        if (q.isEmpty()) {
            visibleList.addAll(fullList)
        } else {
            visibleList.addAll(
                fullList.filter { n ->
                    n.title.contains(q, ignoreCase = true) ||
                            n.content.contains(q, ignoreCase = true)

                }
            )
        }
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int): Note {
        return visibleList[position]
    }

    inner class NoteViewHolder( val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // ADD: klik


        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            binding.tvContent.text = note.content

            binding.root.setOnClickListener {

                onItemClick(note)
            }
        }
        }
    }





