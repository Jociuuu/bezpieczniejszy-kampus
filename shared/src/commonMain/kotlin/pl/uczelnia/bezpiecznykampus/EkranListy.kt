package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

// Etap 2 (L3, zadanie 3.6): stan wyszukiwania i filtrów (KryteriaFiltrowania) oraz pozycja
// przewinięcia listy są przechowywane wyżej – w GlownyEkran (rememberSaveable + własny Saver).
// Dzięki temu nie giną po obrocie, po zabiciu procesu, po otwarciu szczegółów na telefonie
// ani przy zmianie układu jedno-/dwupanelowego, gdy obrót zmienia szerokość ekranu.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkranListy(
    alarmy: List<Alarm>,
    mojBudynekId: Int?,
    onWybierzAlarm: (Int) -> Unit,
    modifier: Modifier = Modifier,
    wybranyId: Int? = null,
    kryteria: KryteriaFiltrowania = KryteriaFiltrowania(),
    onZmianaKryteriow: (KryteriaFiltrowania) -> Unit = {},
    stanListy: LazyListState = rememberLazyListState()
) {
    ObserwatorCyklu("Lista")

    // Filtrowanie: fraza (tytuł lub nazwa budynku) + kategoria + własne warunki,
    // sortowanie: aktywne na górze, potem od najnowszych
    val widoczne = filtrujAlarmy(alarmy, kryteria, mojBudynekId)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.lista_tytul)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).fillMaxSize()) {
            OutlinedTextField(
                value = kryteria.fraza,
                onValueChange = { onZmianaKryteriow(kryteria.copy(fraza = it)) },
                label = { Text(stringResource(Res.string.lista_szukaj)) },
                leadingIcon = { Icon(painterResource(Res.drawable.ic_szukaj), contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            // Filtr kategorii – poziomo przewijany rząd FilterChip
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = kryteria.kategoria == null,
                    onClick = { onZmianaKryteriow(kryteria.copy(kategoria = null)) },
                    label = { Text(stringResource(Res.string.filtr_wszystkie)) }
                )
                RodzajZagrozenia.entries.forEach { rodzaj ->
                    FilterChip(
                        selected = kryteria.kategoria == rodzaj,
                        onClick = {
                            onZmianaKryteriow(kryteria.copy(kategoria = if (kryteria.kategoria == rodzaj) null else rodzaj))
                        },
                        label = { Text(stringResource(rodzaj.etykieta)) }
                    )
                }
            }
            // Własne warunki filtrowania
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = kryteria.tylkoAktywne,
                    onClick = { onZmianaKryteriow(kryteria.copy(tylkoAktywne = !kryteria.tylkoAktywne)) },
                    label = { Text(stringResource(Res.string.filtr_tylko_aktywne)) }
                )
                FilterChip(
                    selected = kryteria.tylkoMojBudynek && mojBudynekId != null,
                    onClick = { onZmianaKryteriow(kryteria.copy(tylkoMojBudynek = !kryteria.tylkoMojBudynek)) },
                    enabled = mojBudynekId != null,
                    label = { Text(stringResource(Res.string.filtr_moj_budynek)) }
                )
            }
            if (widoczne.isEmpty()) {
                PustyStan(stringResource(Res.string.lista_pusto))
            } else {
                LazyColumn(
                    state = stanListy,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(widoczne, key = { it.id }) { alarm ->
                        KartaAlarmu(
                            alarm = alarm,
                            zaznaczona = alarm.id == wybranyId,
                            onClick = { onWybierzAlarm(alarm.id) }
                        )
                    }
                }
            }
        }
    }
}
