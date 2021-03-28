package com.example.jankenteamb.ui.profile


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jankenteamb.R
import com.example.jankenteamb.adapter.TopScoreAdapter
import com.example.jankenteamb.adapter.TopScorePdfAdapter
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.ui.login.LoginActivity
import com.example.jankenteamb.utils.FirebaseHelper
import com.example.jankenteamb.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hendrix.pdfmyxml.PdfDocument
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_edit_profile.view.*
import org.koin.android.ext.android.inject
import java.io.File
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var topScoreAdapter: TopScoreAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private val factory by inject<ProfileViewModel.Factory>()
    private val firebaseHelper by inject<FirebaseHelper>()

    private lateinit var page: AbstractViewRenderer
    private lateinit var listTopScore: List<UserData>
    private lateinit var userData: UserData
    private lateinit var uriPhoto: Uri

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        overridePendingTransition(R.anim.slide_dari_kanan, R.anim.slide_ke_kiri)
//        val permissionWriteCheck = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val permissionReadCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//        if (permissionWriteCheck != PackageManager.PERMISSION_GRANTED || permissionReadCheck != PackageManager.PERMISSION_GRANTED){
//            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//
//            }else{
//                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), WRITE_READ_EXTERNAL_STORAGE)
//            }
//        }

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    Log.d("pemission", "check")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Log.d("pemission", "rational")
                }
            }).check()


        auth = FirebaseAuth.getInstance()
        topScoreAdapter = TopScoreAdapter()

        profileViewModel = ViewModelProvider(
            this,
            factory
        ).get(ProfileViewModel::class.java)

        showLoading(true)
        profileViewModel.userLiveData.observe(this, onSuccessGetUserData)
        profileViewModel.errorLiveData.observe(this, onError)
        profileViewModel.successLiveData.observe(this, onSuccess)
        profileViewModel.topScoreLiveData.observe(this, onSuccessGetTopScore)
        profileViewModel.frameUriLiveData.observe(this, onSuccessDownloadFrame)
        profileViewModel.photoUriLiveData.observe(this, onSuccessDownloadPhoto)
        profileViewModel.usernameUpdateLiveData.observe(this, onSuccessUpdateUsername)

        profileViewModel.getUserDataFromRoom()
        profileViewModel.getTopScoreFromFirebase()

        rv_experience_log.layoutManager = LinearLayoutManager(this)
        rv_experience_log.adapter = topScoreAdapter

        page = object : AbstractViewRenderer(baseContext, R.layout.pdf_template) {
            override fun initView(p0: View?) {
                val rv = p0?.findViewById<RecyclerView>(R.id.rv_experience_log_pdf)
                val tvWin = p0?.findViewById<TextView>(R.id.tv_win_pdf)
                val tvLose = p0?.findViewById<TextView>(R.id.tv_lose_pdf)
                val tvDraw = p0?.findViewById<TextView>(R.id.tv_draw_pdf)
                val tvLevel = p0?.findViewById<TextView>(R.id.tv_level_status_pdf)
                val tvEmail = p0?.findViewById<TextView>(R.id.tv_email_label_pdf)
                val tvUsername = p0?.findViewById<TextView>(R.id.tv_username_label_pdf)
                val pbExp = p0?.findViewById<ProgressBar>(R.id.pb_xp_status_pdf)

                val adapter = TopScorePdfAdapter()
                adapter.addData(listTopScore)
                rv?.layoutManager = LinearLayoutManager(baseContext)
                rv?.adapter = adapter

                tvWin?.text = userData.win.toString()
                tvLose?.text = userData.lose.toString()
                tvDraw?.text = userData.draw.toString()
                tvLevel?.text = userData.level.toString()
                tvEmail?.text = tv_email_label.text
                tvUsername?.text = tv_username_label.text
                pbExp?.progress = userData.exp
                pbExp?.max = when (userData.level) {
                    1 -> 5
                    2 -> 10
                    3 -> 15
                    4 -> 25
                    5 -> 40
                    else -> if (userData.exp > 100) 100 else userData.exp
                }
            }

        }

        iv_profile_picture.setOnClickListener {
            startActivity(Intent(this, UploadFotoGunakanFrameActivity::class.java))
        }

        tv_username_label.setOnClickListener {
            showEditDialog(tv_username_label.text.toString())
        }

        btn_back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_dari_kiri, R.anim.slide_ke_kanan)
        }

        btn_logout.setOnClickListener {
            profileViewModel.deleteAchievementData()
            profileViewModel.deleteUserData()
            firebaseHelper.signoutFirebase()
            val intent = Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        iv_download_pdf.setOnClickListener {

            PdfDocument.Builder(baseContext).addPage(page).orientation(PdfDocument.A4_MODE.PORTRAIT)
                .progressMessage(R.string.app_name)
                .progressTitle(R.string.app_name)
                .renderWidth(1500)
                .renderHeight(2115)
                .saveDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .filename("Profile and Top Score")
                .listener(object : PdfDocument.Callback {
                    override fun onComplete(p0: File?) {
                        Log.d(PdfDocument.TAG_PDF_MY_XML, "complete")
                    }

                    override fun onError(p0: Exception?) {
                        Log.d(PdfDocument.TAG_PDF_MY_XML, "Error: ${p0?.message}")
                    }
                })
                .create()
                .createPdf(this@ProfileActivity)
        }
    }

    override fun onResume() {
        profileViewModel.downloadPhotoProfile()
        super.onResume()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            WRITE_READ_EXTERNAL_STORAGE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d("permission", "Permission has been granted")
//                } else {
//                    Log.d("permission", "Permission has been denied")
//                }
//            }
//        }
//    }

    private fun showEditDialog(username: String) {
        this.let {
            val builder = AlertDialog.Builder(this)
            val inflateView =
                this.layoutInflater.inflate(
                    R.layout.dialog_edit_profile,
                    cl_profile_activity,
                    false
                )
            builder.setView(inflateView)
            inflateView.et_edit_username.setText(username)
            val dialog = builder.create()
            dialog.show()

            inflateView.btn_cancel.setOnClickListener {
                dialog.cancel()
            }

            inflateView.btn_update.setOnClickListener {
                profileViewModel.updateUsername(inflateView.et_edit_username.text.toString())
                dialog.cancel()
            }
        }
    }


    private val onSuccessUpdateUsername = Observer<String> { username ->
        tv_username_label.text = username
    }


    val onError = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    private val onSuccess = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }


    private val onSuccessGetTopScore = Observer<List<UserData>> { topScore ->
        if (topScore.isEmpty()) {
            tv_experience_title.visibility = View.INVISIBLE
            rv_experience_log.visibility = View.INVISIBLE
            showLoading(false)
        } else {
            listTopScore = topScore
            topScoreAdapter.addData(topScore)
            showLoading(false)
        }
    }


    private val onSuccessGetUserData = Observer<UserData> { userData ->
        this.userData = userData
        tv_win.text = userData.win.toString()
        tv_lose.text = userData.lose.toString()
        tv_draw.text = userData.draw.toString()
        tv_level_status.text = userData.level.toString()
        tv_email_label.text = auth.currentUser?.email
        tv_username_label.text = auth.currentUser?.displayName
        pb_xp_status.progress = userData.exp
        pb_xp_status.max = when (userData.level) {
            1 -> 5
            2 -> 10
            3 -> 15
            4 -> 25
            5 -> 40
            else -> if (userData.exp > 100) 100 else userData.exp
        }

        if (userData.frameUrl != "") {
            profileViewModel.downloadFrameProfile(userData.frameUrl)
        } else {
            profileViewModel.downloadPhotoProfile()
        }
    }

    private val onSuccessDownloadFrame = Observer<Uri> { uri ->
        Glide.with(baseContext)
            .load(uri)
            .apply(RequestOptions().circleCrop())
            .into(iv_frame_profile)
    }

    private val onSuccessDownloadPhoto = Observer<Uri> { uri ->
        this.uriPhoto = uri
        Glide.with(baseContext)
            .load(uri)
            .apply(RequestOptions().circleCrop())
            .into(iv_profile_picture)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            pb_top_score.visibility = View.VISIBLE
        } else {
            pb_top_score.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_dari_kanan, R.anim.slide_ke_kiri)
    }

}