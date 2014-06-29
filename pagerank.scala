val initialValues = List(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)
val jumpProb = 0.15

val pageRank = (1 to 10000).foldLeft(initialValues) { (currentValues, _) =>
	val a = currentValues(0)
	val b = currentValues(1)
	val c = currentValues(2)

	val baseProb = jumpProb * 1.0 / 3.0

	val newA = baseProb + (1 - jumpProb) * b
	val newB = baseProb + (1 - jumpProb) * (a + c)
	val newC = baseProb

	List(newA, newB, newC)
}

println(pageRank)
