/*
 *  Copyright 2012 Peter Karich info@jetsli.de
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.jetsli.graph.util.shapes;

/**
 * A shape interface to implement circles or rectangles.
 *
 * @author Peter Karich
 */
public interface Shape {

  /**
   * @return true if edges or areas of this and the specified shapes overlap
   */
  boolean intersect(Shape o);

  /**
   * @return true only if lat and lon are inside (or on the edge) of this shape
   */
  boolean contains(double lat, double lon);

  /**
   * @return true if the specified shape is fully contained in this shape. Only iff
   *         s1.contains(s2) && && s2.contains(s1) then s1 is equal to s2
   */
  boolean contains(Shape s);

  /**
   * @return the minimal rectangular bounding box of this shape
   */
  BBox getBBox();
}
