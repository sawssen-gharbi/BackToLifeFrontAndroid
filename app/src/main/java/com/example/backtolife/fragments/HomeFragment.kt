package com.example.backtolife.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.backtolife.*
import com.example.backtolife.API.UserApi
import com.example.backtolife.models.ReportResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


const val DATE = "DATE"
const val MOOD = "MOOD"
const val DEPRESSEDMOOD = "DEPRESSEDMOOD"
const val ELEVATEDMOOD = "ELEVATEDMOOD"
const val IRRITABILITYMOOD = "IRRITABILITYMOOD"
const val SYMPTOMS = "SYMPTOMS"
const val IDREPORT = "IDREPORT"

class HomeFragment : Fragment() , View.OnClickListener  {


    private lateinit var mSharedPref: SharedPreferences

    lateinit var dayDatePicker : DayScrollDatePicker
    lateinit var SelectedDate : String

    private lateinit var btnAdd: Button
    private lateinit var btnSignOut: Button

    private lateinit var imageHappy: ImageView
    private lateinit var imageCalm: ImageView
    private lateinit var imageManic: ImageView
    private lateinit var imageAngry: ImageView
    private lateinit var imageSad: ImageView


    private lateinit var textHappy: TextView
    private lateinit var textCalm: TextView
    private lateinit var textManic: TextView
    private lateinit var textAngry: TextView
    private lateinit var textSad: TextView
    private lateinit var textPourDep : TextView
    private lateinit var textPourEl : TextView
    private lateinit var textPourIrr : TextView

    private lateinit var sDepressed: SeekBar
    private lateinit var sElevated: SeekBar
    private lateinit var sIrritability: SeekBar


    private lateinit var btnPSNo: Button
    private lateinit var btnPSYes: Button



    lateinit var textViewName: TextView

    var startPoint = 0
    var endPoint = 0

    val map: HashMap<String, String> = HashMap()



    override fun onCreateView(



        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        refresh(context)
        return   inflater.inflate(R.layout.fragment_home, container, false)



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val apiInterface = UserApi.create()


        mSharedPref =
            requireContext().getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE);
        val textName = mSharedPref.getString(FULLNAME, "").toString()
        textViewName = view.findViewById(R.id.textViewName)
        textViewName.text = textName

        btnAdd = view.findViewById(R.id.buttonAdd)



        imageHappy = view.findViewById(R.id.imageViewHappy)
        textHappy = view.findViewById(R.id.textViewHappy)

        imageCalm = view.findViewById(R.id.imageViewCalm)
        textCalm = view.findViewById(R.id.textViewCalm)

        imageManic = view.findViewById(R.id.imageViewManic)
        textManic = view.findViewById(R.id.textViewManic)

        imageAngry = view.findViewById(R.id.imageViewAngry)
        textAngry = view.findViewById(R.id.textViewAngry)

        imageSad = view.findViewById(R.id.imageViewSad)
        textSad = view.findViewById(R.id.textViewSad)

        sDepressed = view.findViewById(R.id.sliderDepressed)
        sElevated = view.findViewById(R.id.sliderElevated)
        sIrritability = view.findViewById(R.id.sliderIrritability)

        textPourDep = view.findViewById(R.id.textViewPourDep)
        textPourEl = view.findViewById(R.id.textViewPourEl)
        textPourIrr = view.findViewById(R.id.textViewPourIrr)

        btnPSNo = view.findViewById(R.id.btnPsychoticSymptomsNo)
        btnPSYes = view.findViewById(R.id.PsychoticSymptomsYes)

        // btnSignOut = view.findViewById(R.id.buttonSignOut)
        dayDatePicker = view.findViewById(R.id.dayDatePicker)

        //Google Sign In

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val gsc = GoogleSignIn.getClient(view.context, gso);
        val acct = GoogleSignIn.getLastSignedInAccount(view.context)
        if (acct != null) {
            val fullName = acct.displayName
            val email = acct.email
            textViewName.text = fullName

        }

        fun signOut() {
            gsc.signOut().addOnCompleteListener {
                activity?.finish()
                val intent = Intent(activity, SplashScreen::class.java)
                startActivity(intent)
            }
        }

        // btnSignOut.setOnClickListener {
        //  signOut()
        //  }

        //Date Picker
        // val calender = Calendar.getInstance()

        // fun updateTable(c: Calendar) {
        // val mf = "dd-MM-yyyy"
        // val sdf = SimpleDateFormat(mf, Locale.FRANCE)
        //textSelectedDate.setText(sdf.format(c.time))

        //}

        // val datepicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
        //calender.set(Calendar.YEAR, year)
        //calender.set(Calendar.MONTH, month)
        //calender.set(Calendar.DAY_OF_MONTH, day)

        // updateTable(calender)

        // }


        dayDatePicker.setStartDate(1, 11, 2022)
        dayDatePicker.getSelectedDate {

            SelectedDate = it.toString()
            Toast.makeText(context, SelectedDate, Toast.LENGTH_SHORT).show()
        }

        //OnClick Method
        imageHappy.setOnClickListener(this)
        imageCalm.setOnClickListener(this)
        imageManic.setOnClickListener(this)
        imageAngry.setOnClickListener(this)
        imageSad.setOnClickListener(this)
        btnPSYes.setOnClickListener(this)
        btnPSNo.setOnClickListener(this)




