package com.bloodbowlclub.lib.tests;

import com.bloodbowlclub.lib.services.DateService;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

@Setter
public class FakeDateService extends DateService {

  // default value = 2025-01-01T00:00:00
  private LocalDateTime todayIs;
  private UnaryOperator<LocalDateTime> nextNow;

  public FakeDateService() {
    super();
    this.nextNow = UnaryOperator.identity();
    resetTodayIsToDefault();
  }

  public FakeDateService(String todayIs) {
    super();
    this.todayIs = LocalDateTime.parse(todayIs);
    this.nextNow = UnaryOperator.identity();
  }

  @Override
  public LocalDateTime now() {
    LocalDateTime result = todayIs;
    todayIs = nextNow.apply(todayIs);
    return result;
  }

  public void resetTodayIsToDefault() {
    todayIs = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
  }
}
