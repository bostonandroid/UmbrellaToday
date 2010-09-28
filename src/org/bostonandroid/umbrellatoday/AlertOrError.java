package org.bostonandroid.umbrellatoday;

interface AlertOrError {
  public AlertOrError onSuccess(ValueRunner<SavedAlert> f);
  public AlertOrError onFailure(ValueRunner<Alert> f);
}