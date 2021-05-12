package sample;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    final static long TIME_LIMIT = (long) 40000 * (long) 100000;
    protected int id;
    protected int checkersCount = 0;
    protected Board board;

    protected ArrayList<Checker> myCheckers;

    Player(int id, Board board) {
        this.id = id;
        this.board = board;
        myCheckers = new ArrayList<>();
    }

    private static int getKillsCount(ArrayList<ArrayList<Integer>> arr, ArrayList<ArrayList<Checker>> mat, int i, int j) {
        int val = 0;
        for (ArrayList<Integer> array : arr) {
            int a = array.get(0), b = array.get(1);
            if (Math.abs(i - a) == 2) {
                val += 2;

                if (mat.get((i + a) / 2).get((j + b) / 2).isKing)
                    val += 2;
            }
        }
        return val;
    }

    static int evaluate(ArrayList<ArrayList<Checker>> mat, int id) {
        int score = 0;

        for (int i = 0; i < Board.DIMEN; ++i) {
            for (int j = 0; j < Board.DIMEN; ++j) {
                if (mat.get(i).get(j).id == 1) {
                    if (id == 1)
                        score += getKillsCount(PlayerW.getNeighbours(mat, i, j), mat, i, j)*100;
                    if (mat.get(i).get(j).isKing)
                        score += 6000000;
                    else {
                        score += 3000000;
                        score += i * 10000;        //how close to be king
                    }
                } else {

                    if (id == 2)
                        score -= getKillsCount(PlayerB.getNeighbours(mat, i, j), mat, i, j)*100;

                    if (mat.get(i).get(j).isKing)
                        score -= 6000000;
                    else {
                        score -= 3000000;
                        score -= (Board.DIMEN - 1 - i) * 10000;   //how close to be king
                    }
                }
            }
        }
        return score + new Random().nextInt(199) - 99;
    }

    static boolean isValid(int a) {
        return a >= 0 && a < Board.DIMEN;
    }

    static void printMat(ArrayList<ArrayList<Checker>> mat) {
        for (int i = 0; i < Board.DIMEN; ++i) {
            for (int j = 0; j < Board.DIMEN; ++j) {
                System.out.print(mat.get(i).get(j).id);
            }
            System.out.println();
        }

        System.out.println("=====");
    }

    static ArrayList<ArrayList<Integer>> getNeighbours(ArrayList<ArrayList<Checker>> mat, int i, int j) {
        return null;
    }

    int getId() {
        return id;
    }

    void addChecker(Checker p) {
        myCheckers.add(p);
        checkersCount = myCheckers.size();
    }

    void removeChecker(Checker ch) {
        myCheckers.remove(ch);
        checkersCount = myCheckers.size();
    }

    int getCheckersCount() {
        return checkersCount;
    }

    void moveIt(Checker ch, int a, int b) {
    }

    public String play(String move) {
        return move;
    }

    public String play() {
        return "";
    }
}

class PlayerW extends Player {
    final static int id = 1;
    private final static int DEPTH = 5;
    private static int[] movesANormal = {1, 1};
    private static int[] movesBNormal = {-1, 1};
    private static int[] movesAKing = {-1, -1};
    private static int[] movesBKing = {-1, 1};

    PlayerW(Board board) {
        super(id, board);
    }

    static ArrayList<ArrayList<Integer>> getNeighbours(ArrayList<ArrayList<Checker>> mat, int i, int k) {
        ArrayList<ArrayList<Integer>> ans = new ArrayList<ArrayList<Integer>>();
        for (int j = 0; j < 2; ++j) {
            if (isValid(i + movesANormal[j]) && isValid(k + movesBNormal[j]) && mat.get(i + movesANormal[j]).get(k + movesBNormal[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + movesANormal[j]);
                a.add(k + movesBNormal[j]);
                ans.add(a);
            }
        }

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + 2 * movesANormal[j]) && isValid(k + 2 * movesBNormal[j]) && mat.get(i + movesANormal[j]).get(k + movesBNormal[j]).id == PlayerB.id && mat.get(i + 2 * movesANormal[j]).get(k + 2 * movesBNormal[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + 2 * movesANormal[j]);
                a.add(k + 2 * movesBNormal[j]);
                ans.add(a);
            }
        }

