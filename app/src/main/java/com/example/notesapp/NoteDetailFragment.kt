package com.example.notesapp

import android.os.Bundle
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import kotlinx.coroutines.launch


class NoteDetailFragment : Fragment() {

    private var currentNote: com.example.notesapp.enteties.Note? = null
    private var isEditing = false

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val vm: NoteViewModel by activityViewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arg oxu
        val noteId = requireArguments().getLong("noteId")

        // DB-dən notu çək və UI-yə yaz

        viewLifecycleOwner.lifecycleScope.launch {
            val note =
                vm.getById(noteId)  // repo public deyilsə, VM-dən pass-through yaz: vm.getById(id)
            note?.let {

                // View-mode mətnləri

                binding.tvdetailNoteTitle.text = it.title
                binding.tvdetailNote.text = it.content

                // EditText-lərə ilkin dəyər qoyuruq (hələ gizlidir)

                binding.etdetailNote.setText(it.content)
                binding.etdetailNoteTitle.setText(it.title)

                currentNote = it

                setEditMode(false)
            }
        }

        // Geri düyməsi
        binding.ibdetailtarrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Qələm → edit rejimi
        binding.ibdetailedit.setOnClickListener {
            setEditMode(true)
            // Başlığa fokus ver
            binding.etdetailNoteTitle.requestFocus()
            binding.etdetailNoteTitle.setSelection(binding.etdetailNoteTitle.text?.length ?:0)

            // Klaviaturanı aç
            val imm = requireContext().getSystemService(android.view.inputmethod.InputMethodManager::class.java)
            imm?.showSoftInput(
                binding.etdetailNoteTitle ,
                android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
            )
        }

        binding.ibdetailsave.setOnClickListener {
            val title = binding.etdetailNoteTitle.text.toString().trim()
            val content = binding.etdetailNote.text.toString().trim()
            val note = currentNote ?:return@setOnClickListener

            // Dəyişiklikləri yadda saxla
            viewLifecycleOwner.lifecycleScope.launch {
                vm.update(note.copy(title = title , content = content))

                // UI yeniləmə
                binding.tvdetailNoteTitle.text = title
                binding.tvdetailNote.text = content
                setEditMode(false)
            }
        }





    }

    private fun setEditMode(edit: Boolean) {
        isEditing = edit

        // TextView-lər
        binding.tvdetailNote.visibility = if (edit) View.GONE else View.VISIBLE
        binding.tvdetailNoteTitle.visibility = if (edit) View.GONE else View.VISIBLE

        // EditText-lər
        binding.etdetailNote.visibility = if (edit) View.VISIBLE else View.GONE
        binding.etdetailNoteTitle.visibility = if (edit) View.VISIBLE else View.GONE

        // Düymələr
        binding.ibdetailedit.visibility = if (edit) View.GONE else View.VISIBLE
        binding.ibdetailsave.visibility = if (edit) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}