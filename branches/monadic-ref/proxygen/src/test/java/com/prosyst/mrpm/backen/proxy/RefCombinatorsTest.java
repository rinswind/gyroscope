package com.prosyst.mrpm.backen.proxy;

import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;
import com.prosyst.mprm.backend.proxy.ref.Refs;

/**
 * FIX Convert this code into unit tests.
 * 
 * @param args
 */
public class RefCombinatorsTest {
  public static void main(String[] args) {
    ObjectFactory<String, Integer> a = new ObjectFactory<String, Integer>() {
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

    ObjectFactory<Integer, Integer> b = new ObjectFactory<Integer, Integer>() {
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

    ObjectFactory<Integer, String> c = new ObjectFactory<Integer, String>() {
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

    /* Test if both combos are indeed disjoint */
    RefFactory<String, String> fact = Refs.combinator(a).to(b).to(c).factory();
    
    Ref<String, String> combo1 = fact.ref();
    Ref<String, String> combo2 = fact.ref();
    
    System.out.println("----");
    combo1.bind("Test AA", null);
    System.out.println("----");
    combo2.bind("Test BBB", null);
    System.out.println("----");
    combo1.unbind();
    System.out.println("----");
    combo2.unbind();
    
    /* Test expected equivalence of to/from combinations */
    excersise(Refs.combinator(a).to(b).to(c).factory().ref());
    excersise(Refs.combinator(c).from(b).from(a).factory().ref());
  }

  private static void excersise(Ref<String, String> ref) {
    System.out.println("------------------------");

    System.out.println("--- bind ---");
    ref.bind("This is short string!", null);

    System.out.println("--- unbind ---");
    ref.unbind();
  }
}
