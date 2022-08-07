package im.vector.app.random_quote

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import im.vector.app.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RandomQuoteActivity : AppCompatActivity() {

    private var quoteView: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Globits", "-->Hallo from activity 1")

        setContentView(R.layout.activity_random_quote)

        quoteView = findViewById(R.id.textView1)
        quoteView!!.text = "loading..."

        getRandomQuote()

        // random quote button
        findViewById<Button>(R.id.btnGetRandomQuote).setOnClickListener {
            getRandomQuote()
        }

        // back button
        findViewById<Button>(R.id.btnGoHome).setOnClickListener {
            finish()
        }
    }

    internal fun getRandomQuote() {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(QuoteService::class.java)
        val call = service.getRandomQuote()
        call.enqueue(object : Callback<QuoteResponse> {
            override fun onResponse(call: Call<QuoteResponse>, response: Response<QuoteResponse>) {
                if (response.code() != 200) {
                    return
                }

                val quotes = response.body()!!.quotes
                if (quotes.size > 0) {
                    quoteView!!.text = quotes[0].text
                }

            }

            override fun onFailure(call: Call<QuoteResponse>, t: Throwable) {
                quoteView!!.text = t.message
            }

        })
    }

    companion object {
        val baseUrl = "https://goquotes-api.herokuapp.com/api/v1/"
    }
}
