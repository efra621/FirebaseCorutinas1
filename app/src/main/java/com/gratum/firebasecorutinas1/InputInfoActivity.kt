package com.gratum.firebasecorutinas1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.gratum.firebasecorutinas1.databinding.ActivityInputInfoBinding
import com.gratum.firebasecorutinas1.firebase.storage.FirebaseStorageManager
import com.gratum.firebasecorutinas1.util.StoragePermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InputInfoActivity : AppCompatActivity() {

    private lateinit var imageSelectedUri: Uri

    //Seleccionar imagen
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageSelectedUri = it.data?.data!!
                binding.imageView.setImageURI(imageSelectedUri)
            }
        }

    private lateinit var binding: ActivityInputInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageView.setOnClickListener {
            solicitarPermisos()
        }

        binding.button.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main){
                val result = async{

                    FirebaseStorageManager.uploadImage(
                        uri = imageSelectedUri,
                        folderName = FirebaseStorageManager.MEMBER_IMAGE_FOLDER,
                        fileName = "test.jpg"
                    )
                }
                val imageUrl = result.await()
                Log.d("???", imageUrl)
            }
        }
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
}