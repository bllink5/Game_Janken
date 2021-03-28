package com.example.jankenteamb.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.AddFrameAdapter
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import com.example.jankenteamb.viewmodel.EditPhotoProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_upload_foto_gunakan_frame.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class UploadFotoGunakanFrameActivity : AppCompatActivity() {
    private lateinit var adapter: AddFrameAdapter
//    private val profilePresenter by inject<ProfilePresenter>()
    private var fileUri: Uri? = null
    private val factory by inject<EditPhotoProfileViewModel.Factory>()
    private lateinit var editPhotoProfileViewModel: EditPhotoProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_foto_gunakan_frame)
        overridePendingTransition(R.anim.slide_dari_bawah,R.anim.slide_ke_atas)
//        profilePresenter.setView(this)

        editPhotoProfileViewModel = ViewModelProvider(
            this,
            factory).get(EditPhotoProfileViewModel::class.java)
        editPhotoProfileViewModel.photoUriLiveData.observe(this, onSuccessPhotoUri)
        editPhotoProfileViewModel.frameListLiveData.observe(this, onSuccessGetFrame)
        editPhotoProfileViewModel.errorLiveData.observe(this, onError)

        adapter = AddFrameAdapter()
        btn_upload.visibility = View.INVISIBLE

        CoroutineScope(Dispatchers.IO).launch {
//            profilePresenter.downloadPhotoProfile()
//            profilePresenter.getUserFrameList()

            editPhotoProfileViewModel.downloadPhotoProfile()
            editPhotoProfileViewModel.getUserFrameList()
        }

        adapter.setOnClickListener(object : AddFrameAdapter.OnClickListenerCallback{
            override fun onClickListenerCallback(shopListFirestore: ShopListDataFirestoreAdapter) {
                addFrame(shopListFirestore)
            }

        })

        rv_add_frame.layoutManager = GridLayoutManager(baseContext, 2)
        rv_add_frame.adapter = adapter

        showLoading(true)
        showEmpty(true)

        iv_pick_profile.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        btn_back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_dari_atas,R.anim.slide_ke_bawah)
        }

        btn_upload.setOnClickListener {
//            profilePresenter.uploadPhotoProfile(fileUri!!)
            btn_upload.visibility = View.GONE
            val clParams = cl_daftar_frame.layoutParams as ConstraintLayout.LayoutParams
            clParams.topToBottom = cl_update_profile.id
            editPhotoProfileViewModel.uploadPhotoProfile(fileUri!!)
        }
    }

    fun addFrame(shopListFirestore: ShopListDataFirestoreAdapter){
//        profilePresenter.updateFrameUrl(shopListFirestore.frameUrl)
        editPhotoProfileViewModel.updateFrameUrl(shopListFirestore.frameUrl)
        editPhotoProfileViewModel.getUserFrameList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                fileUri = result.uri
                Glide.with(baseContext!!)
                    .load(fileUri)
                    .apply(RequestOptions().circleCrop())
                    .into(iv_upload_photo)
                btn_upload.visibility = View.VISIBLE
                val clParams = cl_daftar_frame.layoutParams as ConstraintLayout.LayoutParams
                clParams.topToBottom = btn_upload.id

                Log.d("UriPath", fileUri?.path.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("PROFILE_Activity", error.toString())
            }
        }
    }

    val onError = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

//    override fun onSuccessUploadPhoto(uri: Uri) {
//        runOnUiThread{
//            Glide.with(baseContext!!)
//                .load(uri)
//                .apply(RequestOptions().circleCrop())
//                .into(iv_upload_photo)
//            btn_upload.visibility = View.INVISIBLE
//        }
//    }

    private val onSuccessPhotoUri = Observer<Uri> {uri ->
        Glide.with(baseContext!!)
                .load(uri)
                .apply(RequestOptions().circleCrop())
                .into(iv_upload_photo)
    }


    private val onSuccessGetFrame = Observer<List<ShopListDataFirestoreAdapter>> { listFrame ->
        if (listFrame.isNotEmpty()) {
            adapter.addData(listFrame)
            showEmpty(false)
            showLoading(false)
        } else{
            showEmpty(true)
            showLoading(false)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            pb_frame_list.visibility = View.VISIBLE
        } else {
            pb_frame_list.visibility = View.GONE
        }
    }

    private fun showEmpty(state: Boolean) {
        if (state) {
            iv_empty_frame.visibility = View.VISIBLE
            tv_empty_frame.visibility = View.VISIBLE
        } else {
            iv_empty_frame.visibility = View.GONE
            tv_empty_frame.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_dari_bawah,R.anim.slide_ke_atas)
    }
}