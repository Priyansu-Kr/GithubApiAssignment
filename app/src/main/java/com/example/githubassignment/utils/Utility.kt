package com.example.githubassignment.utils
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.core.graphics.toColorInt

object Constants{
     val BASE_URL = "https://api.github.com/"

    private val colors = mapOf(
        "Kotlin" to "#A97BFF",
        "Java" to "#b07219",
        "Python" to "#3572A5",
        "JavaScript" to "#f1e05a",
        "TypeScript" to "#2b7489",
        "C++" to "#f34b7d",
        "C#" to "#178600",
        "PHP" to "#4F5D95",
        "HTML" to "#e34c26",
        "CSS" to "#563d7c",
        "Go" to "#00ADD8",
        "Swift" to "#ffac45",
        "Rust" to "#dea584",
        "Dart" to "#00B4AB",
        "Ruby" to "#701516",
        "Shell" to "#89e051",
        "C" to "#f762ed",
        "Markdown" to "#d05af4"
    )

    fun getColor(language: String?): Int {
        val colorHex = colors[language] ?: "#A2A4A6"
        return colorHex.toColorInt()
    }

    fun getFormattedText(text: String): SpannableString {

        val slashIndex = text.indexOf("/")

        if (slashIndex == -1) {
            return SpannableString(text)
        }

        return SpannableString(text).apply {

            setSpan(
                StyleSpan(Typeface.BOLD),

                slashIndex + 1,

                text.length,

                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}