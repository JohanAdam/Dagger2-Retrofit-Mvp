package io.reciteapp.recite.data.model;

public class FeedbackAnswerResponse {

  private String questionID_FK;
  private String answer;
  private String id;

  public FeedbackAnswerResponse() {
  }

  public FeedbackAnswerResponse(String questionID_FK, String answer, String id) {
    this.questionID_FK = questionID_FK;
    this.answer = answer;
    this.id = id;
  }

  public String getQuestionID_FK() {
    return questionID_FK;
  }

  public void setQuestionID_FK(String questionID_FK) {
    this.questionID_FK = questionID_FK;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
