package com.bloodbowlclub.lib.tests;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@lombok.Builder
@lombok.Getter
public class TestCaseOptions {

  /**
   * List of fields to exclude from the comparison. Only works on first level of object.
   */
  @lombok.Builder.Default
  private List<String> fieldToExclude = List.of();

  /**
   * allows multiple assertEqualsResultset in the same test by incrementing the resultSetNumber for
   * each call
   */
  @lombok.Builder.Default
  private int resultSetNumber = 1;

  /**
   * excludes ids and timestamps from the comparison.
   */
  @lombok.Builder.Default
  private boolean excludeIdsAndTimestamps = false;

  private static List<String> idsAndTimestampsFields = List.of("id", "timestampedAt");

  public List<String> getFieldToExclude() {
    if (excludeIdsAndTimestamps) {
      return Stream.concat(fieldToExclude.stream(), idsAndTimestampsFields.stream())
          .collect(Collectors.toList());
    }
    return fieldToExclude;
  }
}
