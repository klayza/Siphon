// Clayton Wieberg
// 5-12-2022
// Siphon (Capstone Project)
package com.example.siphon

import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.siphon.ui.theme.SiphonTheme
import okhttp3.*
import java.io.IOException
import kotlin.math.roundToInt


var images = mutableListOf<String>("http://www.irtc.org/ftp/pub/stills/2006-12-31/ornmcplx.jpg")
var cur_index = 0
var scrolledUp = false
var scrolledDown = false


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiphonTheme {
                // Sets the column for the image
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                )
                {
                    SwipeableSample()
                    /*ToolBar()   // Adds the toolbar
                    TextInput() // Initializes the textbox
                    CoilImage() // Sets the bg image to default*/
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableSample() {
    val squareSize = 50.dp

    val swipeableState = rememberSwipeableState("A")
    val sizePx = with(LocalDensity.current) { (350.dp - squareSize).toPx() }
    val anchors = mapOf(0f to "A", sizePx / 2 to "B", sizePx to "C")
    Log.d("PAK", anchors.keys.toString())
    val image = images[0]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                resistance = ResistanceConfig(0.9f, 1f)
            )
            .background(Color.Black)
    ) {
        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(squareSize)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = "desc",
                modifier = Modifier.clip(CircleShape).fillMaxSize(),
                contentScale = ContentScale.Fit

            )

        }
    }
}


@Composable
// Takes url as argument, and will set the background to image url.
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

                    // Determines which direction the user is scrolling
                    val (x, y) = dragAmount
                    when {
                        y > 0 -> {
                            scrolledDown = true; Log.d("PAK", "Scrolled down")
                        }   // User swipes down
                        y < 0 -> {
                            scrolledUp = true; Log.d("PAK", "Scrolled up")
                        } // User swipes up
                    }

                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
            painter = painter,
            contentDescription = "Image Description",
            contentScale = ContentScale.Fit)

            // Invokes function depending on swipe direction
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
// Asks for directory url
fun TextInput() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        label = { Text("Enter a directory: ")},
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,

    )
    // When the url entered is a valid url, it begins looking for images
    if (URLUtil.isValidUrl(text)){
        ScrapeImages(text)
    }
}

@Composable
// The toolbar for a list view and settings
// List allows you to select previous urls
// Settings allow you to customize experience
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
// Function to set and scrape for images
fun ScrapeImages(url: String) {
    // If the user enters an image url
    if (".jpg" in url || ".png" in url || ".jpeg" in url)
    {
        Log.d("PAK", "scrape ${url}")
        CoilImage(url)
        return
    }

    // To avoid crashing
    else if (url == "https://" || url == "http://") {
        return
    }

    // When the user enters a directory
    else {
        // Request init
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {

            // When a response is received
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string().toString()

                // Find all elements on page
                val elements = body.split("<").toTypedArray()   // Adds all elements to array
                for (emt in elements) {
                    Log.d("PAK", emt)
                    // Looks for images and adds them to list
                    if (emt.endsWith(".jpg") || emt.endsWith(".png")) {
                        var temp = ""
                        for (i in emt.lastIndex downTo 0) {
                            if (emt[i] == '>') {
                                images.add(url.plus("/" + temp.reversed()))
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

            // When the request fails
            override fun onFailure(call: Call, e: IOException) {
                Log.d("PAK", "Failed to connect")
            }

        })
        // After scraping for images, sets image to bg (Not working)
        Log.d("PAK", "Herere")
        Log.d("PAK", images.size.toString())
        if (images.isNotEmpty()) {
            Log.d("PAK", "herere 2")
            CoilImage(images[0])
        }
    }
}

@Composable
// Sets bg to the next image in the image list (Not working)
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









