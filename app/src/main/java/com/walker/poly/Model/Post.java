package com.walker.poly.Model;

public class Post {
    private  String askedby,date ,postid,publisher,question,topic,questionimage;

    public Post() {
    }

    public Post(String askedby, String date, String postid, String publisher, String question, String topic, String questionimage) {
        this.askedby = askedby;
        this.date = date;
        this.postid = postid;
        this.publisher = publisher;
        this.question = question;
        this.topic = topic;
        this.questionimage = questionimage;
    }

    public String getAskedby() {
        return askedby;
    }

    public void setAskedby(String askedby) {
        this.askedby = askedby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuestionimage() {
        return questionimage;
    }

    public void setQuestionimage(String questionimage) {
        this.questionimage = questionimage;
    }
}
