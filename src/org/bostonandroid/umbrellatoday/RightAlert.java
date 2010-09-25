package org.bostonandroid.umbrellatoday;

class RightAlert implements AlertOrError {
  private SavedAlert alert;

  RightAlert(SavedAlert a) {
    this.alert = a;
  }
  
  public AlertOrError onSuccess(EitherRunner<SavedAlert> f) {
    f.run(this.alert);
    return this;
  }
 
  public AlertOrError onFailure(EitherRunner<Alert> f) {
    return this;
  }
}