        sDepressed.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                textPourDep.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    startPoint = p0.progress
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    endPoint = p0.progress
                }

            }


        })

        sElevated.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                textPourEl.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    startPoint = p0.progress
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    endPoint = p0.progress
                }

            }


        })

        sIrritability.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                textPourIrr.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    startPoint = p0.progress
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    endPoint = p0.progress
                }

            }


        })


        btnAdd.setOnClickListener {


            map["user"] = mSharedPref.getString(ID, "")!!
            map["date"] = SelectedDate.toString()
            map["depressedMood"] = sDepressed.progress.toString()
            map["elevatedMood"] = sElevated.progress.toString()
            map["irritabilityMood"] = sIrritability.progress.toString()


            CoroutineScope(Dispatchers.IO).launch {
                apiInterface.addReport(map,mSharedPref.getString(ID, "").toString()).enqueue(object : Callback<ReportResponse> {
                    override fun onResponse(
                        call: Call<ReportResponse>, response:
                        Response<ReportResponse>
                    ) {
                        val report = response.body()
                        Log.e("iduser",map["user"].toString())
                        Log.e("success: ", report.toString())
                        if (report != null) {
                            mSharedPref.edit().apply {
                                putString(IDREPORT,report.report._id)
                                putString(DATE, report.report.date)
                                putString(MOOD, report.report.mood)
                                putString(DEPRESSEDMOOD, report.report.depressedMood.toString())
                                putString(ELEVATEDMOOD, report.report.elevatedMood.toString())
                                putString(
                                    IRRITABILITYMOOD,
                                    report.report.irritabilityMood.toString()
                                )

                            }.apply()


                        }
                    }


                    override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                        println("messin")
                    }
                })
            }
        }





        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(p0: View?) {

        if (p0 != null) {
            when(p0.id){

                R.id.imageViewHappy  ->  {

                    map["mood"] = textHappy.text.toString()
                    Toast.makeText(context,"Your choice is " + map["mood"],Toast.LENGTH_SHORT).show()
                    imageHappy.setImageResource(R.drawable.happy_outline)
                    imageCalm.setImageResource(R.drawable.yin_yang_symbol)
                    imageManic.setImageResource(R.drawable.celtic)
                    imageAngry.setImageResource(R.drawable.angryy)
                    imageSad.setImageResource(R.drawable.sadd)

                }
                R.id.imageViewCalm ->  {

                    map["mood"] = textCalm.text.toString()
                    Toast.makeText(context,"Your choice is " + map["mood"],Toast.LENGTH_SHORT).show()
                    imageHappy.setImageResource(R.drawable.happyy)
                    imageCalm.setImageResource(R.drawable.calm_outline)
                    imageManic.setImageResource(R.drawable.celtic)
                    imageAngry.setImageResource(R.drawable.angryy)
                    imageSad.setImageResource(R.drawable.sadd)
                }
                R.id.imageViewManic ->  {

                    map["mood"] = textManic.text.toString()
                    Toast.makeText(context,"Your choice is " + map["mood"],Toast.LENGTH_SHORT).show()
                    imageHappy.setImageResource(R.drawable.happyy)
                    imageCalm.setImageResource(R.drawable.yin_yang_symbol)
                    imageManic.setImageResource(R.drawable.celtic_outline)
                    imageAngry.setImageResource(R.drawable.angryy)
                    imageSad.setImageResource(R.drawable.sadd)
                }
                R.id.imageViewAngry ->  {

                    map["mood"] = textAngry.text.toString()
                    Toast.makeText(context,"Your choice is " + map["mood"],Toast.LENGTH_SHORT).show()
                    imageHappy.setImageResource(R.drawable.happyy)
                    imageCalm.setImageResource(R.drawable.yin_yang_symbol)
                    imageManic.setImageResource(R.drawable.celtic)
                    imageAngry.setImageResource(R.drawable.angry_outline)
                    imageSad.setImageResource(R.drawable.sadd)
                }
                R.id.imageViewSad ->  {

                    map["mood"] = textSad.text.toString()
                    Toast.makeText(context,"Your choice is " + map["mood"],Toast.LENGTH_SHORT).show()
                    imageHappy.setImageResource(R.drawable.happyy)
                    imageCalm.setImageResource(R.drawable.yin_yang_symbol)
                    imageManic.setImageResource(R.drawable.celtic)
                    imageAngry.setImageResource(R.drawable.angryy)
                    imageSad.setImageResource(R.drawable.sad_outline)
                }
                R.id.PsychoticSymptomsYes -> {
                    map["symptoms"] = btnPSYes.text.toString()
                    Toast.makeText(context,"Your choice is " + map["symptoms"],Toast.LENGTH_SHORT).show()
                }
                R.id.btnPsychoticSymptomsNo -> {
                    map["symptoms"] = btnPSNo.text.toString()
                    Toast.makeText(context,"Your choice is " + map["symptoms"],Toast.LENGTH_SHORT).show()
                }


            }
        }
    }
}


fun refresh(context: Context?) {
    context?.let {
        val fragementManager = (context as? AppCompatActivity)?.supportFragmentManager
        fragementManager?.let {
            val currentFragement = fragementManager.findFragmentById(R.id.itemHome)
            currentFragement?.let {
                val fragmentTransaction = fragementManager.beginTransaction()
                fragmentTransaction.detach(it)
                fragmentTransaction.attach(it)
                fragmentTransaction.commit()
            }
        }
    }
}



fun isValide(): Boolean {
    return true
}




