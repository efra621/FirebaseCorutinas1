package com.gratum.firebasecorutinas1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.gratum.firebasecorutinas1.adapter.Adapter1
import com.gratum.firebasecorutinas1.databinding.ActivityMainBinding
import com.gratum.firebasecorutinas1.databinding.ActivityPaginationTestBinding
import com.gratum.firebasecorutinas1.model.MemberModel
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModel
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaginationTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaginationTestBinding

    private val layoManager = LinearLayoutManager(this)
    private lateinit var adapter1: Adapter1

    private var offset: DocumentSnapshot? = null

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaginationTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initFlow()
        initList()

    }

    private fun initFlow() {
        lifecycleScope.launch(Dispatchers.Main) {
            whenCreated {

                viewModel.memberViewModelState.collect {

                    when(it){
                        is MemberViewModelState.RegisterSuccessfully ->{

                        }
                        is MemberViewModelState.SingSuccessfully ->{

                        }
                        is MemberViewModelState.Error ->{
                            Log.d("???", "${it.message}")
                        }

                        is MemberViewModelState.UpdateSuccessfully ->{

                        }

                        is MemberViewModelState.DeleteSuccessfully ->{

                        }
                        is MemberViewModelState.Empty -> {
                            Log.d("???", "No more data")
                        }
                        is MemberViewModelState.LoadMoreSuccessfully -> {
                            this@PaginationTestActivity.offset = it.offset
                            adapter1.loadMore(it.list)
                        }
                        is MemberViewModelState.Loading -> {

                        }
                        is MemberViewModelState.None -> {

                        }
                        is MemberViewModelState.ReloadSuccessfully -> {
                            this@PaginationTestActivity.offset = it.offset
                            adapter1.reload(it.list)
                        }
                    }
                }
            }
        }
    }

    private fun initList() {
        binding.swiperefresh.setOnRefreshListener {
            binding.swiperefresh.isRefreshing =false
            reload()
        }
        adapter1 = Adapter1 {
            loadMore()
        }

        binding.rvMain.layoutManager = layoManager
        binding.rvMain.adapter = adapter1
    }

    private fun reload() {
        viewModel.reload(MemberModel.CREATE_DATE_KEY, limit = 2)
    }

    private fun loadMore() {
        viewModel.loadMore(MemberModel.CREATE_DATE_KEY,offset= offset, limit = 2)
    }

    private fun showProgress() {
        binding.progress2.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress2.visibility = View.GONE
    }

}