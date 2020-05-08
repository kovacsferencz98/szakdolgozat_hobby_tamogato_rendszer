
# Hobby Támogató rendszer
	
Ez a tároló a szakdolgozatomhoz készített webalkalmazást tartalmazza. A forráskód felhasználása engedély köteles.

## Leírás

A szakdolgozat célja egy olyan webalkalmazás fejlesztése, mely különböző szabadidős tevékenységek végzését támogatja, olyan formában, hogy segít hasonló érdeklődési körű partnereket keresni e tevékenységekhez.

A felhasználó bejelentkezés után különböző funkcionalitások közül választhat. Az elsődleges feature a tevékenységek meghirdetése  lesz. Itt előre meghatározott kategóriákból kiválasztásra kerül a tevékenység típusa, meg kell adni ennek a helyszínét, pontos időintervallumot, résztvevők maximális számát, eddigi résztvevők számát, résztvevők minimális számát (amit ha nem sikerül elérni a tevékenység nem fog bekövetkezni), előre látható költségek, illetve lesz lehetőség bővebb leírást adni a tevékenységről.

Továbbá a felhasználónak lehetősége lesz térképen böngészni a közelében meghírdetett tevékenységeket, illetve lehetősége lesz ezeket célirányosan szűrni típus, helyszín, idő, résztvevők száma, árkategória, stb. alapján. Ha sikerül szimpatikus tevékenységet találni, erre egyszerűen lehet majd jelentkezni. A jelentkezést a hirdetőnek el kell fogadnia. A hirdető és jelentkező felhasználók közötti kommunikációt is lehetővé kell tenni.

A webalkalmazáshoz egy reszponzív, modern, vizuálisan kellemes felhasználói felületnek kell tartoznia, mely minél több eszközön és felbontás mellett is megfelelő felhasználói élményt tud biztosítani.

## Futtatás

A repository klónozása után, a projekt gyökér könyvtárában ki kell adni a következő parancsot a back-end alkalmazás elindításához:
``````console
 mvn spring-boot:run
``````
A front-end alkalmazás indításához a /frontend mappában a következő parancs kiadása szükséges:
``````console
npm run serve
``````