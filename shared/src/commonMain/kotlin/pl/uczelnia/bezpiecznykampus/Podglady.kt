package pl.uczelnia.bezpiecznykampus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// Zadanie 2.12 – podglądy wszystkich pięciu ekranów w trybie jasnym i ciemnym

@Preview
@Composable
fun EkranListyJasny() {
    AppTheme(darkTheme = false) { EkranListy(przykladoweAlarmy, mojBudynekId = 4, onWybierzAlarm = {}) }
}

@Preview
@Composable
fun EkranListyCiemny() {
    AppTheme(darkTheme = true) { EkranListy(przykladoweAlarmy, mojBudynekId = 4, onWybierzAlarm = {}) }
}

@Preview
@Composable
fun EkranSzczegolowJasny() {
    AppTheme(darkTheme = false) { EkranSzczegolow(przykladoweAlarmy[0], onWstecz = {}, onPotwierdzOdbior = {}, onOdwolaj = {}) }
}

@Preview
@Composable
fun EkranSzczegolowCiemny() {
    AppTheme(darkTheme = true) { EkranSzczegolow(przykladoweAlarmy[0], onWstecz = {}, onPotwierdzOdbior = {}, onOdwolaj = {}) }
}

@Preview
@Composable
fun EkranFormularzaJasny() {
    AppTheme(darkTheme = false) { EkranFormularza(onZapisz = {}) }
}

@Preview
@Composable
fun EkranFormularzaCiemny() {
    AppTheme(darkTheme = true) { EkranFormularza(onZapisz = {}) }
}

@Preview
@Composable
fun EkranStatystykJasny() {
    AppTheme(darkTheme = false) { EkranStatystyk(przykladoweAlarmy) }
}

@Preview
@Composable
fun EkranStatystykCiemny() {
    AppTheme(darkTheme = true) { EkranStatystyk(przykladoweAlarmy) }
}

@Preview
@Composable
fun EkranUstawienJasny() {
    AppTheme(darkTheme = false) { EkranUstawien(Ustawienia(mojBudynekId = 4), onZmiana = {}) }
}

@Preview
@Composable
fun EkranUstawienCiemny() {
    AppTheme(darkTheme = true) { EkranUstawien(Ustawienia(mojBudynekId = 4), onZmiana = {}) }
}

// Cała aplikacja z dolną nawigacją
@Preview
@Composable
fun AplikacjaJasna() {
    AppTheme(darkTheme = false) { GlownyEkran(Ustawienia(), onZmianaUstawien = {}) }
}

@Preview
@Composable
fun AplikacjaCiemna() {
    AppTheme(darkTheme = true) { GlownyEkran(Ustawienia(), onZmianaUstawien = {}) }
}