        if (!mat.get(i).get(k).isKing)
            return ans;

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + movesAKing[j]) && isValid(k + movesBKing[j]) && mat.get(i + movesAKing[j]).get(k + movesBKing[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + movesAKing[j]);
                a.add(k + movesBKing[j]);
                ans.add(a);
            }

        }

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + 2 * movesAKing[j]) && isValid(k + 2 * movesBKing[j]) && mat.get(i + movesAKing[j]).get(k + movesBKing[j]).id == PlayerB.id && mat.get(i + 2 * movesAKing[j]).get(k + 2 * movesBKing[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + 2 * movesAKing[j]);
                a.add(k + 2 * movesBKing[j]);
                ans.add(a);
            }
        }
        return ans;
    }

    static ArrayList<Integer> search(ArrayList<ArrayList<Checker>> mat, int depth, int Min, int Max) {

        if (depth <= 0) {
            ArrayList<Integer> arr = new ArrayList<>(5);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(evaluate(mat, PlayerW.id));
            return arr;
        }


        ArrayList<Integer> moves = new ArrayList<>(5);
        ArrayList<Integer> movesBackup = new ArrayList<>(5);

        int score = Min;

        boolean flag = true;

        for (int i = 0; i < Board.DIMEN; ++i) {
            for (int k = 0; k < Board.DIMEN; ++k) {
                Checker ch = mat.get(i).get(k);
                if (ch.id != PlayerW.id)
                    continue;

                for (ArrayList<Integer> array : getNeighbours(mat, i, k)) {
                    int a = array.get(0), b = array.get(1);
                    if (Math.abs(i - a) == 1) {
                        flag = false;
                        Checker c1 = ch;
                        Checker c2 = mat.get(a).get(b);
                        mat.get(i).set(k, c2);
                        mat.get(a).set(b, c1);
                        boolean p = c1.isKing;
                        if (a == Board.DIMEN - 1) {
                            c1.isKing = true;
                        }

                        ArrayList<Integer> arr = PlayerB.search(mat, depth - 1, score, Max);
                        if (arr.get(4) > score  && arr.get(4)!=Integer.MAX_VALUE-1) {
                            moves = arr;
                            moves.set(0, i);
                            moves.set(1, k);
                            moves.set(2, a);
                            moves.set(3, b);
                            score = arr.get(4);
                        } else {
                            movesBackup = arr;
                            movesBackup.set(0, i);
                            movesBackup.set(1, k);
                            movesBackup.set(2, a);
                            movesBackup.set(3, b);
                            movesBackup.set(4, Integer.MAX_VALUE-1);
                        }
                        mat.get(i).set(k, c1);
                        mat.get(a).set(b, c2);
                        c1.isKing = p;

                        if (score > Max)
                            return moves;
                    } else {
                        flag = false;
                        Checker c1 = ch;
                        Checker c2 = mat.get((i + a) / 2).get((k + b) / 2);
                        Checker c3 = mat.get(a).get(b);
                        Checker c4 = new Checker(0, 0, 0);

                        mat.get(i).set(k, c4);
                        mat.get((i + a) / 2).set((k + b) / 2, c3);
                        mat.get(a).set(b, c1);
                        boolean p = c1.isKing;
                        if (a == Board.DIMEN - 1) {
                            c1.isKing = true;
                        }

                        ArrayList<Integer> arr = PlayerB.search(mat, depth - 1, score, Max);
                        if(arr.get(4)!=Integer.MAX_VALUE-1)
                            arr.set(4,arr.get(4)+200+200*(c2.isKing?1:0));

                        if (arr.get(4) > score  && arr.get(4)!=Integer.MAX_VALUE-1) {
                            moves = arr;
                            moves.set(0, i);
                            moves.set(1, k);
                            moves.set(2, a);
                            moves.set(3, b);
                            score = arr.get(4);
                        } else {
                            movesBackup = arr;
                            movesBackup.set(0, i);
                            movesBackup.set(1, k);
                            movesBackup.set(2, a);
                            movesBackup.set(3, b);
                            movesBackup.set(4, Integer.MAX_VALUE-1);
                        }

                        mat.get(i).set(k, c1);
                        mat.get((i + a) / 2).set((k + b) / 2, c2);
                        mat.get(a).set(b, c3);

                        c1.isKing = p;

                        if (score > Max)
                            return moves;
                    }
                }
            }
        }

        if (flag || moves.size() + movesBackup.size() == 0) {
            ArrayList<Integer> arr = new ArrayList<>(5);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(Integer.MAX_VALUE-1);
            return arr;
        }

        if (moves.size() == 0)
            return movesBackup;
        return moves;

    }

    @Override
    public String play() {
        String ans = "";

        int Min = Integer.MIN_VALUE, Max = Integer.MAX_VALUE;
        ArrayList<Integer> arr = new ArrayList<>();

        long startTime = System.nanoTime();
        int i = 0;
        for (i = DEPTH; ; ++i) {
            arr = search(board.mat, i, Min, Max);

            long endTime = System.nanoTime();

            if ((endTime - startTime) * 2 > TIME_LIMIT)
                break;
        }


        ans += "\nDepth:- " + i + "\n";
        if (arr.get(0) == -1) {
            checkersCount = 0;
            ans += "Accept my defeat" + "\n";
            return ans;
        }
        ans += "P1 Played \n" + arr.get(0) + " " + arr.get(1) + " " + arr.get(2) + " " + arr.get(3) + "\n";
        if (Math.abs(arr.get(0) - arr.get(2)) == 1) {
            moveIt(board.mat.get(arr.get(0)).get(arr.get(1)), arr.get(2), arr.get(3));
        } else {
            board.removeIt((arr.get(0) + arr.get(2)) / 2, (arr.get(1) + arr.get(3)) / 2);
            moveIt(board.mat.get(arr.get(0)).get(arr.get(1)), arr.get(2), arr.get(3));
        }
        return ans;
    }

    @Override
    void moveIt(Checker ch, int a, int b) {
        int a1 = ch.a;
        int b1 = ch.b;
        ch.a = a;
        ch.b = b;
        if (a == Board.DIMEN - 1)
            ch.isKing = true;
        board.moveIt(a1, b1, a, b);
    }
}


