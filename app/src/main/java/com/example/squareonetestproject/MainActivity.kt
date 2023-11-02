package com.example.squareonetestproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.squareonetestproject.ui.theme.SquareOneTestProjectTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.Arrays

class MainActivity : ComponentActivity() {
    private val vm by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(UseCase()))[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SquareOneTestProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(vm)
                }
            }
        }
    }
}
@Composable
fun Main(vm: MainViewModel) {
    val movies by vm.movies.collectAsStateWithLifecycle()
    LazyColumn {
        items(movies) { movie ->

            var showDetails by remember { mutableStateOf(false) }

            if(showDetails) Dialog(onDismissRequest = { showDetails = false }) {
                Card {
                    LazyColumn {
                        item {
                            Column {
                                AsyncImage(
                                    model = movie.poster.create(LocalContext.current),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(text = movie.name)
                                Text(text = movie.overview)
                                Text(text = movie.releaseDate)
                                Text(text = movie.genresList.contentToString())
                            }
                        }

                    }

                }
                movie.name
            }
            TextButton(onClick = { showDetails = true }) {
                Text(text = movie.name)
            }
        }
    }
}

/**
 *
 */
class MovieDetail(
    val name: String,
    val poster: ImageRequestFactory,
    val genresList: Array<Int>,
    val overview: String,
    val releaseDate: String
) {
    class ImageRequestFactory(
        private val url: String,
        private val authToken: String
    ) {
        fun create(context: Context): ImageRequest {
            return ImageRequest.Builder(context)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $authToken")
                .data(url)
                .build()

        }
    }
}

class MainViewModel(useCase: UseCase) : ViewModel() {
    class Factory(private val useCase: UseCase): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(useCase) as T
        }
    }

    val movies = useCase.getMovies().stateIn(
        viewModelScope, SharingStarted.Lazily, listOf())
}