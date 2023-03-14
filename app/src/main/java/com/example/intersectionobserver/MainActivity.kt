package com.example.intersectionobserver

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.intersectionobserver.ui.theme.IntersectionObserverTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntersectionObserverTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val (items, setItems) = remember {
        (0..50).map { index ->
            "Item: Initial $index"
        }.let {
            mutableStateOf(it)
        }
    }

    LazyColumn {
        items(items.size) { index ->
            Text(
                modifier = Modifier.height(56.dp), text = items[index]
            )
        }
        item {
            IntersectionObserverWithProgress(visibleListener = {
                (items + (0..50).map {
                    "Item: generated ${Random.nextInt()}"
                }).let(setItems)
            })
        }
    }
}

@Composable
fun IntersectionObserver(
    visibleListener: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed = remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    if (isResumed.value) {
        visibleListener()
        Log.d("hoge", "isResumed")
    } else {
        Log.d("hoge", "Not isResumed")
    }
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            isResumed.value = event == Lifecycle.Event.ON_RESUME
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun IntersectionObserverWithProgress(
    visibleListener: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed = remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    if (isResumed.value) {
        Handler().postDelayed({
            visibleListener()
        }, 500)
        Log.d("hoge", "isResumed")
    } else {
        Log.d("hoge", "Not isResumed")
    }
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            isResumed.value = event == Lifecycle.Event.ON_RESUME
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    CircularProgressIndicator()
}