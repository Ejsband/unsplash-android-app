package com.project.androidunsplash.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.androidunsplash.domain.MetaDataUseCase
import com.project.androidunsplash.domain.UserUseCase
import com.project.androidunsplash.entity.Image
import com.project.androidunsplash.entity.ImageUrl
import com.project.androidunsplash.entity.UnsplashImage
import com.project.androidunsplash.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val metaDataUseCase: MetaDataUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow(
        User(
            name = "User Name",
            username = "alias",
            location = "location",
            profile_image = Image("")
        )
    )
    val userState = _userState.asStateFlow()

    fun reloadUserState() {
        viewModelScope.launch(Dispatchers.IO) {

            val metaData = metaDataUseCase.getMetaData("key")

            val header = "${metaData.tokenType} ${metaData.accessToken}"

            viewModelScope.launch(Dispatchers.IO) {
                val user = userUseCase.getUser(header)
                _userState.value = user
            }
        }
    }

    private val _userLikedImageListState = MutableStateFlow<List<UnsplashImage>>(mutableListOf())
    val userLikedImageListState = _userLikedImageListState.asStateFlow()

    fun reloadUserLikedImageListState() {
        viewModelScope.launch(Dispatchers.IO) {

            val meta = metaDataUseCase.getMetaData("key")
            val accessToken = "${meta.tokenType} ${meta.accessToken}"
            val user = withContext(Dispatchers.IO) {
                userUseCase.getUser(accessToken)
            }
            val list = userUseCase.getUserLikedImageList(
                user.username,
                accessToken
            )
            _userLikedImageListState.value = list
        }
    }

    private val _userImageItemState = MutableStateFlow(
        UnsplashImage(
            "",
            ImageUrl(""),
            "",
            false,
            User("", "", "", Image(""))
        )
    )
    val userImageItemState = _userImageItemState.asStateFlow()

    fun reloadUserImageItemState(imageId: String) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }
            _userImageItemState.value =
                userUseCase.getUserImageItem(imageId, "${data.tokenType} ${data.accessToken}")
        }
    }

    fun deleteMetaData() {
        viewModelScope.launch(Dispatchers.IO) {
            metaDataUseCase.deleteMetaData(metaDataUseCase.getMetaData("key"))
        }
    }
}