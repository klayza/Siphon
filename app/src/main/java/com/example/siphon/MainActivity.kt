package com.example.siphon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
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
                    var text by rememberSaveable { mutableStateOf("") }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Enter a directory:") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    CoilImage()
                }
            }
        }
    }
}

@Composable
fun CoilImage() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = "https://wallpapers.com/images/high/captain-marvel-mobile-94h0epto4hul40bn.jpg",
            builder = {}
        )
        // Stretches image
        Image(modifier = Modifier.fillMaxSize(), painter = painter, contentDescription = "Image Description", contentScale = ContentScale.Fit)
    }
}

@Composable
fun BackgroundColor() {
    Surface(color = Color.DarkGray) {}

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SiphonTheme {
        Greeting("Android")
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







