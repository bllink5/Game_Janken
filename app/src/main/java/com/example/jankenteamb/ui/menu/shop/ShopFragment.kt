package com.example.jankenteamb.ui.menu.shop

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Slide
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.ShopAdapter
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import com.example.jankenteamb.viewmodel.ShopViewModel
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ShopFragment : Fragment() {

    private val factory by inject<ShopViewModel.Factory>()
    private lateinit var shopViewModel: ShopViewModel
    private lateinit var shopAdapter: ShopAdapter
//    private val shopPresenter by inject<ShopPresenter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = Slide(Gravity.END).apply { duration = 200 }

        shopViewModel = ViewModelProvider(
            this, factory
        ).get(ShopViewModel::class.java)

//        shopViewModel.userLiveData.observe(viewLifecycleOwner, userLiveData)
        shopViewModel.userPointLiveData.observe(viewLifecycleOwner, getPointData)
        shopViewModel.errorLiveData.observe(viewLifecycleOwner, onError)
        shopViewModel.frameCollectionLiveData.observe(viewLifecycleOwner, onSuccessGetShop)
        shopViewModel.successLiveData.observe(viewLifecycleOwner, onSuccesAddFrameUser)
        shopViewModel.loadingLiveData.observe(viewLifecycleOwner, loading)
        shopAdapter = ShopAdapter()
//        shopPresenter.setView(this)
        CoroutineScope(Dispatchers.Main).launch {
            shopViewModel.getShopListFromFirebase()
        }

        shopViewModel.getPointData()
//        shopPresenter.getPointData()

        rv_shop_list.layoutManager = GridLayoutManager(requireContext(), 2)
        rv_shop_list.adapter = shopAdapter
        shopAdapter.setOnClickListener(object : ShopAdapter.OnClickListenerCallback {
            override fun onClickListenerCallback(shopListFirestore: ShopListDataFirestoreAdapter) {
                showDialog(shopListFirestore)
            }
        })

    }

    fun showDialog(shopListDataFirestore: ShopListDataFirestoreAdapter) {
        val alertDialog = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val frameUrl = shopListDataFirestore.frameUrl
                val title = shopListDataFirestore.title
                val point = shopListDataFirestore.price
                setTitle("Frame Shop")
                setMessage("Anda ingin membeli frame ini?")
                setPositiveButton("Ya") { _, _ ->
                    shopViewModel.addFrameUser(frameUrl, title, point)
                }
                setNegativeButton("Tidak") { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    //    override fun showLoading() {
//        pb_shop.visibility = View.VISIBLE
//    }
//
//    override fun hideLoading() {
//        activity?.runOnUiThread {
//            pb_shop.visibility = View.GONE
//        }
//
//    }
    private val loading = Observer<Boolean> {
        if (it == false) {
            activity?.runOnUiThread {
                pb_shop.visibility = View.GONE
            }
        }else{
            activity?.runOnUiThread {
                pb_shop.visibility = View.VISIBLE
            }
        }
    }

    private val onSuccessGetShop = Observer<MutableList<ShopListDataFirestoreAdapter>> { shopList ->
        activity?.runOnUiThread {
            if (shopList.size == 0) {
                rv_shop_list.visibility = View.GONE
                tv_empty_shop_list.visibility = View.VISIBLE
            } else {
                tv_empty_shop_list.visibility = View.GONE
                rv_shop_list.visibility = View.VISIBLE
                shopAdapter.addData(shopList)
            }
        }
    }


    val onError = Observer<String> { msg ->
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

    }

    private val onSuccesAddFrameUser = Observer<String> { msg ->
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
        CoroutineScope(Dispatchers.Main).launch {
            shopViewModel.getShopListFromFirebase()
        }

        shopViewModel.getPointData()
//        shopPresenter.getPointData()
    }

    private val getPointData = Observer<Int> {
        activity?.runOnUiThread {
            tv_shop_coin.text = it.toString()
        }
    }
}