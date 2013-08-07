package com.talkingteddy;

/**
 * Interface specifying what a minimal task should have.
 */
public interface Task {
    /**
     * Returns the question text in plain form.
     * @return the question text in plain text form.
     */
    String getQuestion();

    /**
     * Returns url-friendly question text interpolated by plus("+") signs.
     * @return url-friendly question text interpolated by plus("+") signs
     */
    String getEncodedQuestion();

    /**
     * Returns speech answer in plain form.
     *
     * If there are many candidate answers, randomly returns one of them.
     *
     * @return speech answer in plain form, randomly from a number of candidates, if any.
     */
    String getRandomAnswer();


}
