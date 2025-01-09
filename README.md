<p align="center">
<img src="shared/src/commonMain/composeResources/drawable/app_icon.png" alt="Logo" width="200"/>
</p>

<h1 style="text-align: center;">Pray Latin</h1>

## About

__Pray Latin__ is an application helping with praying in Latin and learning Latin prayers.

It displays prayers in your own language inline to help understand the Latin text which in turn allows to quickly learn prayer in Latin.

__It contains large collection of prayers__ in Latin and other languages and it can be used as daily prayer book.

__This is an open source software__ available online on
[the project development site](https://just-4.dev/LatinPray). 

## License

Source code is available under __[AGPLv3 License](https://www.gnu.org/licenses/agpl-3.0.en.html)__.

## Contact

You can contact the author of the app by sending an email to address: [devel@praylatin.app](mailto:devel@praylatin.app) or 
you can submit request online on this project development website: [new Issue](https://just-4.dev/LatinPray/~issues/new)

Please do not hesitate to contact me if you have any problems using the app, suggestions for new features, improvements or correct mistakes.

## Translations

The app already has UI translation to a few languages. You can submit a new translation in your own language. To do so, please create a copy of
the [translation file](shared/src/commonMain/composeResources/values/strings.xml) in your language and submit as a [new issue](https://just-4.dev/LatinPray/~issues/new).
It will be added to the project after review.

## New prayers

If you have suggestions or ideas for new prayers to be added to the app, please send me an email or open a new issue as described above. 

However, the best way to have new prayers in the app is to submit them in ready to use format as `yaml` file. This reduces the amount of work for the developer and allows for quickly adding new content. If you are interested in contributing new prayers, please continue to read below to learn about the prayer file format.

## Prayer yaml file format

```yaml
title: Angelus Domini
lang: la
language: Latina
tags:
  - Primus
  - Beata Virgo Maria
links:
  - type: youtube
    url: https://www.youtube.com/watch?v=41KBZsdC2dw&pp=ygUOQW5nZWx1cyBEb21pbmk%3D
  - type: youtube
    url: https://www.youtube.com/watch?v=EuIAfMyNj1I&pp=ygUOQW5nZWx1cyBEb21pbmk%3D
lines:
  - __Angelus Domini nuntiavit Mariae__
  - Et concepit de Spiritu Sancto.
  - "@avemaria"
  - ^^^
  - __Ecce ancilla Domini,__
  - Fiat mihi secundum verbum tuum.
  - "@avemaria"
  - ^^^
  - __Et Verbum caro factum est,__
  - Et habitavit in nobis.
  - "@avemaria"
  - ^^^
  - __Ora pro nobis,__ sancta Dei Genetrix,
  - Ut digni efficiamur promissionibus Christi.
  - ^^^
  - "__Oremus:__"
  - Gratiam tuam, quaesumus, Domine,
  - mentibus nostris infunde;
  - ut qui, Angelo nuntiante,
  - Christi Filii tui incarnationem cognovimus,
  - per passionem eius et crucem 
  - ad resurrectionis gloriam perducamur.
  - Per eumdem Christum Dominum nostrum. Amen.
notes: |
  > Lorem ipsum dolor sit amet, at illum vitae per. Te unum dissentiunt ius. 
  His decore similique conceptam cu. Vim magna appellantur cu. 
  His ut amet dissentiet, ad pri verterem instructior, vix id tale adipisci.
  > 
  > Ea duo populo labore officiis, sit labore fabulas et, ex dicunt nominati has. 
  Novum labores euripidis cum id, et his porro praesent. No his solet salutandi. 
  His percipit adipiscing cu. An mea quot facilisi neglegentur, et quodsi 
  conclusionemque sea. Mei ei hinc dolorem quaestio, iriure laoreet accumsan quo te.
```