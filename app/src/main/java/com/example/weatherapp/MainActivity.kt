package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.bumptech.glide.Glide
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var cityNameTextView: TextView
    private lateinit var todaysCurrentTempTextView: TextView
    private var selectedCityLon: Double = 0.0
    private var selectedCityLat: Double = 0.0
    private val apiKey = "2600c42ed4c992a7f5f3e1c7b1a253f3"
    private var units: String = "metric" // Default unit is metric (Celsius)
    private lateinit var todaysHighTextView: TextView
    private lateinit var todaysLowTextView: TextView
    private lateinit var currentDateAndTimeTextView: TextView
    private lateinit var todaysWeatherConditionTextView: TextView
    private lateinit var rainTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var todayWeatherWidgetImageView: ImageView
    private lateinit var todayPlus1: TextView
    private lateinit var todaysHighPlus1: TextView
    private lateinit var todaysLowPlus1: TextView
    private lateinit var todaysWeatherConditionPlus1: TextView
    private lateinit var todayWeatherWidgetPlus1: ImageView
    private lateinit var todayWeatherWidgetPlus2: ImageView
    private lateinit var todayWeatherWidgetPlus3: ImageView
    private lateinit var todayWeatherWidgetPlus4: ImageView
    private lateinit var todayPlus2: TextView
    private lateinit var todaysHighPlus2: TextView
    private lateinit var todaysLowPlus2: TextView
    private lateinit var todaysWeatherConditionPlus2: TextView

    private lateinit var todayPlus3: TextView
    private lateinit var todaysHighPlus3: TextView
    private lateinit var todaysLowPlus3: TextView
    private lateinit var todaysWeatherConditionPlus3: TextView

    private lateinit var todayPlus4: TextView
    private lateinit var todaysHighPlus4: TextView
    private lateinit var todaysLowPlus4: TextView
    private lateinit var todaysWeatherConditionPlus4: TextView

    private var defaultLat: Double = 49.24966
    private var defaultLon: Double = -123.119339

    private val cityCountryMap = mutableMapOf<String, Pair<Double, Double>>()
    private lateinit var cityInputAutoComplete: AutoCompleteTextView



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Initialize views


        cityNameTextView = findViewById(R.id.cityName)
        todaysCurrentTempTextView = findViewById(R.id.todaysCurrentTemp)
        val searchCityButton: MaterialButton = findViewById(R.id.searchCity)
        todaysHighTextView = findViewById(R.id.todaysHigh)
        todaysLowTextView = findViewById(R.id.todaysLow)
        currentDateAndTimeTextView = findViewById(R.id.currentDateAndTime)
        todaysWeatherConditionTextView = findViewById(R.id.todaysWeatherCondition)
        rainTextView = findViewById(R.id.rain)
        windSpeedTextView = findViewById(R.id.windSpeed)
        humidityTextView = findViewById(R.id.humidity)
        todayWeatherWidgetImageView = findViewById(R.id.todayWeatherWidget)

        cityInputAutoComplete = findViewById(R.id.cityInputEditText)
        todayPlus1 = findViewById(R.id.todayPlus1)
        todaysHighPlus1 = findViewById(R.id.todaysHighPlus1)
        todaysLowPlus1 = findViewById(R.id.todaysLowPlus1)
        todaysWeatherConditionPlus1 = findViewById(R.id.todaysWeatherConditionPlus1)
        todayWeatherWidgetPlus1 = findViewById(R.id.todayWeatherWidgetPlus1)
        todayWeatherWidgetPlus2 = findViewById(R.id.todayWeatherWidgetPlus2)
        todayWeatherWidgetPlus3 = findViewById(R.id.todayWeatherWidgetPlus3)
        todayWeatherWidgetPlus4 = findViewById(R.id.todayWeatherWidgetPlus4)
        todayPlus2 = findViewById(R.id.todayPlus2)
        todaysHighPlus2 = findViewById(R.id.todaysHighPlus2)
        todaysLowPlus2 = findViewById(R.id.todaysLowPlus2)
        todaysWeatherConditionPlus2 = findViewById(R.id.todaysWeatherConditionPlus2)

        todayPlus3 = findViewById(R.id.todayPlus3)
        todaysHighPlus3 = findViewById(R.id.todaysHighPlus3)
        todaysLowPlus3 = findViewById(R.id.todaysLowPlus3)
        todaysWeatherConditionPlus3 = findViewById(R.id.todaysWeatherConditionPlus3)

        todayPlus4 = findViewById(R.id.todayPlus4)
        todaysHighPlus4 = findViewById(R.id.todaysHighPlus4)
        todaysLowPlus4 = findViewById(R.id.todaysLowPlus4)
        todaysWeatherConditionPlus4 = findViewById(R.id.todaysWeatherConditionPlus4)


        selectedCityLon = defaultLon
        selectedCityLat = defaultLat
        // Make API request to fetch weather data for the default city
        fetchWeatherData(selectedCityLat, selectedCityLon)
        fetchWeatherDataForNextFourDays(selectedCityLat, selectedCityLon)

        // Read the JSON data using a JsonReader
        val reader = JsonReader(InputStreamReader(resources.openRawResource(R.raw.city_list)))
        try {
            reader.beginArray()
            while (reader.hasNext()) {
                reader.beginObject()
                var cityName: String? = null
                var countryName: String? = null
                var lon: Double? = null
                var lat: Double? = null
                while (reader.hasNext()) {
                    val name = reader.nextName()
                    when (name) {
                        "name" -> cityName = reader.nextString()
                        "country" -> countryName = reader.nextString()
                        "coord" -> {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                val coordName = reader.nextName()
                                when (coordName) {
                                    "lon" -> lon = reader.nextDouble()
                                    "lat" -> lat = reader.nextDouble()
                                    else -> reader.skipValue()
                                }
                            }
                            reader.endObject()
                        }
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                if (cityName != null && countryName != null && lon != null && lat != null) {
                    val cityCountryKey = "$cityName, $countryName"
                    cityCountryMap[cityCountryKey] = Pair(lon, lat)
                }
            }
            reader.endArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader.close()
        }

        // Set up autocomplete functionality
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cityCountryMap.keys.toList())
        cityInputAutoComplete.setAdapter(adapter)

        searchCityButton.setOnClickListener {
            val selectedCityCountry = cityInputAutoComplete.text.toString()


            // Retrieve lon and lat of the selected city from the map
            val selectedCityCoordinates = cityCountryMap[selectedCityCountry]
            if (selectedCityCoordinates != null) {
                cityNameTextView.text = selectedCityCountry
                selectedCityLon = selectedCityCoordinates.first
                selectedCityLat = selectedCityCoordinates.second
                // Make API request to OpenWeatherMap
                fetchWeatherData(selectedCityLat, selectedCityLon)
                fetchWeatherDataForNextFourDays(selectedCityLat, selectedCityLon)
            } else {
                // Clear city input field
                cityInputAutoComplete.setText("")

                // Show toast message to select a valid city
                Toast.makeText(this, "Please select a valid city", Toast.LENGTH_SHORT).show()
            }
        }



        // Refresh button click listener
        val refreshButton: MaterialButton = findViewById(R.id.refresh)
        refreshButton.setOnClickListener {
            // Make API request to OpenWeatherMap with the previously selected city's lat and lon
            fetchWeatherData(selectedCityLat, selectedCityLon)
            fetchWeatherDataForNextFourDays(selectedCityLat, selectedCityLon)
        }


        val celOrFehButton: MaterialButton = findViewById(R.id.celOrFeh)
        celOrFehButton.setOnClickListener {
            if (units == "metric") {
                units = "imperial"
                celOrFehButton.text = getString(R.string.imp)
            } else {
                units = "metric"
                celOrFehButton.text = "Metric"
            }
            fetchWeatherData(selectedCityLat, selectedCityLon)
            fetchWeatherDataForNextFourDays(selectedCityLat, selectedCityLon)
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        // Clear existing data
        todaysCurrentTempTextView.text = ""
        todaysHighTextView.text = "H: -"
        todaysLowTextView.text = "L: -"
        currentDateAndTimeTextView.text = "Date/ time"
        todaysWeatherConditionTextView.text = "weatherDesc"
        rainTextView.text = "Rain: -"
        windSpeedTextView.text = "Wind: -"
        humidityTextView.text = "Humidity: -"

        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=$units"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                Log.d("APIResponse", response)

                val jsonObject = JSONObject(response)
                if (jsonObject.has("city")) {
                    val cityObject = jsonObject.getJSONObject("city")
                    val offsetSeconds = cityObject.getInt("timezone") // Get timezone offset in seconds
                    Log.d("TimeZoneOffset", "$offsetSeconds seconds")

                    val offsetMillis = (offsetSeconds + (7 * 3600)) * 1000 // Convert seconds to milliseconds

                    val currentTimeMillis = System.currentTimeMillis()
                    val localTimeMillis = currentTimeMillis + offsetMillis

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = localTimeMillis
                    val formattedDateTime = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(calendar.time)

                    val list = jsonObject.getJSONArray("list")
                    if (list.length() > 0) {
                        val item = list.getJSONObject(0) // Only look at the first item in the list

                        val main = item.getJSONObject("main")
                        val temp = main.getDouble("temp").toInt()
                        val minTemperature = main.getDouble("temp_min").toInt()
                        val maxTemperature = main.getDouble("temp_max").toInt()
                        val humidityPercentage = main.getInt("humidity")


                        val weatherArray = item.getJSONArray("weather")
                        if (weatherArray.length() > 0) {
                            val weatherItem = weatherArray.getJSONObject(0)
                            val weatherDesc = weatherItem.getString("description")


                            val iconCode = weatherItem.getString("icon")
                            // Replace the "n" suffix with "d" to get the day version of the icon
                            val dayIconCode = iconCode.replace("n", "d")


                            val iconUrl = "https://openweathermap.org/img/wn/$dayIconCode@4x.png"

                            // Load the image into the ImageView on the main thread using Glide
                            launch(Dispatchers.Main) {
                                Glide.with(this@MainActivity)
                                    .load(iconUrl)
                                    .placeholder(R.drawable.sunny) // Placeholder image while loading
                                    .error(R.drawable.rainy) // Error image if loading fails
                                    .into(todayWeatherWidgetImageView)
                            }


                            val windObject = item.getJSONObject("wind")
                            val windSpeed = windObject.getDouble("speed")

                            val rainPercentage = if (item.has("rain")) {
                                val rainObject = item.getJSONObject("rain")
                                if (rainObject.has("3h")) rainObject.getDouble("3h") else 0.0
                            } else {
                                0.0
                            }


                            // Update UI on the main thread
                            withContext(Dispatchers.Main) {
                                todaysCurrentTempTextView.text = "$temp°"
                                todaysHighTextView.text = "H: $maxTemperature°"
                                todaysLowTextView.text = "L: $minTemperature°"
                                currentDateAndTimeTextView.text = formattedDateTime
                                todaysWeatherConditionTextView.text = weatherDesc
                                rainTextView.text = "$rainPercentage mm"
                                humidityTextView.text = "$humidityPercentage %"
                                if(units == "metric")
                                {windSpeedTextView.text = "$windSpeed M/S"}
                                else{windSpeedTextView.text = "$windSpeed MPH"}

                            }
                        }
                    }
                } else {
                    Log.e("APIError", "JSON response does not contain 'city' key")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("APIError", "Error fetching weather data: ${e.message}")
            }
        }


    }

    private fun fetchWeatherDataForNextFourDays(lat: Double, lon: Double) {
        // Clear existing data in the UI
        todayPlus1.text = ""
        todaysHighPlus1.text = "H: -"
        todaysLowPlus1.text = "L: -"
        todaysWeatherConditionPlus1.text = "Weather Condition"

        todayPlus2.text = ""
        todaysHighPlus2.text = "H: -"
        todaysLowPlus2.text = "L: -"
        todaysWeatherConditionPlus2.text = "Weather Condition"

        todayPlus3.text = ""
        todaysHighPlus3.text = "H: -"
        todaysLowPlus3.text = "L: -"
        todaysWeatherConditionPlus3.text = "Weather Condition"

        todayPlus4.text = ""
        todaysHighPlus4.text = "H: -"
        todaysLowPlus4.text = "L: -"
        todaysWeatherConditionPlus4.text = "Weather Condition"

        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=$units"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                Log.d("APIResponse", response)

                val jsonObject = JSONObject(response)
                val list = jsonObject.getJSONArray("list")

                // Iterate through the list to get weather data for the next four days
                for (i in 1 until list.length() step 8) { // Increment by 8 to get data for each day
                    if (i >= list.length()) break // Check if index is within bounds

                    val item = list.getJSONObject(i) // Fetch data for the day at index i

                    val main = item.getJSONObject("main")
                    val minTemperature = main.getDouble("temp_min").toInt()
                    val maxTemperature = main.getDouble("temp_max").toInt()

                    val weatherArray = item.getJSONArray("weather")
                    if (weatherArray.length() > 0) {
                        val weatherItem = weatherArray.getJSONObject(0)
                        val weatherDesc = weatherItem.getString("main")
                        val iconCode = weatherItem.getString("icon")
                        // Replace the "n" suffix with "d" to get the day version of the icon
                        val dayIconCode = iconCode.replace("n", "d")

                        val iconUrl = "https://openweathermap.org/img/wn/$dayIconCode@4x.png"

                        // Get the timestamp for the day from the API response
                        val dateTimeString = item.getString("dt_txt")
                        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val dateTime = dateTimeFormat.parse(dateTimeString)
                        val calendar = Calendar.getInstance()
                        calendar.time = dateTime
                        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())

                        // Update UI on the main thread based on the day
                        withContext(Dispatchers.Main) {
                            when (i) {
                                1 -> {
                                    todayPlus1.text = dayOfWeek
                                    todaysHighPlus1.text = "H: $maxTemperature°"
                                    todaysLowPlus1.text = "L: $minTemperature°"
                                    todaysWeatherConditionPlus1.text = weatherDesc
                                    Glide.with(this@MainActivity)
                                        .load(iconUrl)
                                        .placeholder(R.drawable.sunny) // Placeholder image while loading
                                        .error(R.drawable.rainy) // Error image if loading fails
                                        .into(todayWeatherWidgetPlus1)
                                }
                                9 -> {
                                    todayPlus2.text = dayOfWeek
                                    todaysHighPlus2.text = "H: $maxTemperature°"
                                    todaysLowPlus2.text = "L: $minTemperature°"
                                    todaysWeatherConditionPlus2.text = weatherDesc
                                    Glide.with(this@MainActivity)
                                        .load(iconUrl)
                                        .placeholder(R.drawable.sunny) // Placeholder image while loading
                                        .error(R.drawable.rainy) // Error image if loading fails
                                        .into(todayWeatherWidgetPlus2)
                                }
                                17 -> {
                                    todayPlus3.text = dayOfWeek
                                    todaysHighPlus3.text = "H: $maxTemperature°"
                                    todaysLowPlus3.text = "L: $minTemperature°"
                                    todaysWeatherConditionPlus3.text = weatherDesc
                                    Glide.with(this@MainActivity)
                                        .load(iconUrl)
                                        .placeholder(R.drawable.sunny) // Placeholder image while loading
                                        .error(R.drawable.rainy) // Error image if loading fails
                                        .into(todayWeatherWidgetPlus3)
                                }
                                25 -> {
                                    todayPlus4.text = dayOfWeek
                                    todaysHighPlus4.text = "H: $maxTemperature°"
                                    todaysLowPlus4.text = "L: $minTemperature°"
                                    todaysWeatherConditionPlus4.text = weatherDesc
                                    Glide.with(this@MainActivity)
                                        .load(iconUrl)
                                        .placeholder(R.drawable.sunny) // Placeholder image while loading
                                        .error(R.drawable.rainy) // Error image if loading fails
                                        .into(todayWeatherWidgetPlus4)
                                }

                                else -> {}
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("APIError", "Error fetching weather data: ${e.message}")
            }
        }
    }











}
