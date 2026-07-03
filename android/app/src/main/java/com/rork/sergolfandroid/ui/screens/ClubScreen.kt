package com.rork.sergolfandroid.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.rork.sergolfandroid.ui.theme.AppColors

private const val CLUB_URL = "https://www.glasgowgolfclub.com"
private const val BOOKING_URL = "https://bookwhen.com/ser-golf"

@Composable
fun ClubScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=1200&q=80",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(modifier = Modifier.fillMaxSize().background(Color(0x990A140A)))
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = "https://glasgowgolfclub.com/wp-content/uploads/2025/08/Glasgow-Golf-Club-Logo-Flat-White.png",
                    contentDescription = "Glasgow Golf Club",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(60.dp).padding(bottom = 8.dp),
                )
                Text("Glasgow Golf Club", color = AppColors.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text("Killermont, Glasgow", color = AppColors.TextSecondary, fontSize = 14.sp)
            }
        }

        // SER badge section
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.CardGreen)
                .border(1.dp, AppColors.GoldDark, RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.EmojiEvents, null, tint = AppColors.Gold, modifier = Modifier.size(18.dp))
                Text("SUNDAY EARLY RISERS", color = AppColors.Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Text(
                "The Sunday Early Risers (SER's) are a friendly group of golfers who meet every Sunday morning at the beautiful Glasgow Golf Club Killermont course for breakfast and a competitive yet social round of golf.",
                color = AppColors.TextSecondary, fontSize = 14.sp, lineHeight = 22.sp,
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("ABOUT THE CLUB", color = AppColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            InfoCard(
                Icons.Filled.History,
                "Rich Heritage",
                "Glasgow Golf Club is the ninth oldest golf club in the world. Founded in 1787, the club operates both the historic Killermont parkland course and the prestigious Gailes Links, which has been chosen by the R&A for Open Championship final qualifying in 2027.",
            )
            InfoCard(
                Icons.Filled.LocationOn,
                "Killermont Course",
                "Set in the beautiful surroundings of Bearsden, the Glasgow Golf Club Killermont course offers a challenging yet enjoyable parkland experience and a stunning clubhouse.",
            )
            InfoCard(
                Icons.Filled.Group,
                "SER Community",
                "Our group organises weekly Sunday morning breakfast, friendly matches and special outings throughout the year.",
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("QUICK LINKS", color = AppColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            LinkCard(Icons.Filled.Language, "Club Website", "glasgowgolfclub.com") {
                context.startActivity(Intent(Intent.ACTION_VIEW, CLUB_URL.toUri()))
            }
            LinkCard(Icons.Filled.EmojiEvents, "Book an Event", "bookwhen.com/ser-golf") {
                context.startActivity(Intent(Intent.ACTION_VIEW, BOOKING_URL.toUri()))
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(
            "© 2026 Alan Wood",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.CardGreen)
            .border(1.dp, AppColors.CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(AppColors.BackgroundDark),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = AppColors.Gold, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = AppColors.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text, color = AppColors.TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun LinkCard(icon: ImageVector, title: String, url: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.CardGreen)
            .border(1.dp, AppColors.CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, null, tint = AppColors.Gold, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AppColors.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(url, color = AppColors.TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = AppColors.TextMuted, modifier = Modifier.size(16.dp))
    }
}
