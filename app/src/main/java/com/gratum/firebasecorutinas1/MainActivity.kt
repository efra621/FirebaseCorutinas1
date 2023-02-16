package com.gratum.firebasecorutinas1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.gratum.firebasecorutinas1.databinding.ActivityInputInfoBinding
import com.gratum.firebasecorutinas1.databinding.ActivityMainBinding
import com.gratum.firebasecorutinas1.model.MemberModel
import com.gratum.firebasecorutinas1.util.StoragePermission
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModel
import com.gratum.firebasecorutinas1.viewmodel.MemberViewModelState
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var imageSelectedUri: Uri

    //Seleccionar imagen
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageSelectedUri = it.data?.data!!
                binding.imageView2.setImageURI(imageSelectedUri)
            }
        }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MemberViewModel by viewModels()
    private var memberModel: MemberModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initFlow()
        setListener()


        binding.button2.setOnClickListener {
            val pantalla1 = Intent(this, InputInfoActivity::class.java)
            startActivity(pantalla1)
        }


        binding.btIntentToPagination.setOnClickListener {
            val pantalla1 = Intent(this, PaginationTestActivity::class.java)
            startActivity(pantalla1)
        }

    }

    private fun deleteTest(uuId: String) {
        viewModel.delete(uuId)
    }

    private fun modifyTest(memberModel: MemberModel) {

        memberModel.email = "test_new@test.com"
        memberModel.nickname = "tester new"
        memberModel.modifyDate = getCurrentDateTimeString()

        viewModel.modify(memberModel)
    }

    private fun signInTest() {
        viewModel.signIn("test", "123456")
    }

//    private fun registerTest() {
//        val memberMode = MemberModel()
//
//        //as user seq
//        val uuid = getRandomUUIDString()
//
//        memberMode.uuId = uuid
//        memberMode.id = "efratest"
//        memberMode.password = "123456"
//        memberMode.email = "efra@test.com"
//        memberMode.nickname = "tester"
//        memberMode.profileImageUrl = ""
//        memberMode.profileImageFileCloudPath = ""
//        memberMode.createDate = getCurrentDateTimeString()
//        memberMode.modifyDate = getCurrentDateTimeString()
//        memberMode.createBy = uuid
//        memberMode.modifyBy = uuid
//
//        viewModel.register(memberMode)
//    }

    private fun getRandomUUIDString(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun getCurrentDateTimeString(): String {
        val date = Calendar.getInstance().time
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)

    }

    private fun solicitarPermisos() {
        if (StoragePermission.hasPermission(this)) {
            Toast.makeText(this, "Tiene permisos", Toast.LENGTH_SHORT).show()
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)

        } else {
            StoragePermission.requestPermission(this)
            if (!StoragePermission.shouldShowRequestPermissionRationale(this)) {
                StoragePermission.explainPermission(this)
            }
        }
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

                            //update view when register succesfully
                            memberModel = it.memberModel
                            updateView()

                            hideProgress()
                        }
                        is MemberViewModelState.SingSuccessfully -> {
                            Log.d("???", "${it.memberModel.toDictionary()}")
                            Toast.makeText(this@MainActivity, "sign in success", Toast.LENGTH_SHORT)
                                .show()

                            //update view when sign in succesfully
                            memberModel = it.memberModel
                            updateView()
                        }
//                        is MemberViewModelState.UpdateSuccessfully ->{
//                            //modify success
//                            Log.d("???", "${it.memberModel.toDictionary()}")
//                            Toast.makeText(this@MainActivity, "modify in success", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//
//                        is MemberViewModelState.DeleteSuccessfully ->{
//                            //delete success
//                            Log.d("???", "${it.uuId}")
//                            Toast.makeText(this@MainActivity, "delete in success", Toast.LENGTH_SHORT)
//                                .show()
//                        }

                        is MemberViewModelState.Loading -> {
                            //show progresss here
                            showProgress()
                        }

                        is MemberViewModelState.Empty -> {
                            //Selected is empty
                            hideProgress()
                        }
                        is MemberViewModelState.Error -> {
                            Log.d("???", "Error")
                            hideProgress()
                        }

                        is MemberViewModelState.None -> Unit
                        else -> {
                            hideProgress()
                        }
                    }
                }
            }
        }
    }


    private fun setListener() {
        binding.button2.setOnClickListener {

        }

        binding.btRegister.setOnClickListener {

            memberModel = MemberModel().apply {
                uuId = getRandomUUIDString()
                id = binding.etId.text.toString().trim()
                password = binding.etPassword.text.toString().trim()
                email = binding.etEmail.text.toString().trim()
                nickname = binding.etNickName.text.toString().trim()
                createDate = getCurrentDateTimeString()
                modifyDate = createDate
                createBy = uuId
                modifyBy = uuId
            }

            //this is just a simple example in real project you should check is image uri null
            viewModel.register(imageSelectedUri, "${memberModel!!.uuId}.jpg", memberModel!!)
        }
        binding.btSignIn.setOnClickListener {
            val id = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.signIn(id, password)
        }

        binding.imageView2.setOnClickListener {
            solicitarPermisos()
        }
    }

    //Cargar datos
    private fun updateView() {

        Picasso.get().load(memberModel!!.profileImageUrl).into(binding.imageView2)
        binding.etNickName.setText(memberModel!!.nickname)
        binding.etEmail.setText(memberModel!!.email)

    }

    private fun showProgress() {
          //binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        //binding.progress.visibility = View.GONE
    }
}