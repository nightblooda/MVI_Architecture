package com.example.mviexample.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mviexample.R
import com.example.mviexample.model.BlogPost
import com.example.mviexample.model.User
import com.example.mviexample.ui.DataStateListener
import com.example.mviexample.ui.main.state.MainStateEvent.*
import com.example.mviexample.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(),
    MainRecyclerAdapter.Interaction
{

    private val TAG: String = "AppDebug"

    override fun onItemSelected(position: Int, item: BlogPost) {
        println("DEBUG: CLICKED ${position}")
        println("DEBUG: CLICKED ${item}")
    }

    lateinit var viewModel: MainViewModel

    lateinit var dataStateHandler: DataStateListener

    lateinit var mainRecyclerAdapter: MainRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")


        initRecyclerView()
        subscribeObservers()
    }

    private fun initRecyclerView(){
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)
            mainRecyclerAdapter = MainRecyclerAdapter(this@MainFragment)
            adapter = mainRecyclerAdapter
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            // Handle Loading and Message
            dataStateHandler.onDataStateChange(dataState)

            // handle Data<T>
            dataState.data?.let{ event ->
                event.getContentIfNotHandled()?.let{ mainViewState ->

                    println("DEBUG: DataState: ${mainViewState}")

                    mainViewState.blogPosts?.let{
                        // set BlogPosts data

                        viewModel.setBlogListData(it)
                    }

                    mainViewState.user?.let{
                        // set User data
                        viewModel.setUser(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            viewState.blogPosts?.let {blogPosts ->
                // set BlogPosts to RecyclerView
                println("DEBUG: Setting blog posts to RecyclerView: ${blogPosts}")
                mainRecyclerAdapter.submitList(blogPosts)
            }

            viewState.user?.let{ user ->
                // set User data to widgets
                println("DEBUG: Setting User data: ${user}")
                setUserProperties(user)

            }
        })
    }

    fun setUserProperties(user: User){
        email.setText(user.email)
        username.setText(user.username)

        view?.let{
            Glide.with(it.context)
                .load(user.image)
                .into(image)
        }

    }

    fun triggerGetUserEvent(){
        viewModel.setStateEvent(GetUserEvent("1"))
    }

    fun triggerGetBlogsEvent(){
        viewModel.setStateEvent(GetBlogPostsEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_get_blogs-> triggerGetBlogsEvent()

            R.id.action_get_user-> triggerGetUserEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            dataStateHandler = context as DataStateListener
        }catch(e: ClassCastException){
            println("$context must implement DataStateListener")
        }

    }
}














