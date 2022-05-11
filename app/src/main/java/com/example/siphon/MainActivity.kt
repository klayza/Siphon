package com.example.siphon

import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.siphon.ui.theme.SiphonTheme
import okhttp3.*
import java.io.IOException


// var images = mutableListOf<String>()
// line below is temp
var images = mutableListOf("http://www.irtc.org/ftp/pub/stills/1997-08-31/arevotma.jpg", "http://www.irtc.org/ftp/pub/stills/2006-12-31/rb_shoot.jpg", "http://www.irtc.org/ftp/pub/stills/2006-12-31/matches.jpg")
var cur_index = 0
var scrolledUp = false
var scrolledDown = false


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
    Box(contentAlignment = Alignment.Center) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        val painter = rememberImagePainter(data = url)
        // Stretches image
        Image(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()

                    val (x, y) = dragAmount
                    when {
                        y > 0 -> {
                            scrolledDown = true
                        }   // User swipes down
                        y < 0 -> {
                            scrolledUp = true; } // User swipes up
                    }

                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
            painter = painter,
            contentDescription = "Image Description",
            contentScale = ContentScale.Fit)

            Log.d("pak", "before if block. down: ${scrolledDown} up: ${scrolledUp}")
            if (scrolledUp) {
                Log.d("PAK", "running next img")
                nextImage()
            }
            else if (scrolledDown) {
                Log.d("PAK", "running back img")
                backImage()
            }
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
                IconButton(onClick = {  }) {
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
fun ScrapeImages(url: String) {

    // Need to change to find files instead of an extension
    if (".jpg" in url || ".png" in url)
    {
        Log.d("PAK", "scrape ${url}")
        CoilImage(url)
    }
    else {
        // val url = "http://www.irtc.org/ftp/pub/stills/2006-12-31/"      // Will need to be replaced
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string().toString()
                Log.d("PAK", body)

                // Find all elements on page
                val elements = body.split("<").toTypedArray()
                for (emt in elements) {
                    Log.d("PAK", emt)
                    if (emt.endsWith(".jpg") || emt.endsWith(".png")) {
                        var temp = ""
                        for (i in emt.lastIndex downTo 0) {
                            if (emt[i] == '>') {
                                images.add(url.plus(temp.reversed()))
                                temp = ""
                                continue
                            }
                            else {
                                temp += emt[i]
                            }
                        }
                    }
                }
                for (img in images)
                    Log.d("PAK", img)

            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("PAK", "Failed to connect")
            }

        })
    }
}

@Composable
fun nextImage() {
    Log.d("PAK", "block ${images[cur_index]}")
    if (cur_index == images.size) {
        CoilImage(images[0])
    }
    Log.d("PAK", "coiling ${images[cur_index]}")
    CoilImage(images[cur_index])
    cur_index += 1
}

@Composable
fun backImage() {

}









