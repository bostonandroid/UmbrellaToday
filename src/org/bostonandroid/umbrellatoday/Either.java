package org.bostonandroid.umbrellatoday;
public interface Either<T> {
  public Either<T> onSuccess(ValueRunner<T> f);
  public Either<T> onFailure(ValueRunner<T> f);
}
