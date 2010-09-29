package org.bostonandroid.umbrellatoday;

interface ValueProducerRunner<T,S> {
  public S run(T value);
}
