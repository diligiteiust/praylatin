# Bible file format

Bible is stored in files in [Yaml format](https://en.wikipedia.org/wiki/YAML). It means the file has a specific and strict structure. 
This is to make it possible for the app to display Bible verses line by line with translation interlines. 

The Bible verses can be formatted using [Markdown](https://www.markdownguide.org). However, Bible text formatting is kept
to absulute minimum and only within convention used by recognized publishers of the Bible. 

Each Bible file contains just one, entire chapter of on of the Bible books. Except the chapter
verses, the file also contains additional metadata, such as language, translation name and
other details.

## Basic Bible chapter information

Mandatory prayer information:
1. __title__ contains book name and chapter number
2. __book__ contains book name
3. __chapter__ contains chapter number
2. __lang__ is the 2-letter code of the prayer language, for example: __en__
3. __language__ is the full name of the prayer language. The language name must be in the native language, for example: __Español__
4. __source__ source information where the scripture text is taken from.
4. __lines__ is the the list of the Bible's chapter verses.
5. __comments__ commentaries to the verses
6. __transcription__ information about translation, transcription or other details
7. __ref__ book reference abbreviations

## Prayer file example

```yaml
title: Genesis 1
book: Genesis
chapter: 1
lang: la
language: Latina
lines:
  "1": In principio creavit Deus caelum et terram.
  "2": 'Terra autem erat inanis et vacua, et tenebrae erant super faciem abyssi: et spiritus Dei ferebatur super aquas.'
  "3": 'Dixitque Deus: Fiat lux. Et facta est lux.'
  "4": 'Et vidit Deus lucem quod esset bona: et divisit lucem a tenebris.'
  "5": 'Appellavitque lucem Diem, et tenebras Noctem: factumque est vespere et mane, dies unus.'
  "6": 'Dixit quoque Deus: Fiat firmamentum in medio aquarum: et dividat aquas ab aquis.'
  "7": 'Et fecit Deus firmamentum, divisitque aquas, quae erant sub firmamento, ab his, quae erant super firmamentum. Et factum est ita.'
  "8": 'Vocavitque Deus firmamentum, Caelum: et factum est vespere et mane, dies secundus.'
  "9": 'Dixit vero Deus: Congregentur aquae, quae sub caelo sunt, in locum unum: et appareat arida. Et factum est ita.'
  "10": Et vocavit Deus aridam Terram, congregationesque aquarum appellavit Maria. Et vidit Deus quod esset bonum.
comments:
  "### Verse 1"

  Beginning. As St. Matthew begins his Gospel with the same title as this work, the Book of the 
  Generation, or Genesis, so St. John adopts the first words of Moses, in the beginning...
```
