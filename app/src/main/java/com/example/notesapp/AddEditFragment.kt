package com.example.notesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notesapp.databinding.FragmentAddEditBinding
import androidx.core.content.getSystemService




class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
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

        _binding= FragmentAddEditBinding.inflate(inflater , container ,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etTitle.requestFocus()
        requireContext().getSystemService<InputMethodManager>()
            ?.showSoftInput(binding.etTitle,InputMethodManager.SHOW_IMPLICIT)

        binding.ibtarrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content =binding.etTypesomething.text.toString().trim()

            if(title.isEmpty() && content.isEmpty()) {
                Toast.makeText(requireContext(), "Empty notes cannot be saved." , Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener

            }
            vm.add(title,content)
            hideKeyboard(binding.root)
            Toast.makeText(requireContext(), "Successfully saved." , Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()

        }
    }

    private fun hideKeyboard(view: View) {
        // Yeni (Insets) yolu

        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())

        // Köhnə InputMethodManager yolu

        val imm = requireContext().getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(view.windowToken ,0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }
}