class PlayerB extends Player {
    final static int DEPTH = 5;
    final static int id = 2;
    private static int[] movesANormal = {-1, -1};
    private static int[] movesBNormal = {-1, 1};
    private static int[] movesAKing = {1, 1};
    private static int[] movesBKing = {-1, 1};

    PlayerB(Board board) {
        super(id, board);
    }

    static ArrayList<ArrayList<Integer>> getNeighbours(ArrayList<ArrayList<Checker>> mat, int i, int k) {
        ArrayList<ArrayList<Integer>> ans = new ArrayList<ArrayList<Integer>>();
        for (int j = 0; j < 2; ++j) {
            if (isValid(i + movesANormal[j]) && isValid(k + movesBNormal[j]) && mat.get(i + movesANormal[j]).get(k + movesBNormal[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + movesANormal[j]);
                a.add(k + movesBNormal[j]);
                ans.add(a);
            }
        }

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + 2 * movesANormal[j]) && isValid(k + 2 * movesBNormal[j]) && mat.get(i + movesANormal[j]).get(k + movesBNormal[j]).id == PlayerW.id && mat.get(i + 2 * movesANormal[j]).get(k + 2 * movesBNormal[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + 2 * movesANormal[j]);
                a.add(k + 2 * movesBNormal[j]);
                ans.add(a);
            }
        }

