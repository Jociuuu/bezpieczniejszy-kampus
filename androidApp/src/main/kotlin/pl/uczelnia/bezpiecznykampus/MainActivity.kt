package pl.uczelnia.bezpiecznykampus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// Etap 2 (L3, przykład 3.1): komunikaty w Logcat pokazujące kolejność wywołań metod cyklu życia.
// W Logcat wpisz filtr: CYKL
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Log.d(
            "CYKL",
            "MainActivity: onCreate, stan zapisany: " + (savedInstanceState != null)
        )
        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("CYKL", "MainActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CYKL", "MainActivity: onResume")
    }

    override fun onPause() {
        Log.d("CYKL", "MainActivity: onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("CYKL", "MainActivity: onStop")
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("CYKL", "MainActivity: onRestart")
    }

    override fun onDestroy() {
        Log.d(
            "CYKL",
            "MainActivity: onDestroy, zmiana konfiguracji: " + isChangingConfigurations
        )
        super.onDestroy()
    }
}
