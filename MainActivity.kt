package com.safetube.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandRed = Color(0xFFE62117)
private val Ink = Color(0xFF171717)
private val Muted = Color(0xFF6B7280)
private val SurfaceSoft = Color(0xFFF3F4F6)

data class Channel(
    val id: String,
    val name: String,
    val description: String,
    val color: Color
)

data class Video(
    val id: String,
    val channelId: String,
    val title: String,
    val views: String,
    val age: String,
    val duration: String,
    val thumbnailLabel: String,
    val color: Color
)

private val demoChannels = listOf(
    Channel("muslim-tv", "Muslim TV", "ইসলামিক আলোচনা ও কুরআন তিলাওয়াত", Color(0xFF087F70)),
    Channel("ahle-hadith", "Ahle Hadith Media", "ইসলামিক শিক্ষা ও লেকচার", Color(0xFF183B66)),
    Channel("nature-bangla", "Nature Bangla", "প্রকৃতি ও ভ্রমণ", Color(0xFF4C8B27)),
    Channel("tech-bangla", "Tech Bangla", "প্রযুক্তি বিষয়ক ভিডিও", Color(0xFF6554C0))
)

private val demoVideos = listOf(
    Video("demo1", "muslim-tv", "সূরা ইয়াসিন | সম্পূর্ণ তিলাওয়াত", "১.৮M views", "২ months ago", "42:17", "সূরা ইয়াসিন", Color(0xFF0E6655)),
    Video("demo2", "ahle-hadith", "জীবনে সফল হতে যা করবেন | মোটিভেশনাল লেকচার", "২.৫M views", "১ year ago", "15:23", "জীবনে সফল হতে", Color(0xFF174A83)),
    Video("demo3", "nature-bangla", "প্রকৃতির অপরূপ সৌন্দর্য | নেচার ভিডিও", "950K views", "১ year ago", "12:36", "Nature Beauty", Color(0xFF287A45)),
    Video("demo4", "tech-bangla", "মোবাইলের প্রয়োজনীয় কিছু সেটিংস", "320K views", "৩ weeks ago", "08:41", "Tech Tips", Color(0xFF5141A5))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SafeTubeApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeTubeApp() {
    var subscribed by remember { mutableStateOf(setOf("muslim-tv", "ahle-hadith")) }
    var page by remember { mutableStateOf("Home") }
    var query by remember { mutableStateOf("") }
    var selectedVideo by remember { mutableStateOf<Video?>(null) }
    val context = LocalContext.current

    val subscribedChannels = demoChannels.filter { it.id in subscribed }
    // Strict client-side allow-list: only videos whose channel ID is subscribed can enter Home.
    val homeVideos = demoVideos.filter { it.channelId in subscribed }
        .sortedByDescending { demoVideos.indexOf(it) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandRed,
            onPrimary = Color.White,
            background = Color.White,
            surface = Color.White,
            onSurface = Ink
        )
    ) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    NavItem("Home", page, { page = "Home" }, Icons.Default.Home)
                    NavItem("Channels", page, { page = "Channels" }, Icons.Default.Subscriptions)
                    NavItem("Library", page, { page = "Library" }, Icons.Default.VideoLibrary)
                    NavItem("Settings", page, { page = "Settings" }, Icons.Default.Settings)
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Header(
                    query = query,
                    onQuery = { query = it },
                    onSearch = { page = "Add Channels" }
                )
                when (page) {
                    "Home" -> HomePage(homeVideos, subscribedChannels.size, onVideo = { selectedVideo = it })
                    "Channels" -> ChannelsPage(subscribedChannels, onRemove = { id -> subscribed = subscribed - id })
                    "Add Channels" -> AddChannelsPage(
                        query = query,
                        onQuery = { query = it },
                        subscribed = subscribed,
                        onToggle = { id ->
                            subscribed = if (id in subscribed) subscribed - id else subscribed + id
                        }
                    )
                    "Library" -> InfoPage("আপনার লাইব্রেরি", "পরে দেখার জন্য ভিডিও এখানে সংরক্ষণ করা যাবে।")
                    else -> InfoPage("Settings", "Safe Tube-এর সেটিংস এখানে থাকবে।")
                }
            }
        }

        selectedVideo?.let { video ->
            AlertDialog(
                onDismissRequest = { selectedVideo = null },
                title = { Text(video.title, fontWeight = FontWeight.Bold) },
                text = {
                    Text("এটি একটি ডেমো ভিডিও কার্ড। লাইভ YouTube ভিডিও চালাতে YouTube API ও অনুমোদিত player integration যুক্ত করতে হবে।")
                },
                confirmButton = {
                    TextButton(onClick = { selectedVideo = null }) { Text("ঠিক আছে") }
                }
            )
        }
    }
}

