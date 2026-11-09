package pl.uczelnia.bezpiecznykampus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pl.uczelnia.bezpiecznykampus.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkranUstawien(
    ustawienia: Ustawienia,
    onZmiana: (Ustawienia) -> Unit,
    modifier: Modifier = Modifier
) {
    ObserwatorCyklu("Ustawienia")
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.ustawienia_tytul)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(stringResource(Res.string.ustawienia_sekcja_alarmy), style = MaterialTheme.typography.titleMedium)
            WierszPrzelacznika(
                tytul = stringResource(Res.string.ust_powiadomienia),
                opis = stringResource(Res.string.ust_powiadomienia_opis),
                zaznaczony = ustawienia.powiadomienia,
                onZmiana = { onZmiana(ustawienia.copy(powiadomienia = it)) }
            )
            WierszPrzelacznika(
                tytul = stringResource(Res.string.ust_dzwiek),
                opis = stringResource(Res.string.ust_dzwiek_opis),
                zaznaczony = ustawienia.dzwiekIWibracje,
                onZmiana = { onZmiana(ustawienia.copy(dzwiekIWibracje = it)) }
            )
            WierszPrzelacznika(
                tytul = stringResource(Res.string.ust_offline),
                opis = stringResource(Res.string.ust_offline_opis),
                zaznaczony = ustawienia.zapisOffline,
                onZmiana = { onZmiana(ustawienia.copy(zapisOffline = it)) }
            )
            // Ręczne wskazanie budynku – alternatywa, gdy użytkownik nie udostępni lokalizacji
            PoleWyboru(
                etykieta = stringResource(Res.string.ust_moj_budynek),
                opcje = przykladoweBudynki,
                wybrana = przykladoweBudynki.firstOrNull { it.id == ustawienia.mojBudynekId },
                opisOpcji = { it.nazwa },
                onWybor = { onZmiana(ustawienia.copy(mojBudynekId = it.id)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(stringResource(Res.string.ustawienia_sekcja_wyglad), style = MaterialTheme.typography.titleMedium)
            Motyw.entries.forEach { motyw ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = ustawienia.motyw == motyw,
                            onClick = { onZmiana(ustawienia.copy(motyw = motyw)) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = ustawienia.motyw == motyw, onClick = null)
                    Text(stringResource(motyw.etykieta), style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp))
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            // O aplikacji – logo z zasobów z opisem dla czytnika ekranu (zadanie 2.11)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = stringResource(Res.string.opis_logo),
                    modifier = Modifier.size(64.dp)
                )
                Column(Modifier.padding(start = 16.dp)) {
                    Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(Res.string.ustawienia_o_aplikacji),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
