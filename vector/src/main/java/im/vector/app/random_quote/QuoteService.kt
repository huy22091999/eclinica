package im.vector.app.random_quote

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

interface QuoteService {
    @GET("random?count=1")
    fun getRandomQuote(): Call<QuoteResponse>
}

class QuoteResponse {
    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("count")
    var count: Int = 0

    @SerializedName("quotes")
    var quotes = ArrayList<Quote>()
}

class Quote {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("author")
    var author: String? = null

    @SerializedName("tag")
    var tag: String? = null
}
