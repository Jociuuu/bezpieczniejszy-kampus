package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*

// Sprawdzenie formatu "RRRR-MM-DD GG:MM" wraz z zakresami miesiąca, dnia, godziny i minut
fun poprawnaDataGodzina(tekst: String): Boolean {
    val wynik = Regex("""(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2})""").matchEntire(tekst.trim()) ?: return false
    val (_, miesiac, dzien, godzina, minuta) = wynik.destructured
    return miesiac.toInt() in 1..12 && dzien.toInt() in 1..31 &&
        godzina.toInt() in 0..23 && minuta.toInt() in 0..59
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkranFormularza(
    onZapisz: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    ObserwatorCyklu("Formularz")

    // Etap 2 (L3, zadanie 3.5): wszystkie dane wpisane przez użytkownika oraz stan komunikatów
    // o błędach są w rememberSaveable – przetrwają obrót ekranu i zabicie procesu przez system.
    var tytul by rememberSaveable { mutableStateOf("") }
    var instrukcje by rememberSaveable { mutableStateOf("") }
    var rodzaj by rememberSaveable { mutableStateOf(RodzajZagrozenia.POZAR) }       // enum (Serializable)
    // Budynek zapisujemy jako identyfikator – obiektu Budynek nie da się zapisać w rememberSaveable
    var budynekId by rememberSaveable { mutableStateOf<Int?>(null) }
    var zachowanie by rememberSaveable { mutableStateOf(WymaganeZachowanie.EWAKUACJA) }
    var testowy by rememberSaveable { mutableStateOf(true) }        // domyślnie alarm TESTOWY – bezpieczniej
    var data by rememberSaveable { mutableStateOf("") }
    var odbiorcy by rememberSaveable { mutableStateOf("") }
    var probaZapisu by rememberSaveable { mutableStateOf(false) }   // błędy pokazujemy po kliknięciu "Ogłoś"

    val budynek = budynekId?.let { budynekDlaId(it) }
    // Stan przejściowy – obiekt pomocniczy snackbara nie musi przetrwać obrotu
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Walidacja – null oznacza poprawne pole
    val bladTytul = if (tytul.isBlank()) stringResource(Res.string.blad_tytul) else null
    val bladInstrukcje = if (instrukcje.isBlank()) stringResource(Res.string.blad_instrukcje) else null
    val bladBudynek = if (budynek == null) stringResource(Res.string.blad_budynek) else null
    val bladData = if (!poprawnaDataGodzina(data)) stringResource(Res.string.blad_data) else null
    val liczbaOdbiorcow = odbiorcy.toIntOrNull()
    val bladOdbiorcy = if (liczbaOdbiorcow == null || liczbaOdbiorcow <= 0) stringResource(Res.string.blad_odbiorcy) else null
    val formularzPoprawny = listOf(bladTytul, bladInstrukcje, bladBudynek, bladData, bladOdbiorcy).all { it == null }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.formularz_tytul)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()                                  // miejsce na klawiaturę
                .verticalScroll(rememberScrollState())         // formularz się przewija
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(Res.string.formularz_wymagane),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = tytul,
                onValueChange = { tytul = it },
                label = { Text(stringResource(Res.string.pole_tytul_wym)) },
                singleLine = true,
                isError = probaZapisu && bladTytul != null,
                supportingText = if (probaZapisu && bladTytul != null) { { Text(bladTytul) } } else null,
                modifier = Modifier.fillMaxWidth()
            )
            PoleWyboru(
                etykieta = stringResource(Res.string.pole_rodzaj),
                opcje = RodzajZagrozenia.entries,
                wybrana = rodzaj,
                opisOpcji = { stringResource(it.etykieta) },
                onWybor = { rodzaj = it }
            )
            PoleWyboru(
                etykieta = stringResource(Res.string.pole_budynek_wym),
                opcje = przykladoweBudynki,
                wybrana = budynek,
                opisOpcji = { it.nazwa },
                onWybor = { budynekId = it.id },
                blad = if (probaZapisu) bladBudynek else null
            )
            PoleWyboru(
                etykieta = stringResource(Res.string.pole_zachowanie),
                opcje = WymaganeZachowanie.entries,
                wybrana = zachowanie,
                opisOpcji = { stringResource(it.etykieta) },
                onWybor = { zachowanie = it }
            )
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text(stringResource(Res.string.pole_data_wym)) },
                placeholder = { Text("2026-10-22 10:30") },
                singleLine = true,
                isError = probaZapisu && bladData != null,
                supportingText = if (probaZapisu && bladData != null) { { Text(bladData) } } else null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = odbiorcy,
                onValueChange = { odbiorcy = it },
                label = { Text(stringResource(Res.string.pole_odbiorcy_wym)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = probaZapisu && bladOdbiorcy != null,
                supportingText = if (probaZapisu && bladOdbiorcy != null) { { Text(bladOdbiorcy) } } else null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = instrukcje,
                onValueChange = { instrukcje = it },
                label = { Text(stringResource(Res.string.pole_instrukcje_wym)) },
                minLines = 3,
                isError = probaZapisu && bladInstrukcje != null,
                supportingText = if (probaZapisu && bladInstrukcje != null) { { Text(bladInstrukcje) } } else null,
                modifier = Modifier.fillMaxWidth()
            )
            WierszPrzelacznika(
                tytul = stringResource(Res.string.pole_testowy),
                opis = stringResource(Res.string.pole_testowy_opis),
                zaznaczony = testowy,
                onZmiana = { testowy = it }
            )
            Button(
                onClick = {
                    probaZapisu = true
                    if (formularzPoprawny) {
                        onZapisz(
                            Alarm(
                                id = 0,                                    // identyfikator nada lista w App
                                tytul = tytul.trim(),
                                rodzaj = rodzaj,
                                status = StatusAlarmu.AKTYWNY,
                                budynekId = budynek!!.id,
                                zachowanie = zachowanie,
                                testowy = testowy,
                                dataOgloszenia = data.trim(),
                                instrukcje = instrukcje.trim(),
                                liczbaOdbiorcow = liczbaOdbiorcow!!,
                                liczbaPotwierdzen = 0
                            )
                        )
                        // wyczyszczenie formularza po zapisie
                        tytul = ""; instrukcje = ""; budynekId = null; data = ""; odbiorcy = ""
                        probaZapisu = false
                        scope.launch { snackbar.showSnackbar(getString(Res.string.formularz_zapisano)) }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(stringResource(Res.string.przycisk_oglos))
            }
        }
    }
}
