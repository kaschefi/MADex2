package com.example.quiz_app_starter

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFullQuizFlow() {
        // 1. Start from Main Menu
        composeTestRule.onNodeWithText("Play!").performClick()

        // 2. Go through 10 questions
        for (i in 1..10) {
            // Wait for question to appear
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodes(hasText("Q$i:", substring = true)).fetchSemanticsNodes().isNotEmpty()
            }

            // Select the first answer option (Radio buttons don't always have text, but AnswerCard has text)
            // We can click the Card/Row by its text. Since we don't know the exact answers here without looking at model,
            // we'll just find a node that isn't the question or submit button.
            // Actually, let's just pick one we know from dummy data for the first few or just find any clickable.
            
            // In QuestionScreen.kt, AnswerCard uses Card(onClick = onSelect) containing Text(text = answer).
            // We can just click on one of the answer texts.
            // For simplicity in this test, we can use a more generic approach if possible, 
            // but since it's a "full test", let's try to finish it.
            
            // Just click the first answer for each question to move fast.
            // We need to know at least one answer text or use a tag.
            // Let's look at getDummyQuestions again. 
            // Q1: Hadrian's Wall, Caesar's Wall, ...
            // Q2: Bleach, Chloroform, ...
            
            val answerToClick = when(i) {
                1 -> "Hadrian's Wall"
                2 -> "Bleach"
                3 -> "England"
                4 -> "Keyser Söze"
                5 -> "Shaking My Head"
                6 -> "Bad Boys"
                7 -> "Reincarnation"
                8 -> "Wayne Enterprises"
                9 -> "butterflies and moths"
                10 -> "UB40"
                else -> ""
            }

            composeTestRule.onNodeWithText(answerToClick).performClick()
            composeTestRule.onNodeWithText("Submit").performClick()
            
            // Click "Next" on the dialog
            composeTestRule.onNodeWithText("Next").performClick()
        }

        // 3. Verify Finish Screen
        composeTestRule.onNodeWithText("Game Over").assertExists()
        composeTestRule.onNodeWithText("Score:").assertExists()
        composeTestRule.onNodeWithText("10").assertExists() // All answers above were correct

        // 4. Test "Play again"
        composeTestRule.onNodeWithText("Play again").performClick()
        
        // Should be back at Q1
        composeTestRule.onNodeWithText("Q1:", substring = true).assertExists()
    }
}
