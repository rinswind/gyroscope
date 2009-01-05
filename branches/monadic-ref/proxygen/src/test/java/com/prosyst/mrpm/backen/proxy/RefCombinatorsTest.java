package com.prosyst.mrpm.backen.proxy;

import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.prosyst.mprm.backend.proxy.ref.Transformer;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;
import com.prosyst.mprm.backend.proxy.ref.Refs;

/**
 * FIX Convert this code into real unit tests, which do assert oracles hold true.
 */
public class RefCombinatorsTest {
  private static final Transformer<String, Integer> A = new Transformer<String, Integer>() {
    @Override
    public String toString() {
      return "A";
    }

    public Integer create(String arg, Map<String, Object> props) {
      int val = arg.length();
      System.out.println(arg + "->" + val);
      return val;
    }

    public void destroy(Integer val, String arg, Map<String, Object> props) {
      System.out.println(val + "->" + arg);
    }
  };

  private static final Transformer<Integer, Integer> B = new Transformer<Integer, Integer>() {
    @Override
    public String toString() {
      return "B";
    }

    public Integer create(Integer arg, Map<String, Object> props) {
      int val = arg * 2;
      System.out.println(arg + "->" + val);
      return val;
    }

    public void destroy(Integer val, Integer arg, Map<String, Object> props) {
      System.out.println(val + "->" + arg);
    }
  };

  private static final Transformer<Integer, String> C = new Transformer<Integer, String>() {
    @Override
    public String toString() {
      return "C";
    }

    public String create(Integer arg, Map<String, Object> props) {
      String val = "This is the integer " + arg;
      System.out.println(arg + "->" + val);
      return val;
    }

    public void destroy(String val, Integer arg, Map<String, Object> props) {
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
