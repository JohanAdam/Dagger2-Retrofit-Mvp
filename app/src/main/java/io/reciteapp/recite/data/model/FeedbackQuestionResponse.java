package io.reciteapp.recite.data.model;

public class FeedbackQuestionResponse {

  private int questionType;
  private String questionText;

  public FeedbackQuestionResponse() {
  }

  public FeedbackQuestionResponse(int questionType, String questionText) {
    this.questionType = questionType;
    this.questionText = questionText;
  }

  public int getQuestionType() {
    return questionType;
  }

  public void setQuestionType(int questionType) {
    this.questionType = questionType;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }
}
