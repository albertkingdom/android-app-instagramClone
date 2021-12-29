package com.albertkingdom.loginsignuptest.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertkingdom.loginsignuptest.api.ImgurApi
import com.albertkingdom.loginsignuptest.api.PushNotificationApi
import com.albertkingdom.loginsignuptest.model.*
import com.albertkingdom.loginsignuptest.util.FileUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception


class MyViewModel : ViewModel() {
    var imageImgurUrl: String? = null
    private val db: FirebaseFirestore = Firebase.firestore
    val TAG = "viewModel"
    val isLogin: MutableLiveData<Boolean> = MutableLiveData(false)

    var postList: MutableLiveData<List<Post>> = MutableLiveData()
    var postIdList: List<String> = listOf()
    var singleUserPostList: MutableLiveData<List<Post>> = MutableLiveData()
    var singleUserPostIdList: List<String> = listOf()
    var clickedPostPosition: Int? = null
    var userEmailToBeShowInProfile: String? = null
    var followingUserList: MutableLiveData<List<String>> = MutableLiveData()
    var othersfollowingUserList: MutableLiveData<List<String>> = MutableLiveData()
    var fansCount: MutableLiveData<Int> = MutableLiveData(0)
    val auth: FirebaseAuth = Firebase.auth


    init {
        if (checkIsLogIn()) {
            val loginUserEmail = auth.currentUser?.email.toString()
            isLogin.value = true
            getFollowingUser(loginUserEmail)
            getFans()
        }
    }

    private fun uploadImage(imageUri: Uri, context: Context, isCameraPhoto: Boolean) =

