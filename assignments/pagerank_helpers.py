def pr_old(a, b, c):
    a1 = 0.05 + 0.85 * b
    b1 = 0.05 + 0.85 * a
    c1 = 0.05
    return [a1, b1, c1]


def pr(a, b, c):
    a1 = (0.05 * a) + (0.90 * b) + (0.05 * c)
    b1 = (0.90 * a) + (0.05 * b) + (0.90 * c)
    c1 = (0.05 * a) + (0.05 * b) + (0.05 * c)
    #return [round(a1, 4), round(b1, 4), round(c1, 4)]
    return [a1, b1, c1]


def foo(a, b, c, i=10):
    r = [a, b, c]
    for t in range(i):
        print '\t'.join([str(round(bar, 4)) for bar in r])
        r = pr(*r)
