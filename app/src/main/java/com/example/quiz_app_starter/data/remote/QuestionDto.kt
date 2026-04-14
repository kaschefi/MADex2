package com.example.quiz_app_starter.data.remote

import com.example.quiz_app_starter.model.Question
import com.google.gson.annotations.SerializedName

data class QuestionDto(
    val category: String,
    val id: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val question: QuestionTextDto,
    val tags: List<String>,
    val type: String,
    val difficulty: String,
    val regions: List<String>?,
    val isNiche: Boolean
)

data class QuestionTextDto(
    val text: String
)

fun QuestionDto.toEntity(): Question {
    val allAnswers = (incorrectAnswers + correctAnswer).shuffled()
    return Question(
        category = this.category,
        id = this.id,
        correctAnswer = this.correctAnswer,
        answers = allAnswers,
        tags = this.tags,
        question = this.question.text,
        type = this.type,
        difficulty = this.difficulty,
        regions = this.regions,
        isNiche = this.isNiche
    )
}

fun List<QuestionDto>.toEntityList(): List<Question> {
    return this.map { it.toEntity() }
}
