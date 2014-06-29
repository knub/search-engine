val initialValues = List(1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0)
val jumpProb = 0.15

val pageRank = (1 to 19).foldLeft(initialValues) { (currentValues, i) =>
	val a = currentValues(0)
	val b = currentValues(1)
	val c = currentValues(2)

	val baseProb = jumpProb * 1.0 / 3.0

	val newA = baseProb + (1 - jumpProb) * b
	val newB = baseProb + (1 - jumpProb) * (a + c)
	val newC = baseProb

	val newValues = List(newA, newB, newC)
	println(f"$i%2d $newA%.4f $newB%.4f $newC%.4f")
	newValues
}

