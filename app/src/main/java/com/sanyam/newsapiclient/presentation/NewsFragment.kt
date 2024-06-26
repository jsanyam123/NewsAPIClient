package com.sanyam.newsapiclient.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanyam.newsapiclient.R
import com.sanyam.newsapiclient.data.util.Resource
import com.sanyam.newsapiclient.databinding.FragmentNewsBinding
import com.sanyam.newsapiclient.presentation.adapter.NewsAdapter
import com.sanyam.newsapiclient.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {
    private  lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var fragmentNewsBinding: FragmentNewsBinding
    private var country = "in"
    private var page = 1
    private var isScrolling = false
    private var isLoading = false
    private var isLastPage = false
    private var pages = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNewsBinding = FragmentNewsBinding.bind(view)
        viewModel= (activity as MainActivity).viewModel
        newsAdapter= (activity as MainActivity).newsAdapter
        newsAdapter.setOnItemClickListener {
          val bundle = Bundle().apply {
             putSerializable("selected_article",it)
          }
          findNavController().navigate(
              R.id.action_newsFragment_to_infoFragment,
              bundle
          )
        }
        initRecyclerView()
        viewNewsList()
        setSearchView()
    }

    private fun viewNewsList() {
        viewModel.getNewsHeadLines(country,page)
        viewModel.newsHeadLines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Log.i("MYTAG", "came here ${it.articles.toList().size}")
                        newsAdapter.differ.submitList(it.articles.toList())
                        pages = if (it.totalResults % 20 == 0) {
                            it.totalResults / 20
                        } else {
                            it.totalResults / 20 + 1
                        }
                        isLastPage = page == pages
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun initRecyclerView() {
       // newsAdapter = NewsAdapter()
        fragmentNewsBinding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }

    }

    private fun showProgressBar(){
        isLoading = true
        fragmentNewsBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        isLoading = false
        fragmentNewsBinding.progressBar.visibility = View.INVISIBLE
    }

   private val onScrollListener = object : RecyclerView.OnScrollListener(){
       override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
           super.onScrollStateChanged(recyclerView, newState)
           if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
               isScrolling = true
           }

       }

       override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
           super.onScrolled(recyclerView, dx, dy)
           val layoutManager = fragmentNewsBinding.rvNews.layoutManager as LinearLayoutManager
           val sizeOfTheCurrentList = layoutManager.itemCount
           val visibleItems = layoutManager.childCount
           val topPosition = layoutManager.findFirstVisibleItemPosition()

           val hasReachedToEnd = topPosition+visibleItems >= sizeOfTheCurrentList
           val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling
           if(shouldPaginate){
               page++
               viewModel.getNewsHeadLines(country,page)
               isScrolling = false

           }


       }
   }

   //search
   private fun setSearchView(){
     fragmentNewsBinding.svNews.setOnQueryTextListener(
         object : SearchView.OnQueryTextListener{
             override fun onQueryTextSubmit(p0: String?): Boolean {
                 viewModel.searchNews(country, p0.toString(), page)
                 viewSearchedNews()
                 return false
             }

             override fun onQueryTextChange(p0: String?): Boolean {
                 MainScope().launch {
                     delay(2000)
                     viewModel.searchNews(country, p0.toString(), page)
                     viewSearchedNews()
                 }
                 return false
             }
         })

         fragmentNewsBinding.svNews.setOnCloseListener(
             SearchView.OnCloseListener {
                 initRecyclerView()
                 viewNewsList()
                 false
             })
   }

   fun viewSearchedNews(){
       viewModel.searchedNews.observe(viewLifecycleOwner) { response ->
           when (response) {
               is Resource.Success -> {
                   hideProgressBar()
                   response.data?.let {
                       Log.i("MYTAG", "came here ${it.articles.toList().size}")
                       newsAdapter.differ.submitList(it.articles.toList())
                       pages = if (it.totalResults % 20 == 0) {
                           it.totalResults / 20
                       } else {
                           it.totalResults / 20 + 1
                       }
                       isLastPage = page == pages
                   }
               }
               is Resource.Error -> {
                   hideProgressBar()
                   response.message?.let {
                       Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG).show()
                   }
               }
               is Resource.Loading -> {
                   showProgressBar()
               }
           }
       }
   }
}