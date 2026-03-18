package com.example.quiz_app_starter.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quiz_app_starter.R
import com.example.quiz_app_starter.model.Question
import com.example.quiz_app_starter.model.getDummyQuestions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    questions: List<Question> = getDummyQuestions()
){
    var showDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var timeOver by remember { mutableStateOf(false) }
    var correctCounter by remember { mutableStateOf(0) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val currentQuestion = questions[currentQuestionIndex]
    val timerDurationSeconds = 0
    val maxTime = 30
    var currentTime by remember { mutableStateOf(timerDurationSeconds) }

    // Alerts
    if (showDialog && isCorrect) {
        Alert(
            title = "Correct!",
            text = "",
            onNextClicked = {
                currentQuestionIndex += 1
                selectedAnswer = null
                showDialog = false
            }
        )
    }else if (showDialog && timeOver){
        Alert(
            title = "No answer selected.\nTime is out.",
            text = "The correct answer was: ${currentQuestion.correctAnswer}",
            onNextClicked = {
                currentQuestionIndex += 1
                selectedAnswer = null
                showDialog = false
                timeOver = false
            }
        )
    } else if (showDialog){
        Alert(
            title = "Wrong!",
            text = "The correct answer was: ${currentQuestion.correctAnswer}",
            onNextClicked = {
                currentQuestionIndex += 1
                selectedAnswer = null
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Quiz App")
                }, actions = {
                    IconButton(onClick = { Log.d("app","exit")}) {
                        Image(
                            painter = painterResource(id = R.drawable.outline_exit_to_app_24),
                            contentDescription = "Exit",
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(Color.Green)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (isAnswerCorrect(selectedAnswer,currentQuestion)){
                        isCorrect = true
                        correctCounter ++
                    }else if(selectedAnswer != null) {
                    showDialog = true
                }
                          },
                modifier = Modifier
                           .fillMaxWidth()
                           .padding(16.dp)
                ) {
                Text("submit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ){
            LaunchedEffect(key1 = currentQuestionIndex) {
                currentTime = timerDurationSeconds

                while(currentTime < maxTime){
                    delay(1000L) // 1 sec
                    currentTime ++
                }
                showDialog = true
                timeOver = true

            }
            LinearProgressIndicator(
                progress = { currentTime.toFloat() / maxTime.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(16.dp))
            QuestionCard(currentQuestionIndex,currentQuestion)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentQuestion.answers) { answer ->
                    AnswerCard(
                        answer = answer,
                        isSelected = (answer == selectedAnswer),
                        onSelect = { selectedAnswer = answer }
                    )
                }
            }
        }
    }

}


@Composable
fun QuestionCard(currentQuestionIndex: Int, currentQuestion: Question){
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Q${currentQuestionIndex + 1}: ${currentQuestion.question}",
            modifier = Modifier.padding(16.dp)
        )
    }
}
@Composable
fun AnswerCard(answer: String, isSelected: Boolean, onSelect: () -> Unit ){
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = answer)
            RadioButton(
                onClick = onSelect,
                selected = isSelected,
            )

        }
    }
}

fun isAnswerCorrect(selectedAnswer: String?, currentQuestion: Question): Boolean{
    return currentQuestion.correctAnswer == selectedAnswer
}
@Composable
fun Alert(
    title: String,
    text: String,
    onNextClicked: () -> Unit 
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onNextClicked) {
                Text(text = "Next")
            }
        }
    )
}