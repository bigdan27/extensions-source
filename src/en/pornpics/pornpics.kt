package eu.kanade.tachiyomi.extension.all.yournamehere

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.network.GET
import okhttp3.Request
import org.jsoup.nodes.Element
import eu.kanade.tachiyomi.network.as->

class YourSource : ParsedHttpSource() {

    // ==========================================================
    //            @START_OF_CHANGES_HERE
    //            These are the only things you need to change in this file.
    //            The rest of the code should not be edited.
    // ==========================================================

    // 1. BASIC INFORMATION
    // @NAME_CHANGE: Give your extension a name. This will appear in Mihon's list.
    override val name = "My Gallery Source"

    // @URL_CHANGE: Put the main website address here.
    override val baseUrl = "https://www.your-gallery-website.com"

    // @LANG_CHANGE: Set the language. Use "en" for English.
    override val lang = "en"

    // 2. FEATURE TOGGLES
    // @FEATURE_TOGGLE: Change this to 'true' to enable the "Latest" section.
    // If you ever want to disable it, just change it back to 'false'.
    override val supportsLatest = true

    // 3. LATEST UPDATES SECTION
    // @URL_CHANGE: Change the part of the URL that gets you to the latest content.
    // Example: if the latest page is "example.com/updates", change "/latest" to "/updates".
    override fun latestUpdatesRequest(page: Int): Request {
        return GET("$baseUrl/latest?page=$page", headers)
    }

    // @SELECTOR_CHANGE: This is the "container" code you found with the web inspector.
    // It's the big box that holds a single gallery item (image, title, link).
    // Example: "div.gallery-item"
    override fun latestUpdatesSelector(): String {
        return "div.gallery-item"
    }

    // @PARSING_CHANGE: This tells the code how to get the title, link, and image from each gallery item.
    // Change the code inside the parentheses to match the codes you found.
    override fun latestUpdatesFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.setUrlWithoutBaseUrl(element.select("a.gallery-link").first()!!.attr("href"))
        manga.title = element.select("div.gallery-title").text()
        manga.thumbnail_url = element.select("img.gallery-cover").first()?.attr("src")
        return manga
    }

    // @SELECTOR_CHANGE: This is the code for the "Next Page" button.
    // Example: "ul.pagination li a:contains(Next)"
    override fun latestUpdatesNextPageSelector(): String? {
        return "ul.pagination li a:contains(Next)"
    }

    // 4. SEARCH SECTION
    // @URL_CHANGE: This tells the code how to build a search URL.
    // Example: change "/search" and "?q=" to match the site's search URL.
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val searchUrl = "$baseUrl/search?q=$query&page=$page"
        return GET(searchUrl, headers)
    }

    // @SELECTOR_CHANGE: This should match the same "container" code as the latest section.
    override fun searchMangaSelector() = latestUpdatesSelector()

    // @PARSING_CHANGE: This should use the same parsing logic as the latest section.
    override fun searchMangaFromElement(element: Element): SManga {
        return latestUpdatesFromElement(element)
    }

    // @SELECTOR_CHANGE: This should use the same "Next Page" button code as the latest section.
    override fun searchMangaNextPageSelector() = latestUpdatesNextPageSelector()

    // 5. DETAILS PAGE
    // @PARSING_CHANGE: Change this to match the codes for the title, author, and description on the details page.
    override fun mangaDetailsParse(document: Element): SManga {
        val manga = SManga.create()
        manga.title = document.select("h1.gallery-title").text()
        manga.author = document.select("div.gallery-author").text()
        manga.description = document.select("div.gallery-description").text()
        manga.genre = ""
        return manga
    }

    // 6. CHAPTERS/PAGES SECTION
    // @SELECTOR_CHANGE: The code for the list of chapters or pages.
    override fun chapterListSelector(): String {
        return "div.page-list a"
    }

    // @PARSING_CHANGE: The code to get the link and name for each chapter/page.
    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.setUrlWithoutBaseUrl(element.attr("href"))
        chapter.name = "Page ${element.text()}"
        return chapter
    }

    // 7. IMAGES
    // @URL_CHANGE: The code to get the link to a specific page.
    override fun pageListRequest(chapter: SChapter): Request {
        return GET("$baseUrl${chapter.url}", headers)
    }

    // @PARSING_CHANGE: The code to get the link to the actual image.
    override fun pageListParse(document: Element): List<eu.kanade.tachiyomi.source.model.Page> {
        val pages = mutableListOf<eu.kanade.tachiyomi.source.model.Page>()
        document.select("img.page-image").forEachIndexed { i, element ->
            val url = element.attr("src")
            pages.add(eu.kanade.tachiyomi.source.model.Page(i, "", url))
        }
        return pages
    }

    // We can ignore this one for now.
    override fun imageUrlParse(document: Element) = ""

// ==========================================================
//            @END_OF_CHANGES_HERE
//            Do not edit anything below this line.
// ==========================================================
