package u.ficappx.ui.components.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier,
    visiblePageRange: Int = 1
){
    if (totalPages <= 1) return

    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        IconButton(onClick = {if (currentPage > 1) onPageChange(currentPage - 1)}, modifier = Modifier.defaultMinSize(40.dp)) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                "",
                tint = if (currentPage > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        var pagesToDispay = mutableSetOf<Int>()
        pagesToDispay.add(1)
        for(i in (currentPage - visiblePageRange)..(currentPage + visiblePageRange)){
            if(i in 1..totalPages) pagesToDispay.add(i)
        }
        pagesToDispay.add(totalPages)
        pagesToDispay = pagesToDispay.sorted().toMutableSet()
        var lastPage = 0
        pagesToDispay.forEach { page ->
            if (page - lastPage > 1){
                Text("...")
            }
            PageNumberButton(
                page = page,
                isSelected = page == currentPage,
                onClick = onPageChange
            )
            lastPage = page
        }

        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages,
            modifier = Modifier.defaultMinSize(40.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Следующая страница",
                tint = if (currentPage < totalPages) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}


@Composable
private fun PageNumberButton(
    page: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 200), label = "PageButtonBackgroundColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 200), label = "PageButtonTextColor"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick(page) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = page.toString(),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}