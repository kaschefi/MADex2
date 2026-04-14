package com.example.quiz_app_starter.data.repository

import android.util.Log
import com.example.quiz_app_starter.data.local.QuestionDao
import com.example.quiz_app_starter.data.remote.TriviaApiService
import com.example.quiz_app_starter.data.remote.toEntityList
import com.example.quiz_app_starter.model.Question
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val apiService: TriviaApiService
) {
    // Single Source of Truth: Data always comes from the Database
    fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions()
    }

    /**
     * Offline-first logic: 
     * 1. Fetch from network
     * 2. Save to local database (cache)
     * 3. UI stays updated because it observes the database Flow
     */
    suspend fun refreshQuestions(limit: Int = 10, categories: String? = null) {
        try {
            val response = apiService.getQuestions(limit, categories)
            val entities = response.toEntityList()
            questionDao.insertQuestions(entities)
            Log.d("QuestionRepository", "Successfully refreshed questions from network")
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Failed to refresh questions: ${e.message}")
            // We catch the exception so the app doesn't crash. 
            // The UI will still show the cached data from the Room DB.
        }
    }

    fun getRandomQuestion(): Flow<Question?> {
        return questionDao.getRandomQuestion()
    }

    suspend fun insertQuestion(question: Question) {
        questionDao.insertQuestion(question)
    }

    suspend fun insertQuestions(questions: List<Question>) {
        questionDao.insertQuestions(questions)
    }
}
