# Prayer file format

Prayers are stored in [Yaml format](https://en.wikipedia.org/wiki/YAML). It means the file has a specific and strict structure. This is to make it possible for the app to display prayers line by line with translation interlines of the prayers.

The prayer content can be formatted using [Markdown](https://www.markdownguide.org). However, some sophisticated formatting or markdown tags may cause unexpected result in the displayed content. Therefore, it is recommended to avoid overusing formatting. Typically, you want to use __bold__ or _italic_ to highlight some parts of the text.

Except the prayer content, there is some additional information required and some optional. So, please pay attention to include all required information, otherwise the prayer will not load in the app.

## Basic prayer information

Mandatory prayer information:
1. __title__ is the prayer title in the same language as the prayer content
2. __lang__ is the 2-letter code of the prayer language, for example: __en__
3. __language__ is the full name of the prayer language. The language name must be in the native language, for example: __Español__
4. __lines__ is the prayer content split into individual lines. It does not matter how the prayer is split. However, what matters is that in all languages the prayer must be split the same way to translation displayed inline makes sense and matches the main prayer content. As the prayer is displayed on small devices like mobile phones, each line should be relatively short. It easier to read and pray if each line is a full sentence or part of the sentence that makes sense on its own.

## Prayer file example

```yaml
title: Ave Maria
lang: la
language: Latina
lines:
  - Ave Maria, gratia plena
  - Dominus tecum.
  - Benedicta Tu in mulieribus,
  - et benedictus fructus ventris tui, Iesus.
  - Sancta Maria, Mater Dei,
  - ora pro nobis peccatoribus,
  - nunc, et in hora mortis nostrae.
```

## Additional and advanced options

### Content formatting

In the example above we can update the prayer content with some text formatting using [Markdown](https://www.markdownguide.org). While most of markdown formatting is supported and should work I highly recommend to avoid overusing formatting. Some formatting, like links in the prayer content while they work, they may cause displaying issue.

Also, some formatting tags may require to put content in quotes. Here is an example:

```yaml
title: Ave Maria
lang: la
language: Latina
lines:
  - __Ave Maria__, gratia plena
  - Dominus tecum.
  - Benedicta Tu in mulieribus,
  - et benedictus fructus ventris tui, __Iesus__.
  - "**Sancta Maria**, Mater Dei,"
  - ora pro nobis peccatoribus,
  - nunc, et in hora mortis nostrae.
```
This will result in the following prayer formatting:
> __Ave Maria__, gratia plena<br/>
> Dominus tecum.<br/>
> Benedicta Tu in mulieribus,<br/>
> et benedictus fructus ventris tui, __Iesus__.<br/>
> **Sancta Maria**, Mater Dei,<br/>
> ora pro nobis peccatoribus,<br/>
> nunc, et in hora mortis nostrae.<br/>

Please note, when stars: '\*' are used for formatting, the line must be in quotes.

### Advanced and optional prayer information

1. __tags__ allows to assign tags or labels to the prayer which is then used for prayer grouping on the list. This helps with finding prayers on the long list in the app.<br/>
   Example:
   ```yaml
   tags:
     - Basic
     - Blessed Virgin Mary
   ```
2. __links__ allows to provide links related to the prayer. Right now, only links to `youtube` are allowed and supported. Adding links, will display links below the prayer content.<br/>
   Example:
    ```yaml
    links:
      - type: youtube
        title: Ave Maria by Luciano Pavarotti
        url: https://www.youtube.com/watch?v=XpYGgtrMTYs
    ```
3. __notes__ allows to add additional information about the prayer which is then displayed below the prayer and links in the app. Notes tag allows to add free form content, formatted using markdown tags.<br/>
   Example:
   ```yaml
   notes: |
     The prayer, also known as the Hail Mary, is a central devotion in the Roman Catholic Church. 
     The prayer has two parts, with the first part coming from the Gospel of Luke and the second 
     part added in the 15th century. It is commonly recited in private and in public, 
     and is said 150 times as part of the Rosary.
   ```
4. __dates__ allows you to specify dates for the prayer. These are dates for which the prayer is recommended. For example, _Joyful Rosary Mysteries_ are recommended for: _Monday_, _Thursday_ and _Saturday_. _Litany of the Sacred Heart of Jesus_ is recommended for the month of _June_ and so on.<br/>
   The format for putting dates is based on `crontab` file. You can find a very good description on the syntax [here](https://www.baeldung.com/cron-expressions).<br/>
   __This is not yet implemented in the app.__<br/>
   Example:
    ```yaml
    dates:
       - "* * * * 1,4,6"
       - "* * * 6 *"
    ```

### Non standard formatting and advanced content techniques

There are some non-standard formatting tags which are not defined in Markdown spec but available to use while preparing prayer file.

#### Embedding prayers

Some prayers contain other prayers text as part of it content. For example prayer _The Angelus Prayer_ _(Angelus Domini)_:

> __Angelus Domini nuntiavit Mariae__<br/>
>  __Et concepit de Spiritu Sancto.__<br/>
>      Ave Maria, gratia plena<br/>
>      Dominus tecum.<br/>
>      Benedicta Tu in mulieribus,<br/>
>      et benedictus fructus ventris tui, Iesus.<br/>
>      Sancta Maria, Mater Dei,<br/>
>      ora pro nobis peccatoribus,<br/>
>      nunc, et in hora mortis nostrae.<br/>
>  <br/>
>  __Ecce ancilla Domini,__<br/>
>  __Fiat mihi secundum verbum tuum.__<br/>
>      Ave Maria, gratia plena<br/>
>      Dominus tecum.<br/>
>      Benedicta Tu in mulieribus,<br/>
>      et benedictus fructus ventris tui, Iesus.<br/>
>      Sancta Maria, Mater Dei,<br/>
>      ora pro nobis peccatoribus,<br/>
>      nunc, et in hora mortis nostrae.<br/>
>  <br/>
>  __Et Verbum caro factum est,__<br/>
>  __Et habitavit in nobis.__<br/>
>      Ave Maria, gratia plena<br/>
>      Dominus tecum.<br/>
>      Benedicta Tu in mulieribus,<br/>
>      et benedictus fructus ventris tui, Iesus.<br/>
>      Sancta Maria, Mater Dei,<br/>
>      ora pro nobis peccatoribus,<br/>
>      nunc, et in hora mortis nostrae.<br/>
>

As you can see _Ave Maria_ is included several times as part of the prayer. We could prepare the prayer file like this with _Ave Maria_ text included as many times as necessary but this solution comes with a few problems:
1. It is a lot of work to include the same prayer many times
2. It takes a lot of space
3. If you want to change something in the included prayer you have to do it multiple times
4. Mistakes are easy to make and difficult to find and correct

For this reason we have _prayer embed_ tag: __'\@prayerfile'__ which allows you to point to a file from which a prayer must be included in the content. For _The Angelus Prayer_ the file can look like this:

```yaml
lines:
  - The Angel of the Lord declared unto Mary.
  - Et concepit de Spiritu Sancto.
  - "@avemaria"
  - 
  - Behold the handmaid of the Lord.
  - Be it done unto me according to thy word.
  - "@avemaria"
  - 
  - And the Word was made flesh.
  - And dwelt among us.
  - "@avemaria"
```

This makes the file much smaller and overall content much shorter and easier to manage. When the app displays the prayer it automatically includes the _Ave Maria_ prayer from specified file and includes it in the content of the main prayer.

This tag has a variation: __'\@@prayerfile'__, note the double __'\@@'__. In this variant, the included prayer title is also added to the content. With a single __'\@`__ only the prayer text is added. This variations is sometimes useful when creating prayers lists.

#### Empty lines

Normally when we prepare prayer file like in previous example, the empty lines are removed from the rendered text. And all the text is displayed line under a lines. This sometimes makes it harder to read the text. For readability it makes sense to add an empty line to the displayed text, to separate sections of the content. To add an empty line which is displayed you can use __^^^__ tag.

Using this, the above example looks like this:

```yaml
lines:
  - The Angel of the Lord declared unto Mary.
  - Et concepit de Spiritu Sancto.
  - "@avemaria"
  - ^^^
  - Behold the handmaid of the Lord.
  - Be it done unto me according to thy word.
  - "@avemaria"
  - ^^^
  - And the Word was made flesh.
  - And dwelt among us.
  - "@avemaria"
```

For more practical examples, formatting and tags, please take a look at existing prayers files.