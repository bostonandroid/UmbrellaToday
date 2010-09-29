package org.bostonandroid.umbrellatoday;

class Just<T> implements Maybe<T> {
  T value;

  Just(T o) {
    this.value = o;
  }
  
  public Maybe<T> perform(ValueRunner<T> f) {
    f.run(value());
    return this;
  }
  
  public Maybe<T> orElse(Runnable f) {
    return this;
  }
  
  private T value() {
    return this.value;
  }
}