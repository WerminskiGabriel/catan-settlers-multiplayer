Kolejność (bez gotowych rozwiązań)
Akcje API — rozdziel „faza tury” od „co gracz chce zrobić”. Jedna prośba = jedna czynność (rzut, budowa drogi, osady,
handel, koniec tury itd.). Dopóki to leży w jednym worku, frontend i testy będą się gryźć.

Stan gry w odpowiedzi — DTO musi nieść to, czego klient potrzebuje do gry (plansza, figury, ręka, kości, zbójca…), nie
tylko nicki i fazę. Najpierw zdecyduj co widać publicznie vs prywatnie, potem mapuj z Game.

Setup — osobna logika początkowego ustawiania (kolejność, darmowe budynki/drogi, surowce z drugiej osady). Potem
normalna tura.

Robber / 7 — poprawny discard, ruch zbójcy, kradzież, brak produkcji z hexu ze zbójcą.

Reszta zasad Catanu — miasta, bank/porty, VP, najdłuższa droga, armia, karty rozwoju. Po kolei, z testami domeny przy
każdej regule.

Frontend — lobby → create/join → hex board → przyciski zależne od fazy. React dopiero gdy JSON stanu i akcje są
stabilne.

Multiplayer na żywo — WebSockety / push, gdy REST + UI już działają w trybie „odśwież / poll”.

Testy — domena przy regułach; @WebMvcTest przy mapowaniu HTTP; mało pełnych integration testów.