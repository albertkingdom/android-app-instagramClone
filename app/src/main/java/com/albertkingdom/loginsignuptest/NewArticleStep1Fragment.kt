package com.albertkingdom.loginsignuptest

import android.content.ContentUris
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albertkingdom.loginsignuptest.adapter.ClickGallery
import com.albertkingdom.loginsignuptest.adapter.NewArticlePhotoAdapter
import com.albertkingdom.loginsignuptest.model.InternalStoragePhoto
import com.albertkingdom.loginsignuptest.model.Photo
import com.albertkingdom.loginsignuptest.util.AlertDialogUtil
import com.albertkingdom.loginsignuptest.util.AlertReason
import com.albertkingdom.loginsignuptest.viewModel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

const val TAG = "NewArticleFragment2"
class NewArticleStep1Fragment: Fragment(R.layout.new_article_step_1_fragment) {
    private lateinit var imageView: ImageView
    private lateinit var cameraButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewArticlePhotoAdapter
    private val galleryImageList: MutableLiveData<List<Photo>> = MutableLiveData()
    private  val viewModel: MyViewModel by activityViewModels()
    private var imageRelativePath: Uri? = null
    private var cameraPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.image)
        recyclerView = view.findViewById(R.id.photo_grid_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        adapter = NewArticlePhotoAdapter()
        adapter.onClickImageListener = object : ClickGallery {
            override fun onClickImage(position: Int) {
                imageView.setImageURI(galleryImageList.value?.get(position)?.uri)
                imageRelativePath = galleryImageList.value?.get(position)?.uri
            }

        }
        recyclerView.adapter = adapter


        galleryImageList.observe(viewLifecycleOwner, { list ->
            Log.d(TAG, "galleryImageList size = ${list.size}")
            adapter.submitList(list)
            if (list.isNotEmpty()) {
                imageView.setImageURI(list[0].uri)
                imageRelativePath = list[0].uri
            }
        })


        cameraButton = view.findViewById(R.id.camera_button)


        cameraButton.setOnClickListener {
            takePhoto.launch()
        }

        lifecycleScope.launch(Dispatchers.IO){
            loadImagesFromExternalStorage()
        }




    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_article_fragment_menu_2,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.next_button -> {

                if (imageRelativePath != null && viewModel.checkIsLogIn()) {
                    Log.d(TAG, "upload photo from gallery")

                    navigateToNextStep()
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

    /*
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ result: Uri? ->
        if(result != null) {
            imageView.setImageURI(result)
            Log.d(TAG, result.toString())
           imageRelativePath = result
            Log.d(TAG, imageRelativePath.toString())
        }
    }
    */

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap ->
        Log.d(TAG, "take photo result....$bitmap")
        if (bitmap == null) {
            return@registerForActivityResult
        }
        val isSavedSuccessfully = savePhotoToInternalStorage(UUID.randomUUID().toString(), bitmap)
        Log.d(TAG, isSavedSuccessfully.toString())
        if(isSavedSuccessfully) {
            loadPhotosFromInternalStorageAndNavigate()

        }
    }
    private fun loadImagesFromExternalStorage(): ArrayList<Photo> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI //content://media/external/images/media
        val cursor: Cursor?

        val listOfAllImages = ArrayList<Photo>()

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)

        cursor = activity?.contentResolver?.query(uri, projection, null, null, "DATE_ADDED DESC")

        val idColumn: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor != null && cursor.moveToNext()) {

            val id = idColumn?.let { cursor.getInt(it) }
            //  to combine content://xxx and image id to a full path
            val fullContentPath = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id!!.toLong())

            Log.d(TAG, "Image Full Content Path...$fullContentPath")
            listOfAllImages.add(Photo(fullContentPath))
        }
        galleryImageList.postValue(listOfAllImages)

        cursor?.close()
        return listOfAllImages
    }


//    private fun setPickImageListener() {
//        imageView.setOnClickListener {
//            //pickImageLauncher.launch("image/*")
//
//            val modalBottomSheet = BottomSheetFragment()
//            modalBottomSheet.setOnClickButton(object : OnClickButton {
//                override fun onClickCamera() {
//                    takePhoto.launch()
//                }
//
//                override fun onClickGallery() {
//                    pickImageLauncher.launch("image/*")
//                }
//
//            })
//            modalBottomSheet.show(parentFragmentManager, BottomSheetFragment.TAG)
//        }
//    }




    private fun navigateToNextStep() {
        val bundle: Bundle = if (cameraPhotoPath != null) {
            bundleOf("cameraPhoto" to cameraPhotoPath.toString())
        } else {
            bundleOf("uri" to imageRelativePath.toString())
        }

        findNavController().navigate(R.id.action_newArticleFragment2_to_newArticleStep2Fragment, bundle)

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

    private fun loadPhotosFromInternalStorageAndNavigate() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            val latestPhoto = photos.last()
            //imageView.setImageBitmap(latestPhoto.bmp)
            cameraPhotoPath = latestPhoto.url.toString()
            Log.d(TAG, "cameraPhotoPath...$cameraPhotoPath")

            navigateToNextStep()
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

    override fun onDestroyView() {
        super.onDestroyView()
        cameraPhotoPath = null

    }

}