        viewModelScope.async {
            //val file = FileUtil.getFileFromUri(imageUri, context)
            //println("$TAG path= ${FileUtil.getPath(context, imageUri).toString()}")

            // if isCameraPhoto, just get File instance from url. if photo is from gallery, use util method to get absolute path
//            val file = if (isCameraPhoto) File(imageUri.toString()) else File(FileUtil.getPath(context, imageUri).toString())
            val file = if (isCameraPhoto) File(imageUri.toString()) else File(FileUtil.fileFromContentUri(context, imageUri).toString())
            Log.d(TAG, "path...${file.absolutePath}")
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file!!)


            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image", file.name,
                requestFile
            )
            val uploadResult = ImgurApi.retrofitService.getResult(body)

            println("upload result:... status:${uploadResult.status}...link..${uploadResult.data.link}")

            val statusCode = uploadResult.status
            val imgurLink = uploadResult.data.link


            if (statusCode == 200) {
                imageImgurUrl = imgurLink
            }
        }


    fun uploadToFirebase(
        postTextContent: String,
        imageRelativePath: Uri,
        context: Context,
        userEmail: String,
        isCameraPhoto: Boolean
    ) {


        viewModelScope.launch {
            val uploadImageResult = uploadImage(imageRelativePath, context, isCameraPhoto)

            uploadImageResult.await()


            /* val post = hashMapOf(
                 "content" to postTextContent,
                 "userEmail" to userEmail,
                 "imageLink" to imageImgurUrl
             ) */
//            val commentList = listOf<Comment>(
//                Comment(userEmail= "test0@gmail.com", commentContent = "wow1"),
//                Comment(userEmail= "test0@gmail.com", commentContent = "wow2")
//            )
            val post = Post(
                imageLink = imageImgurUrl!!,
                userEmail = userEmail,
                postContent = postTextContent,
                commentList = null,
                timestamp = FieldValue.serverTimestamp()
            )


            val postRef = db.collection("post").document()

            postRef
                .set(post)
                .addOnSuccessListener {
                    Log.d(TAG, "success")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    fun updateProfile(newName: String?, imageRelativePath: Uri?, context: Context) {
        if (!checkIsLogIn()) {
            Log.w("update profile fragment", "user is null")
            return
        }
        imageImgurUrl = null
        // upload photo if user choose new profile photo
        if (imageRelativePath != null) {
            viewModelScope.launch {

                val uploadImageResult = uploadImage(imageRelativePath, context, false)

                uploadImageResult.await()
                val profileUpdates = userProfileChangeRequest {
                    displayName = newName
                    photoUri = Uri.parse(imageImgurUrl)
                }
                auth.currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("update profile fragment", "User profile updated.")
                        }
                    }
            }

        } else {
            val profileUpdates = userProfileChangeRequest {
                displayName = newName

            }
            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("update profile fragment", "User profile updated.")
                    }
                }
        }

    }

    fun getPost() {
        // Create a reference to the cities collection
        val postRef = db.collection("post")

        // Create a query against the collection.
        val loginUserEmail = auth.currentUser?.email.toString()
        val query = postRef.whereNotEqualTo("userEmail", loginUserEmail).orderBy("userEmail")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (value != null) {
                value.documents.forEach {
                    println("value.document...${it.id}")
                }
                val tempIdList = value.documents.map {
                    it.id
                }
                postIdList = tempIdList
                println("TAG...$postList")
            }

            val tempPostList = value?.toObjects<Post>()
            Log.d(TAG, "postlist...$tempPostList")
            postList.postValue(tempPostList)
        }

    }

    fun addComment(commentContent: String) {
        // if clickedPostPosition != null, get -> postIdList[clickedPostPosition]
        val id = clickedPostPosition?.let { postIdList[it] }
        val postRef = id?.let { db.collection("post").document(it) }
        val newComment =
            Comment(userEmail = auth.currentUser?.email, commentContent = commentContent)
        postRef?.update("commentList", FieldValue.arrayUnion(newComment))
    }

    fun checkIsLogIn(): Boolean {
        if (auth.currentUser != null) {
            isLogin.value = true


            return true
        }

        isLogin.value = false
        return false
    }

    fun signOut() {
        auth.signOut()

    }

    fun getSingleUserPost(email: String) {

        val postRef = db.collection("post")


        val query = postRef.whereEqualTo("userEmail", email).orderBy("timestamp", Query.Direction.DESCENDING)

        query.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (value != null) {
                value.documents.forEach {
                    println("value.document...${it.id}")
                }
                val tempIdList = value.documents.map {
                    it.id
                }
                singleUserPostIdList = tempIdList

            }

            val tempPostList = value?.toObjects<Post>()

            singleUserPostList.postValue(tempPostList)
        }

    }

    fun followUser() {


//            val userlist = listOf(User(email = userEmailToBeShowInProfile))
        val userlist = listOf(userEmailToBeShowInProfile.toString())
        val follow = Follow(followingUserEmail = userlist)


        val postRef = db.collection("userList").document(auth.currentUser?.email.toString())


        postRef
            .set(follow, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }

    fun cancelFollowUser() {
        val loginUserEmail = auth.currentUser?.email.toString()
        val ref = db.collection("userList").document(auth.currentUser?.email.toString())

        val newFollowingList = followingUserList.value?.filter { email ->
            email != userEmailToBeShowInProfile
        }
        val follow = Follow(followingUserEmail = newFollowingList)

        ref
            .set(follow)
            .addOnSuccessListener {
                Log.d(TAG, "success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getFollowingUser(email: String) {
        val loginUserEmail = auth.currentUser?.email.toString()
        val userListRef = db.collection("userList").document(email)


        userListRef.get().addOnSuccessListener { documentSnapshot ->
//            val follow = documentSnapshot.toObject<Follow>()?.followingUserEmail?.map { user ->
//                user.email.toString()
//            }
            val follow = documentSnapshot.toObject<Follow>()?.followingUserEmail
            if (email == loginUserEmail) {
                followingUserList.value = follow
            } else {
                othersfollowingUserList.value = follow
            }

        }


    }

    fun getFans(email: String? = null) {
        val emailToQuery: String?
        val loginUserEmail = auth.currentUser?.email.toString()
        emailToQuery = if (isLogin.value == true) {
            loginUserEmail
        } else if (email != null) {
            email
        } else {
            Log.e(TAG, "getFans() need email input")
            return
        }

//        val query = db.collection("userList").whereArrayContains("follwingUserEmail", User(email = loginUserEmail))
        val query = db.collection("userList").whereArrayContains("followingUserEmail", emailToQuery)
        query.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (value != null) {
                println("get fans...${value.documents.size}")
                fansCount.value = value.documents.size
            }

        }


    }

    // send a firebase notification
    fun sendNotificationToFirebase(notification: PushNotification) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val response = PushNotificationApi.retrofitService.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d(TAG, "Response: ${Gson().toJson(response)}" )
                    Log.d(TAG, "Response: is successful...${response.body()?.string()}")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

    }
}