package com.example.quiz_app_starter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quiz_app_starter.ui.theme.QuizappstarterTheme
import androidx.compose.ui.graphics.ColorFilter
import com.example.quiz_app_starter.presentation.QuestionScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizappstarterTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainMenuScreen(
                        bestScore = 12,
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
                QuestionScreen()
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    bestScore: Int = 0,
    modifier: Modifier
) {
    TopBar()
    Column(
        modifier = modifier
            .padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressBar()

        Image(
            painter = painterResource(id = R.drawable.quizicon),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp),
            colorFilter = ColorFilter.tint(Color.Green)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "My Awesome QuizApp",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Test your knowledge!",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.primary.copy(0.5f))
                .padding(16.dp)

        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Best of all time",
                    color = MaterialTheme.colorScheme.background.copy(0.7f)
                )
                Text(text = bestScore.toString(), fontSize = 64.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Log.d("app", "play")
            },
            modifier = Modifier.fillMaxWidth(0.6f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Play!", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("hi")
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@Composable
fun ProgressBar() {
    LinearProgressIndicator(progress = {0.5f})
}

@Preview(showBackground = true, name = "MainMenuPreview")
@Composable
fun MainMenuScreenPreview() {
    QuizappstarterTheme {
        MainMenuScreen(3, Modifier)
    }
}