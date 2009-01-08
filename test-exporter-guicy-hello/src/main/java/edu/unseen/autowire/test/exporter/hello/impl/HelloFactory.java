package edu.unseen.autowire.test.exporter.hello.impl;

import edu.unseen.autowire.test.exporter.hello.Hello;

public interface HelloFactory {
  Hello create(int no);
}
