package com.hritwik.sassyskies.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.hritwik.sassyskies.repository.ApiKeyRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val fallbackApiKey: String
) {

    private fun getGenerativeModel(): GenerativeModel {
        // Get API key from user data or use fallback
        val apiKey = runBlocking {
            apiKeyRepository.getGeminiApiKey().takeIf { it.isNotBlank() }
                ?: fallbackApiKey
        }

        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.9f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 150
            }
        )
    }

    suspend fun generateGlobalSarcasticMessage(
        weatherDescription: String,
        temperature: Int,
        humidity: Int,
        cityName: String,
        feelsLike: Int
    ): Result<String> {
        return try {
            val prompt = buildGlobalPrompt(weatherDescription, temperature, humidity, cityName, feelsLike)
            val response = getGenerativeModel().generateContent(prompt)
            val message = response.text?.trim() ?: getFallbackMessage(weatherDescription, temperature)

            // Ensure the message isn't too long
            val finalMessage = if (message.length > 200) {
                message.take(197) + "..."
            } else {
                message
            }

            Result.success(finalMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateIndianSarcasticMessage(
        weatherDescription: String,
        temperature: Int,
        humidity: Int,
        cityName: String,
        feelsLike: Int
    ): Result<String> {
        return try {
            val prompt = buildIndianPrompt(weatherDescription, temperature, humidity, cityName, feelsLike)
            val response = getGenerativeModel().generateContent(prompt)
            val message = response.text?.trim() ?: getIndianFallbackMessage(weatherDescription, temperature)

            // Ensure the message isn't too long
            val finalMessage = if (message.length > 200) {
                message.take(197) + "..."
            } else {
                message
            }

            Result.success(finalMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun buildGlobalPrompt(
        weatherDescription: String,
        temperature: Int,
        humidity: Int,
        cityName: String,
        feelsLike: Int
    ): String {
        return """
            Generate an absolutely brutal, savage, and unapologetically sarcastic weather roast for a weather app called "Sassy Skies". 
            
            Weather details:
            - Weather: $weatherDescription
            - Temperature: ${temperature}°C
            - Feels like: ${feelsLike}°C
            - Humidity: $humidity%
            - Location: $cityName
            
            Requirements:
            - Keep it under 150 characters but make every word count
            - Be ruthlessly sarcastic, brutally honest, and devastatingly witty
            - Use profanity liberally if it enhances the roast - "fuck", "shit", "damn", "hell", "ass", "bitch" are all fair game
            - Absolutely destroy the weather situation with zero mercy
            - Make it relatable by focusing on how miserable/annoying this weather makes life
            - Don't mention the app name
            - Be creative, savage, and avoid boring generic responses
            - Channel the energy of someone who's genuinely pissed off at the weather
            
            Examples of the brutal tone we want:
            - "It's fucking raining. Again. Because apparently the sky has commitment issues."
            - "Congratulations! You're living in Satan's sweaty armpit today."
            - "This humidity is more suffocating than your ex's personality."
            - "Weather so shitty even the weather app is embarrassed to show it."
            - "It's colder than your mother-in-law's heart out there."
            - "Fog so thick you could cut it with a knife. Too bad you can't see the knife."
            - "Wind strong enough to blow away your hopes and dreams."
            
            Generate ONE absolutely savage weather roast that would make people laugh while they suffer:
        """.trimIndent()
    }

    private fun buildIndianPrompt(
        weatherDescription: String,
        temperature: Int,
        humidity: Int,
        cityName: String,
        feelsLike: Int
    ): String {
        return """
        Generate a ruthless, savage Indian meme-style weather roast that absolutely destroys the weather condition with desi slang and brutal honesty!

        Weather details:
        - Weather: $weatherDescription
        - Temperature: ${temperature}°C
        - Feels like: ${feelsLike}°C
        - Humidity: $humidity%
        - Location: $cityName

        Requirements:
        - Keep it under 150 characters but make it devastatingly brutal
        - Be absolutely ruthless and savage while roasting the WEATHER ONLY
        - Do NOT insult or roast the user – all insults must target the weather
        - Tone: Desi, meme-worthy, laugh-out-loud funny
        - Focus on user’s exaggerated reaction or struggle due to the weather
        - Describe how the *user is feeling or struggling* because of the weather
        - Be witty, meme-worthy, savage, and instantly funny
        - Channel the energy of a frustrated Indian who's fed up with the weather
        - Only give text no special character like (!@#$%^&*)

        Examples of the ruthlessly savage Indian weather tone we want:
        - "Bc ye kya barish hai, DTC bus se bhi zyada unreliable!"
        - "Garmi itni hai ki mirchi bhi bol rahi: ‘bas kar bhencho!’"
        - "Humidity level: Mumbai local ke peak hour se bhi zyada chipchipa!"
        - "Fog itna thick hai ki Delhi ka pollution bhi sharma jaaye madarchod!"
        - "Mausam bhi teri train ki tarah late aur bekaar hai saale!"
        - "Thand itni hai ki Rajma chawal bhi freezer mein lag raha!"
        - "Heatwave: Solar panel bhi burnout ho gaya hoga bc!"
        - "Barish aisi ki lagta hai Mausam Vibhag ne dimaag pe plastic chadha rakha hai!"
        - "Wind speed: Auto waale ke mood jaisi – kabhi bhi palat jaaye!"
        - "Saala mausam harami boyfriend jaisa – kabhi sunshine, kabhi heartbreak!"

        Generate ONE absolutely ruthless Indian meme-style weather roast targeting only the weather:
    """.trimIndent()
    }


    private fun getFallbackMessage(weatherDescription: String, temperature: Int): String {
        return when {
            weatherDescription.contains("rain") -> "It's raining. Shocking revelation, I know."
            weatherDescription.contains("snow") -> "Snow. Because walking was too easy anyway."
            weatherDescription.contains("clear") && temperature > 25 -> "It's hot. Water is wet. News at 11."
            weatherDescription.contains("cloud") -> "Cloudy with a chance of meh."
            temperature < 0 -> "It's freezing. Surprise, surprise."
            else -> "Weather happened. Congratulations on existing."
        }
    }

    private fun getIndianFallbackMessage(weatherDescription: String, temperature: Int): String {
        return when {
            weatherDescription.contains("rain") -> "Bc barish bhi teri life jaisi unpredictable! Mumbai local se bhi worse!"
            weatherDescription.contains("snow") -> "Snow?! Saala tera future bhi itna hi unclear hai!"
            weatherDescription.contains("clear") && temperature > 25 -> "Garmi itni hai ki tera bank balance bhi melt ho gaya gandu!"
            weatherDescription.contains("cloud") -> "Clouds bhi teri tarah confused hain - koi direction nahi bc!"
            weatherDescription.contains("fog") -> "Fog itna thick hai ki tera brain fog permanent lag raha madarchod!"
            weatherDescription.contains("storm") -> "Storm aa raha hai! Teri life ki tarah chaotic aur unpredictable!"
            temperature < 0 -> "Thand itni hai ki teri ex ka dil bhi warm lag raha saale!"
            temperature > 35 -> "Garmi level: Teri life choices se bhi zyada regretful bhencho!"
            else -> "Weather bhi teri tarah bekaar hai... koi sense nahi banata chutiye!"
        }
    }
}