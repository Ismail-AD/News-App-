package com.example.tazakhabar.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tazakhabar.APIsInterface.GetNewsApi
import com.example.tazakhabar.Utils.Constants.API_KEY
import com.example.tazakhabar.View.MainActivity
import com.example.tazakhabar.modelClasses.Article
import com.example.tazakhabar.modelClasses.TotalArticles
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel //Hilt will automatically provide any dependencies that your ViewModel needs.
// This saves you the trouble of manually passing in dependencies
class viewModel @Inject constructor(val getNewsApi: GetNewsApi) : ViewModel() {

    private lateinit var country: String
    private lateinit var category: String
    private var pageSize: Int = 0
    var changeAbleList = MutableLiveData<List<Article>?>()
    var receviedNews = ArrayList<Article>()

    fun argumentSetter(country: String, pageSize: Int) {
        this.country = country
        this.pageSize = pageSize
        getNewsFromApi(country, pageSize, "")
    }

    fun getNewsList(): MutableLiveData<List<Article>?> {
        return changeAbleList
    }

    fun searchNews(searchText: String) {
        receviedNews.clear()
        changeAbleList.value = null
        val callInstance: Call<TotalArticles> =
            getNewsApi.get_News_with_keywords(searchText, API_KEY)
        callInstance.enqueue(object : Callback<TotalArticles> {
            override fun onResponse(call: Call<TotalArticles>, response: Response<TotalArticles>) {
                receviedNews.addAll(response.body()!!.articles)
                changeAbleList.value = receviedNews
            }
            override fun onFailure(call: Call<TotalArticles>, t: Throwable) {
                changeAbleList.value = null
            }
        })
    }
    fun getCategory(category: String) {
        this.category = category
        getNewsFromApi(country, pageSize, this.category)
    }

    private fun getNewsFromApi(country: String, pageSize: Int, category: String) {
        receviedNews.clear()
        changeAbleList.value = null
        val callObject: Call<TotalArticles>
        if (category == "") {
            callObject = getNewsApi.get_General_News(country, pageSize, API_KEY)
        } else {
            callObject = getNewsApi.get_Category_Wise(country, category, pageSize, API_KEY)
        }

        // anonymous object that implements the Callback interface to define the response and error handling methods.
        callObject.enqueue(object : Callback<TotalArticles> {
            override fun onResponse(call: Call<TotalArticles>, response: Response<TotalArticles>) {
                receviedNews.addAll(response.body()!!.articles)
                changeAbleList.value = receviedNews
            }
            override fun onFailure(call: Call<TotalArticles>, t: Throwable) {
                changeAbleList.value =
                    null //suppose if we create another request and that request failed
                // then old state should not be display
            }
        })
    }


}