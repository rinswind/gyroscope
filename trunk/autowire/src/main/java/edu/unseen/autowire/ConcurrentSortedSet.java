package edu.unseen.autowire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A simple unoptimized sorted container. The main characteristic it has is a
 * weakly consistent iterator that returns the content in order.
 * 
 * FIX Is it worth optimizing this until it becomes like the TreeSet? Probably
 * not since the addition/removal of service is after all a rare operation. The
 * fast iteration it more important that the add/remove here - for that I use an
 * ArrayList that gives me O(1) random access. Should I replace the internal
 * ArrayList with ConcurrentLinkedList. It seems to have the same weakly
 * consistent iterator. Than I can remove all synchronization?
 * 
 * @author Todor Boev
 * 
 * @param <E>
 */
public class ConcurrentSortedSet<E> implements Iterable<E> {
  private final Comparator<E> comp;
  private final List<E> list;
  
  public ConcurrentSortedSet(Comparator<E> comp) {
    this.comp = comp;
    this.list = new ArrayList<E>();
  }
  
  /**
   * @return the first element or <code>null</code> if the container is empty.
   */
  public synchronized E first() {
    return list.isEmpty() ? null : list.get(0);
  }
  
  /**
   * @return the last element or <code>null</code> if the container is empty.
   */
  public synchronized E last() {
    return list.isEmpty() ? null : list.get(list.size() - 1);
  }
  
  /**
   * @return
   */
  public synchronized boolean isEmpty() {
    return list.isEmpty();
  }
  
  /**
   * @return
   */
  public synchronized int size() {
    return list.size();
  }
  
  /**
   * We guarantee that if hasNext() returns true next() will always return a
   * value. It is possible that that value has since been removed from the
   * container and is in some way invalid.
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private int index;
      private E prev;
      private E cur;
      
      public boolean hasNext() {
        synchronized (ConcurrentSortedSet.this) {
          if (cur == null) {
            if (index < list.size()) {
              cur = list.get(index++);
            }
          }
          
          return cur != null;
        }
      }

      public E next() {
        synchronized (ConcurrentSortedSet.this) {
          if (cur == null) {
            throw new NoSuchElementException();
          }
          
          prev = cur;
          cur = null;
          return prev;
        }
      }

      public void remove() {
        synchronized (ConcurrentSortedSet.this) {
          if (ConcurrentSortedSet.this.remove(prev)) {
            prev = null;
            index--;
          }
        }
      }
    };
  }
  
  /**
   * @param e
   */
  public synchronized void add(E e) {
    /* Do a dumb bubble-sort insertion. */
    int index = 0;
    while (index < list.size() && comp.compare(list.get(index), e) > 0) {
      index++;
    }
    list.add(index, e);
  }
  
  /**
   * @param e
   */
  public synchronized boolean remove(E e) {
    return list.remove(e);
  }
}
