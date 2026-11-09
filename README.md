# Bezpieczny Kampus

Aplikacja mobilna wspomagająca ewakuację i reagowanie na zagrożenia na terenie uczelni
(Kotlin Multiplatform + Compose Multiplatform, platforma docelowa: Android).

## Jak uruchomić

1. Android Studio (Otter 2025.2.1 lub nowsze, z wtyczką Kotlin Multiplatform) → **File → Open…** i wskaż katalog projektu
   (lub **File → New → Project from Version Control**, jeśli klonujesz repozytorium).
2. Poczekaj na synchronizację Gradle (pierwsza trwa kilka minut).
3. Wybierz konfigurację **androidApp** i emulator lub telefon, kliknij **Run** (▶).
4. Z wiersza poleceń: `./gradlew :androidApp:assembleDebug` – plik APK powstaje w
   `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

Struktura: `shared/` – wspólny kod Compose (ekrany, model, zasoby w `composeResources`), `androidApp/` – `MainActivity`, która wyświetla `App()`.

## Opis

Bezpieczny Kampus informuje studentów i pracowników o alarmach ogłoszonych na terenie kampusu
(np. pożar, zagrożenie z powietrza, wyciek substancji). Dla każdego alarmu aplikacja pokazuje, co
należy zrobić (ewakuacja, udanie się do schronienia lub pozostanie w sali) i wskazuje właściwy
punkt zbiórki albo miejsce schronienia przypisane do budynku. Użytkownik potwierdza odbiór
komunikatu, a administrator widzi zbiorcze statystyki potwierdzeń. Alarmy testowe (ćwiczenia) są
wyraźnie odróżnione od rzeczywistych. Aplikacja jest prototypem dydaktycznym i nie zastępuje
obowiązujących procedur bezpieczeństwa uczelni.

## Do kogo jest skierowana

- studenci i pracownicy uczelni – odbierają alarmy, sprawdzają punkt zbiórki, potwierdzają odbiór,
- administrator / Straż Akademicka – ogłasza, aktualizuje i odwołuje alarmy, przegląda statystyki.

## Ekrany

1. **Lista alarmów** – pole wyszukiwania (tytuł lub budynek), filtr rodzaju zagrożenia (FilterChip),
   dodatkowe filtry „Tylko aktywne” i „Mój budynek”, widok pustego stanu.
2. **Szczegóły alarmu** – wymagane zachowanie, punkt zbiórki / miejsce schronienia, instrukcje,
   dane budynku, liczba potwierdzeń; akcje „Potwierdzam odbiór” i „Odwołaj alarm”.
3. **Ogłoś alarm (formularz)** – tytuł, rodzaj zagrożenia, budynek, wymagane zachowanie, data
   i godzina, liczba odbiorców, instrukcje, przełącznik „Alarm testowy”; walidacja pól wymaganych.
4. **Statystyki** – liczba alarmów (wszystkie / aktywne / testowe), odsetek potwierdzeń odbioru,
   udział alarmów z ewakuacją, podział według rodzaju zagrożenia (paski postępu).
5. **Ustawienia** – powiadomienia, dźwięk i wibracje, instrukcje offline, ręczny wybór „mojego
   budynku” (gdy lokalizacja jest niedostępna), motyw (systemowy / jasny / ciemny).

Nawigacja: dolny pasek z zakładkami Alarmy, Ogłoś, Statystyki, Ustawienia. Na ekranie o szerokości
co najmniej 600 dp lista i szczegóły są wyświetlane obok siebie.

## Model danych

**Alarm** (encja główna)

| Pole | Typ | Opis |
|---|---|---|
| id | Int | identyfikator |
| tytul | String | nazwa alarmu |
| rodzaj | RodzajZagrozenia | kategoria: pożar, z powietrza, wyciek substancji, podejrzany przedmiot, inne |
| status | StatusAlarmu | stan: aktywny, zaktualizowany, odwołany |
| budynekId | Int | powiązanie z encją Budynek |
| zachowanie | WymaganeZachowanie | ewakuacja / schronienie / pozostań w sali |
| testowy | Boolean | alarm ćwiczebny czy rzeczywisty |
| dataOgloszenia | String | data i godzina ogłoszenia |
| instrukcje | String | komunikat dla użytkowników |
| liczbaOdbiorcow | Int | do ilu osób skierowano alarm |
| liczbaPotwierdzen | Int | ile osób potwierdziło odbiór |

**Budynek** (encja powiązana – jeden budynek ma wiele alarmów)

| Pole | Typ | Opis |
|---|---|---|
| id | Int | identyfikator |
| nazwa | String | np. „Budynek E – Elektrotechnika i Informatyka” |
| adres | String | adres budynku |
| punktZbiorki | String | punkt zbiórki na zewnątrz (ewakuacja) |
| miejsceSchronienia | String | miejsce schronienia wewnątrz |
| pojemnoscSchronienia | Int | maksymalna liczba osób w schronieniu |

Punkt zbiórki i miejsce schronienia są osobnymi polami, ponieważ są to różne rodzaje lokalizacji.

## Planowane API (L5)

- własne REST API systemu alarmowego (na etapie prototypu symulowane: plik JSON z alarmami
  i budynkami udostępniony np. przez GitHub Pages lub lokalny serwer Ktor) – pobieranie aktywnych
  alarmów, wysyłanie potwierdzeń odbioru i statusów,
- Open-Meteo (https://open-meteo.com, bez klucza API) – kierunek i prędkość wiatru jako informacja
  pomocnicza przy wycieku substancji niebezpiecznej.

## Planowane powiadomienia (L7)

- powiadomienie o nowym alarmie dla mojego budynku (wysoki priorytet, dźwięk i wibracja),
- powiadomienie o aktualizacji i odwołaniu alarmu,
- przypomnienie o potwierdzeniu odbioru, jeśli użytkownik nie zrobił tego po kilku minutach,
- wyraźnie oznaczone powiadomienia o alarmach testowych (ćwiczenia).

## Elementy symulowane

Na etapach 1–2 wszystkie dane (alarmy, budynki, liczby potwierdzeń) są **przykładowe** i przechowywane
w pamięci aplikacji (`Model.kt`). Nie ma połączenia z serwerem, a powiadomienia nie są jeszcze
wysyłane. Lista alarmów nie jest jeszcze zapisywana trwale – po obrocie ekranu lub ponownym
uruchomieniu wraca do danych przykładowych (zapis trwały: L6). Aplikacja nie twierdzi, że pokazuje
bieżące alarmy: po dłuższym pobycie w tle wyświetla komunikat, że lista może być nieaktualna.

## Dostępność

Sprawdzone i poprawione elementy (zadanie 2.14):

1. Przycisk powrotu i logo mają opis `contentDescription`; ikony na dolnym pasku mają widoczną
   etykietę tekstową, a grafika pustego stanu jest oznaczona jako dekoracyjna (`null`).
2. Status alarmu jest przekazywany tekstem („Aktywny”, „Odwołany”, „TEST · …”), a nie tylko kolorem.
3. Wiersze z przełącznikiem są klikalne w całości (`toggleable`, `Role.Switch`) i mają min. 56 dp
   wysokości; opcje motywu mają min. 48 dp (`selectable`, `Role.RadioButton`).
4. Wszystkie rozmiary tekstu są podane w `sp`; po ustawieniu największej czcionki teksty zawijają się,
   a długie ekrany (szczegóły, formularz, statystyki, ustawienia) są przewijane.
5. Kolory pochodzą wyłącznie z motywu (pary `primary`/`onPrimary` itd.) – w trybie ciemnym tekst
   pozostaje czytelny; nie użyto stałych `Color.White` ani `Color.Black`.
6. Błędy formularza są pokazywane tekstem pod polem (`supportingText`), a nie tylko czerwoną ramką.
7. Formularz przesuwa się nad klawiaturą (`imePadding` + `verticalScroll`).

## Etap 2 - cykl życia aplikacji

Stan projektu z etapu 1 zapisano commitem „Etap 1 - interfejs użytkownika”, a wynik tego etapu
commitem „Etap 2 - cykl życia aplikacji” (zadania 3.1 i 3.11).

Co zmieniono w kodzie (skrót):

| Plik | Zmiana |
|---|---|
| `androidApp/.../MainActivity.kt` | logi `CYKL` we wszystkich metodach cyklu życia (`onCreate` … `onDestroy`) |
| `shared/build.gradle.kts`, `libs.versions.toml` | biblioteka `org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose` (wersja zgodna z CMP 1.10.x) |
| `CyklZycia.kt` (nowy) | `ObserwatorCyklu`, licznik czasu `zliczajCzasWAplikacji`, `ReakcjaNaPowrot` |
| `Model.kt` | `KryteriaFiltrowania` + `KryteriaFiltrowaniaSaver`, `UstawieniaSaver`, funkcja `filtrujAlarmy` |
| `App.kt` | zakładka, otwarty alarm (id), filtry i pozycja listy w `rememberSaveable`; `rememberSaveableStateHolder` dla zakładek; obsługa nieistniejącego id; komunikat po powrocie z tła |
| `EkranFormularza.kt` | wszystkie pola i stan błędów w `rememberSaveable`, budynek zapisywany jako `budynekId` |
| `EkranListy.kt` | stan filtrów przeniesiony do `GlownyEkran` (state hoisting) |
| `EkranSzczegolow.kt` | potwierdzenie odbioru przeniesione do `GlownyEkran` – nie da się potwierdzić dwa razy |
| `EkranStatystyk.kt` | kafelek „Czas w aplikacji” |
| `AndroidManifest.xml` | bez zmian – brak atrybutu `android:configChanges`, więc obrót usuwa aktywność |

### Zadanie 3.2. Obserwacja cyklu życia Androida

Logi z `MainActivity` (filtr `CYKL` w Logcat). Zrzut z testu obrotu: `docs/logcat_obrot.png`.

| Test | Zaobserwowana kolejność wpisów | Wnioski |
|---|---|---|
| 1. Uruchomienie | `onCreate, stan zapisany: false` → `onStart` → `onResume` | pierwsze uruchomienie, brak zapisanego stanu |
| 2. Przejście do tła (Home) | `onPause` → `onStop` | aktywność niewidoczna, ale nadal istnieje (brak `onDestroy`) |
| 3. Powrót do aplikacji | `onRestart` → `onStart` → `onResume` | ta sama aktywność wraca, stan w pamięci jest zachowany |
| 4. Obrót ekranu | `onPause` → `onStop` → `onDestroy, zmiana konfiguracji: true` → `onCreate, stan zapisany: true` → `onStart` → `onResume` | obrót niszczy aktywność i tworzy nową; przetrwa tylko stan zapisany (`rememberSaveable`) |
| 5. Przycisk Wstecz | `onPause` → `onStop` (bez `onDestroy`) | od Androida 12 Wstecz na głównej aktywności tylko przenosi aplikację do tła |
| (a) Lista ostatnich aplikacji i powrót | `onPause` → `onStop` → `onRestart` → `onStart` → `onResume` | otwarcie Overview działa jak przejście do tła |
| (b) Symulowane połączenie (Extended controls → Phone) | `onPause` → `onStop`, po zakończeniu `onRestart` → `onStart` → `onResume` | ekran połączenia zasłania aplikację całkowicie; przy powiadomieniu typu heads-up nie ma żadnych wpisów – aplikacja zostaje na pierwszym planie |
| (c) Tryb ciemny wł./wył. | `onPause` → `onStop` → `onDestroy, zmiana konfiguracji: true` → `onCreate, stan zapisany: true` → `onStart` → `onResume` | zmiana trybu to zmiana konfiguracji (`uiMode`) – aktywność jest tworzona od nowa jak przy obrocie |

### Zadanie 3.3. Obserwator cyklu życia w Compose

`ObserwatorCyklu` (plik `CyklZycia.kt`) jest wywoływany w `App()` oraz na wszystkich pięciu ekranach:
`Lista`, `Szczegoly`, `Formularz`, `Statystyki`, `Ustawienia`. Wpisy z `println` widać w Logcat
z etykietą `System.out`. Zrzut: `docs/logcat_compose.png`.

Przykładowa sekwencja – przejście z zakładki Alarmy do Ogłoś:
`[Lista]: kompozycja usunięta` → `[Formularz]: kompozycja dodana` → `[Formularz]: ON_START` →
`[Formularz]: ON_RESUME`. Wpisy `MainActivity` się nie pojawiają.

Wnioski:

1. Przejście między zakładkami nie zmienia cyklu życia aktywności – zmienia się tylko kompozycja.
   Ekran opuszczany jest usuwany z kompozycji, nowy jest dodawany. Nowo dodany ekran od razu
   dostaje zaległe zdarzenia (`ON_START`, `ON_RESUME`), bo `LifecycleEventEffect` po rejestracji
   otrzymuje zdarzenia aż do bieżącego stanu.
2. Po obrocie ekranu cała kompozycja jest usuwana (`kompozycja usunięta` dla `App` i bieżącego ekranu
   po `onDestroy`) i tworzona od nowa po `onCreate`. Dlatego zmienne w zwykłym `remember` są tracone.
   Zdarzenia `ON_PAUSE`/`ON_STOP` pojawiają się w logu **przed** odpowiadającymi im wpisami
   `onPause`/`onStop` (biblioteka Lifecycle wysyła je przed wywołaniem metody), a `ON_START`/`ON_RESUME`
   **po** `onStart`/`onResume`.

Dlaczego `ON_START` i `ON_STOP` są wspólne dla wszystkich ekranów, a „kompozycja dodana” nie?
Aplikacja ma jedną aktywność i wszystkie ekrany obserwują ten sam właściciel cyklu życia
(`LocalLifecycleOwner` = `MainActivity`). Gdy aktywność staje się widoczna lub niewidoczna, zdarzenie
dostają wszystkie obserwatory naraz. Natomiast dodanie i usunięcie z kompozycji dotyczy
pojedynczego fragmentu interfejsu. Zależy od tego, który ekran jest akurat wyświetlany.

### Zadanie 3.4. Inwentaryzacja stanu aplikacji

Rodzaje: **P** – stan przejściowy (`remember`), **I** – stan interfejsu do zachowania (`rememberSaveable`),
**D** – dane aplikacji (docelowo ViewModel – L4 i pamięć trwała – L6).
Kolumna „Etap 1” pokazuje sposób przechowywania przed zmianami; ⚠ oznacza zmienną błędnie
sklasyfikowaną.

| Ekran / plik | Zmienna | Typ | Rodzaj | Etap 1 | Etap 2 (teraz) |
|---|---|---|---|---|---|
| App | `ustawienia` | `Ustawienia` | D | ⚠ `remember` (reset motywu po obrocie) | `rememberSaveable` + `UstawieniaSaver` (tymczasowo, docelowo L6) |
| GlownyEkran | `zakladka` | `Zakladka` (enum) | I | ⚠ `remember` | `rememberSaveable` |
| GlownyEkran | `wybranyAlarmId` | `Int?` | I | ⚠ `remember` | `rememberSaveable` (tylko id) |
| GlownyEkran | `kryteria` | `KryteriaFiltrowania` | I | ⚠ 4 osobne `remember` w EkranListy | `rememberSaveable` + `KryteriaFiltrowaniaSaver` |
| GlownyEkran | `stanListy` | `LazyListState` | I | – (brak) | `rememberLazyListState` (wbudowany Saver) |
| GlownyEkran | `stanZakladek` | `SaveableStateHolder` | I | – (brak) | `rememberSaveableStateHolder` |
| GlownyEkran | `alarmy` | `SnapshotStateList<Alarm>` | D | ⚠ `remember` | ⚠ nadal `remember` – lista to dane aplikacji, nie zapisujemy jej w stanie interfejsu; przeniesienie do ViewModel (L4) i bazy (L6) |
| GlownyEkran | `potwierdzone` | `SnapshotStateList<Int>` | D | ⚠ `remember(alarm.id)` w EkranSzczegolow | ⚠ `remember` w GlownyEkran (jak wyżej, L4/L6) |
| GlownyEkran | `snackbar` | `SnackbarHostState` | P | – | `remember` |
| CyklZycia (`zliczajCzasWAplikacji`) | `sekundy` | `Int` | I | – | `rememberSaveable` |
| CyklZycia (`zliczajCzasWAplikacji`) | `widoczna` | `Boolean` | P | – | `remember` |
| CyklZycia (`ReakcjaNaPowrot`) | `przejscieDoTla` | `ValueTimeMark?` | P | – | `remember` |
| EkranListy | przewinięcie rzędów filtrów | `ScrollState` | P | `rememberScrollState` | bez zmian |
| EkranSzczegolow | przewinięcie ekranu | `ScrollState` | P | `rememberScrollState` | bez zmian |
| EkranFormularza | `tytul`, `instrukcje`, `data`, `odbiorcy` | `String` | I | ⚠ `remember` | `rememberSaveable` |
| EkranFormularza | `rodzaj`, `zachowanie` | enum | I | ⚠ `remember` | `rememberSaveable` |
| EkranFormularza | `budynek` → `budynekId` | `Budynek?` → `Int?` | I | ⚠ `remember` (cały obiekt) | `rememberSaveable` (tylko id) |
| EkranFormularza | `testowy` | `Boolean` | I | ⚠ `remember` | `rememberSaveable` |
| EkranFormularza | `probaZapisu` (widoczność błędów) | `Boolean` | I | ⚠ `remember` | `rememberSaveable` |
| EkranFormularza | `snackbar` | `SnackbarHostState` | P | `remember` | bez zmian |
| EkranStatystyk, EkranUstawien | przewinięcie ekranu | `ScrollState` | P | `rememberScrollState` | bez zmian |
| Komponenty (`PoleWyboru`) | `rozwiniete` | `Boolean` | P | `remember` | bez zmian (otwarta lista rozwijana może się zamknąć po obrocie) |

### Zadanie 3.5. Zachowanie stanu formularza

Test: wpisanie wartości w pole na zakładce **Ogłoś**, obrót emulatora (Rotate Left).

| Pole | Przed poprawką | Po poprawce |
|---|---|---|
| Tytuł (tekst) | pole puste | tekst zachowany |
| Rodzaj zagrożenia (lista) | wraca do „Pożar” | wybrana wartość zachowana |
| Budynek (lista) | pole puste | wybrany budynek zachowany (zapisywany jako `budynekId`) |
| Wymagane zachowanie (lista) | wraca do „Ewakuacja” | wybrana wartość zachowana |
| Data i godzina (tekst) | pole puste | tekst zachowany |
| Liczba odbiorców (liczba) | pole puste | wartość zachowana |
| Instrukcje (tekst wielowierszowy) | pole puste | tekst zachowany |
| Alarm testowy (przełącznik) | wraca do „włączony” | stan przełącznika zachowany |
| Komunikaty o błędach (po kliknięciu „Ogłoś” z pustymi polami) | znikają | nadal widoczne pod polami |

Dodatkowo dzięki `rememberSaveableStateHolder` w `GlownyEkran` wpisane dane nie giną po przejściu
na inną zakładkę i powrocie do formularza.

### Zadanie 3.6. Zachowanie stanu listy i nawigacji

W `rememberSaveable` są: fraza wyszukiwania, filtr rodzaju zagrożenia, oba własne warunki
(„Tylko aktywne”, „Mój budynek”) – razem w klasie `KryteriaFiltrowania`, wybrana zakładka
oraz otwarty alarm. Otwarty alarm zapisujemy jako `wybranyAlarmId: Int?`, a nie jako obiekt
`Alarm` (obiekt nie jest typem prostym i mógłby być nieaktualny).

Stan listy jest przechowywany w `GlownyEkran`, a nie w `EkranListy`. `EkranListy` występuje w dwóch
miejscach kodu (układ jedno- i dwupanelowy). Obrót telefonu zmienia szerokość z < 600 dp na
≥ 600 dp, więc ekran listy byłby tworzony w innym miejscu i jego `rememberSaveable` zaczynałoby
od zera. Na telefonie lista jest też usuwana z kompozycji po otwarciu szczegółów, więc wcześniej
po powrocie filtry się zerowały.

**Identyfikator nieistniejącego elementu.** Scenariusz: ogłoś nowy alarm (dostaje id 13), otwórz go
na liście i obróć ekran. `wybranyAlarmId = 13` przetrwa obrót, ale lista alarmów (zwykłe `remember`,
dane aplikacji) wraca do 12 przykładowych alarmów. Efekt `LaunchedEffect(wybranyAlarmId, alarmy.size)`
wykrywa brak alarmu, ustawia `wybranyAlarmId = null` (powrót do listy) i pokazuje komunikat
„Wybrany alarm nie jest już dostępny – wrócono do listy”. Ten sam mechanizm zadziała, gdy w L5/L10
alarm zostanie usunięty na serwerze.

### Zadanie 3.7. Własny Saver

Klasa `KryteriaFiltrowania` (plik `Model.kt`) ma cztery pola: `fraza: String`,
`kategoria: RodzajZagrozenia?`, `tylkoAktywne: Boolean`, `tylkoMojBudynek: Boolean`.
`KryteriaFiltrowaniaSaver` (`listSaver`) zamienia obiekt na listę
`[fraza, nazwa kategorii lub "", tylkoAktywne, tylkoMojBudynek]` i odtwarza go z tej listy.
Drugi Saver, `UstawieniaSaver`, zapisuje w ten sam sposób ustawienia (motyw jako nazwa enuma,
brak budynku jako `-1`).

Dlaczego bez Savera się nie da: na Androidzie `rememberSaveable` zapisuje stan w obiekcie `Bundle`,
który przyjmuje tylko typy proste (liczby, tekst, wartości logiczne, ich listy) oraz klasy `Parcelable`
lub `Serializable`. Zwykła `data class` z kodu wspólnego nie spełnia tych warunków. Próba zapisu
kończy się wyjątkiem `IllegalArgumentException` („…cannot be saved using the current
SaveableStateRegistry…”). W kodzie wspólnym (commonMain) nie można też użyć adnotacji
`@Parcelize` bez dodatkowej konfiguracji, więc Saver jest rozwiązaniem działającym na wszystkich
platformach. Wartość `null` kategorii zapisujemy jako pusty tekst, bo wszystkie elementy listy
muszą dać się zapisać.

### Zadanie 3.8. Testy zabicia procesu

Metody: „Don't keep activities” (Developer options) oraz
`adb shell am kill pl.uczelnia.bezpiecznykampus` (aplikacja w tle). W obu przypadkach Logcat pokazał
po powrocie `MainActivity: onCreate, stan zapisany: true`. Zrzut formularza po zabiciu procesu:
`docs/formularz_po_kill.png`.

| Ekran | Stan wprowadzony przed testem | Stan po powrocie | Uwagi |
|---|---|---|---|
| Lista alarmów | fraza „bud”, filtr „Pożar”, „Tylko aktywne”, lista przewinięta | wszystko zachowane | – |
| Szczegóły alarmu | otwarty alarm „Pożar w laboratorium chemicznym” | ten sam alarm otwarty | potwierdzenie odbioru nie jest zachowane – to dane aplikacji, zapis trwały w L6 |
| Ogłoś alarm | wypełnione wszystkie pola, widoczne błędy walidacji | wszystkie pola i błędy zachowane | – |
| Statystyki | licznik czasu np. 1 min 20 s | zakładka Statystyki, licznik kontynuuje od zapisanej wartości | alarm dodany przed testem znika ze statystyk (lista alarmów jest w pamięci – L6) |
| Ustawienia | motyw „Ciemny”, wybrany budynek E, wyłączone powiadomienia | wszystko zachowane | ustawienia są tymczasowo w `rememberSaveable`; po zamknięciu aplikacji przez użytkownika (Force stop) wracają do domyślnych – zapis trwały w L6 |

Po testach opcja „Don't keep activities” została wyłączona.

### Zadanie 3.9. Reakcja na przejście do tła

1. **Licznik czasu w aplikacji** (`zliczajCzasWAplikacji` w `CyklZycia.kt`, przykład 3.7). Pętla
   w `LaunchedEffect` liczy sekundy tylko między `ON_START` a `ON_STOP`. Funkcja jest wywołana
   w `GlownyEkran`, więc licznik działa niezależnie od zakładki, a wynik widać na ekranie
   **Statystyki** („Czas w aplikacji”). Test: 20 s w aplikacji, Home, 10 s w tle, powrót – licznik
   pokazuje czas bez tych 10 s.
2. **Własna funkcja: ostrzeżenie o nieaktualnych danych po powrocie** (`ReakcjaNaPowrot`).
   Wybrałem `ON_STOP` (zapamiętanie chwili przejścia do tła) i `ON_START` (sprawdzenie, jak długo
   aplikacja była niewidoczna). Jeśli przerwa trwała co najmniej 30 s, pojawia się komunikat
   „Aplikacja była w tle przez … s. Lista alarmów może być nieaktualna…”. Uzasadnienie: w aplikacji
   alarmowej użytkownik nie może być wprowadzony w błąd, że widzi bieżące alarmy (wymaganie
   projektu, funkcjonalność 10). `ON_START`/`ON_STOP` oznaczają widoczność aplikacji, a nie fokus
   (`ON_PAUSE` występuje też np. przy otwartym oknie systemowym). W L5 w tym samym miejscu nastąpi
   automatyczne pobranie aktualnych alarmów z serwera. Obrót ekranu nie wyświetla komunikatu,
   bo znacznik czasu jest w zwykłym `remember` i ginie razem z kompozycją.

### Zadanie 3.10. Analiza porównawcza Android – iOS

Na Androidzie ekranem zarządza aktywność (`MainActivity`) z metodami `onCreate`, `onStart`, `onResume`,
`onPause`, `onStop` i `onDestroy`. Na iOS odpowiednikiem są zdarzenia aplikacji i kontrolera widoku
(`willResignActive`, `didEnterBackground`, `willEnterForeground`, `didBecomeActive`). Biblioteka
Lifecycle w Compose Multiplatform mapuje je na te same zdarzenia: `willResignActive` → `ON_PAUSE`,
`didEnterBackground` → `ON_STOP`, `willEnterForeground` → `ON_START`, `didBecomeActive` → `ON_RESUME`.
Dzięki temu `ObserwatorCyklu`, licznik czasu i `ReakcjaNaPowrot` z `commonMain` działają na obu
platformach bez zmian. Największa różnica dotyczy obrotu ekranu: Android usuwa i tworzy aktywność
od nowa, więc stan w `remember` ginie. Na iOS kontroler widoku tylko zmienia układ, więc interfejs
nie jest odtwarzany, a `remember` zwykle zostaje. Nie można jednak na tym polegać w aplikacji
wieloplatformowej, dlatego stan interfejsu i tak trzymamy w `rememberSaveable`. Przy braku pamięci
Android zabija proces w tle i przy powrocie odtwarza aktywność z zapisanym stanem (`Bundle`).
iOS zawiesza aplikację w tle i może ją zakończyć bez powiadomienia. Przywracanie stanu działa
tam inaczej i nie jest gwarantowane w ten sam sposób. Na obu platformach aplikacja zamknięta przez
użytkownika uruchamia się od nowa. Dlatego dane, które muszą przetrwać (lista alarmów,
potwierdzenia, ustawienia), zostaną w L6 zapisane w pamięci trwałej. Wnioski oparto na tabeli
z instrukcji i dokumentacji Compose Multiplatform (projekt testowano tylko na emulatorze Androida).

## Licencja

Kod źródłowy udostępniany na licencji **EUPL-1.2** (pełny tekst w pliku `LICENSE`) (European Union Public Licence), która pozwala
instytucjom publicznym na swobodne korzystanie, modyfikowanie i rozpowszechnianie aplikacji.
Ikony pochodzą z zestawu Material Icons (Google, licencja Apache 2.0).
