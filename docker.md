
# Docker fájl magyarázata

Ez egy többlépcsős (multi-stage) Docker fájl, amely egy Java alkalmazás építését és futtatását teszi lehetővé. Részletes magyarázat soronként:

```dockerfile
FROM maven AS builder
```
- Az első build szakasz kezdődik, amely a hivatalos Maven képet használja alapként
- A `builder` egy elnevezés, amire később hivatkozni lehet

```dockerfile
COPY src /usr/src/app/src
```
- A forráskód mappát átmásolja a helyi környezetből a konténer `/usr/src/app/src` útvonalára

```dockerfile
COPY pom.xml /usr/src/app
```
- A Maven projekt konfigurációs fájlt másolja a konténerbe

```dockerfile
RUN mvn -f /usr/src/app/pom.xml clean package
```
- Futtatja a Maven parancsot, amely megtisztítja (clean) és felépíti (package) a projektet
- A végeredmény egy futtatható JAR fájl lesz a `/usr/src/app/target/` mappában

```dockerfile
FROM eclipse-temurin:21-jre-alpine
```
- A második build szakasz kezdődik
- Az Eclipse Temurin 21-es Java Runtime Environment Alpine Linux változatát használja alapképként
- Ez egy karcsú, csak futtatásra alkalmas környezet

```dockerfile
WORKDIR /tmp
```
- Beállítja a konténer munkakönyvtárát `/tmp`-re

```dockerfile
COPY --from=builder /usr/src/app/target/*.jar app.jar
```
- Az első szakaszból (builder) átmásolja a létrehozott JAR fájlt az új konténerbe
- Az új konténerben `app.jar` néven lesz elérhető

```dockerfile
ENTRYPOINT ["java","-jar","app.jar"]
```
- Meghatározza, hogy a konténer indításakor milyen parancsot kell végrehajtani
- A Java virtuális gép fogja futtatni a korábban átmásolt JAR fájlt

Ez a megközelítés előnye, hogy a végső Docker kép csak a futtatáshoz szükséges komponenseket tartalmazza, így kisebb méretű és biztonságosabb.



# Docker Compose fájl magyarázata

Ez egy Docker Compose konfigurációs fájl, amely több szolgáltatás együttes futtatását teszi lehetővé. A fájl részletes magyarázata:

```yaml
version: '3'
```
- Meghatározza a Docker Compose fájl formátumának verzióját (3-as verzió)

```yaml
services:
```
- A szolgáltatások definícióinak kezdete, amelyek a többkonténeres alkalmazást alkotják

```yaml
  db:
    image: postgres
```
- Az első szolgáltatás neve: `db`
- A hivatalos PostgreSQL adatbázis képet használja a Docker Hub-ról

```yaml
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: humbug123
      POSTGRES_DB: solarwatchapi
```
- Környezeti változók beállítása a PostgreSQL konténer számára:
    - Felhasználónév: `postgres`
    - Jelszó: `humbug123`
    - Adatbázis neve: `solarwatchapi`

```yaml
    volumes:
      - ./data:/var/lib/postgresql/data
```
- Kötet (volume) létrehozása, amely összeköti a helyi `./data` könyvtárat a konténer `/var/lib/postgresql/data` könyvtárával
- Ez biztosítja, hogy az adatok megmaradjanak a konténer újraindítása után is

```yaml
    ports:
      - 5432:5432
```
- A konténer 5432-es portját (alapértelmezett PostgreSQL port) összeköti a host 5432-es portjával
- Így a PostgreSQL adatbázis elérhető lesz a host gépen is ezen a porton

```yaml
  app:
    build: .
```
- A második szolgáltatás neve: `app`
- Az alkalmazás képét az aktuális könyvtárban található Dockerfile alapján építi fel

```yaml
#    image: solarwatch
```
- Kikommentezett sor, amely azt mutatja, hogy alternatívaként használható lenne egy előre elkészített `solarwatch` nevű kép is

```yaml
    depends_on:
    -   db
```
- Függőségi kapcsolat: az `app` szolgáltatás csak a `db` szolgáltatás elindulása után fog elindulni

```yaml
    environment:
      DB_USERNAME: postgres
      DB_PASSWORD: humbug123
      DB_URL: jdbc:postgresql://db:5432/solarwatchapi
      SECRET_KEY: key
      EXPIRATION: 860000
```
- Környezeti változók beállítása az alkalmazás konténer számára:
    - Adatbázis felhasználónév: `postgres`
    - Adatbázis jelszó: `humbug123`
    - Adatbázis URL: `jdbc:postgresql://db:5432/solarwatchapi` (a `db` a másik szolgáltatás neve)
    - Titkos kulcs és lejárati idő (valószínűleg JWT tokenekhez)

```yaml
    ports:
      - 8080:8080
```
- A konténer 8080-as portját összeköti a host 8080-as portjával
- Így az alkalmazás elérhető lesz a host gépen is ezen a porton

Ez a Docker Compose fájl lehetővé teszi, hogy egyetlen paranccsal (`docker-compose up`) elindítható legyen mind az adatbázis, mind az alkalmazás, a megfelelő konfigurációval és kapcsolódásokkal.
