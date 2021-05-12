package sample;

import javafx.application.Platform;

import java.util.ArrayList;

class Board {
    public static final int DIMEN = 8;
    ArrayList<ArrayList<Checker>> mat;
    Controller controller;
    int lastDeath;
    int current;
    int P1Count;
    int P2Count;
    boolean running, computerTurn;
    private Player p1, p2;


    Board(Controller controller) {
        this.controller = controller;
        p1 = new PlayerW(this);
        p2 = new PlayerB(this);
        mat = new ArrayList<>(8);

        int k = 0;
        for (int i = 0; i < 3; ++i) {
            ArrayList<Checker> a1 = new ArrayList<Checker>(8);
            for (int j = 0; j < DIMEN; ++j) {
                if ((j & 1) == k) {
                    Checker c = new Checker(i, j, p1.getId());
                    a1.add(c);
                    p1.addChecker(c);
                } else a1.add(new Checker(i, j, 0));
            }
            mat.add(a1);

            k ^= 1;
        }

        for (int i = 3; i < 5; ++i) {
            ArrayList<Checker> a1 = new ArrayList<Checker>(8);
            for (int j = 0; j < 8; ++j) {
                a1.add(new Checker(i, j, 0));
            }
            mat.add(a1);
        }

        k = 1;
        for (int i = 5; i < DIMEN; ++i) {
            ArrayList<Checker> a1 = new ArrayList<Checker>(8);
            for (int j = 0; j < DIMEN; ++j) {
                if ((j & 1) == k) {
                    Checker c = new Checker(i, j, p2.getId());
                    a1.add(c);
                    p2.addChecker(c);
                } else a1.add(new Checker(i, j, 0));
            }
            mat.add(a1);
            k ^= 1;
        }

    }

    void computerMove() {
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                String s = p1.play();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.output.setText(s);
                    }
                });

                if (p1.getCheckersCount() == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.output.setText("======================P2 Wins======================");
                        }
                    });

                    running = false;
                }
                if (p1.getCheckersCount() < P1Count) {
                    P1Count = p1.getCheckersCount();
                    lastDeath = current;
                }
                current++;
                if (current - lastDeath > 50) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.output.setText("======================TIE======================");
                        }
                    });

                    running = false;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.output.appendText("\nEnter the space separated starting and destination coordinates " + "\nExample-> to move from (A,B) to (C,D) type A B C D in one line\nIf You want to quit press Q\n" + "\n");
                    }
                });

                computerTurn = false;
            }
        });
        T.setDaemon(true);
        T.start();
    }

    void humanMove(String move) {
        if (!running) {
            controller.output.appendText("\n Game is finished\n");
        } else if (computerTurn) {
            controller.output.setText("Its computer turn Please wait\n");
        } else {
            String s = p2.play(move);

            controller.output.setText(s);

            if (move.length() + 13 < s.length())
                return;

            if (p2.getCheckersCount() == 0) {
                controller.output.setText("======================P1 Wins======================");
                running = false;
            }
            if (p2.getCheckersCount() < P2Count) {
                P1Count = p2.getCheckersCount();
                lastDeath = current;
            }
            current++;
            if (current - lastDeath > 50) {
                controller.output.setText("======================TIE======================");
                running = false;
            }

            computerTurn = true;
            if (running)
                computerMove();
        }
    }

    void start() {
        lastDeath = -1;
        current = 0;
        P1Count = p1.getCheckersCount();
        P2Count = p2.getCheckersCount();

        computerTurn = true;
        running = true;

        computerMove();
    }

    void removeIt(int a, int b) {
        Checker ch = mat.get(a).get(b);
        mat.get(a).set(b, new Checker(a, b, 0));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.removeIt(a, b);
            }
        });

        if (ch.id == p1.getId())
            p1.removeChecker(ch);
        else p2.removeChecker(ch);
    }


    private void printMat() {
        for (int i = 0; i < DIMEN; ++i) {
            for (int j = 0; j < DIMEN; ++j) {
                System.out.print(mat.get(i).get(j).id);
            }
            System.out.println();
        }

        System.out.println("=====");
    }

    void moveIt(int a1, int b1, int a2, int b2) {
        final boolean king = mat.get(a1).get(b1).isKing;

        mat.get(a2).set(b2, mat.get(a1).get(b1));
        mat.get(a1).set(b1, new Checker(a1, b1, 0));


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.moveIt(a1, b1, a2, b2, king);
            }
        });
    }
}

