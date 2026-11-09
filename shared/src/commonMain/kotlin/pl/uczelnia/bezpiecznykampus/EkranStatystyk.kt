package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*
import kotlin.math.roundToInt

// Pojedynczy kafelek statystyk (dane do wyświetlenia)
private data class Kafelek(val etykieta: String, val wartosc: String, val postep: Float? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkranStatystyk(
    alarmy: List<Alarm>,
    modifier: Modifier = Modifier,
    sekundyWAplikacji: Int = 0          // L3, zadanie 3.9: licznik liczony tylko, gdy aplikacja jest widoczna
) {
    ObserwatorCyklu("Statystyki")
    // Wartości wyliczane z danych
    val wszystkie = alarmy.size
    val aktywne = alarmy.count { it.aktywny }
    val testowe = alarmy.count { it.testowy }
    val odbiorcy = alarmy.sumOf { it.liczbaOdbiorcow }
    val potwierdzenia = alarmy.sumOf { it.liczbaPotwierdzen }
    val procentPotwierdzen = if (odbiorcy > 0) potwierdzenia.toFloat() / odbiorcy else 0f
    val ewakuacje = alarmy.count { it.zachowanie == WymaganeZachowanie.EWAKUACJA }

    val kafelki = buildList {
        add(Kafelek(stringResource(Res.string.stat_wszystkie), "$wszystkie"))
        add(Kafelek(stringResource(Res.string.stat_aktywne), "$aktywne"))
        add(Kafelek(stringResource(Res.string.stat_testowe), "$testowe"))
        add(
            Kafelek(
                stringResource(Res.string.stat_potwierdzenia),
                "${(procentPotwierdzen * 100).roundToInt()}% ($potwierdzenia / $odbiorcy)",
                procentPotwierdzen
            )
        )
        add(
            Kafelek(
                stringResource(Res.string.stat_ewakuacje),
                "$ewakuacje / $wszystkie",
                if (wszystkie > 0) ewakuacje.toFloat() / wszystkie else 0f
            )
        )
        add(
            Kafelek(
                stringResource(Res.string.stat_czas_w_aplikacji),
                stringResource(Res.string.stat_czas_wartosc, sekundyWAplikacji / 60, sekundyWAplikacji % 60)
            )
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.stat_tytul)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(Modifier.padding(innerPadding).fillMaxSize()) {
            val kolumny = if (maxWidth < 600.dp) 1 else 2          // responsywność (zadanie 2.13)
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                kafelki.chunked(kolumny).forEach { wiersz ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        wiersz.forEach { k ->
                            KartaStatystyki(k.etykieta, k.wartosc, Modifier.weight(1f), k.postep)
                        }
                        if (wiersz.size < kolumny) Spacer(Modifier.weight(1f))
                    }
                }

                Text(
                    stringResource(Res.string.stat_wg_kategorii),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                RodzajZagrozenia.entries.forEach { rodzaj ->
                    val liczba = alarmy.count { it.rodzaj == rodzaj }
                    KartaStatystyki(
                        etykieta = stringResource(rodzaj.etykieta),
                        wartosc = "$liczba",
                        modifier = Modifier.fillMaxWidth(),
                        postep = if (wszystkie > 0) liczba.toFloat() / wszystkie else 0f
                    )
                }
            }
        }
    }
}
