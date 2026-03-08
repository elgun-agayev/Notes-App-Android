package com.example.notesapp.viewmodel

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.dao.NoteDAO
import com.example.notesapp.enteties.Note


@Database(entities = [Note::class], version = 1, exportSchema = false)

abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDAO(): NoteDAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun get(context: Context): NoteDatabase =
            INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext ,
                    NoteDatabase::class.java,
                    "notes_db"
                ).build().also {INSTANCE = it}
            }
    }
}