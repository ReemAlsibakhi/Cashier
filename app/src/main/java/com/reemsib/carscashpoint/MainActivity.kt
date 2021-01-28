package com.reemsib.carscashpoint

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.orhanobut.hawk.Hawk
import com.reemsib.carscashpoint.adapter.CompanyAdapter
import com.reemsib.carscashpoint.adapter.ModelAdapter
import com.reemsib.carscashpoint.adapter.ModelAdapter.RecyclerViewActionListener
import com.reemsib.carscashpoint.model.AppConstants
import com.reemsib.carscashpoint.model.Company
import com.reemsib.carscashpoint.model.User
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() , IPickResult ,RecyclerViewActionListener {
    var models = ArrayList<String>()
    var companyList = ArrayList<Company>()
    val db = Firebase.firestore
    var ImageURI: Uri? = null
    private lateinit var database: DatabaseReference
    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Hawk.init(applicationContext).build()
        database = Firebase.database.reference

//       // val settings = firestoreSettings {
//        //    isPersistenceEnabled = true
//        }
//     //   db.firestoreSettings = settings

       if (Hawk.contains(AppConstants.IMAGE)) {
           avi2.show()
           Glide.with(this)
               .load("${Hawk.get(AppConstants.IMAGE) as Any}")
               .listener(object : RequestListener<Drawable?> {
                   override fun onLoadFailed(
                       e: GlideException?,
                       model: Any?,
                       target: com.bumptech.glide.request.target.Target<Drawable?>?,
                       isFirstResource: Boolean
                   ): Boolean {
                       avi2.hide()
                       return false
                   }

                   override fun onResourceReady(
                       resource: Drawable?,
                       model: Any?,
                       target: com.bumptech.glide.request.target.Target<Drawable?>?,
                       dataSource: DataSource?,
                       isFirstResource: Boolean
                   ): Boolean {
                       avi2.hide()

                       return false
                   }
               })
               .into(img_car)

           btn_addImg.setImageResource(R.drawable.ic_close)

       }
        companyList.add(Company(0, R.drawable.hyundai, "HYUNDAI"))
        companyList.add(Company(1, R.drawable.kia, "KIA"))
        companyList.add(Company(2, R.drawable.toyota, "TOYOTA"))
        companyList.add(Company(3, R.drawable.nissan, "NISSAN"))
        companyList.add(Company(4, R.drawable.bmw, "BMW"))
        companyList.add(Company(5, R.drawable.mercedes, "MERCEDES"))
        companyList.add(Company(6, R.drawable.ford, "FORD"))
        companyList.add(Company(7, R.drawable.chevrolet, "CHEVROLET"))
        companyList.add(Company(8, R.drawable.mitsubishi, "MITSUBISHI"))
        companyList.add(Company(9, R.drawable.other, "OTHER"))

        val adapter_company=CompanyAdapter(this, companyList)
        gv_company.adapter = adapter_company


        //click on realtive you car
        rl_company.setOnClickListener {
            checkVisible()
        }
        //click on realtive it model
        rl_model.setOnClickListener {
            if(you_car.text.toString()==getString(R.string.your_car)){
                Toast.makeText(this, getString(R.string.choose_car), Toast.LENGTH_LONG).show()
            }else{
                if (isNetworkConnected(applicationContext)){
                    if (models.size != 0) {
                        models.clear() }
                    readModel(you_car.text.toString())
                    checkVisible_Model()
                }else{
                   // readModel(you_car.text.toString())
                    Toast.makeText(this, getString(R.string.connect_failed), Toast.LENGTH_SHORT).show()

                }
            }
        }
        //click listener on company images
        adapter_company.setOnItemClickListener(object : CompanyAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, position: Int, name: String) {
                you_car.text = name
                tv_model.text = getString(R.string.model)
                hideGrid()
                if (models.size != 0) {
                    models.clear()
                }
                readModel(name)
            }
            override fun onLongItemClick(view: View?, position: Int) {
            }
        })

         btn_addImg.setOnClickListener {
             if (btn_addImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_add).getConstantState()) {
                 PickImageDialog.build(PickSetup()).show(this) }
             else {
                 img_car.setImageResource(0)
                 Hawk.delete(AppConstants.IMAGE)
                 btn_addImg.setImageResource(R.drawable.ic_add)
             }
        }

        btn_sendOffer.setOnClickListener {
            val mobile:String=ed_mobile.text.toString().trim()
            val car_company:String=you_car.text.toString()
            val car_model:String=tv_model.text.toString()

            when {
                car_company==getString(R.string.your_car) || car_company.isEmpty()  ->{
                    Toast.makeText(this, getString(R.string.choose_car), Toast.LENGTH_LONG).show()}
                (car_model== getString(R.string.model) || car_model.isEmpty()) ->{
                    Toast.makeText(this, getString(R.string.choose_model), Toast.LENGTH_LONG).show()}
                mobile.isEmpty()  ->{
                    Toast.makeText(this, getString(R.string.enter_mobile), Toast.LENGTH_LONG).show()}
               mobile.length !=9 && mobile.length !=10  ->{
                   Toast.makeText(this, getString(R.string.enter_mobile_valid), Toast.LENGTH_LONG).show()
               }else -> {
                    SendData(car_company, car_model, mobile)
                }
            }
        }

        }

    private fun checkVisible_Model() {
        if( rv_model.visibility== View.GONE){
            rv_model.visibility=View.VISIBLE
        }else{
            rv_model.visibility=View.GONE

        }
    }

    fun SendData(company: String, model: String, mobile: String) {
          if (isNetworkConnected(applicationContext)){

              val user = User(company, model, mobile)
              database.child(AppConstants.COLLECTION_CUSTOMER).push().setValue(user)
                  .addOnSuccessListener {
                      changebutton()
                      you_car.text=getString(R.string.your_car)
                      tv_model.text=getString(R.string.model)
                      ed_mobile.setText("")

                  }.addOnFailureListener {
                      Toast.makeText(this, getString(R.string.error_add), Toast.LENGTH_SHORT).show()
                      Log.w("add user", "Error adding document", it)
                  }
          }else{
              Toast.makeText(this, getString(R.string.connect_failed), Toast.LENGTH_SHORT).show()

          }

    }

    private fun changebutton() {
        val transition = btn_sendOffer.getBackground() as  (TransitionDrawable)
        btn_sendOffer.text=getString(R.string.add_success)
        btn_sendOffer.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
        transition.startTransition(500)

        object : CountDownTimer(5000, 50) {
            override fun onTick(arg0: Long) {
            }

            override fun onFinish() {
                transition.reverseTransition(500)
                btn_sendOffer.text=getString(R.string.send_me_offer)
                btn_sendOffer.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorYellow
                    )
                )
            }
        }.start()
    }



    fun readModel(company: String) :ArrayList<String>{
        db.collection(AppConstants.COLLECTION_MODEL)
            .whereEqualTo("company", company)
            .get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        models = document.get("model") as ArrayList<String>
                        //      Toast.makeText(applicationContext, "$models", Toast.LENGTH_LONG).show()

                    }
                    rv_model.layoutManager = LinearLayoutManager(this)
                    val adapter_model = ModelAdapter(this, this, models)
                    rv_model.adapter = adapter_model

                }
            })
        return models
    }


    fun hideGrid(){
     gv_company.visibility=View.GONE

 }

    private fun checkVisible() {
        if(rv_model.visibility==View.VISIBLE)  {
            rv_model.visibility=View.GONE
        }
        if( gv_company.visibility== View.GONE){
            gv_company.visibility=View.VISIBLE
        }else {
            gv_company.visibility=View.GONE

        }
    }


    override fun onPickResult(r: PickResult?) {
        startAnim()
            if (r!!.error == null) {
                val storageRef = storage.reference
                val imagesRef = storageRef.child("images")

                val image = imagesRef.child("image" + System.currentTimeMillis() + ".png")
                val uploadTask = image.putFile(r.uri)

                uploadTask.addOnFailureListener { exception ->
                    Log.e("upload task", exception.message.toString())
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { uploadTask ->
                      image.downloadUrl.addOnSuccessListener { uri ->
                          stopAnim()
                          ImageURI = uri
                         Glide.with(this).load(ImageURI).into(img_car)
                          Hawk.put(AppConstants.IMAGE, ImageURI)
                         Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                    }.addOnFailureListener { exception ->
                        stopAnim()
                        Log.e("fail image", exception.message.toString())
                          Toast.makeText(this, getString(R.string.upload_fail), Toast.LENGTH_LONG).show(); }
                } } else {
                Toast.makeText(this, r.getError().toString(), Toast.LENGTH_LONG).show();
            }

        btn_addImg.setImageResource(R.drawable.ic_close)


    }

    override fun onViewClicked(clickedItemPosition: Int, model: String) {
                tv_model.text=model
                rv_model.visibility=View.GONE
    }

    fun startAnim() {
        avi.show()
    }

    fun stopAnim() {
        avi.hide()


    }
//     fun isNetworkConnected(): Boolean {
//        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
//
//}
    fun isNetworkConnected(context: Context?): Boolean {
        if (context == null)
            return false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities: NetworkCapabilities? =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            } else {
                try {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        Log.i("update_statut", "Network is available : true")
                        return true
                    }
                } catch (e: Exception) {
                    Log.i("update_statut", "" + e.message)
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ")
        return false
    }
}

