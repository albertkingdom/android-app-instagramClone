package com.albertkingdom.loginsignuptest

import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.albertkingdom.loginsignuptest.api.Constants.Companion.TOPIC_NEW_POST
import com.albertkingdom.loginsignuptest.model.InternalStoragePhoto
import com.albertkingdom.loginsignuptest.model.NotificationData
import com.albertkingdom.loginsignuptest.model.PushNotification
import com.albertkingdom.loginsignuptest.service.FirebaseService
import com.albertkingdom.loginsignuptest.util.AlertDialogUtil
import com.albertkingdom.loginsignuptest.util.AlertReason
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class NewArticleFragment: Fragment(R.layout.new_article_fragment) {
    private lateinit var imageView: ImageView
    private lateinit var textView: TextInputLayout

    private val viewModel: MyViewModel by activityViewModels()
    private var imageRelativePath: Uri? = null
    private var cameraPhotoPath: String? = null
    private var userEmail: String? = null
    val TAG = "NewArticleFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        userEmail = viewModel.auth.currentUser?.email
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.image)
        textView = view.findViewById(R.id.text_input)

        setPickImageListener()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_article_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.share_button -> {
                Log.d(TAG,"share button click")
                val postContent = textView.editText?.text.toString()

                // share post with camera image
                if (cameraPhotoPath != null && viewModel.checkIsLogIn()) {
                    Log.d(TAG,"upload photo from camera")
                    viewModel.uploadToFirebase(
                        postContent,
                        cameraPhotoPath!!.toUri(),
                        requireContext(),
                        userEmail!!,
                        true
                    )
                    navigateToList()

                    constructNotificationToFirebase()
                    return true
                }

                if (imageRelativePath != null && viewModel.checkIsLogIn()) {
                    Log.d(TAG, "upload photo from gallery")
                    viewModel.uploadToFirebase(
                        postContent,
                        imageRelativePath!!,
                        requireContext(),
                        userEmail!!,
                        false
                    )
                    constructNotificationToFirebase()
                    navigateToList()
                } else if (imageRelativePath == null && viewModel.checkIsLogIn()){
                    AlertDialogUtil.showAlertDialog(AlertReason.EMPTY_IMAGE, requireContext())
                } else {
                    AlertDialogUtil.showAlertDialog(AlertReason.NOT_LOGIN, requireContext())
                }

                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

    // alternative image picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ result: Uri? ->
        if(result != null) {
            imageView.setImageURI(result)
            Log.d(TAG, result.toString())
           imageRelativePath = result
            Log.d(TAG, imageRelativePath.toString())
        }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        val isSavedSuccessfully = savePhotoToInternalStorage(UUID.randomUUID().toString(), it)
        Log.d(TAG, isSavedSuccessfully.toString())
        if(isSavedSuccessfully) {
            loadPhotosFromInternalStorageToImageView()
        }
    }
    private fun setPickImageListener() {
        imageView.setOnClickListener {
            //pickImageLauncher.launch("image/*")

            val modalBottomSheet = BottomSheetFragment()
            modalBottomSheet.setOnClickButton(object : OnClickButton {
                override fun onClickCamera() {
                    takePhoto.launch()
                }

                override fun onClickGallery() {
                    pickImageLauncher.launch("image/*")
                }

            })
            modalBottomSheet.show(parentFragmentManager, BottomSheetFragment.TAG)
        }
    }

    private fun navigateToList() {
        findNavController().navigate(R.id.postlist)

    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap):Boolean {
        return try {
            deletePhotosInInternalStorage()

            activity?.openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)){
                    throw  IOException("Couldn't save bitmap")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = activity?.filesDir?.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Log.d(TAG, "loadPhotosFromInternalStorage...${it.path}")
                InternalStoragePhoto(it.name, bmp, it.path.toUri())

            } ?: listOf()
        }
    }

    private fun loadPhotosFromInternalStorageToImageView() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            val latestPhoto = photos.last()
            imageView.setImageBitmap(latestPhoto.bmp)
            cameraPhotoPath = latestPhoto.url.toString()
            Log.d(TAG, "cameraPhotoPath...$cameraPhotoPath")
        }
    }

    // delete previous taken photo
    private fun deletePhotosInInternalStorage(): Boolean {
        return try {
            val files = activity?.filesDir?.listFiles()
            files?.filter { it.isFile && it.name.endsWith(".jpg") }?.forEach {
                activity?.deleteFile(it.name)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun constructNotificationToFirebase() {
        PushNotification(
            NotificationData("New Post", "$userEmail just share new post!", FirebaseService.token, userEmail),
            TOPIC_NEW_POST

        ).also {
            viewModel.sendNotificationToFirebase(it)
        }
    }
}

