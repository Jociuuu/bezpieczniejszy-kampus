package pl.uczelnia.bezpiecznykampus

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import org.jetbrains.compose.resources.StringResource
import pl.uczelnia.bezpiecznykampus.resources.*

// ---------- Typy wyliczeniowe (etykiety pobierane z zasobów, zadanie 2.11) ----------

enum class RodzajZagrozenia(val etykieta: StringResource) {
    POZAR(Res.string.zagrozenie_pozar),
    Z_POWIETRZA(Res.string.zagrozenie_z_powietrza),
    WYCIEK_SUBSTANCJI(Res.string.zagrozenie_wyciek),
    PODEJRZANY_PRZEDMIOT(Res.string.zagrozenie_przedmiot),
    INNE(Res.string.zagrozenie_inne)
}

enum class WymaganeZachowanie(val etykieta: StringResource) {
    EWAKUACJA(Res.string.zachowanie_ewakuacja),
    SCHRONIENIE(Res.string.zachowanie_schronienie),
    POZOSTAN_W_SALI(Res.string.zachowanie_pozostan)
}

enum class StatusAlarmu(val etykieta: StringResource) {
    AKTYWNY(Res.string.status_aktywny),
    ZAKTUALIZOWANY(Res.string.status_zaktualizowany),
    ODWOLANY(Res.string.status_odwolany)
}

// ---------- Encja 2: budynek kampusu ----------
// Punkt zbiórki (na zewnątrz) i miejsce schronienia (wewnątrz) to różne rodzaje lokalizacji.
data class Budynek(
    val id: Int,
    val nazwa: String,
    val adres: String,
    val punktZbiorki: String,
    val miejsceSchronienia: String,
    val pojemnoscSchronienia: Int
)

// ---------- Encja 1 (główna): alarm ----------
data class Alarm(
    val id: Int,
    val tytul: String,                       // nazwa
    val rodzaj: RodzajZagrozenia,            // kategoria
    val status: StatusAlarmu,                // stan
    val budynekId: Int,                      // powiązanie z encją Budynek
    val zachowanie: WymaganeZachowanie,
    val testowy: Boolean,                    // alarm ćwiczebny / rzeczywisty
    val dataOgloszenia: String,              // "RRRR-MM-DD GG:MM"
    val instrukcje: String,
    val liczbaOdbiorcow: Int,
    val liczbaPotwierdzen: Int
) {
    val aktywny: Boolean get() = status != StatusAlarmu.ODWOLANY
}

// ---------- Dane PRZYKŁADOWE (symulowane, nie pochodzą z rzeczywistego systemu uczelni) ----------

val przykladoweBudynki = listOf(
    Budynek(1, "Budynek A – Rektorat", "ul. Uczelniana 1", "Parking przed budynkiem A", "Piwnica, sala A-01", 80),
    Budynek(2, "Budynek B – Wydział Mechaniczny", "ul. Uczelniana 3", "Trawnik przy bramie głównej", "Korytarz poziomu -1", 150),
    Budynek(3, "Budynek C – Centrum Innowacji", "ul. Uczelniana 5", "Plac przy fontannie", "Sala konferencyjna C-012", 120),
    Budynek(4, "Budynek E – Elektrotechnika i Informatyka", "ul. Uczelniana 7", "Parking P2 za budynkiem E", "Laboratorium E-005 (poziom -1)", 200),
    Budynek(5, "Budynek L – Biblioteka", "ul. Uczelniana 9", "Plac przed biblioteką", "Magazyn książek, poziom -1", 60)
)

