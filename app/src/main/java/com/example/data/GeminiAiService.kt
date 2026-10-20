package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiProduceRecommendation(
    val title: String,
    val suggestedItems: List<String>,
    val culinaryTip: String,
    val seasonalHighlight: String
)

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getRecommendations(
        cartItemNames: List<String>,
        preference: String = "Organic, seasonal, healthy farm-to-table meals"
    ): AiProduceRecommendation = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiAiService", "Using built-in seasonal farm intelligence heuristic.")
            return@withContext getFallbackRecommendation(cartItemNames, preference)
        }

        try {
            val prompt = """
                You are a master local farm-to-table chef and agricultural specialist.
                The buyer has selected these items in their farm cart: ${if (cartItemNames.isEmpty()) "Fresh Honeycrisp Apples, Raw Honey" else cartItemNames.joinToString(", ")}.
                User preference: $preference.
                
                Provide a JSON object with:
                1. "title": Catchy title for this seasonal recipe or harvest pairing
                2. "suggestedItems": Array of 3 farm products that pair well (e.g. ["Heirloom Rainbow Tomatoes", "Pasture-Raised Brown Eggs", "Organic Tuscan Lacinato Kale"])
                3. "culinaryTip": 1-2 sentence preparation or preservation tip for maximum flavor and nutrition
                4. "seasonalHighlight": Why these items are at peak season right now
                
                Return ONLY valid JSON.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val root = JSONObject(responseString)
                val text = root.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val parsedJson = JSONObject(text.trim())
                val suggestedArray = parsedJson.optJSONArray("suggestedItems") ?: JSONArray()
                val itemsList = mutableListOf<String>()
                for (i in 0 until suggestedArray.length()) {
                    itemsList.add(suggestedArray.getString(i))
                }

                AiProduceRecommendation(
                    title = parsedJson.optString("title", "Autumn Harvest Pairing"),
                    suggestedItems = if (itemsList.isEmpty()) listOf("Heirloom Rainbow Tomatoes", "Grass-Fed Milk") else itemsList,
                    culinaryTip = parsedJson.optString("culinaryTip", "Store apples unwashed in a cool cellar to maintain crispness."),
                    seasonalHighlight = parsedJson.optString("seasonalHighlight", "Apples and late heirloom crops are at maximum sugar brix sweetness.")
                )
            } else {
                Log.w("GeminiAiService", "Gemini API response unsuccessful: ${response.code}. Using fallback.")
                getFallbackRecommendation(cartItemNames, preference)
            }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Error calling Gemini API: ${e.message}", e)
            getFallbackRecommendation(cartItemNames, preference)
        }
    }

    private fun getFallbackRecommendation(
        cartItemNames: List<String>,
        preference: String
    ): AiProduceRecommendation {
        return if (cartItemNames.any { it.contains("Apple", ignoreCase = true) }) {
            AiProduceRecommendation(
                title = "Orchard Warm Spiced Tart Pairing 🍏🍯",
                suggestedItems = listOf("Raw Wildflower Honey", "Pasture-Raised Brown Eggs", "A2 Whole Grass-Fed Milk"),
                culinaryTip = "Toss freshly sliced Honeycrisps with raw wildflower honey and bake inside a rustic butter crust at 375°F for 35 minutes.",
                seasonalHighlight = "October mountain harvests boast exceptional sugar-acid balance, ideal for baking without refined sugars."
            )
        } else if (cartItemNames.any { it.contains("Tomato", ignoreCase = true) || it.contains("Kale", ignoreCase = true) }) {
            AiProduceRecommendation(
                title = "Rustic Farmhouse Tuscan Stew 🍅🥬",
                suggestedItems = listOf("Heirloom Rainbow Tomatoes", "Organic Tuscan Lacinato Kale", "Farmstead Cheddar"),
                culinaryTip = "Simmer tomatoes slowly with roasted garlic, then fold in destemmed lacinato kale during the final 4 minutes to preserve vibrant enzymes.",
                seasonalHighlight = "Heirloom field tomatoes concentrated by cool autumn nights produce an incomparably savory marinara base."
            )
        } else {
            AiProduceRecommendation(
                title = "Peak Autumn Community Harvest 🌾🍯",
                suggestedItems = listOf("Honeycrisp Crisp Apples", "Pasture-Raised Brown Eggs", "Organic Mountain Strawberries"),
                culinaryTip = "Whisk fresh farm eggs with a splash of cream-line milk for rich, custardy breakfast scrambles topped with sliced heritage berries.",
                seasonalHighlight = "Local growers report record high brix levels this week across orchards and apiaries."
            )
        }
    }
}
