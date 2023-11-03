package com.example.squareonetestproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.squareonetestproject.ui.theme.SquareOneTestProjectTheme

@Composable
fun Detail(
    name: String,
    overview: String,
    releaseDate: String,

    backdrop: MovieDetail.ImageRequestFactory,
    genresList: List<String>
) {
    Column {
        AsyncImage(
            model = backdrop.create(LocalContext.current),
            contentDescription = null,
            modifier = Modifier.height(200.dp).align(Alignment.CenterHorizontally)
        )

        Text(text = name)
        Text(text = overview)
        Text(text = releaseDate)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(genresList) {
                Card {
                    Text(text = it)
                }

            }
        }
    }
}
class DetailActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name")!!
        val overview = intent.getStringExtra("overview")!!
        val releaseDate = intent.getStringExtra("releaseDate")!!
        val genresList = intent.getStringArrayExtra("genresList")!!.toList()
        val backdrop = intent.getSerializableExtra("backdrop")!! as MovieDetail.ImageRequestFactory

        setContent {
            SquareOneTestProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Detail(
                        name = name,
                        overview = overview,
                        releaseDate = releaseDate,
                        genresList = genresList,
                        backdrop = backdrop
                    )
                }
            }
        }
    }
}