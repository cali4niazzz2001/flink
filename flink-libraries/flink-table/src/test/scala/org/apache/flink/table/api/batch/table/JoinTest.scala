/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.api.batch.table

import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.utils.TableTestBase
import org.apache.flink.table.utils.TableTestUtil._
import org.junit.Test

class JoinTest extends TableTestBase {

  @Test
  def testLeftOuterJoinEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.leftOuterJoin(s, 'a === 'z).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "y", "z")
        ),
        term("where", "=(a, z)"),
        term("join", "a", "b", "y", "z"),
        term("joinType", "LeftOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testLeftOuterJoinEquiAndLocalPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.leftOuterJoin(s, 'a === 'z && 'b < 2).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "y", "z")
        ),
        term("where", "AND(=(a, z), <(b, 2))"),
        term("join", "a", "b", "y", "z"),
        term("joinType", "LeftOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testLeftOuterJoinEquiAndNonEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.leftOuterJoin(s, 'a === 'z && 'b < 'x).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        batchTableNode(1),
        term("where", "AND(=(a, z), <(b, x))"),
        term("join", "a", "b", "x", "y", "z"),
        term("joinType", "LeftOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testRightOuterJoinEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.rightOuterJoin(s, 'a === 'z).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "y", "z")
        ),
        term("where", "=(a, z)"),
        term("join", "a", "b", "y", "z"),
        term("joinType", "RightOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testRightOuterJoinEquiAndLocalPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.rightOuterJoin(s, 'a === 'z && 'x < 2).select('b, 'x)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "x", "z")
        ),
        term("where", "AND(=(a, z), <(x, 2))"),
        term("join", "a", "b", "x", "z"),
        term("joinType", "RightOuterJoin")
      ),
      term("select", "b", "x")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testRightOuterJoinEquiAndNonEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.rightOuterJoin(s, 'a === 'z && 'b < 'x).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        batchTableNode(1),
        term("where", "AND(=(a, z), <(b, x))"),
        term("join", "a", "b", "x", "y", "z"),
        term("joinType", "RightOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testFullOuterJoinEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.fullOuterJoin(s, 'a === 'z).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "y", "z")
        ),
        term("where", "=(a, z)"),
        term("join", "a", "b", "y", "z"),
        term("joinType", "FullOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testFullOuterJoinEquiAndLocalPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.fullOuterJoin(s, 'a === 'z && 'b < 2).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        unaryNode(
          "DataSetCalc",
          batchTableNode(1),
          term("select", "y", "z")
        ),
        term("where", "AND(=(a, z), <(b, 2))"),
        term("join", "a", "b", "y", "z"),
        term("joinType", "FullOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }

  @Test
  def testFullOuterJoinEquiAndNonEquiPred(): Unit = {
    val util = batchTestUtil()
    val t = util.addTable[(Int, Long, String)]("T", 'a, 'b, 'c)
    val s = util.addTable[(Long, String, Int)]("S", 'x, 'y, 'z)

    val joined = t.fullOuterJoin(s, 'a === 'z && 'b < 'x).select('b, 'y)

    val expected = unaryNode(
      "DataSetCalc",
      binaryNode(
        "DataSetJoin",
        unaryNode(
          "DataSetCalc",
          batchTableNode(0),
          term("select", "a", "b")
        ),
        batchTableNode(1),
        term("where", "AND(=(a, z), <(b, x))"),
        term("join", "a", "b", "x", "y", "z"),
        term("joinType", "FullOuterJoin")
      ),
      term("select", "b", "y")
    )

    util.verifyTable(joined, expected)
  }
}
