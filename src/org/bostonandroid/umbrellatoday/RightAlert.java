package org.bostonandroid.umbrellatoday;

class RightAlert implements AlertOrError {
  private SavedAlert alert;

  RightAlert(SavedAlert a) {
    this.alert = a;
  }
  
  public AlertOrError onSuccess(ValueRunner<SavedAlert> f) {
    f.run(this.alert);
    return this;
  }
 
  public AlertOrError onFailure(ValueRunner<Alert> f) {
    return this;
  }
}