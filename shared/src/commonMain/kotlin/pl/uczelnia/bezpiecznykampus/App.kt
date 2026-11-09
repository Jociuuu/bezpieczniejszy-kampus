package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
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
    var ustawienia by remember { mutableStateOf(Ustawienia()) }
    val ciemny = when (ustawienia.motyw) {
        Motyw.SYSTEMOWY -> isSystemInDarkTheme()
        Motyw.JASNY -> false
        Motyw.CIEMNY -> true
    }
    AppTheme(darkTheme = ciemny) {
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
    var zakladka by remember { mutableStateOf(startowaZakladka) }
    var wybranyAlarmId by remember { mutableStateOf(startowyAlarmId) }
    // Lista alarmów w pamięci (dane przykładowe); w L4/L6 zastąpi ją repozytorium i baza danych
    val alarmy = remember { mutableStateListOf<Alarm>().apply { addAll(przykladoweAlarmy) } }

    fun zmienAlarm(id: Int, zmiana: (Alarm) -> Alarm) {
        val indeks = alarmy.indexOfFirst { it.id == id }
        if (indeks >= 0) alarmy[indeks] = zmiana(alarmy[indeks])
    }

    Scaffold(
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
        when (zakladka) {
            Zakladka.ALARMY -> BoxWithConstraints(zawartosc) {
                val szeroki = maxWidth >= 600.dp
                val wybrany = alarmy.firstOrNull { it.id == wybranyAlarmId }

                val szczegoly: @Composable (Alarm, Boolean) -> Unit = { alarm, zPowrotem ->
                    EkranSzczegolow(
                        alarm = alarm,
                        onWstecz = if (zPowrotem) ({ wybranyAlarmId = null }) else null,
                        onPotwierdzOdbior = { zmienAlarm(alarm.id) { it.copy(liczbaPotwierdzen = it.liczbaPotwierdzen + 1) } },
                        onOdwolaj = { zmienAlarm(alarm.id) { it.copy(status = StatusAlarmu.ODWOLANY) } }
                    )
                }

                if (szeroki) {
                    // Tablet / telefon w poziomie: lista i szczegóły obok siebie
                    Row(Modifier.fillMaxSize()) {
                        EkranListy(
                            alarmy = alarmy,
                            mojBudynekId = ustawienia.mojBudynekId,
                            onWybierzAlarm = { wybranyAlarmId = it },
                            wybranyId = wybranyAlarmId,
                            modifier = Modifier.weight(1f)
                        )
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
                    EkranListy(
                        alarmy = alarmy,
                        mojBudynekId = ustawienia.mojBudynekId,
                        onWybierzAlarm = { wybranyAlarmId = it }
                    )
                }
            }

            Zakladka.OGLOS -> EkranFormularza(
                onZapisz = { nowy -> alarmy.add(0, nowy.copy(id = (alarmy.maxOfOrNull { it.id } ?: 0) + 1)) },
                modifier = zawartosc
            )

            Zakladka.STATYSTYKI -> EkranStatystyk(alarmy, zawartosc)

            Zakladka.USTAWIENIA -> EkranUstawien(ustawienia, onZmianaUstawien, zawartosc)
        }
    }
}
