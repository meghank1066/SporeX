package com.example.sporex_app.ui.community

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.sporex_app.network.PostResponse
import java.time.ZoneId
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun PostResponse.toCommunityPost(): CommunityPost {
    return CommunityPost(
        id = id.hashCode(),
        author = user_name,
        content = content,
        timestamp = formatTimestamp(created_at),
        comments = replies.mapIndexed { index, reply ->
            Comment(
                id = index + 1,
                author = reply.user_name,
                content = reply.content
            )
        }.toMutableList()
    )
}

fun formatBackendDate(raw: String): String {
    return raw
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestamp(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Just now"

    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val postTime = LocalDateTime.parse(dateString, formatter)
            .atZone(ZoneOffset.UTC)

        val now = java.time.ZonedDateTime.now(ZoneId.systemDefault())
        val postLocal = postTime.withZoneSameInstant(ZoneId.systemDefault())

        val duration = Duration.between(postLocal, now)

        when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> postLocal.toLocalDate().toString()
        }
    } catch (e: Exception) {
        dateString
    }
}