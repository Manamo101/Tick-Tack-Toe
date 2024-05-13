package org.example;

public class GameEnding {
    public static int[] doesWin(String sign, String[] board) {
        if (board[0].equals(sign) && board[1].equals(sign) && board[2].equals(sign)){
            return new int[]{0, 1, 2};
        }
        else if (board[3].equals(sign) && board[4].equals(sign) && board[5].equals(sign)){
            return new int[]{3, 4, 5};
        }
        else if (board[6].equals(sign) && board[7].equals(sign) && board[8].equals(sign)){
            return new int[]{6, 7, 8};
        }
        else if (board[0].equals(sign) && board[3].equals(sign) && board[6].equals(sign)){
            return new int[]{0, 3, 6};
        }
        else if (board[1].equals(sign) && board[4].equals(sign) && board[7].equals(sign)){
            return new int[]{1, 4, 7};
        }
        else if (board[2].equals(sign) && board[5].equals(sign) && board[8].equals(sign)){
            return new int[]{2, 5, 8};
        }
        else if (board[0].equals(sign) && board[4].equals(sign) && board[8].equals(sign)){
            return new int[]{0, 4, 8};
        }
        else if (board[2].equals(sign) && board[4].equals(sign) && board[6].equals(sign)){
            return new int[]{2, 4, 6};
        }
        else {
            return null;
        }
    }

    public static boolean isDraw(String[] board) {
        for (String cell: board) {
            if (cell.isEmpty()){
                return false;
            }
        }
        return true;
    }
}
