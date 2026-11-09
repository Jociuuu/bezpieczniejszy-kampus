# Bezpieczny Kampus

Aplikacja mobilna wspomagająca ewakuację i reagowanie na zagrożenia na terenie uczelni
(Kotlin Multiplatform + Compose Multiplatform, platforma docelowa: Android).

## Jak uruchomić

1. Android Studio → **File → New → Project from Version Control** (lub „Clone Repository” na ekranie startowym) i wklej adres tego repozytorium.
2. Poczekaj na synchronizację Gradle (pierwsza trwa kilka minut).
3. Wybierz konfigurację **androidApp** i emulator lub telefon, kliknij **Run**.

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

Na etapie 1 wszystkie dane (alarmy, budynki, liczby potwierdzeń) są **przykładowe** i przechowywane
w pamięci aplikacji (`Model.kt`). Nie ma połączenia z serwerem, a powiadomienia nie są jeszcze
wysyłane.

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

## Licencja

Kod źródłowy udostępniany na licencji **EUPL-1.2** (European Union Public Licence), która pozwala
instytucjom publicznym na swobodne korzystanie, modyfikowanie i rozpowszechnianie aplikacji.
Ikony pochodzą z zestawu Material Icons (Google, licencja Apache 2.0).
