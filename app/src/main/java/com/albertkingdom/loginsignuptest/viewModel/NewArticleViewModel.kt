package com.albertkingdom.loginsignuptest.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.albertkingdom.loginsignuptest.TAG
import com.albertkingdom.loginsignuptest.model.Photo

class NewArticleViewModel: ViewModel() {
    private val _isLogin: MutableLiveData<Boolean> = MutableLiveData(true)

    private val _galleryImageList: MutableLiveData<List<Photo>> = MutableLiveData()
    val galleryImageList: LiveData<List<Photo>> = _galleryImageList

    val _selectedImagePosition: MutableLiveData<Int> = MutableLiveData(0)

    val imageRelativePath: LiveData<Uri> = Transformations.map(_selectedImagePosition) { position ->
        galleryImageList.value?.get(position)?.uri
    }

    private val _cameraPhotoPath: MutableLiveData<String> = MutableLiveData()
    val cameraPhotoPath: LiveData<String> = _cameraPhotoPath

    private val _isUseCameraPhoto: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUseCameraPhoto: LiveData<Boolean> = _isUseCameraPhoto

    fun setGalleryImageList(list: List<Photo>) {
        _galleryImageList.value = list
    }

    fun setSeletedImagePosition(position: Int) {
        _selectedImagePosition.value = position
    }

    fun checkIfOkToGoToStep2(): Boolean {

        if (_selectedImagePosition.value == null || !_isLogin.value!!){
           return false
        }
        return true
    }

    fun setCameraPhotoPath(path: String) {
        _cameraPhotoPath.value = path
    }

    fun setIsUseCameraPhoto(isUseCameraPhoto: Boolean) {
        _isUseCameraPhoto.value = isUseCameraPhoto
    }
}