val przykladoweAlarmy = listOf(
    Alarm(1, "Pożar w laboratorium chemicznym", RodzajZagrozenia.POZAR, StatusAlarmu.AKTYWNY, 2,
        WymaganeZachowanie.EWAKUACJA, false, "2026-10-21 10:15",
        "Opuść budynek najbliższym wyjściem ewakuacyjnym. Nie korzystaj z wind.", 420, 315),
    Alarm(2, "Ćwiczenia ewakuacyjne – budynek E", RodzajZagrozenia.POZAR, StatusAlarmu.AKTYWNY, 4,
        WymaganeZachowanie.EWAKUACJA, true, "2026-10-21 09:00",
        "To jest ALARM TESTOWY. Udaj się do punktu zbiórki i potwierdź dotarcie w aplikacji.", 610, 588),
    Alarm(3, "Ostrzeżenie przed zagrożeniem z powietrza", RodzajZagrozenia.Z_POWIETRZA, StatusAlarmu.ZAKTUALIZOWANY, 1,
        WymaganeZachowanie.SCHRONIENIE, false, "2026-10-20 14:40",
        "Udaj się do wyznaczonego miejsca schronienia. Oddal się od okien.", 95, 61),
    Alarm(4, "Wyciek gazu w kotłowni", RodzajZagrozenia.WYCIEK_SUBSTANCJI, StatusAlarmu.ODWOLANY, 3,
        WymaganeZachowanie.EWAKUACJA, false, "2026-10-18 08:05",
        "Opuść budynek, nie używaj otwartego ognia ani włączników światła.", 210, 198),
    Alarm(5, "Podejrzany pakunek w holu", RodzajZagrozenia.PODEJRZANY_PRZEDMIOT, StatusAlarmu.ODWOLANY, 5,
        WymaganeZachowanie.EWAKUACJA, false, "2026-10-15 12:30",
        "Nie zbliżaj się do pakunku. Opuść budynek wyjściem od strony parkingu.", 140, 120),
    Alarm(6, "Zadymienie klatki schodowej", RodzajZagrozenia.POZAR, StatusAlarmu.ODWOLANY, 1,
        WymaganeZachowanie.EWAKUACJA, false, "2026-10-12 16:20",
        "Korzystaj z klatki schodowej po stronie zachodniej.", 88, 80),
    Alarm(7, "Ćwiczenia – schronienie w budynku", RodzajZagrozenia.Z_POWIETRZA, StatusAlarmu.ODWOLANY, 4,
        WymaganeZachowanie.SCHRONIENIE, true, "2026-10-08 11:00",
        "ALARM TESTOWY. Przejdź do miejsca schronienia na poziomie -1.", 590, 512),
    Alarm(8, "Rozlanie substancji w laboratorium", RodzajZagrozenia.WYCIEK_SUBSTANCJI, StatusAlarmu.ODWOLANY, 4,
        WymaganeZachowanie.POZOSTAN_W_SALI, false, "2026-10-06 13:10",
        "Pozostań w sali, zamknij drzwi i okna do czasu odwołania alarmu.", 160, 101),
    Alarm(9, "Awaria instalacji elektrycznej", RodzajZagrozenia.INNE, StatusAlarmu.ODWOLANY, 2,
        WymaganeZachowanie.EWAKUACJA, false, "2026-10-02 09:45",
        "Opuść budynek spokojnie, zachowaj ostrożność na nieoświetlonych korytarzach.", 380, 290),
    Alarm(10, "Pożar śmietnika przy budynku", RodzajZagrozenia.POZAR, StatusAlarmu.ODWOLANY, 3,
        WymaganeZachowanie.POZOSTAN_W_SALI, false, "2026-09-29 17:30",
        "Zamknij okna od strony dziedzińca. Ewakuacja nie jest wymagana.", 120, 66),
    Alarm(11, "Ćwiczenia – podejrzany przedmiot", RodzajZagrozenia.PODEJRZANY_PRZEDMIOT, StatusAlarmu.ODWOLANY, 5,
        WymaganeZachowanie.EWAKUACJA, true, "2026-09-25 10:00",
        "ALARM TESTOWY. Opuść bibliotekę i udaj się na plac przed budynkiem.", 75, 70),
    Alarm(12, "Wyciek wody – zalanie korytarza", RodzajZagrozenia.INNE, StatusAlarmu.ODWOLANY, 1,
        WymaganeZachowanie.POZOSTAN_W_SALI, false, "2026-09-20 15:55",
        "Omijaj korytarz parteru, korzystaj z wyjścia bocznego.", 70, 41)
)

fun budynekDlaId(id: Int): Budynek? = przykladoweBudynki.firstOrNull { it.id == id }

