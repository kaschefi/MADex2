package com.example.quiz_app_starter.presentation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_starter.data.repository.QuestionRepository
import com.example.quiz_app_starter.model.Question
import com.example.quiz_app_starter.model.QuestionScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState = MutableStateFlow(QuestionScreenState())
    val uiState: StateFlow<QuestionScreenState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeQuestions()
        refreshQuestions()
    }

    private fun observeQuestions() {
        viewModelScope.launch {
            repository.getAllQuestions().collect { questions ->
                _uiState.update { it.copy(questions = questions) }
                // Start timer for the first question if data is ready and quiz hasn't started
                if (questions.isNotEmpty() && _uiState.value.currentQuestionIndex == 0 && timerJob == null) {
                   startTimer()
                }
            }
        }
    }

    fun refreshQuestions() {
        viewModelScope.launch {
            repository.refreshQuestions(limit = 10)
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val state = _uiState.value
        if (!state.showDialog && state.questions.isNotEmpty() && state.currentQuestionIndex < state.questions.size) {
            startTimer()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
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
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size) {
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
