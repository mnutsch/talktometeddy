package com.talkingteddy;

import java.util.Random;

/**
 * A collection of basic tasks
 */
public class BasicTask implements Task {

    private String question;
    private String[] answers;

    Random r = new Random();

    public BasicTask(String question, String[] answers) {
        this.question = question;
        this.answers = answers;
    }


    @Override

    public String getQuestion() {
        return question;
    }

    @Override
    public String getEncodedQuestion() {
        return java.net.URLEncoder.encode(question);
    }

    @Override
    public String getRandomSpeechAnswer() {
        return answers[r.nextInt(answers.length)];
    }
}
