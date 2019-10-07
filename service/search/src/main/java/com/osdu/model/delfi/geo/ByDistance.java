package com.osdu.model.delfi.geo;

import com.osdu.model.delfi.geo.exception.GeoLocationException;
import lombok.Data;
import lombok.NonNull;

@Data
public class ByDistance implements GeoLocation {

  static final int Y_INDEX = 1;
  static final int X_INDEX = 0;

  @NonNull
  Point point;
  @NonNull
  Double distance;

  /**
   * Constructor.
   * @param coordinates coordinates
   * @param distance distance
   */
  public ByDistance(Object[] coordinates, Double distance) {
    if (coordinates.length != 1) {
      throw new GeoLocationException(
          " By Distance GeoJSON requires exactly 1 point for creation, actual, received "
              + coordinates.length);
    }
    point = new Point(Double.valueOf(coordinates[X_INDEX].toString()),
        Double.valueOf(coordinates[Y_INDEX].toString()));

    this.distance = distance;
  }
}