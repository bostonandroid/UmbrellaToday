package org.bostonandroid.umbrellatoday;
public class Left<T> implements Either<T> {
  private T value;
  Left(T theValue) { this.value = theValue; }

  public Either<T> onSuccess(ValueRunner<T> f) {
    return this;
  }

  public Either<T> onFailure(ValueRunner<T> f) {
    f.run(this.value);
    return this;
  }
}