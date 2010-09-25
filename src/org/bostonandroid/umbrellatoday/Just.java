package org.bostonandroid.umbrellatoday;

class Just<T> implements Maybe<T> {
  T value;

  Just(T o) {
    this.value = o;
  }
  
  public Maybe<T> perform(EitherRunner<T> f) {
    f.run(this.value);
    return this;
  }
}