package com.example.jankenteamb.ui.menu.history

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.HistoryAdapter
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData
import com.example.jankenteamb.viewmodel.MenuHistoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.inject

class MenuHistoryFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var menuHistoryViewModel: MenuHistoryViewModel
    private val factory by inject<MenuHistoryViewModel.Factory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = Slide(Gravity.END).apply { duration = 200 }

        historyAdapter = HistoryAdapter()
        auth = FirebaseAuth.getInstance()
        menuHistoryViewModel = ViewModelProvider(
            this, factory
        ).get(MenuHistoryViewModel::class.java)

        menuHistoryViewModel.errorLiveData.observe(viewLifecycleOwner, onError)
        menuHistoryViewModel.historyLiveData.observe(viewLifecycleOwner, onSuccessGetHistory)
        menuHistoryViewModel.getHistoryFromFirebase()

        rv_history.layoutManager = LinearLayoutManager(
            this@MenuHistoryFragment.requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_history.adapter = historyAdapter
        showLoading(true)
    }

    val onError = Observer<String> {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private val onSuccessGetHistory =
        Observer<List<GameHistoryFirestoreData>> { historyList ->
            if (historyList.isEmpty()) {
                tv_history_empty.isVisible = true
                showLoading(false)
            } else {
                historyAdapter.addData(historyList)
                showLoading(false)
            }
        }

    private fun showLoading(state: Boolean) {
        if (state) {
            pb_history.visibility = View.VISIBLE
        } else {
            pb_history.visibility = View.GONE
        }
    }

    /*
        override fun onError(msg: String) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        override fun onSuccess(msg: String) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        override fun onSuccessGetHistory(historyList: MutableList<GameHistoryFirestoreData>) {

            this.requireActivity().runOnUiThread {
                if (historyList.size == 0) {
                    tv_history_empty.isVisible = true
                } else {
                    historyAdapter.addData(historyList)
                }
            }
        }
    */

}