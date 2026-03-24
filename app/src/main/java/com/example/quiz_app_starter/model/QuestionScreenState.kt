package com.example.quiz_app_starter.model
/**
 * Encapsulates the state for the Question Screen.
 * Includes questions, progress, selection, and dialog information.
 */
data class QuestionScreenState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val currentTime: Int = 0,
    val maxTime: Int = 30,
    val showDialog: Boolean = false,
    val isCorrect: Boolean = false,
    val timeOver: Boolean = false,
    val points: Int = 0
) {
    val currentQuestion: Question? = questions.getOrNull(currentQuestionIndex)

    val timerProgress: Float = if (maxTime > 0) currentTime.toFloat() / maxTime.toFloat() else 0f

    val dialogTitle: String = when {
        isCorrect -> "Correct!"
        timeOver -> "Time is out!"
        else -> "Wrong!"
    }

    val dialogText: String = if (isCorrect || currentQuestion == null) {
        ""
    } else {
        "The correct answer was: ${currentQuestion.correctAnswer}"
    }
}