package misk.digester

import java.util.Calendar

/** Window represents a time range */
class Window (private val start: Calendar, private val end: Calendar) {

  /** Returns true if the given time t falls within the window's Start <= t < End. */
  fun contains(t: Calendar): Boolean {
    return !t.before(start) && t.before(end)
  }

  /** Returns true when the other window has the same start and end times. */
  fun equals(other: Window): Boolean {
    return start.time == other.start.time && end.time == other.end.time
  }

  /** Returns string representing the start and end time */
  fun string(): String {
    return "[${start.time}, ${end.time})"
  }
}

/** Windower contains multiple windows with a specified duration */
class Windower {
  private var windowSize: Int = 0
  val startSecs: MutableList<Int> = mutableListOf()

  /**
   * Creates a new Windower, which will create windows of windowSecs duration.
   * The size of the window must be in the range of 1 to 60 seconds, and must divide 60 without any remainder:
   * 1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30 second window sizes are permitted.
   * Stagger defines how many windows will contain a single time; a value of 1 means windows never overlap.
   */
  constructor(windowSecs: Int, stagger: Int) {
    require (windowSecs > 0 || windowSecs <= 60)  {
      "windowSecs must be in the range of (0, 60]"
    }
    require (60 % windowSecs == 0) {
      "60 % windowSecs must be 0, not ${60 % windowSecs}"
    }
    require(stagger > 0 && stagger <= windowSecs) {
      "stagger must be >= 1 and <= windowSecs"
    }

    var start = 0
    while (start < 60) {
      startSecs.add(start)

      for (i in 1..(stagger-1)) {
        startSecs.add(start + i*windowSecs/stagger)
      }

      start += windowSecs
    }

    this.windowSize = windowSecs
  }

  /**
   * Returns all windows that the given time falls into.
   * The returned slice will be ordered by window start time, and
   * the number of windows in the returned slice will the same as the stagger
   * given when the Windower was created.
   */
  fun windowContaining(t: Calendar): MutableList<Window> {
    // Find the earliest possible time the window could start,
    // then round up to the nearest second boundary
    var windowStart: Calendar = t.clone() as Calendar
    windowStart.add(Calendar.SECOND, -windowSize)

    var boundaryIndex = 0
    for (i in 0..(startSecs.count()-1)) {
      if (startSecs[i] >= windowStart.get(Calendar.SECOND)) {
        boundaryIndex = i
        break
      }
    }

    windowStart.set(Calendar.SECOND, startSecs[boundaryIndex])
    var windowEnd: Calendar = windowStart.clone() as Calendar
    windowEnd.add(Calendar.SECOND, windowSize)

    // Keep creating potential windows until none hold the given time
    val windows: MutableList<Window> = mutableListOf()
    var currWindow = Window(windowStart.clone() as Calendar, windowEnd.clone() as Calendar)
    while (true) {
      if (currWindow.contains(t)) {
        windows.add(currWindow)
      } else if (windows.count() > 0) {
        // if len(windows) == 0 that means the first potential window started too early.
        // otherwise, len(windows) > 0 means that all possible windows were already appended
        break
      }

      boundaryIndex= nextTime(windowStart, boundaryIndex)
      windowEnd = windowStart.clone() as Calendar
      windowEnd.add(Calendar.SECOND, windowSize)
      currWindow = Window(windowStart.clone() as Calendar, windowEnd.clone() as Calendar)
    }

    return windows
  }

  /** Returns index of next window in the minute if one exists or 0 if none left */
  private fun nextBoundaryIndex(i: Int): Int {
    if (i + 1 >= startSecs.count()) {
      return 0
    }
    return i + 1
  }

  /**
   * Returns the start time of the next window and boundary index of where
   * the window is within the current minute
   */
  private fun nextTime(start: Calendar, startBoundary: Int): Int {
    val boundaryIndex = nextBoundaryIndex(startBoundary)
    if (boundaryIndex == 0) {
      start.set(Calendar.SECOND, 0)
      start.add(Calendar.MINUTE, 1)
      return boundaryIndex
    }

    start.set(Calendar.SECOND, startSecs[boundaryIndex])
    return boundaryIndex
  }
}