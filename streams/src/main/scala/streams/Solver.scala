package streams

trait Solver extends GameDef {

  /** 목표에 도달했는지: 목표 칸 위에 '서 있는' 상태 */
  def done(b: Block): Boolean =
    b.isStanding && b.b1 == goal

  /** 이웃 + 누적 이동기록(history: 최신이 머리). Stream으로 지연 계산 */
  def neighborsWithHistory(b: Block, history: List[Move])(implicit t: Terrain): Stream[(Block, List[Move])] =
    b.legalNeighbors(t).toStream.map { case (nb, m) => (nb, m :: history) }

  /** 이미 방문한 블록 제거 */
  def newNeighborsOnly(neighs: Stream[(Block, List[Move])],
                       explored: Set[Block]): Stream[(Block, List[Move])] =
    neighs.filterNot { case (b, _) => explored(b) }

  /** BFS: 같은 거리 레벨을 먼저 모두 방출하고, 그 다음 레벨을 재귀적으로 확장 */
  def from(initial: Stream[(Block, List[Move])],
           explored: Set[Block])(implicit t: Terrain): Stream[(Block, List[Move])] = {
    if (initial.isEmpty) Stream.empty
    else {
      val more = initial.flatMap { case (b, hist) => neighborsWithHistory(b, hist) }
      val newMore = newNeighborsOnly(more, explored)
      initial #::: from(newMore, explored ++ newMore.map(_._1).toSet)
    }
  }

  /** 시작점에서 출발하는 모든 경로(최단거리 순으로 흘러나옴) */
  lazy val pathsFromStart: Stream[(Block, List[Move])] =
    from(Stream((startBlock, Nil)), Set(startBlock))(terrain)

  /** 목표 블록으로 끝나는 모든 경로 */
  lazy val pathsToGoal: Stream[(Block, List[Move])] =
    pathsFromStart.filter { case (b, _) => done(b) }

  /** 최단 해(맨 앞이 첫 이동) */
  lazy val solution: List[Move] =
    pathsToGoal.headOption.map(_._2.reverse).getOrElse(Nil)
}
