package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*

// ---------- 1. Znacznik kategorii (rodzaju zagrożenia) ----------
@Composable
fun ZnacznikKategorii(tekst: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = tekst,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ---------- 2. Znacznik statusu alarmu (kolor + tekst, kolor nie jest jedynym nośnikiem informacji) ----------
@Composable
fun ZnacznikStatusu(status: StatusAlarmu, testowy: Boolean, modifier: Modifier = Modifier) {
    val (tlo, tekst) = when {
        testowy -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        status == StatusAlarmu.ODWOLANY -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }
    val etykieta = if (testowy) {
        stringResource(Res.string.znacznik_test, stringResource(status.etykieta))
    } else {
        stringResource(status.etykieta)
    }
    Surface(modifier = modifier, shape = MaterialTheme.shapes.small, color = tlo, contentColor = tekst) {
        Text(
            text = etykieta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ---------- 3. Karta alarmu na liście ----------
@Composable
fun KartaAlarmu(
    alarm: Alarm,
    modifier: Modifier = Modifier,
    zaznaczona: Boolean = false,
    onClick: () -> Unit = {}
) {
    val budynek = budynekDlaId(alarm.budynekId)
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (zaznaczona) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ZnacznikStatusu(alarm.status, alarm.testowy)
                ZnacznikKategorii(stringResource(alarm.rodzaj.etykieta))
            }
            Text(alarm.tytul, style = MaterialTheme.typography.titleMedium)
            Text(
                text = budynek?.nazwa ?: stringResource(Res.string.brak_budynku),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(Res.string.ogloszono, alarm.dataOgloszenia),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------- 4. Wiersz z przełącznikiem (cały wiersz jest klikalny, min. 56 dp wysokości) ----------
@Composable
fun WierszPrzelacznika(
    tytul: String,
    opis: String?,
    zaznaczony: Boolean,
    onZmiana: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = zaznaczony, role = Role.Switch, onValueChange = onZmiana)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(tytul, style = MaterialTheme.typography.bodyLarge)
            if (opis != null) {
                Text(opis, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.width(16.dp))
        // onCheckedChange = null – kliknięcie obsługuje cały wiersz (toggleable)
        Switch(checked = zaznaczony, onCheckedChange = null)
    }
}

// ---------- 5. Karta z liczbą (statystyki), opcjonalnie z paskiem postępu ----------
@Composable
fun KartaStatystyki(
    etykieta: String,
    wartosc: String,
    modifier: Modifier = Modifier,
    postep: Float? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(etykieta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(wartosc, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            if (postep != null) {
                LinearProgressIndicator(progress = { postep }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

// ---------- 6. Widok pustego stanu ----------
@Composable
fun PustyStan(komunikat: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ilustracja_pusto),
            contentDescription = null,           // grafika dekoracyjna – treść niesie tekst poniżej
            modifier = Modifier.size(96.dp)
        )
        Text(
            text = komunikat,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ---------- 7. Wiersz "etykieta: wartość" (ekran szczegółów) ----------
@Composable
fun WierszInformacji(etykieta: String, wartosc: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(etykieta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(wartosc, style = MaterialTheme.typography.bodyLarge)
    }
}

// ---------- 8. Pole wyboru z listy rozwijanej (formularz, ustawienia) ----------
@Composable
fun <T> PoleWyboru(
    etykieta: String,
    opcje: List<T>,
    wybrana: T?,
    opisOpcji: @Composable (T) -> String,
    onWybor: (T) -> Unit,
    modifier: Modifier = Modifier,
    blad: String? = null
) {
    var rozwiniete by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(
            value = wybrana?.let { opisOpcji(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(etykieta) },
            trailingIcon = { Text(if (rozwiniete) "▲" else "▼") },
            isError = blad != null,
            supportingText = blad?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        // Przezroczysta warstwa nad polem – kliknięcie rozwija listę
        Box(
            Modifier
                .matchParentSize()
                .clickable(role = Role.DropdownList) { rozwiniete = true }
        )
        DropdownMenu(expanded = rozwiniete, onDismissRequest = { rozwiniete = false }) {
            opcje.forEach { opcja ->
                DropdownMenuItem(
                    text = { Text(opisOpcji(opcja)) },
                    onClick = {
                        onWybor(opcja)
                        rozwiniete = false
                    }
                )
            }
        }
    }
}

// ---------- Podglądy komponentów ----------

@Preview
@Composable
fun ZnacznikiPodglad() {
    AppTheme {
        Surface {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ZnacznikStatusu(StatusAlarmu.AKTYWNY, testowy = false)
                ZnacznikStatusu(StatusAlarmu.AKTYWNY, testowy = true)
                ZnacznikKategorii("Pożar")
            }
        }
    }
}

@Preview
@Composable
fun KartaAlarmuPodglad() {
    AppTheme {
        Surface { KartaAlarmu(przykladoweAlarmy[0], Modifier.padding(16.dp)) }
    }
}

@Preview
@Composable
fun WierszPrzelacznikaPodglad() {
    AppTheme {
        Surface {
            WierszPrzelacznika("Powiadomienia o alarmach", "Dźwięk nawet w trybie cichym", true, {}, Modifier.padding(16.dp))
        }
    }
}

@Preview
@Composable
fun KartaStatystykiPodglad() {
    AppTheme {
        Surface { KartaStatystyki("Potwierdzenia odbioru", "75%", Modifier.padding(16.dp), postep = 0.75f) }
    }
}

@Preview
@Composable
fun PustyStanPodglad() {
    AppTheme {
        Surface { PustyStan("Brak alarmów spełniających kryteria") }
    }
}

@Preview
@Composable
fun WierszInformacjiPodglad() {
    AppTheme {
        Surface { WierszInformacji("Punkt zbiórki", "Parking P2 za budynkiem E", Modifier.padding(16.dp)) }
    }
}

@Preview
@Composable
fun PoleWyboruPodglad() {
    AppTheme {
        Surface {
            PoleWyboru(
                etykieta = "Budynek *",
                opcje = przykladoweBudynki,
                wybrana = przykladoweBudynki[3],
                opisOpcji = { it.nazwa },
                onWybor = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
