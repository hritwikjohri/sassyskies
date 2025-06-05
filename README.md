# 🌤️ Sassy Skies

> A weather app with personality that roasts your local climate with brutal honesty and sarcastic commentary.

## 📱 About

Sassy Skies is an Android weather application that combines accurate weather forecasting with AI-generated sarcastic commentary. Instead of boring "partly cloudy" descriptions, get brutally honest takes like *"It's fucking raining. Now you know."* or *"Garmi itni hai ki AC bhi ghar jaana chahta hai!"*

Built as a portfolio project showcasing modern Android development practices, clean architecture, and creative problem-solving.

## ✨ Features

### 🎭 Dual Personality Modes
- **Global Sass**: International sarcasm with no boundaries - brutal and universal
- **Desi Tadka**: Indian memes and slang - relatable, funny, and totally bindass

### 🌦️ Comprehensive Weather Data
- Current weather conditions with animated icons
- 7-day detailed forecast
- Real-time location-based updates
- Detailed atmospheric conditions (humidity, pressure, wind speed, visibility)
- Sunrise/sunset times and timezone information

### 🤖 AI-Powered Commentary
- Google Gemini AI generates contextual sarcastic messages
- Fallback to local generation when AI is unavailable
- Dynamic font sizing based on message length
- Highlighted keywords for better readability

### 📱 Modern Android Features
- **Home Screen Widget**: Weather at a glance with sarcastic commentary
- **Material Design 3**: Dynamic theming and modern UI components
- **Smooth Animations**: Weather-specific icon animations and transitions
- **Dark/Light Theme**: Automatic system theme adaptation

### 🎨 Polished User Experience
- Intuitive navigation with detailed weather screens
- Location permission handling with helpful error states
- Offline fallback messages
- Developer information screen
- Customizable sarcasm styles

## 🛠️ Technology Stack

### Core Technologies
- **Kotlin** - Modern Android development language
- **Jetpack Compose** - Declarative UI toolkit
- **Material Design 3** - Latest design system implementation

### Architecture & Patterns
- **MVVM + Clean Architecture** - Scalable and maintainable code structure
- **Hilt** - Dependency injection framework
- **StateFlow** - Reactive state management
- **Repository Pattern** - Data layer abstraction

### Networking & Data
- **Retrofit** - HTTP client for API communication
- **OpenWeatherMap API** - Weather data source
- **Gson** - JSON serialization/deserialization
- **Coroutines** - Asynchronous programming

### AI & Location Services
- **Google AI (Gemini)** - Sarcastic message generation
- **Google Play Services Location** - GPS and location tracking
- **Geocoder** - Address resolution

### UI & Animations
- **Coil** - Image loading with SVG support
- **Custom Animations** - Weather-specific icon animations
- **Dynamic Typography** - Responsive text sizing

### Background Processing
- **WorkManager** - Background widget updates
- **Glance** - Home screen widget framework
- **SharedPreferences** - Local data caching

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK API 24+ (Android 7.0)
- Device with location services

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/sassy-skies.git
   cd sassy-skies
   ```

2. **API Keys Setup**
   - Get a free API key from [OpenWeatherMap](https://openweathermap.org/api)
   - Get a Google AI API key from [Google AI Studio](https://aistudio.google.com/)
   - Add your keys to `app/src/main/java/com/hritwik/sassyskies/di/SassySkiesModule.kt`:
   ```kotlin
   @Provides
   @Named("weather_api_key")
   fun provideWeatherApiKey(): String {
       return "YOUR_OPENWEATHERMAP_API_KEY"
   }

   @Provides
   @Named("gemini_api_key")
   fun provideGeminiApiKey(): String {
       return "YOUR_GEMINI_API_KEY"
   }
   ```

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Permissions Required
- `ACCESS_FINE_LOCATION` - For precise location-based weather
- `ACCESS_COARSE_LOCATION` - For approximate location as fallback
- `INTERNET` - For weather API calls

## 📁 Project Structure

```
app/src/main/java/com/hritwik/sassyskies/
├── core/                   # Navigation and app routing
├── di/                     # Dependency injection modules
├── model/                  # Data models and UI state classes
├── repository/             # Data layer interfaces
├── repositoryImpl/         # Repository implementations
├── screen/                 # UI screens and composables
├── service/                # API services and external integrations
├── ui/theme/              # App theming and typography
├── viewmodel/             # ViewModels for UI state management
├── widget/                # Home screen widget implementation
├── MainActivity.kt        # Main activity
└── SassySkiesApplication.kt # Application class
```

## 🎨 Key Components

### Weather Display
- **AnimatedIcon.kt**: Weather-specific icon animations
- **WeatherContent.kt**: Main weather display with sarcastic commentary
- **DetailedWeatherScreen.kt**: Comprehensive weather information

### AI Integration
- **GeminiService.kt**: Google AI integration for sarcastic message generation
- Contextual prompts for global and Indian humor styles
- Fallback generation for offline scenarios

### Widget System
- **WeatherWidget.kt**: Glance-based home screen widget
- **ComposeWidgetUpdateWorker.kt**: Background updates every 30 minutes
- **WidgetDataManager.kt**: Local caching and data management

## 🌟 Unique Features

### Dynamic Sarcastic Commentary
The app doesn't just tell you it's raining - it roasts the situation:
- *"Weather so shitty even clouds are embarrassed."*
- *"Humidity level: Mumbai local train during monsoon."*
- *"It's freezing. Congrats, you live in a freezer."*

### Cultural Localization
Switch between international and Indian humor styles:
- **Global**: Universal sarcasm with no cultural boundaries
- **Indian**: Desi slang, Bollywood references, and local humor

### Smart Font Sizing
Dynamic typography that adjusts based on message length, ensuring optimal readability for both short quips and longer roasts.

## 🔧 Configuration

### Customizing Sarcasm
Edit prompts in `GeminiService.kt` to adjust the tone and style of generated messages.

### Widget Update Frequency
Modify update intervals in `ComposeWidgetScheduler.kt`:
```kotlin
val workRequest = PeriodicWorkRequestBuilder<ComposeWidgetUpdateWorker>(30, TimeUnit.MINUTES)
```

### Animation Preferences
Customize weather-specific animations in `AnimatedIcon.kt` and `WeatherSpecificAnimatedIcon.kt`.

## 📸 Screenshots

*Add screenshots of your app here showing:*
- Main weather screen with sarcastic commentary
- 7-day forecast view
- Detailed weather information
- Home screen widget
- Meme version selection drawer

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Guidelines
- Follow Kotlin coding conventions
- Use Jetpack Compose for all UI components
- Maintain clean architecture principles
- Add appropriate comments for complex logic
- Test on multiple screen sizes and Android versions

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenWeatherMap** for providing comprehensive weather data
- **Google AI** for powering the sarcastic commentary generation
- **Material Design** for the beautiful design system
- **Android Jetpack** for modern development tools
- **Weather Icons** for the animated SVG weather icons

## 📞 Contact

**Hritwik** - Android Developer & UI/UX Enthusiast

- 📧 Email: hritwikjohri@gmail.com
- 💼 LinkedIn: [linkedin.com/in/hritwikjohri](https://linkedin.com/in/hritwikjohri)
- 💻 GitHub: [github.com/hritwikjohri](https://github.com/hritwikjohri)

---

*Built with ❤️ and a healthy dose of sarcasm*
