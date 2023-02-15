package com.gratum.firebasecorutinas1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.gratum.firebasecorutinas1.model.MemberModel
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModel
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFlow()
        //registerTest()
        signInTest()
    }

    private fun signInTest() {
        viewModel.signIn("test", "123456")
    }

    private fun registerTest() {
        val memberMode = MemberModel()

        //as user seq
        val uuid = getCurrentDateTimeString()

        memberMode.uuId = uuid
        memberMode.id = "test"
        memberMode.password = "123456"
        memberMode.email = "test@test.com"
        memberMode.nickname = "tester"
        memberMode.profileImageUrl = ""
        memberMode.profileImageFileCloudPath = ""
        memberMode.createDate = getCurrentDateTimeString()
        memberMode.modifyDate = getCurrentDateTimeString()
        memberMode.createBy = uuid
        memberMode.modifyBy = uuid

        viewModel.register(memberMode)
    }

    private fun getCurrentDateTimeString(): String {
        val date = Calendar.getInstance().time
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)

    }

    private fun initFlow() {
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                viewModel.memberViewModelState.collect {
                    when (it) {
                        is MemberViewModelState.RegisterSuccessfully -> {
                            //Register succes
                            Log.d("???", "${it.memberModel}")
                            Toast.makeText(this@MainActivity, "Register succes", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is MemberViewModelState.SingSuccessfully -> {
                            Log.d("???", "${it.memberModel.toDictionary()}")
                            Toast.makeText(this@MainActivity, "sign in success", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is MemberViewModelState.Loading -> {
                            //show progresss here
                        }

                        is MemberViewModelState.Empty -> {
                            //Selected is empty
                        }
                        is MemberViewModelState.Error -> {
                            Log.d("???", "Error")
                        }

                        is MemberViewModelState.None -> Unit
                        else -> {

                        }
                    }
                }
            }
        }
    }
}