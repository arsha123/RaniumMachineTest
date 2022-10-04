package com.example.raniummachinetest

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.internal.LinkedTreeMap
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var button_date: Button? = null
    var cal = Calendar.getInstance()
    val getdata = ArrayList<Model>()
    lateinit var barChart: BarChart
    lateinit var progressBar: ProgressBar
    lateinit var max_Speed_text: TextView
    lateinit var max_Speed: TextView
    lateinit var clos_dis_text: TextView
    lateinit var clos_dis: TextView
    lateinit var avg: TextView
    lateinit var avg_text: TextView

    // on below line we are creating
    // a variable for bar data
    lateinit var barData: BarData

    // on below line we are creating a
    // variable for bar data set
    lateinit var barDataSet: BarDataSet

    // on below line we are creating array list for bar data
    lateinit var barEntriesList: ArrayList<BarEntry>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var edit_date_Start = findViewById(R.id.enter_start_edit) as TextView
        var edit_date_end = findViewById(R.id.enter_end_edit) as TextView
        var submit = findViewById(R.id.submit) as Button

        progressBar = findViewById(R.id.progress)
        max_Speed_text = findViewById(R.id.max_Speed_text)
        max_Speed = findViewById(R.id.max_Speed)
        clos_dis_text = findViewById(R.id.clos_dis_text)
        clos_dis = findViewById(R.id.clos_dis)
        avg_text = findViewById(R.id.avg_text)
        avg = findViewById(R.id.avg)
        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(edit_date_Start)
            }
        }
        val dateSetListenerend = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(edit_date_end)
            }
        }
        edit_date_Start!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
        edit_date_end!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListenerend,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
        submit.setOnClickListener(View.OnClickListener {
            Log.d("staaaart", "onCreate: " + edit_date_Start.text)
            Log.d("enddddd", "onCreate: " + edit_date_end.text)
            if (!edit_date_Start.text.toString().equals("") && !edit_date_end.text.toString()
                    .equals("")
            ) {
                progressBar.visibility = View.VISIBLE
                makeRequest(edit_date_Start.text.toString(), edit_date_end.text.toString())
            } else {
                Toast.makeText(this, "Please enter date", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun updateDateInView(view: TextView) {

        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        view!!.text = sdf.format(cal.getTime())
    }

    private fun makeRequest(startdate: kotlin.String, enddate: kotlin.String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/neo/rest/v1/feed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: Nasaapi = retrofit.create(Nasaapi::class.java)
        val call: Call<ModelNasa> = api.getDate(startdate, enddate)
        Log.d("Calllll", "onResponse:" + call.request())

        call.enqueue(object : Callback<ModelNasa> {
            override fun onResponse(call: Call<ModelNasa>, response: Response<ModelNasa>) {
                if (response.isSuccessful) {
                    val jsonData = response.body()
                    val nearEarthObjects =
                        jsonData?.near_earth_objects as LinkedTreeMap<String, Any?>
                    barChart = findViewById(R.id.idBarChart)
                    var x_axis = 1f
                    val labels = ArrayList<kotlin.String>()
                    labels.clear()
                    Log.d("nea ert", "onResponse: " + nearEarthObjects.toString())
                    barEntriesList = ArrayList()
                    var max_speed: Float = 0.0F
                    var min_dis: Float = 0.0F
                    var list_dist: Float = 0.0F
                    var avg_size: Float = 0.0F

                    nearEarthObjects.forEach { (k, v) ->
                        barChart.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE

                        val values = v as List<Any?>
                        val size_of_values = values.size
                        for (i in 0..size_of_values - 1) {

                            val asteroid = values.get(i) as LinkedTreeMap<String, Any?>
                            asteroid.forEach { (a, b) ->
                                if (a.toString() == "close_approach_data") {
                                    val close_data = b as List<LinkedTreeMap<String, Any?>>
                                    for (i in close_data) {
                                        val rel_vel =
                                            i.values.elementAt(3) as LinkedTreeMap<String, Any?>
                                        val closest_data =
                                            i.values.elementAt(4) as LinkedTreeMap<String, Any?>

                                        val kmph = rel_vel.values.elementAt(1) as String
                                        val miss_dist = closest_data.values.elementAt(2) as String

                                        val current_Speed = kmph.toString().toFloat()
                                        val current_dist = miss_dist.toString().toFloat()

                                        if (max_speed == 0.0F) {
                                            max_speed = current_Speed
                                        }
                                        if (current_Speed > max_speed) {
                                            max_speed = current_Speed
                                        }
                                        if (min_dis == 0.0F) {
                                            min_dis = current_dist
                                        }
                                        if (current_Speed > min_dis) {
                                            min_dis = current_dist
                                        }

                                    }
                                }
                            }
                            val asteroid_size = values.get(i) as LinkedTreeMap<String, Any?>
                            asteroid_size.forEach { (a, b) ->
                                if (a.toString() == "estimated_diameter") {
                                    val close_data = b as LinkedTreeMap<Any?, Any?>
                                    val rel_vel =
                                        close_data.values.elementAt(3)
                                    val est_di = rel_vel as LinkedTreeMap<Any?, Any?>

                                    for (i in est_di) {
                                        val est_sit_text =
                                            est_di.values.elementAt(1)
                                        println(" estimmmm dista -> ${est_sit_text.toString()}")
                                        list_dist = est_sit_text.toString().toFloat()


                                    }
                                    val list = listOf(list_dist)
                                    avg_size = list.average().toFloat()
                                    println(" estimmmm -> $avg")

//
//                                    }
                                }
                            }

                        }
                        println("min dist-> $min_dis")

                        println(" Subject Name -> $k and its preference -> $size_of_values")
                        labels.add(k.toString())
                        barEntriesList.add(BarEntry(x_axis, size_of_values.toFloat()))
                        x_axis += 1

                    }
                    println(" max_speed -> ${max_speed}")
                    max_Speed.visibility = View.VISIBLE
                    max_Speed_text.visibility = View.VISIBLE
                    max_Speed_text.text = max_speed.toString() + " kmph"
                    clos_dis.visibility = View.VISIBLE
                    clos_dis_text.visibility = View.VISIBLE
                    clos_dis_text.text = min_dis.toString() + " km"
                    avg.visibility = View.VISIBLE
                    avg_text.visibility = View.VISIBLE
                    avg_text.text = avg_size.toString() + " km"
                    println(" Subject Name -> $labels and its preference -> $barEntriesList")


                    // on below line we are initializing our bar data set
                    barDataSet = BarDataSet(barEntriesList, "Bar Chart Data")

                    barData = BarData(barDataSet)
                    // on below line we are setting data to our bar chart
                    barChart.data = barData

                    // on below line we are setting colors for our bar chart text
                    barDataSet.valueTextColor = Color.BLACK

                    // on below line we are setting color for our bar data set
                    barDataSet.setColor(resources.getColor(R.color.purple_200))

                    val xAxis = barChart.getXAxis()
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
//                    xAxis.valueFormatter = IAxisValueFormatter{ value, axis ->  labels[value.toInt()]}
                    // on below line we are initializing our bar data
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override

                        fun getFormattedValue(value: Float): kotlin.String? {
                            // value is x as index
                            println(" label formatter Name -> $value ")

                            return labels[value.toInt() - 1]
                        }
                    }
                    // on below line we are setting text size
                    barDataSet.valueTextSize = 16f

                    // on below line we are enabling description as false
                    barChart.description.isEnabled = false


                }
            }

            override fun onFailure(call: Call<ModelNasa>, t: Throwable) {
                progressBar.visibility = View.GONE

                Log.d("main", "onFailure: " + t.message)
            }
        })
    }

//    private fun getBarChartData() {
//        barEntriesList = ArrayList()
//
//        // on below line we are adding data
//        // to our bar entries list
//
//        barEntriesList.add(BarEntry(1f, 1f))
//        barEntriesList.add(BarEntry(2f, 2f))
//        barEntriesList.add(BarEntry(3f, 3f))
//        barEntriesList.add(BarEntry(4f, 4f))
//        barEntriesList.add(BarEntry(5f, 5f))
//
//    }

}