Udacity Android Developer Nanodegree - Project 2 & 3

The Movie Database API Key should be added to **gradle.properties** file

```gradle
API_KEY="[TMDB API KEY HERE]"
```

Eventualy change constatnt in **TmdbClient.java** file:

```java
private static final String API_KEY = "[TMDB API KEY HERE]";
```

Hiding API key solution based on blogpost:

["Hiding secret api keys from git" blogpost by Richard Rose](https://richardroseblog.wordpress.com/2016/05/29/hiding-secret-api-keys-from-git/)

MovieProvider (ContentProvider) implementation is based on Sunshine app realized during Android Nanodegree Scholarship Challenge and available here:

[piotrserafin/ud851-Sunshine](https://github.com/piotrserafin/ud851-Sunshine.git)

