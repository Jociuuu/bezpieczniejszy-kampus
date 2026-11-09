package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*

// Zakładki dolnego paska nawigacji (ikony własne z folderu drawable)
enum class Zakladka(val etykieta: StringResource, val ikona: DrawableResource) {
    ALARMY(Res.string.zakladka_alarmy, Res.drawable.ic_alarm),
    OGLOS(Res.string.zakladka_oglos, Res.drawable.ic_dodaj),
    STATYSTYKI(Res.string.zakladka_statystyki, Res.drawable.ic_statystyki),
    USTAWIENIA(Res.string.zakladka_ustawienia, Res.drawable.ic_ustawienia)
}

@Composable
@Preview
fun App() {
    // Ustawienia to docelowo dane trwałe (L6). Do tego czasu rememberSaveable + własny Saver,
    // aby obrót ekranu nie resetował wybranego motywu ani „mojego budynku”.
    var ustawienia by rememberSaveable(stateSaver = UstawieniaSaver) { mutableStateOf(Ustawienia()) }
    val ciemny = when (ustawienia.motyw) {
        Motyw.SYSTEMOWY -> isSystemInDarkTheme()
        Motyw.JASNY -> false
        Motyw.CIEMNY -> true
    }
    AppTheme(darkTheme = ciemny) {
        ObserwatorCyklu("App")
        GlownyEkran(ustawienia = ustawienia, onZmianaUstawien = { ustawienia = it })
    }
}

@Composable
fun GlownyEkran(
    ustawienia: Ustawienia,
    onZmianaUstawien: (Ustawienia) -> Unit,
    startowaZakladka: Zakladka = Zakladka.ALARMY,
    startowyAlarmId: Int? = null
) {
    // ---------- Stan interfejsu (rememberSaveable – przetrwa obrót i zabicie procesu) ----------
    var zakladka by rememberSaveable { mutableStateOf(startowaZakladka) }
    // Zapisujemy tylko identyfikator otwartego alarmu, nie cały obiekt Alarm (zadanie 3.6)
    var wybranyAlarmId by rememberSaveable { mutableStateOf(startowyAlarmId) }
    // Wyszukiwanie i filtry – jedna klasa z własnym Saverem (zadanie 3.7)
    var kryteria by rememberSaveable(stateSaver = KryteriaFiltrowaniaSaver) { mutableStateOf(KryteriaFiltrowania()) }
    // Pozycja przewinięcia listy (LazyListState ma wbudowany Saver)
    val stanListy = rememberLazyListState()
    // Zachowuje stan rememberSaveable każdej zakładki przy przełączaniu (np. szkic formularza)
    val stanZakladek = rememberSaveableStateHolder()

    // ---------- Dane aplikacji (docelowo ViewModel – L4 i baza danych – L6) ----------
    // Lista alarmów w pamięci (dane przykładowe). Celowo NIE w rememberSaveable – całych list
    // nie zapisujemy w stanie interfejsu. Po obrocie lista wraca do danych przykładowych.
    val alarmy = remember { mutableStateListOf<Alarm>().apply { addAll(przykladoweAlarmy) } }
    // Identyfikatory alarmów, których odbiór potwierdził użytkownik (blokuje podwójne potwierdzenie)
    val potwierdzone = remember { mutableStateListOf<Int>() }

    // ---------- Cykl życia (zadanie 3.9) ----------
    val sekundyWAplikacji = zliczajCzasWAplikacji()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    ReakcjaNaPowrot(progSekund = 30) { sekundyWTle ->
        scope.launch { snackbar.showSnackbar(getString(Res.string.powrot_nieaktualne, sekundyWTle)) }
    }

    // Zadanie 3.6: zapisany identyfikator może wskazywać na alarm, którego już nie ma
    // (np. alarm ogłoszony w aplikacji, a po obrocie lista wróciła do danych przykładowych).
    // Wtedy zamykamy szczegóły i wracamy do listy zamiast pokazywać pusty ekran.
    LaunchedEffect(wybranyAlarmId, alarmy.size) {
        val id = wybranyAlarmId
        if (id != null && alarmy.none { it.id == id }) {
            println("CYKL [App]: alarm o id " + id + " nie istnieje – powrót do listy")
            wybranyAlarmId = null
            snackbar.showSnackbar(getString(Res.string.alarm_niedostepny))
        }
    }

    fun zmienAlarm(id: Int, zmiana: (Alarm) -> Alarm) {
        val indeks = alarmy.indexOfFirst { it.id == id }
        if (indeks >= 0) alarmy[indeks] = zmiana(alarmy[indeks])
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                Zakladka.entries.forEach { z ->
                    NavigationBarItem(
                        selected = zakladka == z,
                        onClick = {
                            zakladka = z
                            if (z != Zakladka.ALARMY) wybranyAlarmId = null
                        },
                        icon = { Icon(painterResource(z.ikona), contentDescription = null) }, // opis = etykieta
                        label = { Text(stringResource(z.etykieta)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val zawartosc = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).fillMaxSize()
        stanZakladek.SaveableStateProvider(zakladka.name) {
            when (zakladka) {
                Zakladka.ALARMY -> BoxWithConstraints(zawartosc) {
                    val szeroki = maxWidth >= 600.dp
                    val wybrany = alarmy.firstOrNull { it.id == wybranyAlarmId }

                    val lista: @Composable (Modifier) -> Unit = { mod ->
                        EkranListy(
                            alarmy = alarmy,
                            mojBudynekId = ustawienia.mojBudynekId,
                            onWybierzAlarm = { wybranyAlarmId = it },
                            modifier = mod,
                            wybranyId = wybranyAlarmId,
                            kryteria = kryteria,
                            onZmianaKryteriow = { kryteria = it },
                            stanListy = stanListy
                        )
                    }

                    val szczegoly: @Composable (Alarm, Boolean) -> Unit = { alarm, zPowrotem ->
                        EkranSzczegolow(
                            alarm = alarm,
                            onWstecz = if (zPowrotem) ({ wybranyAlarmId = null }) else null,
                            onPotwierdzOdbior = {
                                if (alarm.id !in potwierdzone) {
                                    potwierdzone.add(alarm.id)
                                    zmienAlarm(alarm.id) { it.copy(liczbaPotwierdzen = it.liczbaPotwierdzen + 1) }
                                }
                            },
                            onOdwolaj = { zmienAlarm(alarm.id) { it.copy(status = StatusAlarmu.ODWOLANY) } },
                            potwierdzono = alarm.id in potwierdzone
                        )
                    }

                    if (szeroki) {
                        // Tablet / telefon w poziomie: lista i szczegóły obok siebie
                        Row(Modifier.fillMaxSize()) {
                            lista(Modifier.weight(1f))
                            VerticalDivider()
                            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                                if (wybrany != null) {
                                    szczegoly(wybrany, false)
                                } else {
                                    PustyStan(stringResource(Res.string.wybierz_alarm))
                                }
                            }
                        }
                    } else if (wybrany != null) {
                        // Telefon: szczegóły jako osobny ekran
                        szczegoly(wybrany, true)
                    } else {
                        lista(Modifier)
                    }
                }

                Zakladka.OGLOS -> EkranFormularza(
                    onZapisz = { nowy -> alarmy.add(0, nowy.copy(id = (alarmy.maxOfOrNull { it.id } ?: 0) + 1)) },
                    modifier = zawartosc
                )

                Zakladka.STATYSTYKI -> EkranStatystyk(alarmy, zawartosc, sekundyWAplikacji)

                Zakladka.USTAWIENIA -> EkranUstawien(ustawienia, onZmianaUstawien, zawartosc)
            }
        }
    }
}
