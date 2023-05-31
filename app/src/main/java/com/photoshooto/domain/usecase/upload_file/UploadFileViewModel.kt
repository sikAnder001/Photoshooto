package com.photoshooto.domain.usecase.upload_file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoshooto.domain.model.FileUploadModel
import com.photoshooto.util.Resource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadFileViewModel constructor(private val uploadFileUseCase: UploadFileUseCase) :
    ViewModel() {

    private val _updateImgFileStatus = MutableLiveData<Resource<FileUploadModel>>()
    val updateImgFileStatus: LiveData<Resource<FileUploadModel>> get() = _updateImgFileStatus
    val showProgressbar = MutableLiveData<Boolean>()

    fun updateImgFile(
        photos: MultipartBody.Part?,
        category: HashMap<String, RequestBody>?, token: String?
    ) {
        showProgressbar.value = true
        viewModelScope.launch {
            uploadFileUseCase.updateImgFile(photos, category, token).let {
                if (it.isSuccessful) {
                    showProgressbar.value = false
                    _updateImgFileStatus.postValue(Resource.success(it.body()))
                } else {
                    showProgressbar.value = false
                    _updateImgFileStatus.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun updateImgFileArrayList(
        photos: ArrayList<MultipartBody.Part?>,
        category: HashMap<String, RequestBody>?, token: String?
    ) {
        showProgressbar.value = true
        viewModelScope.launch {
            uploadFileUseCase.updateImgFileArrayList(photos, category, token).let {
                if (it.isSuccessful) {
                    showProgressbar.value = false
                    _updateImgFileStatus.postValue(Resource.success(it.body()))
                } else {
                    showProgressbar.value = false
                    _updateImgFileStatus.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun resetUpdateImageEvent() {
        _updateImgFileStatus.value = Resource.success(data = null)

    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}