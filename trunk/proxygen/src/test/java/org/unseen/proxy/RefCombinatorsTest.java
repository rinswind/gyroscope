/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.proxy;

import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefFactory;
import org.unseen.proxy.ref.Refs;
import org.unseen.proxy.ref.Transformer;


/**
 * FIX Convert this code into real unit tests, which do assert oracles hold true.
 */
public class RefCombinatorsTest {
  private static final Transformer<String, Integer> A = new Transformer<String, Integer>() {
    @Override
    public String toString() {
      return "A";
    }

    public Integer map(String arg, Map<String, Object> props) {
      int val = arg.length();
      System.out.println(arg + "->" + val);
      return val;
    }

    public void unmap(Integer val, String arg, Map<String, Object> props) {
      System.out.println(val + "->" + arg);
    }
  };

  private static final Transformer<Integer, Integer> B = new Transformer<Integer, Integer>() {
    @Override
    public String toString() {
      return "B";
    }

    public Integer map(Integer arg, Map<String, Object> props) {
      int val = arg * 2;
      System.out.println(arg + "->" + val);
      return val;
    }

    public void unmap(Integer val, Integer arg, Map<String, Object> props) {
      System.out.println(val + "->" + arg);
    }
  };

  private static final Transformer<Integer, String> C = new Transformer<Integer, String>() {
    @Override
    public String toString() {
      return "C";
    }

    public String map(Integer arg, Map<String, Object> props) {
      String val = "This is the integer " + arg;
      System.out.println(arg + "->" + val);
      return val;
    }

    public void unmap(String val, Integer arg, Map<String, Object> props) {
      System.out.println(val + "->" + arg);
    }
  };

  /**
   * Test Ref chains creted from combinator factories are disjoint.
   */
  @Test
  public void testCombinators() {
    RefFactory<String, String> fact = Refs.combinator(A).to(B).to(C).factory();
    
    Ref<String, String> combo1 = fact.ref();
    Ref<String, String> combo2 = fact.ref();
    
    assertTrue(combo1 != combo2);
    
    System.out.println("----");
    combo1.bind("Test AA", null);
    System.out.println("----");
    combo2.bind("Test BBB", null);
    System.out.println("----");
    combo1.unbind();
    System.out.println("----");
    combo2.unbind();
  }

  /**
   * Test expected equivalence of to/from combinations 
   */
  @Test
  public void testEquivalence() {
    excersise(Refs.combinator(A).to(B).to(C).factory().ref());
   
    excersise(Refs.combinator(C).from(B).from(A).factory().ref());
  }
  
  private static void excersise(Ref<String, String> ref) {
    System.out.println("------------------------");

    System.out.println("--- bind ---");
    ref.bind("This is short string!", null);

    System.out.println("--- unbind ---");
    ref.unbind();
  }
}
