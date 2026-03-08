
package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.enteties.Note
import com.example.notesapp.viewmodel.NoteDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class NoteViewModel(app: Application) : AndroidViewModel(app) {

    private val repo by lazy {
        val db = NoteDatabase.get(app)
        NoteRepository(db.noteDAO())   // <- noteDao() olmalıdır
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val notes: StateFlow<List<Note>> =
        query.flatMapLatest { q ->
            if (q.isBlank()) repo.getAll()
            else repo.search(q)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setQuery(q: String) {
        _query.value = q
    }

    fun add(title: String, content: String, pinned: Boolean = false) =
        viewModelScope.launch {
            repo.add(title, content, pinned)
        }

    fun update(note: Note) = viewModelScope.launch { repo.update(note) }

    fun delete(note: Note) = viewModelScope.launch { repo.delete(note) }

    suspend fun getById(id: Long) = repo.getById(id)
}


//package com.example.notesapp
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.notesapp.enteties.Note
//import com.example.notesapp.viewmodel.NoteDatabase
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class NoteViewModel (app: Application) : AndroidViewModel(app) {
//
//    private val dao = NoteDatabase.get(app).noteDao()
//    private val repo = NoteRepository(dao)
//
//    private val _query = MutableStateFlow("")
//    val query = _query =_query.asStateFlow()
//
//
//    val nottes: StateFlow<List<Note>> =
//        query.flatMapLatest { q ->
//
//            if (q.isBlank())
//                repo.getAll()
//
//            else
//                repo.search(q)
//        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
//    fun setQuery (q: String) {
//        _query.value = q
//    }
//
//    fun add(title: String, content: String) = viewModelScope.launch {
//        repo.add(title,content)
//    }
//
//    fun update (note: Note) = viewModelScope.launch {
//        repo.update(note)
//    }
//
//    fun delete (note: Note) = viewModelScope.launch {
//        repo.delete(note)
//    }
//
//}


