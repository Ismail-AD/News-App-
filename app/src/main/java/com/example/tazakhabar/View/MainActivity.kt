package com.example.tazakhabar.View

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tazakhabar.Adapter.adapterClass
import com.example.tazakhabar.Adapter.categoryListAdapter
import com.example.tazakhabar.R
import com.example.tazakhabar.ViewModel.viewModel
import com.example.tazakhabar.databinding.ActivityMainBinding
import com.example.tazakhabar.modelClasses.Article
import com.example.tazakhabar.modelClasses.categoryList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    val viewModelObject by viewModels<viewModel>() // This function creates a ViewModel object associated with the current Activity.

    var newsList = ArrayList<Article>()
    var categoryList = ArrayList<categoryList>()
    lateinit var catName: Array<String>
    lateinit var catTag: Array<String>
    lateinit var catImg: Array<Int>
    lateinit var countryNames: Array<String>
    lateinit var color: Array<Int>
    lateinit var adapterClass: adapterClass
    lateinit var categoryListAdapter: categoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarWithDrawer()
        setCategoryData()

        categoryListAdapter = categoryListAdapter(categoryList, ::onCategoryClicked)
        binding.recyclerViewCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategory.setHasFixedSize(true)

        binding.recyclerViewCategory.adapter = categoryListAdapter
        adapterClass = adapterClass(newsList)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapterClass
        countryWiseSetter("au", 10, countryNames[0])

        //The Observer is notified whenever there is a change in the value of mutableLiveData Instance
        //and the code within the Observer block is executed.
        viewModelObject.getNewsList().observe(this, Observer {
            newsList.clear()
            if (it != null) {
                binding.ShimLayout.stopShimmer()
                binding.ShimLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                newsList.addAll(it)
            }
            adapterClass.notifyDataSetChanged()
        })

        binding.designNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.australia -> countryWiseSetter("au", 10, countryNames[0])
                R.id.brazil -> countryWiseSetter("br", 10, countryNames[1])
                R.id.canada -> countryWiseSetter("ca", 10, countryNames[2])
                R.id.china -> countryWiseSetter("cn", 10, countryNames[3])
                R.id.france -> countryWiseSetter("fr", 10, countryNames[4])
                R.id.germany -> countryWiseSetter("de", 10, countryNames[5])
                R.id.hong_kong -> countryWiseSetter("hk", 10, countryNames[6])
                R.id.italy -> countryWiseSetter("it", 10, countryNames[7])
                R.id.japan -> countryWiseSetter("jp", 10, countryNames[8])
                R.id.philippines -> countryWiseSetter("ph", 10, countryNames[9])
                R.id.portugal -> countryWiseSetter("pt", 10, countryNames[10])
                R.id.romania -> countryWiseSetter("ro", 10, countryNames[11])
                R.id.singapore -> countryWiseSetter("sg", 10, countryNames[12])
                R.id.spain -> countryWiseSetter("es", 10, countryNames[13])
                R.id.switzerland -> countryWiseSetter("ch", 10, countryNames[14])
                R.id.taiwan -> countryWiseSetter("tw", 10, countryNames[15])
                R.id.ukraine -> countryWiseSetter("ua", 10, countryNames[16])
                R.id.united_kingdom -> countryWiseSetter("gb", 10, countryNames[17])
                R.id.united_states -> countryWiseSetter("us", 10, countryNames[18])
            }
            binding.drLayout.closeDrawer(GravityCompat.START)
            //method expects a Boolean value to be returned by the lambda expression.
            //Returning true indicates that the item selection event has been handled successfully
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_news, menu)
        val searchItem = menu!!.findItem(R.id.iSearch)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModelObject.searchNews(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }
    fun countryWiseSetter(countryTag: String, pageSize: Int, nameOfCountry: String) {
        binding.recyclerView.visibility = View.GONE
        binding.ShimLayout.visibility = View.VISIBLE
        binding.ShimLayout.startShimmer()
        viewModelObject.argumentSetter(countryTag, pageSize)
        binding.myToolbar.title = "$nameOfCountry News"
    }

    private fun setCategoryData() {
        catName = resources.getStringArray(R.array.CategoryName)
        countryNames = resources.getStringArray(R.array.countryNames)
        catTag = resources.getStringArray(R.array.categoryTag)
        catImg = arrayOf(R.drawable.global_news_4305,
            R.drawable.ic_baseline_business_24,
            R.drawable.ic_baseline_sports_tennis_24,
            R.drawable.ic_baseline_techonology_24)
        color = arrayOf(
            R.color.purple_100,
            R.color.orange_400,
            R.color.orange_100,
            R.color.blue_A100,
        )
        for (i in catName.indices) {
            val dataObject = categoryList(catName[i], catImg[i], catTag[i], color[i])
            categoryList.add(dataObject)
        }
    }
    fun onCategoryClicked(categoryList: categoryList) {
        binding.recyclerView.visibility = View.GONE
        binding.ShimLayout.visibility = View.VISIBLE
        binding.ShimLayout.startShimmer()
        viewModelObject.getCategory(categoryList.categoryTag)
    }
    private fun setToolbarWithDrawer() {
        setSupportActionBar(binding.myToolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drLayout,
            binding.myToolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drLayout.addDrawerListener(toggle) //toggle object as a listener to the navigation drawer, so that it can respond
        // to drawer events (such as when the drawer is opened or closed) and update the UI accordingly.

//It will synchronize the drawer icon that rotates when the drawer is swiped gestured left or right
// and if you try to removed the syncState() the synchronization will fail thus resulting to buggy rotation
// or it wont even work.
//It is called in the onPostCreate to synchronize the animation all over again when the Activity is restored
        toggle.syncState()
    }
}