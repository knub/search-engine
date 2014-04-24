import math


def kd(x):
    return int(math.log(x, 2))


def kr(x):
    return ~(1 << kd(x)) & x


def kdd(x):
    return kd(kd(x)+1)


def kdr(x):
    return kr(kd(x)+1)


def elias_delta_length_approx(x):
    return 2 * math.log(math.log(x, 2), 2)+math.log(x, 2)
