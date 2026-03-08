package com.example.notesapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.enteties.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {

    @Query("SELECT * FROM notes ORDER BY pinned DESC, updateAt DESC")
    fun getAll(): Flow<List<Note>>


    @Query
        ("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): Note?

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long


    @Update
    suspend fun update (note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("""
    SELECT * FROM notes
    WHERE title   LIKE '%' || :q || '%'
       OR content LIKE '%' || :q || '%'
    ORDER BY pinned DESC, updateAt DESC
""")
    fun search(q: String): Flow<List<Note>>


}