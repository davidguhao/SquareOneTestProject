package com.example.squareonetestproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.squareonetestproject.ui.theme.SquareOneTestProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var vm: MainViewModel
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(vm: MainViewModel) {
    val movies by vm.movies.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    LazyColumn(modifier = Modifier.padding(horizontal = 10.dp), state = listState) {
        items(movies) { movie ->
            val context = LocalContext.current
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                onClick = {
                    context.startActivity(
                        Intent(context, DetailActivity::class.java).apply {
                            putExtra("name", movie.name)
                            putExtra("overview", movie.overview)
                            putExtra("backdropUrl", movie.backdrop.url)
                            putExtra("releaseDate", movie.releaseDate)
                            putExtra("genresList", movie.genresList.toTypedArray())
                        }
                    )
                } ) {
                AsyncImage(
                    model = movie.poster.create(LocalContext.current),
                    contentDescription = null,
                    modifier = Modifier
                        .height(400.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = movie.name)
            }

            listState.checkingScroll(vm::loadMoreMovies)
        }

        item {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Loading...")
            }

        }
    }
}

fun LazyListState.checkingScroll(onScrollToEnd: (Int) -> Unit) {
    val layoutInfo = layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo

    if(layoutInfo.totalItemsCount - visibleItems.size > 0) { // If there are not enough space to show all of items.
        // To the end of bottom
        if (visibleItems.last().index == layoutInfo.totalItemsCount - 1) {
            onScrollToEnd(layoutInfo.totalItemsCount)
        }
    }

}

/**
 *
 */
class MovieDetail(
    val name: String,
    val poster: ImageRequestFactory,
    val backdrop: ImageRequestFactory,

    val genresList: List<String>,
    val overview: String,
    val releaseDate: String,
) {
    /**
     * It could be useless to create a factory for this feature, but
     * at the very first I thought I have to carry the authorization header so I
     * made this class to provide advanced features. But it does not have any security checks...
     */
    class ImageRequestFactory(
        val url: String,
        // private val authToken: String
    ) {
        fun create(context: Context): ImageRequest {
            return ImageRequest.Builder(context)
                .addHeader("accept", "application/json")
                // .addHeader("Authorization", "Bearer $authToken")
                .data(url)
                .build()

        }
    }
}

class MainViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {
    private var originalNum: Int = 0
    fun loadMoreMovies(currentNum: Int) {
        if(originalNum == currentNum) return

        useCase.loadMoreMovies()
        originalNum = currentNum
    }

    val movies = useCase.getMovies().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        listOf())
}