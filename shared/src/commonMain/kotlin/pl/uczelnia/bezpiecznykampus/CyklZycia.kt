package pl.uczelnia.bezpiecznykampus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

// ---------- Zadanie 3.3 (przykład 3.2): obserwator cyklu życia ----------
// Wypisuje do logu (Logcat, etykieta System.out, filtr CYKL) zdarzenia cyklu życia
// oraz moment dodania i usunięcia danego ekranu z kompozycji.
@Composable
fun ObserwatorCyklu(nazwa: String) {
    DisposableEffect(Unit) {
        println("CYKL [" + nazwa + "]: kompozycja dodana")
        onDispose { println("CYKL [" + nazwa + "]: kompozycja usunięta") }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        println("CYKL [" + nazwa + "]: ON_START")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        println("CYKL [" + nazwa + "]: ON_RESUME")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        println("CYKL [" + nazwa + "]: ON_PAUSE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        println("CYKL [" + nazwa + "]: ON_STOP")
    }
}

// ---------- Zadanie 3.9 (przykład 3.7): licznik czasu spędzonego w aplikacji ----------
// Liczy sekundy tylko wtedy, gdy aplikacja jest widoczna (między ON_START a ON_STOP).
// Funkcja jest wywoływana w GlownyEkran (a nie na jednym ekranie), dzięki czemu licznik
// nie zeruje się przy przechodzeniu między zakładkami. Wynik pokazuje ekran Statystyki.
// Licznik jest w rememberSaveable, więc nie zeruje się po obrocie ekranu.
@Composable
fun zliczajCzasWAplikacji(): Int {
    var sekundy by rememberSaveable { mutableStateOf(0) }
    var widoczna by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) { widoczna = true }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { widoczna = false }

    LaunchedEffect(widoczna) {
        while (widoczna) {
            delay(1000)
            sekundy++
        }
    }
    return sekundy
}

// ---------- Zadanie 3.9: własna funkcja związana z tematem aplikacji ----------
// Aplikacja alarmowa nie może sugerować, że pokazuje bieżące alarmy, jeśli przez dłuższy
// czas była w tle (wymaganie projektu, funkcjonalność 10). Przy ON_STOP zapamiętujemy chwilę
// przejścia do tła, a przy ON_START sprawdzamy, jak długo aplikacji nie było widać.
// Jeżeli przerwa była dłuższa niż progSekund, wywołujemy onPowrot – GlownyEkran pokazuje
// wtedy komunikat, że lista alarmów może być nieaktualna (od L5 w tym miejscu nastąpi
// pobranie aktualnych alarmów z serwera).
// Znacznik czasu trzymamy w zwykłym remember: to stan przejściowy, a po obrocie ekranu
// (który nie jest przejściem do tła) ma zostać wyzerowany.
@Composable
fun ReakcjaNaPowrot(progSekund: Long = 30, onPowrot: (sekundyWTle: Long) -> Unit) {
    var przejscieDoTla by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        przejscieDoTla = TimeSource.Monotonic.markNow()
        println("CYKL [Powrot]: aplikacja w tle – zapamiętano czas")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        val znacznik = przejscieDoTla
        if (znacznik != null) {
            val sekundy = znacznik.elapsedNow().inWholeSeconds
            przejscieDoTla = null
            println("CYKL [Powrot]: powrót po " + sekundy + " s")
            if (sekundy >= progSekund) onPowrot(sekundy)
        }
    }
}
