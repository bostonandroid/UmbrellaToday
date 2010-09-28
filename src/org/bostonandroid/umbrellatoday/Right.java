package org.bostonandroid.umbrellatoday;
public class Right<T> implements Either<T> {
  private T value;
  Right(T theValue) { this.value = theValue; }

  public Either<T> onSuccess(ValueRunner<T> f) {
    f.run(this.value);
    return this;
  }

  public Either<T> onFailure(ValueRunner<T> f) {
    return this;
  }
}