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
- **Model** - encje (Order, Payment, Delivery, DiscountCode itp.)
- **Repository** - operacje na bazie danych
- **Service** - logika biznesowa zamówień
- **Controller** - obsługa żądań HTTP
- **Tests** - testy jednostkowe (JUnit, Mockito)


