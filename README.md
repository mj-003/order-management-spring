# Sklep AGD - Moduł Zamówień

## Opis projektu
Projekt stanowi część aplikacji sklepu AGD, odpowiedzialną za składanie zamówień. Zakłada się, że koszyk jest już gotowy, a użytkownik wybiera sposób płatności, dostawy oraz wypełnia odpowiednie formularze w zależności od statusu (klient indywidualny lub firma). Proces kończy się symulacją przekierowania na stronę płatności.

## Technologie
- **Spring Boot** 
- **JUnit** (testy jednostkowe)
- **H2** (baza danych)

## Funkcjonalności
- Wybór typu zamawiającego: **Klient indywidualny / Firma**
- Formularz danych osobowych lub firmowych (NIP, dane fakturowe)
- Wybór metody wysyłki i płatności
- Obsługa kodów rabatowych
- Obsługa punktów lojalnościowych
- Walidacja danych
- Testy jednostkowe logiki zamówień

## Struktura projektu
- **controller** – zawiera klasy obsługujące żądania HTTP (REST API lub MVC).
- **DTOs** – Data Transfer Objects, obiekty używane do przesyłania danych między warstwami aplikacji.
- **entity** – encje JPA, mapujące obiekty na bazę danych.
- **enums** – klasy enumeracji używane np. do statusów zamówień, metod płatności itp.
- **exceptions** – klasy obsługujące wyjątki w aplikacji.
- **model** – prawdopodobnie dodatkowe klasy biznesowe (jeśli masz już entity, można tu trzymać np. niestandardowe obiekty domenowe).
- **repository** – interfejsy do komunikacji z bazą danych (Spring Data JPA).
- **service** – warstwa logiki biznesowej, operacje na encjach.

## Testy
W projekcie zawarto zarówno testy jednostkowe jak i testy UI.


