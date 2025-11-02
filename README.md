# HW2 Q&A Application Automated Testing

## Overview
HW3Testbed is a Java command-line application that performs automated testing for the Discussion Board system, validating question and answer operations within the database. It verifies that CRUD functionality, data integrity, and validation rules behave as expected when users interact with Question and Answer entities via the DiscussionBoardDAO data access layer.

All tests run automatically from a single mainline class and report pass/fail results for each case.

## Features
- Automated testing for Questions and Answers
- Validation of database operations (CRUD)
- Error handling for invalid operations
- Automated summary reporting
- Professional Javadoc documentation for all test methods

## Automated Test Cases
This test suite contains five automated tests that cover key discussion board functionality:
1.	Update Question,	Verifies that a student can update their own question title and content, and that changes persist in the database.
2.	Update Answer	Confirms that a student can edit their answer, and updates are reflected correctly when retrieved.
3.	Propose Answer to Question,	Ensures that a student can post an answer to an existing question and that the association is correct.
4.	Propose Answer Without Question,	Validates that the system prevents creation of answers for nonexistent questions (expected SQLException).
5.	Multiple Answers to Question,	Checks that multiple users can post answers to the same question and that all are stored correctly.

## Getting Started
Clone the repository, Run HW3Testbed.java in src/application as java application
