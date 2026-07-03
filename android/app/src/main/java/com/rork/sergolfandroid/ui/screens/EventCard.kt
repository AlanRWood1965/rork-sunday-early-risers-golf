package com.rork.sergolfandroid.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.rork.sergolfandroid.data.BookedEventsManager
import com.rork.sergolfandroid.data.EventType
import com.rork.sergolfandroid.data.FALLBACK_IMAGE
import com.rork.sergolfandroid.data.GolfEvent
import com.rork.sergolfandroid.ui.theme.AppColors

@Composable
fun EventCard(
    event: GolfEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isSpecial = event.type == EventType.SPECIAL
    val isCancelled = event.cancelled
    val booked = BookedEventsManager.isBooked(event.id)
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.CardGreen)
            .border(
                width = if (isCancelled) 1.dp else if (isSpecial) 1.5.dp else 1.dp,
                color = if (isCancelled) AppColors.Error else if (isSpecial) AppColors.GoldDark else AppColors.CardBorder,
                shape = RoundedCornerShape(16.dp),
            )
            .alpha(if (isCancelled) 0.9f else 1f)
            .clickable { onClick() },
    ) {
        Box {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                fallback = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .alpha(if (isCancelled) 0.5f else 1f),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0x4D0A140A)),
            )
            if (isCancelled) {
                Badge(
                    text = "Cancelled",
                    bg = AppColors.Error,
                    fg = AppColors.White,
                    icon = { Icon(Icons.Filled.Block, null, tint = AppColors.White, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                )
            } else if (isSpecial) {
                Badge(
                    text = "Special Event",
                    bg = AppColors.Gold,
                    fg = AppColors.BackgroundDark,
                    icon = { Icon(Icons.Filled.Star, null, tint = AppColors.BackgroundDark, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                )
            }
        }

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = event.title,
                color = AppColors.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = if (isCancelled) {
                    androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough, color = AppColors.TextSecondary)
                } else androidx.compose.ui.text.TextStyle.Default,
            )

            IconText(Icons.Filled.CalendarMonth, "${event.dayOfWeek}, ${event.date}")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconText(Icons.Filled.LocationOn, event.location)
                IconText(Icons.Filled.Schedule, event.time)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.spotsInfo,
                    color = if (isCancelled) AppColors.Error else AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontStyle = if (isCancelled) FontStyle.Normal else FontStyle.Italic,
                    fontWeight = if (isCancelled) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (isCancelled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, AppColors.Error, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text("CANCELLED", color = AppColors.Error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (booked) {
                    // Green "Booked" pill with long-press to reset
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(AppColors.Success)
                                .combinedClickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        // Open the booking page in browser
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, event.bookingUrl.toUri()),
                                        )
                                    },
                                    onLongClick = {
                                        // Reset booked state
                                        BookedEventsManager.removeBooked(event.id)
                                    },
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = AppColors.White, modifier = Modifier.size(13.dp))
                            Text("BOOKED", color = AppColors.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        }
                        Text(
                            "Long press to reset",
                            color = AppColors.TextMuted,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(top = 3.dp, end = 2.dp),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppColors.Gold)
                            .padding(horizontal = 18.dp, vertical = 7.dp),
                    ) {
                        Text("Book", color = AppColors.BackgroundDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun IconText(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = AppColors.Gold, modifier = Modifier.size(14.dp))
        Text(text, color = AppColors.TextSecondary, fontSize = 13.sp)
    }
}

@Composable
private fun Badge(
    text: String,
    bg: Color,
    fg: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon()
        Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