@Composable
private fun NavItem(label: String, selected: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    NavigationBarItem(
        selected = selected == label,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BrandRed,
            selectedTextColor = BrandRed,
            indicatorColor = Color(0xFFFFE8E6),
            unselectedIconColor = Ink,
            unselectedTextColor = Muted
        )
    )
}

@Composable
private fun Header(query: String, onQuery: (String) -> Unit, onSearch: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 18.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(BrandRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Safe Tube", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                Text("Only your subscribed channels", fontSize = 11.sp, color = Muted)
            }
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Ink)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            placeholder = { Text("চ্যানেল বা ভিডিও খুঁজুন", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun HomePage(videos: List<Video>, channelCount: Int, onVideo: (Video) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("আপনার ভিডিও", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("$channelCountটি চ্যানেল", fontSize = 12.sp, color = Muted)
        }
        if (videos.isEmpty()) {
            EmptyState("এখনো কোনো চ্যানেল যুক্ত করা হয়নি", "Channels থেকে আপনার পছন্দের চ্যানেল যুক্ত করুন।")
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                items(videos, key = { it.id }) { video -> VideoCard(video, onClick = { onVideo(video) }) }
            }
        }
    }
}

@Composable
private fun VideoCard(video: Video, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Box(
            Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(14.dp)).background(video.color),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                Text(video.thumbnailLabel, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                video.duration,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).clip(RoundedCornerShape(5.dp))
                    .background(Color.Black.copy(alpha = 0.8f)).padding(horizontal = 7.dp, vertical = 3.dp),
                color = Color.White,
                fontSize = 11.sp
            )
        }
        Spacer(Modifier.height(9.dp))
        Row(verticalAlignment = Alignment.Top) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(video.color), contentAlignment = Alignment.Center) {
                Text(video.channelId.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(video.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${demoChannels.firstOrNull { it.id == video.channelId }?.name ?: "Channel"}  •  ${video.views}  •  ${video.age}", fontSize = 11.sp, color = Muted, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ChannelsPage(channels: List<Channel>, onRemove: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("My Channels", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("আপনি যেসব চ্যানেল যুক্ত করেছেন", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        if (channels.isEmpty()) EmptyState("কোনো চ্যানেল নেই", "Add Channels থেকে চ্যানেল যুক্ত করুন।")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(channels, key = { it.id }) { channel ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SurfaceSoft).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(channel.color), contentAlignment = Alignment.Center) {
                        Text(channel.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(channel.name, fontWeight = FontWeight.Bold)
                        Text(channel.description, color = Muted, fontSize = 11.sp)
                    }
                    TextButton(onClick = { onRemove(channel.id) }) { Text("Remove", color = BrandRed) }
                }
            }
        }
    }
}

@Composable
private fun AddChannelsPage(query: String, onQuery: (String) -> Unit, subscribed: Set<String>, onToggle: (String) -> Unit) {
    val filtered = demoChannels.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("Add Channels", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("ডেমো তালিকা থেকে চ্যানেল যুক্ত বা বাদ দিন", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("চ্যানেলের নাম লিখুন") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { channel ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(SurfaceSoft).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(channel.color), contentAlignment = Alignment.Center) {
                        Text(channel.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(channel.name, fontWeight = FontWeight.Bold)
                        Text(channel.description, color = Muted, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onToggle(channel.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (channel.id in subscribed) Color(0xFFE5E7EB) else BrandRed),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (channel.id in subscribed) "Added" else "Add", color = if (channel.id in subscribed) Ink else Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Subscriptions, contentDescription = null, tint = BrandRed, modifier = Modifier.size(42.dp))
        Spacer(Modifier.height(10.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Text(message, color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun InfoPage(title: String, message: String) {
    Column(Modifier.fillMaxSize().padding(22.dp)) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, color = Muted)
    }
}
