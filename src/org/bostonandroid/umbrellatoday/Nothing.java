package org.bostonandroid.umbrellatoday;

class Nothing<T> implements Maybe<T> {
  public Maybe<T> perform(ValueRunner<T> f) {
    return this;
  }
  
  public Maybe<T> orElse(Runnable f) {
    f.run();
    return this;
  }
}