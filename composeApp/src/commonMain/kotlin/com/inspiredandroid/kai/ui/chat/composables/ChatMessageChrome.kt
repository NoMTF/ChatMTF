package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.ui.softBlue
import com.inspiredandroid.kai.ui.softPink

internal enum class MessageAuthor {
    Assistant,
    User,
}

@Composable
internal fun ChatMessageChrome(
    author: MessageAuthor,
    nickname: String,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val isUser = author == MessageAuthor.User
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val avatarBackground = if (isUser) {
        Brush.linearGradient(listOf(Color(0xFFBFE1FF), Color(0xFFF8DDE9)))
    } else {
        Brush.linearGradient(listOf(softBlue.copy(alpha = 0.95f), softPink.copy(alpha = 0.9f)))
    }
    val bubbleColor = when {
        isDark && isUser -> Color(0xFF20384E)
        isDark -> Color(0xFF242B34)
        isUser -> Color(0xFFDCEEFF)
        else -> Color(0xFFFFF5FA)
    }
    val bubbleBorder = when {
        isDark && isUser -> Color(0xFF35536D)
        isDark -> Color(0xFF3B4652)
        isUser -> Color(0xFFC5DDF0)
        else -> Color(0xFFEED7E4)
    }
    val contentColor = when {
        isDark -> Color(0xFFE9EEF5)
        isUser -> Color(0xFF17324F)
        else -> Color(0xFF2B3340)
    }
    val nicknameColor = if (isDark) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        Color(0xFF6C7A88)
    }
    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (isUser) {
            Spacer(Modifier.weight(1f))
        } else {
            ChatAvatar(
                label = "C",
                background = avatarBackground,
                textColor = Color.White,
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 328.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        ) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = nicknameColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor, bubbleShape)
                    .border(1.dp, bubbleBorder, bubbleShape),
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
            }
            actions?.let {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
                ) {
                    it()
                }
            }
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            ChatAvatar(
                label = "我",
                background = avatarBackground,
                textColor = Color(0xFF22425F),
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ChatAvatar(
    label: String,
    background: Brush,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = textColor,
        )
    }
}
