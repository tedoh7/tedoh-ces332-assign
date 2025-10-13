package streams

trait Solver extends GameDef {

  /** 목표에 도달했는지 */
  def done(b: Block): Boolean =
    b.isStanding && b.b1 == goal

  /** 이웃 + 누적 이동기록(history: 최신이 머리) */
  def neighborsWithHistory(b: Block, history: List[Move])(implicit t: Terrain): Stream[(Block, List[Move])] =
    b.legalNeighbors(t).toStream.map { case (nb, m) => (nb, m :: history) }

  /** 이미 방문한 블록 제외 */
  def newNeighborsOnly(neighs: Stream[(Block, List[Move])],
                       explored: Set[Block]): Stream[(Block, List[Move])] =
    neighs.filterNot { case (b, _) => explored(b) }

  /** BFS로 모든 경로 확장 */
  def from(initial: Stream[(Block, List[Move])],
           explored: Set[Block])(implicit t: Terrain): Stream[(Block, List[Move])] = {
    if (initial.isEmpty) Stream.empty
    else {
      val more: Stream[(Block, List[Move])] =
        initial.flatMap { case (b, hist) => neighborsWithHistory(b, hist) }
      val newMore = newNeighborsOnly(more, explored)
      initial #::: from(newMore, explored ++ newMore.map(_._1).toSet)
    }
  }

  /** 시작점에서 출발하는 모든 경로 */
  lazy val pathsFromStart: Stream[(Block, List[Move])] =
    from(Stream((startBlock, Nil)), Set(startBlock))(terrain)

  /** 목표 블록으로 끝나는 모든 경로 */
  lazy val pathsToGoal: Stream[(Block, List[Move])] =
    pathsFromStart.filter { case (b, _) => done(b) }

  /** 최단해 (맨 앞이 첫 이동) */
  lazy val solution: List[Move] =
    pathsToGoal.headOption.map(_._2.reverse).getOrElse(Nil)
}
