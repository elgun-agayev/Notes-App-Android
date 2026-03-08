package com.example.notesapp

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Adapter initialize

        notesAdapter = NotesAdapter { note ->
            val bundle = Bundle().apply {
                putLong("noteId" , note.id)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_noteDetailFragment ,
                bundle)
        }
        binding.rvNotes.adapter = notesAdapter


        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())

        binding.etSearch.doAfterTextChanged {
            vm.setQuery(it?.toString().orEmpty())

            // Mətn boşaldılarsa "File not found" gizlə

            if (it.isNullOrBlank()) {
                binding.lnrlytFileNot.visibility = View.GONE
            }
        }






        // Flow toplamaq – VM-dən gələn siyahını adapterə ver

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.notes.collect { list  ->
                    notesAdapter.submitAll(list)

                    // boş/dolu state
//                    val isEmpty = list.isEmpty()
//                    binding.ivphotohome.visibility = if (isEmpty) View.VISIBLE else View.GONE
//                    binding.textView.visibility = if (isEmpty) View.VISIBLE else View.GONE
//                    binding.rvNotes.visibility = if(isEmpty) View.GONE else View.VISIBLE

                    val searching = !binding.etSearch.text.isNullOrBlank()
                    val hasResults = list.isNotEmpty()

                    // Axtarış var, nəticə yoxdur → File not found paneli
                    binding.lnrlytFileNot.visibility = if (searching && !hasResults) View.VISIBLE
                    else View.GONE

                    // Siyahı görünməsi

                    binding.rvNotes.visibility = if (hasResults) View.VISIBLE else View.GONE

                    // Axtarış yoxdur və siyahı boşdur → başlanğıc ekran

                    val showEmptyHero = !searching && !hasResults
                    binding.ivphotohome.visibility = if (showEmptyHero) View.VISIBLE else View.GONE
                    binding.textView.visibility = if (showEmptyHero) View.VISIBLE else View.GONE


                }
            }
        }




        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addEditFragment)
        }




      // Search iconu → axtarış sətrini aç/bağla
        binding.imgBtnSearch.setOnClickListener { toggleSearch() }
      // Close (X)
        binding.ibbasalineclose.setOnClickListener {
            binding.etSearch.setText("")
            vm.setQuery("")
            binding.lnrlytFileNot.visibility = View.GONE
            hideSearch()
        }

        val swipeBg = ColorDrawable(Color.parseColor("#D32F2F")) // qırmızı fon
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.trash)!! // sənin trash.png

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper . SimpleCallback(0,
            ItemTouchHelper.LEFT ) {
            override fun  onMove (
                recyclerView: RecyclerView ,
                viewHolder: RecyclerView.ViewHolder ,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = notesAdapter.getNoteAt(position)
                vm.delete(note)
            }

            // ADD: qırmızı fon + zibil qabı ikonu çəkilməsi

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView

                if (dX < 0) {    // sola sürüşdürülür
                    swipeBg.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    swipeBg.draw(c)

                    val icon = deleteIcon
                    val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + icon.intrinsicHeight
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - icon.intrinsicHeight
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)

                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvNotes)
    }
        // --- helper-lər ---
        private fun toggleSearch() {
            if (binding.frameview.visibility == View.VISIBLE) hideSearch()
            else showSearch()
        }


        private fun showSearch() {
            binding.frameview.visibility = View.VISIBLE
            binding.frameview.requestFocus()
            requireContext().getSystemService<InputMethodManager>()
                ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        }

        private fun hideSearch() {
            binding.frameview.visibility = View.GONE
            requireContext().getSystemService<InputMethodManager>()
                ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}


