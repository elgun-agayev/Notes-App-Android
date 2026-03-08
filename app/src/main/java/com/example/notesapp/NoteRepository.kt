package com.example.notesapp

import com.example.notesapp.dao.NoteDAO
import com.example.notesapp.enteties.Note
import kotlinx.coroutines.flow.Flow


class NoteRepository (private val dao: NoteDAO) {

    fun getAll() : Flow<List<Note>> = dao.getAll()

    fun search (q: String) : Flow<List<Note>> = dao.search(q)

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun add(title: String, content: String , pinned: Boolean = false ) : Long {

        val now = System.currentTimeMillis()
        return dao.insert(Note(title = title , content = content , pinned = pinned ,
            createdAt = now , updateAt = now))
    }

    suspend fun update(note: Note) {
        dao.update(note.copy(updateAt = System.currentTimeMillis()))
    }

    suspend fun delete (note: Note) = dao.delete(note)
}