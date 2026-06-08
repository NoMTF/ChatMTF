@file:Suppress("DEPRECATION")

package com.inspiredandroid.kai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

val softBlue = Color(0xFF6F9FD8)
val softPink = Color(0xFFE7A8C5)
val deepBlue = Color(0xFF2F5E8F)
val gradientBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(softBlue, softPink))

// Animated border gradient colors
val gradientPurple = Color(0xFF6F9FD8)
val gradientViolet = Color(0xFFAFCBEA)
val gradientMagenta = Color(0xFFE7A8C5)

fun Modifier.handCursor() = pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFAFCBEA),
    onPrimary = Color(0xFF0F1D2B),
    primaryContainer = Color(0xFF244865),
    onPrimaryContainer = Color(0xFFEAF3FF),
    secondary = Color(0xFFE7A8C5),
    onSecondary = Color(0xFF2B1722),
    secondaryContainer = Color(0xFF5D3949),
    onSecondaryContainer = Color(0xFFFFEAF3),
    surface = Color(0xFF161A1F),
    surfaceVariant = Color(0xFF252B33),
    surfaceContainer = Color(0xFF1D232A),
    background = Color(0xFF101418),
    onBackground = Color(0xFFF5F7FA),
    onSurface = Color(0xFFF5F7FA),
    onSurfaceVariant = Color(0xFFCBD3DD),
    outline = Color(0xFF6D7886),
    outlineVariant = Color(0xFF37414D),
)

fun ColorScheme.withBlackBackground(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
)

val ColorScheme.isOledFlavor: Boolean get() = background == Color.Black

@Composable
fun kaiAdaptiveCardColors(): CardColors = CardDefaults.cardColors(
    containerColor = if (MaterialTheme.colorScheme.isOledFlavor) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    },
)

@Composable
fun kaiAdaptiveCardBorder(): BorderStroke? = if (MaterialTheme.colorScheme.isOledFlavor) {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
} else {
    null
}

@Composable
fun Modifier.kaiAdaptiveCardSurface(shape: Shape = CardDefaults.shape): Modifier = this
    .clip(shape)
    .background(
        if (MaterialTheme.colorScheme.isOledFlavor) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
    )
    .then(
        if (MaterialTheme.colorScheme.isOledFlavor) {
            Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
        } else {
            Modifier
        },
    )

val LightColorScheme = lightColorScheme(
    primary = deepBlue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEBFA),
    onPrimaryContainer = Color(0xFF14324F),
    secondary = Color(0xFFC56F99),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF8DDE9),
    onSecondaryContainer = Color(0xFF4D2436),
    tertiary = Color(0xFF7CA7A2),
    onTertiary = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF2F5F8),
    surfaceContainer = Color(0xFFF6F8FA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFBFC),
    surfaceContainerHigh = Color(0xFFEFF3F6),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF18212B),
    onSurface = Color(0xFF18212B),
    onSurfaceVariant = Color(0xFF64717F),
    outline = Color(0xFFB8C2CC),
    outlineVariant = Color(0xFFDDE5EC),
    error = Color(0xFFB3261E),
)

@Composable
fun outlineTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
)

@Composable
fun KaiOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        colors = outlineTextFieldColors(),
    )
}

@Composable
fun KaiClearableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    KaiOutlinedTextField(
        modifier = modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = singleLine,
        trailingIcon = {
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.handCursor()
                    .alpha(if (focused && value.isNotEmpty()) 1f else 0f),
                enabled = value.isNotEmpty(),
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Composable
@Preview
fun Theme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        content()
    }
}
