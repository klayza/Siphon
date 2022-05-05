package com.example.siphon

import android.graphics.Color
import android.graphics.drawable.Icon
import android.media.Image
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.siphon.ui.theme.SiphonTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiphonTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                )
                {
                    // Box(modifier = Modifier.fillMaxSize().background(color = Color.DarkGray))
                    ToolBar()
                    TextInput()
                    CoilImage()
                }
            }
        }
    }
}

@Composable
fun CoilImage(url: String = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mordeo.org%2Ffiles%2Fuploads%2F2020%2F01%2FPine-Red-Trees-Road-4K-Ultra-HD-Mobile-Wallpaper.jpg&f=1&nofb=1") {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(data = url)
        // Stretches image
        Image(modifier = Modifier.fillMaxSize(), painter = painter, contentDescription = "Image Description", contentScale = ContentScale.Fit)
    }
}

@Composable
fun TextInput() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        label = { Text("Enter a directory: ")},
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,

    )
    if (URLUtil.isValidUrl(text)){
        ScrapeImages(text)
    }
}

@Composable
fun ToolBar() {
    Column {
        TopAppBar(
            elevation = 4.dp,
            title = { Text("Siphon") },
            backgroundColor = MaterialTheme.colors.primarySurface,
            navigationIcon = {
                IconButton(onClick = {/* Do Something*/ }) {
                    Icon(Icons.Filled.Menu, null)
                }
            },
            actions = {
                IconButton(onClick = {/* Do Something*/ }) {
                    Icon(Icons.Filled.Settings, null)
                }
            })
    }
}

@Composable
fun ScrapeImages(url: String="http://www.irtc.org/ftp/pub/stills/") {
    var Images = emptyList<String>()

    "http://httpbin.org/get".httpGet().response { request, response, result ->

        // Need to change to find files instead of an extension
        if (url.endsWith(".jpg") || url.endsWith(".png")) {
            CoilImage(url)
        }
    }
}







