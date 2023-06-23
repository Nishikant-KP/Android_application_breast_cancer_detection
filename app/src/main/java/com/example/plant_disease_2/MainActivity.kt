package com.example.plant_disease_2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.util.jar.Manifest
import javax.security.auth.login.LoginException

class MainActivity : AppCompatActivity() {

    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap


    private val mInputSize = 96
    private val mModelPath = "converted_model_tf.tflite"
    private val mLabelPath = "labels.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mClassifier = Classifier(assets,mModelPath,mLabelPath,mInputSize)
        setupPermissions()


        val button = findViewById<Button>(R.id.button);
        button.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                callImagePicker();

            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val file : File? = ImagePicker.getFile(data)
            Log.i("TAG", "onActivityResult: "+ (file?.path ?: "null" ))
            var m = BitmapFactory.decodeFile(file?.path)
            mBitmap = BitmapFactory.decodeResource(resources,R.drawable.bacterial)
            mBitmap = scaleImage(m)
            val results =mClassifier.recognizeImage(mBitmap).firstOrNull();
            findViewById<ImageView>(R.id.image).setImageBitmap(m)
            //findViewById<TextView>(R.id.title).text = results?.title
            Log.i("clasajhsgd", (results?.title ?: "null" ) + results?.confidence)
            findViewById<LinearLayout>(R.id.layout).visibility = View.VISIBLE
            findViewById<TextView>(R.id.bcd).visibility = View.GONE
            if(results?.title == "Benign"){
                findViewById<TextView>(R.id.title).setText(R.string.benign)
            }else{
                findViewById<TextView>(R.id.title).setText(R.string.malignant)
            }

        }



    }

    private fun callImagePicker() {
        ImagePicker.with(this)
                .galleryOnly()
                .start();
    }

    fun scaleImage(bitmap: Bitmap?): Bitmap {
        val orignalWidth = bitmap!!.width
        val originalHeight = bitmap.height
        val scaleWidth = mInputSize.toFloat() / orignalWidth
        val scaleHeight = mInputSize.toFloat() / originalHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            Log.i("TAG", "Permission to record denied")
        }
    }

}