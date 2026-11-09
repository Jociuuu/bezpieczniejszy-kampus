package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkranSzczegolow(
    alarm: Alarm,
    onWstecz: (() -> Unit)?,          // null = brak przycisku powrotu (układ dwupanelowy na tablecie)
    onPotwierdzOdbior: () -> Unit,
    onOdwolaj: () -> Unit,
    modifier: Modifier = Modifier,
    // Etap 2 (L3): informacja o potwierdzeniu przychodzi z GlownyEkran. Wcześniej była w remember
    // na tym ekranie, więc po powrocie do listy znikała i można było potwierdzić odbiór drugi raz.
    potwierdzono: Boolean = false
) {
    ObserwatorCyklu("Szczegoly")
    val budynek = budynekDlaId(alarm.budynekId)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.szczegoly_tytul)) },
                navigationIcon = {
                    if (onWstecz != null) {
                        IconButton(onClick = onWstecz) {
                            Icon(
                                painterResource(Res.drawable.ic_wstecz),
                                contentDescription = stringResource(Res.string.opis_wstecz)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ZnacznikStatusu(alarm.status, alarm.testowy)
                ZnacznikKategorii(stringResource(alarm.rodzaj.etykieta))
            }
            Text(alarm.tytul, style = MaterialTheme.typography.headlineSmall)

            // Najważniejsza informacja: co mam zrobić i dokąd iść
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(alarm.zachowanie.etykieta), style = MaterialTheme.typography.titleLarge)
                    val cel = when (alarm.zachowanie) {
                        WymaganeZachowanie.EWAKUACJA ->
                            stringResource(Res.string.szczegoly_idz_do_zbiorki, budynek?.punktZbiorki ?: "–")
                        WymaganeZachowanie.SCHRONIENIE ->
                            stringResource(Res.string.szczegoly_idz_do_schronienia, budynek?.miejsceSchronienia ?: "–")
                        WymaganeZachowanie.POZOSTAN_W_SALI ->
                            stringResource(Res.string.szczegoly_pozostan)
                    }
                    Text(cel, style = MaterialTheme.typography.bodyLarge)
                }
            }

            WierszInformacji(stringResource(Res.string.pole_instrukcje), alarm.instrukcje)
            HorizontalDivider()
            WierszInformacji(stringResource(Res.string.pole_budynek), budynek?.nazwa ?: stringResource(Res.string.brak_budynku))
            WierszInformacji(stringResource(Res.string.pole_adres), budynek?.adres ?: "–")
            WierszInformacji(stringResource(Res.string.pole_punkt_zbiorki), budynek?.punktZbiorki ?: "–")
            WierszInformacji(
                stringResource(Res.string.pole_miejsce_schronienia),
                budynek?.let { stringResource(Res.string.schronienie_z_pojemnoscia, it.miejsceSchronienia, it.pojemnoscSchronienia) } ?: "–"
            )
            HorizontalDivider()
            WierszInformacji(stringResource(Res.string.pole_data), alarm.dataOgloszenia)
            WierszInformacji(
                stringResource(Res.string.pole_rodzaj_alarmu),
                stringResource(if (alarm.testowy) Res.string.alarm_testowy else Res.string.alarm_rzeczywisty)
            )
            WierszInformacji(
                stringResource(Res.string.pole_potwierdzenia),
                stringResource(Res.string.potwierdzenia_wartosc, alarm.liczbaPotwierdzen, alarm.liczbaOdbiorcow)
            )

            // Akcje: jeden główny przycisk (Button) i akcja drugorzędna (OutlinedButton)
            Button(
                onClick = onPotwierdzOdbior,
                enabled = alarm.aktywny && !potwierdzono,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(if (potwierdzono) Res.string.przycisk_potwierdzono else Res.string.przycisk_potwierdz)
                )
            }
            OutlinedButton(
                onClick = onOdwolaj,
                enabled = alarm.aktywny,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.przycisk_odwolaj))
            }
            Text(
                stringResource(Res.string.szczegoly_dane_przykladowe),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