        if (!mat.get(i).get(k).isKing)
            return ans;

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + movesAKing[j]) && isValid(k + movesBKing[j]) && mat.get(i + movesAKing[j]).get(k + movesBKing[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + movesAKing[j]);
                a.add(k + movesBKing[j]);
                ans.add(a);
            }

        }

        for (int j = 0; j < 2; ++j) {
            if (isValid(i + 2 * movesAKing[j]) && isValid(k + 2 * movesBKing[j]) && mat.get(i + movesAKing[j]).get(k + movesBKing[j]).id == PlayerW.id && mat.get(i + 2 * movesAKing[j]).get(k + 2 * movesBKing[j]).id == 0) {
                ArrayList<Integer> a = new ArrayList<>(2);
                a.add(i + 2 * movesAKing[j]);
                a.add(k + 2 * movesBKing[j]);
                ans.add(a);
            }
        }
        return ans;
    }

    static ArrayList<Integer> search(ArrayList<ArrayList<Checker>> mat, int depth, int Min, int Max) {

        if (depth == 0) {
            ArrayList<Integer> arr = new ArrayList<>(5);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add( evaluate(mat, PlayerB.id));
            return arr;
        }


        ArrayList<Integer> moves = new ArrayList<>(5);
        ArrayList<Integer> movesBackup = new ArrayList<>(5);
        int score = Max;

        boolean flag = true;

        for (int i = 0; i < Board.DIMEN; ++i) {
            for (int k = 0; k < Board.DIMEN; ++k) {
                Checker ch = mat.get(i).get(k);
                if (ch.id != PlayerB.id)
                    continue;

                for (ArrayList<Integer> array : getNeighbours(mat, i, k)) {
                    int a = array.get(0), b = array.get(1);

                    if (Math.abs(i - a) == 1) {
                        flag = false;
                        Checker c1 = ch;
                        Checker c2 = mat.get(a).get(b);
                        mat.get(i).set(k, c2);
                        mat.get(a).set(b, c1);

                        boolean p = c1.isKing;
                        if (a == 0) {
                            c1.isKing = true;
                        }

                        ArrayList<Integer> arr = PlayerB.search(mat, depth - 1, Min, score);
                        if (arr.get(4) < score && arr.get(4)!=Integer.MIN_VALUE+1) {
                            moves = arr;
                            moves.set(0, i);
                            moves.set(1, k);
                            moves.set(2, a);
                            moves.set(3, b);
                            score = arr.get(4);
                        } else {
                            movesBackup = arr;
                            movesBackup.set(0, i);
                            movesBackup.set(1, k);
                            movesBackup.set(2, a);
                            movesBackup.set(3, b);
                            movesBackup.set(4, Integer.MIN_VALUE+1);
                        }
                        mat.get(i).set(k, c1);
                        mat.get(a).set(b, c2);

                        c1.isKing = p;


                        if (score < Min)
                            return moves;
                    } else {
                        flag = false;
                        Checker c1 = ch;
                        Checker c2 = mat.get((i + a) / 2).get((k + b) / 2);
                        Checker c3 = mat.get(a).get(b);
                        Checker c4 = new Checker(0, 0, 0);

                        mat.get(i).set(k, c4);
                        mat.get((i + a) / 2).set((k + b) / 2, c3);
                        mat.get(a).set(b, c1);

                        boolean p = c1.isKing;
                        if (a == 0) {
                            c1.isKing = true;
                        } if (score < Min)
                            return moves;

                        ArrayList<Integer> arr = PlayerB.search(mat, depth - 1, Min, score);
                        if(arr.get(4)!=Integer.MIN_VALUE+1)
                            arr.set(4,arr.get(4)-200-200*(c2.isKing?1:0));

                        if (arr.get(4) < score && arr.get(4)!=Integer.MIN_VALUE+1) {
                            moves = arr;
                            moves.set(0, i);
                            moves.set(1, k);
                            moves.set(2, a);
                            moves.set(3, b);
                            score = arr.get(4);

                        } else {
                            movesBackup = arr;
                            movesBackup.set(0, i);
                            movesBackup.set(1, k);
                            movesBackup.set(2, a);
                            movesBackup.set(3, b);
                            movesBackup.set(4, Integer.MIN_VALUE+1);
                        }

                        mat.get(i).set(k, c1);
                        mat.get((i + a) / 2).set((k + b) / 2, c2);
                        mat.get(a).set(b, c3);

                        c1.isKing = p;


                        if (score < Min)
                            return moves;
                    }
                }
            }
        }

        if (flag || moves.size() + movesBackup.size() == 0) {
            ArrayList<Integer> arr = new ArrayList<>(5);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(-1);
            arr.add(Integer.MIN_VALUE+1);
            return arr;
        }

        if (moves.size() == 0)
            return movesBackup;

        return moves;

    }

    @Override
    public String play(String s) {
        String ans = "";

        try {
            int a, b, c, d;
            ans += "recieved \'" + s + "\'" + "\n";

            if (s.charAt(0) == 'Q') {
                checkersCount = 0;
                return ans;
            }

            String[] arr = s.split(" ", -2);

            if (arr.length < 4)
                throw new Exception();

            a = Integer.parseInt(arr[0]);
            b = Integer.parseInt(arr[1]);
            c = Integer.parseInt(arr[2]);
            d = Integer.parseInt(arr[3]);

            if (!(isValid(a) && isValid(b) && isValid(c) && isValid(d)))
                throw new Exception("a");

            if (board.mat.get(a).get(b).id != id || board.mat.get(c).get(d).id != 0)
                throw new Exception("b");

            if (Math.abs(a - c) != Math.abs(b - d)) {
                throw new Exception("c");
            }

            if ((!(Math.abs(a - c) == 2 && handleKillMove(a, b, c, d))) && (!(Math.abs(a - c) == 1 && handleNormalMove(a, b, c, d)))) {
                throw new Exception("d");
            }


        } catch (Exception e) {
            ans += "Invalid input try again\n" + "\n";
            return ans;
            // e.printStackTrace();

        }

        return ans;
    }

    private boolean handleKillMove(int a, int b, int c, int d) {
        if (board.mat.get((a + c) / 2).get((b + d) / 2).id == id || board.mat.get((a + c) / 2).get((b + d) / 2).id == 0) {
            return false;
        }
        if ((c - a == 2 * movesANormal[0] && d - b == 2 * movesBNormal[0]) || (c - a == 2 * movesANormal[1] && d - b == 2 * movesBNormal[1])) {
            board.removeIt((a + c) / 2, (b + d) / 2);
            moveIt(board.mat.get(a).get(b), c, d);
            return true;
        }

        if (!board.mat.get(a).get(b).isKing)
            return false;

        if ((c - a == 2 * movesAKing[0] && d - b == 2 * movesBKing[0]) || (c - a == 2 * movesAKing[1] && d - b == 2 * movesBKing[1])) {
            board.removeIt((a + c) / 2, (b + d) / 2);
            moveIt(board.mat.get(a).get(b), c, d);
            return true;
        }
        return false;
    }

    private boolean handleNormalMove(int a, int b, int c, int d) {

        if ((c - a == movesANormal[0] && d - b == movesBNormal[0]) || (c - a == movesANormal[1] && d - b == movesBNormal[1])) {
            moveIt(board.mat.get(a).get(b), c, d);
            return true;
        }

        if (!board.mat.get(a).get(b).isKing)
            return false;

        if ((c - a == movesAKing[0] && d - b == movesBKing[0]) || (c - a == movesAKing[1] && d - b == movesBKing[1])) {
            moveIt(board.mat.get(a).get(b), c, d);
            return true;
        }
        return false;
    }

    @Override
    void moveIt(Checker ch, int a, int b) {
        int a1 = ch.a;
        int b1 = ch.b;
        ch.a = a;
        ch.b = b;
        if (a == 0)
            ch.isKing = true;
        board.moveIt(a1, b1, a, b);
    }
}