package com.example.quiz_app_starter.presentation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_starter.model.Question
import com.example.quiz_app_starter.model.QuestionScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionScreenViewModel(
    private val questions: List<Question>
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState = MutableStateFlow(QuestionScreenState(questions = questions))
    val uiState: StateFlow<QuestionScreenState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // React to the Screen coming into view
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Resume timer if the quiz is active and no dialog is showing
        val state = _uiState.value
        if (!state.showDialog && state.currentQuestionIndex < questions.size) {
            startTimer()
        }
    }

    // React to the Screen going into the background (e.g., phone call)
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        // Pause the timer by cancelling the background job
        timerJob?.cancel()
    }

    fun onAnswerSelected(answer: String) {
        if (_uiState.value.showDialog) return
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        if (currentState.selectedAnswer == null || currentState.showDialog) return

        val isCorrect = currentState.selectedAnswer == currentQuestion.correctAnswer
        timerJob?.cancel()

        _uiState.update {
            it.copy(
                isCorrect = isCorrect,
                points = if (isCorrect) it.points + 1 else it.points,
                showDialog = true
            )
        }
    }

    fun moveToNextQuestion() {
        _uiState.update {
            it.copy(
                showDialog = false,
                timeOver = false,
                selectedAnswer = null,
                currentTime = 0,
                currentQuestionIndex = it.currentQuestionIndex + 1
            )
        }
        if (_uiState.value.currentQuestionIndex < questions.size) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.currentTime < _uiState.value.maxTime) {
                delay(1000L)
                _uiState.update { it.copy(currentTime = it.currentTime + 1) }
            }
            _uiState.update {
                it.copy(isCorrect = false, timeOver = true, showDialog = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}