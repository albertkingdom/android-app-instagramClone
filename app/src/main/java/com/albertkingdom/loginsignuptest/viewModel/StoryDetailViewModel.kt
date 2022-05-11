package com.albertkingdom.loginsignuptest.viewModel

import androidx.lifecycle.*
import com.albertkingdom.loginsignuptest.model.Post
import kotlinx.coroutines.*

class StoryDetailViewModel: ViewModel() {

    var postList: List<Post> = listOf()
    val currentImageIndex: MutableLiveData<Int> = MutableLiveData(0)
    val currentImageUrl: LiveData<String> = Transformations.map(currentImageIndex) { index ->
        postList[index].imageLink
    }
    val flipCount: MutableLiveData<Int> = MutableLiveData(-1)
    val endOfPostList: MutableLiveData<Boolean> = MutableLiveData(false)
    lateinit var changeImageTimer: Job

    fun setupPostList(list: List<Post>) {
        postList = list
    }

    fun setCurrentImageIndex(index: Int) {
        currentImageIndex.value = index
    }
    fun setTimer() {
        changeImageTimer = viewModelScope.launch {

            while (currentImageIndex.value!! < postList.lastIndex) {

                delay(5000 + 2000)

                println("times up!...1")
                flipCount.value = flipCount.value?.plus(1)
            }


        }

        changeImageTimer.start()
        println("start timer")
    }

    fun cancelTimer() {
        changeImageTimer.cancel()
    }

    fun incrementCurrentImageIndex() {
        if (currentImageIndex.value != postList.lastIndex) {
            currentImageIndex.value = currentImageIndex.value?.plus(1)
        } else {
            endOfPostList.value = true
        }
    }
}