package com.rork.sergolfandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.sergolfandroid.data.EventType
import com.rork.sergolfandroid.data.GolfEvent
import com.rork.sergolfandroid.ui.EventsViewModel
import com.rork.sergolfandroid.ui.theme.AppColors

private const val CUSTOMER_PORTAL_URL = "https://my.bookwhen.com"

private enum class FilterType { ALL, WEEKLY, SPECIAL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(FilterType.ALL) }

    val filtered = when (filter) {
        FilterType.ALL -> state.events
        FilterType.WEEKLY -> state.events.filter { it.type == EventType.WEEKLY }
        FilterType.SPECIAL -> state.events.filter { it.type == EventType.SPECIAL }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BackgroundDark),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        ) {
            item { HeroHeader() }

            if (state.specialEvents.isNotEmpty()) {
                item { SectionLabel("FEATURED", topPadding = 20.dp) }
                items(state.specialEvents, key = { "featured-${it.id}" }) { event ->
                    EventCard(event = event, onClick = { onEventClick(event.id) })
                }
            }

            item {
                SectionLabel("UPCOMING MATCHES", topPadding = 12.dp)
                FilterRow(filter) { filter = it }
            }

            items(filtered, key = { it.id }) { event ->
                EventCard(event = event, onClick = { onEventClick(event.id) })
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyState(
                        isLoading = state.isLoading,
                        error = state.errorMessage,
                        onRetry = { viewModel.load(initial = true) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroHeader() {
    val context = androidx.compose.ui.platform.LocalContext.current
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=1200&q=80",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0x8C0A140A)))

        // My Bookings pill — top-right, opens Bookwhen Customer Portal
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xD90A140A))
                .border(1.dp, AppColors.GoldDark, RoundedCornerShape(20.dp))
                .clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, CUSTOMER_PORTAL_URL.toUri()),
                    )
                }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Filled.AccountCircle, "My Bookings", tint = AppColors.Gold, modifier = Modifier.size(16.dp))
            Text("My Bookings", color = AppColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        ) {
            AsyncImage(
                model = "https://glasgowgolfclub.com/wp-content/uploads/2025/08/Glasgow-Golf-Club-Logo-Flat-White.png",
                contentDescription = "Glasgow Golf Club",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(64.dp).padding(bottom = 10.dp),
            )
            Text("SUNDAY EARLY RISERS", color = AppColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp)
            Spacer(Modifier.height(4.dp))
            Text("Glasgow Golf Club", color = AppColors.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Killermont", color = AppColors.TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        color = AppColors.Gold,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 16.dp, top = topPadding, bottom = 12.dp),
    )
}

@Composable
private fun FilterRow(selected: FilterType, onSelect: (FilterType) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterType.entries.forEach { f ->
            val active = f == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) AppColors.Gold else AppColors.CardGreen)
                    .border(1.dp, if (active) AppColors.Gold else AppColors.CardBorder, RoundedCornerShape(16.dp))
                    .clickable { onSelect(f) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = when (f) {
                        FilterType.ALL -> "All"
                        FilterType.WEEKLY -> "Weekly"
                        FilterType.SPECIAL -> "Special"
                    },
                    color = if (active) AppColors.BackgroundDark else AppColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(isLoading: Boolean, error: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = AppColors.Gold)
                Text("Loading events…", color = AppColors.TextMuted, fontSize = 15.sp)
            }
            error != null -> {
                Text("Couldn't load events", color = AppColors.TextMuted, fontSize = 15.sp, textAlign = TextAlign.Center)
                Text(error, color = AppColors.TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Gold)
                        .clickable { onRetry() }
                        .padding(horizontal = 20.dp, vertical = 9.dp),
                ) {
                    Text("Retry", color = AppColors.BackgroundDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            else -> Text("No upcoming events", color = AppColors.TextMuted, fontSize = 15.sp)
        }
    }
}
