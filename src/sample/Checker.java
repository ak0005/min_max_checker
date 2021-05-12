package sample;

class Checker implements Comparable<Checker> {
    int a;
    int b;
    int id;
    boolean isKing;

    Checker(int a, int b, int id) {
        this.a = a;
        this.b = b;
        this.id = id;
    }

    Checker(int a, int b, int id, boolean isKing) {
        this.a = a;
        this.b = b;
        this.id = id;
        this.isKing = isKing;
    }

    static void swap(Checker a, Checker b) {
        //Checker p2 = new Checker(a.a, a.b, a.id,a.isKing);
        Checker p2 = a;
        a = b;
        b = p2;
    }

    @Override
    public int compareTo(Checker p) {
        if (a == p.a) {
            if (b == p.b) {
                if (id == p.id) {
                    if (isKing == p.isKing)
                        return 0;
                    else if (isKing)
                        return 1;
                    else return -1;
                } else if (id < p.id)
                    return -1;
                else return 1;
            } else if (b < p.b)
                return -1;
            else return 1;
        } else if (a < p.a)
            return -1;
        else return 1;
    }

}
