/**
* HW3Testbed - Automated Tests for Discussion Board
* <p>
* It tests question and answer operations including creation, updates,
* and validation rules.
* 
* @author      Karson Shin
*/
package application;

import databasePart1.DiscussionBoardDAO;
import java.sql.SQLException;

/**
* HW3Testbed Class
*/
public class HW3Testbed {

    private static int numPassed = 0;
    private static int numFailed = 0;
    private static DiscussionBoardDAO dao;
    private static final String TEST_USER = "testStudent";

    /**
    * Main method to execute all automated tests for the discussion board
    * 
    * @param args  command line arguments (not used)
    * @since       1.0
    */
    public static void main(String[] args) {
        System.out.println("HW3Testbed - Discussion Board Automated Tests\n");

        try {
            dao = new DiscussionBoardDAO();
            System.out.println("Test environment initialized successfully");
            System.out.println("________________________________________\n");

            // Execute test cases
            runTest(1, "Update Question", HW3Testbed::testUpdateQuestion);
            runTest(2, "Update Answer", HW3Testbed::testUpdateAnswer);
            runTest(3, "Propose Answer to Question", HW3Testbed::testProposeAnswerToQuestion);
            runTest(4, "Propose Answer Without Question", HW3Testbed::testProposeAnswerWithoutQuestion);
            runTest(5, "Multiple Answers to Question", HW3Testbed::testMultipleAnswersToQuestion);

            printTestSummary();

        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
    * Executes a test case and handles exceptions
    *
    * @param testId        the unique identifier for the test case
    * @param testName      the descriptive name of the test being performed
    * @param testMethod    the method reference to the test implementation
    * @since               1.0
    */
    private static void runTest(int testId, String testName, TestMethod testMethod) {
        System.out.println("Test " + testId + ": " + testName);
        
        try {
            testMethod.run();
            System.out.println("> PASS");
            numPassed++;
        } catch (AssertionError e) {
            System.out.println("> FAIL - " + e.getMessage());
            numFailed++;
        } catch (Exception e) {
            System.out.println("> FAIL - " + e.getMessage());
            numFailed++;
        }
        System.out.println("________________________________________");
        System.out.println();
    }

    /**
    * Custom interface for test methods that throw SQLException
    * 
    * @since     1.0
    */
    @FunctionalInterface
    private interface TestMethod {
        /**
        * Executes a test method that may throw SQLException
        * 
        * @throws SQLException if database operations fail during test execution
        * @since               1.0
        */
        void run() throws SQLException;
    }

    /**
    * Test: Student can update their question
    * <p>
    * Creates a test question, updates its content, and verifies
    * the changes persist in the database.
    *
    * @throws SQLException     if database operations fail during test execution
    * @throws AssertionError   if any test verification fails
    * @since                   1.0
    */
    public static void testUpdateQuestion() throws SQLException {
        System.out.println("Testing question update functionality");
        
        // Create test question
        Question question = new Question("Original Title", "Original content", TEST_USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Update question
        question.setTitle("Updated Title");
        question.setContent("Updated content");
        dao.updateQuestion(question);
        
        // Verify update
        Question retrieved = dao.getQuestionById(questionId);
        assert retrieved != null : "Question not found";
        assert "Updated Title".equals(retrieved.getTitle()) : "Title not updated";
        assert "Updated content".equals(retrieved.getContent()) : "Content not updated";
        
        // Cleanup
        dao.deleteQuestion(questionId);
        System.out.println("Question update test completed");
    }

    /**
    * Test: Student can update their answer
    * <p>
    * Creates a test answer, updates its content, and verifies
    * the changes persist in the database.
    *
    * @throws SQLException     if database operations fail during test execution
    * @throws AssertionError   if any test verification fails
    * @since                   1.0
    */
    public static void testUpdateAnswer() throws SQLException {
        System.out.println("Testing answer update functionality");
        
        // Create test data
        Question question = new Question("Test Question", "Question content", TEST_USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        Answer answer = new Answer(questionId, "Original answer", TEST_USER);
        dao.createAnswer(answer);
        int answerId = answer.getAnswerId();
        
        // Update answer
        answer.setContent("Updated answer");
        dao.updateAnswer(answer);
        
        // Verify update
        Answers answers = dao.getAnswersForQuestion(questionId);
        Answer retrieved = answers.getAllAnswers().stream()
            .filter(a -> a.getAnswerId() == answerId)
            .findFirst().orElse(null);
            
        assert retrieved != null : "Answer not found";
        assert "Updated answer".equals(retrieved.getContent()) : "Answer not updated";
        
        // Cleanup
        dao.deleteAnswer(answerId);
        dao.deleteQuestion(questionId);
        System.out.println("Answer update test completed");
    }

    /**
    * Test: Student can propose answer to selected question
    * <p>
    * Creates a test question and associates an answer with it,
    * then verifies the association is correctly stored.
    *
    * @throws SQLException     if database operations fail during test execution
    * @throws AssertionError   if any test verification fails
    * @since                   1.0
    */
    public static void testProposeAnswerToQuestion() throws SQLException {
        System.out.println("Testing answer proposal functionality");
        
        // Create test question
        Question question = new Question("Test Question", "Need help", TEST_USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Propose answer
        Answer answer = new Answer(questionId, "Test answer", TEST_USER);
        dao.createAnswer(answer);
        
        // Verify answer association
        Answers answers = dao.getAnswersForQuestion(questionId);
        assert answers.size() == 1 : "Answer not created";
        Answer retrieved = answers.getAllAnswers().get(0);
        assert retrieved.getQuestionId() == questionId : "Wrong question association";
        assert TEST_USER.equals(retrieved.getAuthorUserName()) : "Wrong author";
        
        // Cleanup
        dao.deleteAnswer(answer.getAnswerId());
        dao.deleteQuestion(questionId);
        System.out.println("Answer proposal test completed");
    }

    /**
    * Test: System prevents answer without question
    * <p>
    * Attempts to create an answer without a valid question reference
    * and verifies the system properly rejects the operation.
    *
    * @throws SQLException     if database operations fail during test execution
    * @throws AssertionError   if test logic fails unexpectedly
    * @since                   1.0
    */
    public static void testProposeAnswerWithoutQuestion() throws SQLException {
        System.out.println("Testing invalid answer prevention");
        
        try {
            // Try to create answer without valid question
            Answer invalidAnswer = new Answer(-1, "Should fail", TEST_USER);
            dao.createAnswer(invalidAnswer);
            
            // If we get here, test fails
            throw new AssertionError("Should have thrown SQLException");
            
        } catch (SQLException e) {
            // Expected behavior
            System.out.println("Expected Error: " + e.getMessage());
        }
    }

    /**
    * Test: Multiple answers to same question
    * <p>
    * Creates multiple answers for a single question and verifies
    * all answers are correctly associated and retrievable.
    *
    * @throws SQLException     if database operations fail during test execution
    * @throws AssertionError   if any test verification fails
    * @since                   1.0
    */
    public static void testMultipleAnswersToQuestion() throws SQLException {
        System.out.println("Testing multiple answers functionality");
        
        // Create test question
        Question question = new Question("Teamwork Question", "Collaboration tips?", TEST_USER);
        dao.createQuestion(question);
        int questionId = question.getQuestionId();
        
        // Create multiple answers
        String[] answers = {
            "Answer from student A",
            "Answer from student B", 
            "Answer from student C"
        };
        String[] authors = {"studentA", "studentB", "studentC"};
        
        for (int i = 0; i < answers.length; i++) {
            Answer answer = new Answer(questionId, answers[i], authors[i]);
            dao.createAnswer(answer);
        }
        
        // Verify all answers
        Answers allAnswers = dao.getAnswersForQuestion(questionId);
        assert allAnswers.size() == answers.length : "Wrong number of answers";
        
        // Cleanup
        for (Answer answer : allAnswers.getAllAnswers()) {
            dao.deleteAnswer(answer.getAnswerId());
        }
        dao.deleteQuestion(questionId);
        System.out.println("Multiple answers test completed");
    }

    /**
    * Print test results summary
    * 
    * @since     1.0
    */
    private static void printTestSummary() {
        System.out.println("Test Summary");
        System.out.println("________________________________________");
        System.out.println("Total Tests: " + (numPassed + numFailed));
        System.out.println("Passed: " + numPassed);
        System.out.println("Failed: " + numFailed);
        
        if (numFailed == 0) {
            System.out.println("All tests passed!");
        } else {
            System.out.println(numFailed + " test(s) failed");
        }
    }
}