// ---------- Ustawienia aplikacji (ekran ustawień) ----------

enum class Motyw(val etykieta: StringResource) {
    SYSTEMOWY(Res.string.motyw_systemowy),
    JASNY(Res.string.motyw_jasny),
    CIEMNY(Res.string.motyw_ciemny)
}

data class Ustawienia(
    val powiadomienia: Boolean = true,
    val dzwiekIWibracje: Boolean = true,
    val zapisOffline: Boolean = true,
    val motyw: Motyw = Motyw.SYSTEMOWY,
    val mojBudynekId: Int? = null
)

// Zapis ustawień przy obrocie ekranu i zabiciu procesu (rememberSaveable w App).
// Ustawienia to docelowo dane trwałe (L6: DataStore / baza danych) – do tego czasu
// przechowujemy je w rememberSaveable, aby obrót ekranu nie resetował np. motywu.
// Enum zapisujemy jako nazwę (String), a brak budynku jako -1, bo zapis obsługuje tylko typy proste.
val UstawieniaSaver: Saver<Ustawienia, Any> = listSaver<Ustawienia, Any>(
    save = {
        listOf(it.powiadomienia, it.dzwiekIWibracje, it.zapisOffline, it.motyw.name, it.mojBudynekId ?: -1)
    },
    restore = {
        Ustawienia(
            powiadomienia = it[0] as Boolean,
            dzwiekIWibracje = it[1] as Boolean,
            zapisOffline = it[2] as Boolean,
            motyw = Motyw.entries.firstOrNull { m -> m.name == it[3] } ?: Motyw.SYSTEMOWY,
            mojBudynekId = (it[4] as Int).takeIf { id -> id >= 0 }
        )
    }
)

// ---------- Etap 2 (L3, zadanie 3.7): kryteria filtrowania listy + własny Saver ----------
// Cztery pola stanu ekranu listy trzymane w jednej klasie. rememberSaveable nie potrafi zapisać
// takiej klasy samodzielnie (nie jest typem prostym), dlatego podajemy własny Saver, który
// zamienia obiekt na listę wartości prostych i odtwarza go z tej listy.
data class KryteriaFiltrowania(
    val fraza: String = "",
    val kategoria: RodzajZagrozenia? = null,   // null = wszystkie rodzaje
    val tylkoAktywne: Boolean = false,
    val tylkoMojBudynek: Boolean = false
)

val KryteriaFiltrowaniaSaver: Saver<KryteriaFiltrowania, Any> = listSaver<KryteriaFiltrowania, Any>(
    save = { listOf(it.fraza, it.kategoria?.name ?: "", it.tylkoAktywne, it.tylkoMojBudynek) },
    restore = {
        KryteriaFiltrowania(
            fraza = it[0] as String,
            kategoria = RodzajZagrozenia.entries.firstOrNull { r -> r.name == it[1] },
            tylkoAktywne = it[2] as Boolean,
            tylkoMojBudynek = it[3] as Boolean
        )
    }
)

// Filtrowanie i sortowanie listy alarmów według kryteriów (logika wydzielona z ekranu listy)
fun filtrujAlarmy(alarmy: List<Alarm>, kryteria: KryteriaFiltrowania, mojBudynekId: Int?): List<Alarm> =
    alarmy
        .filter { alarm ->
            val budynek = budynekDlaId(alarm.budynekId)?.nazwa ?: ""
            (kryteria.fraza.isBlank() || alarm.tytul.contains(kryteria.fraza, ignoreCase = true) ||
                budynek.contains(kryteria.fraza, ignoreCase = true)) &&
                (kryteria.kategoria == null || alarm.rodzaj == kryteria.kategoria) &&
                (!kryteria.tylkoAktywne || alarm.aktywny) &&
                // gdy budynek nie jest wybrany w ustawieniach, filtr "Mój budynek" jest ignorowany
                (!kryteria.tylkoMojBudynek || mojBudynekId == null || alarm.budynekId == mojBudynekId)
        }
        .sortedWith(compareByDescending<Alarm> { it.aktywny }.thenByDescending { it.dataOgloszenia })
