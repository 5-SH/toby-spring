package com.spring.toby.factorybean;

public class Message {
  String text;

  private Message(String text) {
    this.text = text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public static Message newMessage(String text) {
    return new Message(text);
  }
}
