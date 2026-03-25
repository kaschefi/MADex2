package com.example.quiz_app_starter.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quiz_app_starter.R
import com.example.quiz_app_starter.model.Question
import com.example.quiz_app_starter.model.QuestionScreenState
import com.example.quiz_app_starter.model.getDummyQuestions
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    viewModel: QuestionScreenViewModel, // Consume the ViewModel
    onQuizFinished: (Int) -> Unit,
    onExit: () -> Unit
){
    // Collect the UI state as a Compose State object
    val state by viewModel.uiState.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Attach the ViewModel as an observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(viewModel)

        onDispose {
            // Clean up: remove the observer when the Composable leaves the screen
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }
    // Handle game completion navigation
    if (state.currentQuestionIndex >= state.questions.size) {
        LaunchedEffect(Unit) {
            onQuizFinished(state.points)
        }
        return
    }

    val currentQuestion = state.currentQuestion ?: return

    // Result Dialog
    if (state.showDialog) {
        Alert(
            title = state.dialogTitle,
            text = state.dialogText,
            onNextClicked = { viewModel.moveToNextQuestion() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz App") },
                actions = {
                    IconButton(onClick = onExit) {
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
                onClick = { viewModel.submitAnswer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = state.selectedAnswer != null && !state.showDialog
            ) {
                Text("Submit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ){
            LinearProgressIndicator(
                progress = { state.timerProgress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            QuestionCard(state.currentQuestionIndex, currentQuestion)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(currentQuestion.answers) { answer ->
                    AnswerCard(
                        answer = answer,
                        isSelected = (answer == state.selectedAnswer),
                        onSelect = { viewModel.onAnswerSelected(answer) }
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
