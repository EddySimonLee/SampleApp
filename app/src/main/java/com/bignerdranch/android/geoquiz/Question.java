package com.bignerdranch.android.geoquiz;

/**
 * Created by Lee on 2016-09-27.
 */
public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAgain;

    public Question(int textResId, boolean answerTrue){
        mTextResId = textResId;
        //문자열리소스의 리소스ID를 갖기에 늘 int값이다!!
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

}
