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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.rork.sergolfandroid.data.BookedEventsManager
import com.rork.sergolfandroid.data.EventType
import com.rork.sergolfandroid.data.GolfEvent
import com.rork.sergolfandroid.ui.theme.AppColors

@Composable
fun EventDetailScreen(
    event: GolfEvent?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    if (event == null) {
        Box(modifier = modifier.fillMaxSize().background(AppColors.BackgroundDark), contentAlignment = Alignment.Center) {
            Text("Event not found", color = AppColors.TextMuted, fontSize = 16.sp)
        }
        return
    }

    val isSpecial = event.type == EventType.SPECIAL
    val isCancelled = event.cancelled
    var booked by remember { mutableStateOf(BookedEventsManager.isBooked(event.id)) }
    val bookedInteractionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize().background(AppColors.BackgroundDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(modifier = Modifier.fillMaxSize().background(Color(0x590A140A)))
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x99000000)),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AppColors.White)
                }
                if (isCancelled) {
                    DetailBadge("CANCELLED", AppColors.Error, AppColors.White,
                        { Icon(Icons.Filled.Block, null, tint = AppColors.White, modifier = Modifier.size(14.dp)) },
                        Modifier.align(Alignment.TopEnd).padding(16.dp))
                } else if (isSpecial) {
                    DetailBadge("Special Event", AppColors.Gold, AppColors.BackgroundDark,
                        { Icon(Icons.Filled.Star, null, tint = AppColors.BackgroundDark, modifier = Modifier.size(14.dp)) },
                        Modifier.align(Alignment.TopEnd).padding(16.dp))
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = event.title,
                    color = if (isCancelled) AppColors.TextSecondary else AppColors.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = if (isCancelled) androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle.Default,
                )

                if (isCancelled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1FE57373))
                            .border(1.dp, AppColors.Error, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Block, null, tint = AppColors.Error, modifier = Modifier.size(18.dp))
                        Column {
                            Text("This event has been cancelled", color = AppColors.Error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Please check Bookwhen for the latest information.", color = AppColors.TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoCard(Icons.Filled.CalendarMonth, "Date", "${event.dayOfWeek}, ${event.date}")
                    InfoCard(Icons.Filled.Schedule, "Time", event.time)
                    InfoCard(Icons.Filled.LocationOn, "Location", event.location)
                    InfoCard(Icons.Filled.Group, "Availability", event.spotsInfo)
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ABOUT THIS EVENT", color = AppColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(event.description, color = AppColors.TextSecondary, fontSize = 15.sp, lineHeight = 23.sp)
                }

                Spacer(Modifier.height(100.dp))
            }
        }

        // Bottom booking bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(AppColors.DarkGreen)
                .border(1.dp, AppColors.CardBorder)
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 30.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.location, color = AppColors.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(event.date, color = AppColors.TextMuted, fontSize = 13.sp)
                }
                if (isCancelled) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, AppColors.Error, RoundedCornerShape(24.dp))
                            .padding(horizontal = 20.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Filled.Block, null, tint = AppColors.Error, modifier = Modifier.size(16.dp))
                        Text("CANCELLED", color = AppColors.Error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (booked) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(AppColors.Success)
                                .combinedClickable(
                                    interactionSource = bookedInteractionSource,
                                    indication = null,
                                    onClick = {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, event.bookingUrl.toUri()),
                                        )
                                    },
                                    onLongClick = {
                                        BookedEventsManager.removeBooked(event.id)
                                        booked = false
                                    },
                                )
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = AppColors.White, modifier = Modifier.size(16.dp))
                            Text("Booked", color = AppColors.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "Long press to reset",
                            color = AppColors.TextMuted,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp, end = 2.dp),
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(AppColors.Gold)
                            .clickable {
                                BookedEventsManager.addBooked(event.id)
                                booked = true
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, event.bookingUrl.toUri()),
                                )
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Book Now", color = AppColors.BackgroundDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = AppColors.BackgroundDark, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.CardGreen)
            .border(1.dp, AppColors.CardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, null, tint = AppColors.Gold, modifier = Modifier.size(20.dp))
        Column {
            Text(label.uppercase(), color = AppColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
            Text(value, color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DetailBadge(text: String, bg: Color, fg: Color, icon: @Composable () -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        icon()
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
