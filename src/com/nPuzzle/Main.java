package com.nPuzzle;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static Scanner scanner = new Scanner(System.in);
    public static int finalZeroPosition;
    public static int numberOfRows;

    public static void main(String[] args) {
        int numberOfTiles;
        System.out.print("Enter number of tiles: ");
        numberOfTiles = scanner.nextInt();

        System.out.print("Enter the final Zero position: ");
        finalZeroPosition = scanner.nextInt();

        Tile initialEmptyTile = new Tile(0, 0);
        numberOfRows = (int) Math.sqrt(numberOfTiles + 1);
        int[][] board = new int[numberOfRows][numberOfRows];

        System.out.println("Enter puzzle:\n");
        for(int i = 0; i < numberOfRows; i++){
            for(int j = 0; j < numberOfRows; j++){
                board[i][j] = scanner.nextInt();
                if(board[i][j] == 0)
                    initialEmptyTile = new Tile(i, j);
            }
        }

        if(!isSolvable(numberOfRows, board)){
            System.out.println("No solution!");
            System.exit(0);
        }

        ArrayList<Tile> resultPath = solvePuzzle(board, initialEmptyTile);

        System.out.print("\nSteps needed to resolve the puzzle: ");
        System.out.println(resultPath.size() - 1 + "\n");

        for(int i = 0; i < resultPath.size() - 1; i++){
            System.out.println(getDirections(resultPath.get(i), resultPath.get(i + 1)));
        }
    }

    public static ArrayList<Tile> generateNeighbours(Tile currentTile, Tile previousTile){
        ArrayList<Tile> neighbourTiles = new ArrayList<>();
        int x = currentTile.x();
        int y = currentTile.y();

        if(x > 0){
            Tile tempTile = new Tile(x - 1, y);
            if(!tempTile.equals(previousTile))
                neighbourTiles.add(tempTile);
        }

        if(y > 0){
            Tile tempTile = new Tile(x, y - 1);
            if(!tempTile.equals(previousTile))
                neighbourTiles.add(tempTile);
        }

        if(x < numberOfRows - 1){
            Tile tempTile = new Tile(x + 1, y);
            if(!tempTile.equals(previousTile))
                neighbourTiles.add(tempTile);
        }

        if(y < numberOfRows - 1){
            Tile tempTile = new Tile(x, y + 1);
            if(!tempTile.equals(previousTile))
                neighbourTiles.add(tempTile);
        }

        return neighbourTiles;
    }

    public static int getManhattanDistance(int tileValue, int tileX, int tileY){
        int destX = (tileValue - 1) / numberOfRows;
        int destY = (tileValue - 1) % numberOfRows;

        if(finalZeroPosition != -1 && tileValue > finalZeroPosition){
            destX = tileValue / numberOfRows;
            destY = tileValue % numberOfRows;
        }

        return (Math.abs(tileX - destX) + Math.abs(tileY - destY));
    }

    public static int heuristicValue(int[][] board){
        int hValue = 0;

        for(int i = 0; i < numberOfRows; i++){
            for(int j = 0; j < numberOfRows; j++){
                int tileValue = board[i][j];
                if(tileValue != 0)
                    hValue += getManhattanDistance(tileValue, i, j);
            }
        }

        return hValue;
    }

    public static void swapTiles(int[][] board, ArrayList<Tile> path){
        ArrayList<Tile> tempPath = new ArrayList<>(path);

        int nextX = tempPath.get(tempPath.size() - 1).x();
        int nextY = tempPath.get(tempPath.size() - 1).y();
        tempPath.remove(tempPath.size() - 1);

        int currentX = tempPath.get(tempPath.size() - 1).x();
        int currentY = tempPath.get(tempPath.size() - 1).y();

        int temp = board[currentX][currentY];
        board[currentX][currentY] = board[nextX][nextY];
        board[nextX][nextY] = temp;
    }

    public static int slideTiles(int[][] board, int g, int threshold, ArrayList<Tile> path, Tile prevTile){
        int h = heuristicValue(board);
        int f = g + h;

        if(f > threshold)
            return f;

        if(h == 0)
            return -1;

        int min = Integer.MAX_VALUE;

        Tile current = path.get(path.size() - 1);
        for(Tile neighbour : generateNeighbours(current, prevTile)){
            path.add(neighbour);
            prevTile = current;
            swapTiles(board, path);

            int temp = slideTiles(board, g + 1, threshold, path, prevTile);

            if(temp == -1)
                return -1;

            if(temp < min)
                min = temp;

            swapTiles(board, path);
            path.remove(path.size() - 1);
        }

        return min;
    }

    public static ArrayList<Tile> solvePuzzle(int[][] board, Tile initialEmptyTile){
        ArrayList<Tile> currentPath = new ArrayList<>();
        currentPath.add(initialEmptyTile);

        int threshold = heuristicValue(board);

        while(true){
            int temp = slideTiles(board, 0, threshold, currentPath, new Tile(-1, -1));

            if(temp == -1)
                return currentPath;

            threshold = temp;
        }
    }

    public static String getDirections(Tile current, Tile next){
        int currentX = current.x(), currentY = current.y();
        int nextX = next.x(), nextY = next.y();

        if(currentX > nextX)
            return "down";
        else if(currentX < nextX)
            return "up";
        else if(currentY > nextY)
            return "right";
        else
            return "left";
    }

    public static int getInversionsCount(ArrayList<Integer> list){
        int inversionsCount = 0;

        for(int i = 0; i < list.size() - 1; i++){
            for(int j = i + 1; j < list.size(); j++){
                if(list.get(i) > list.get(j))
                    inversionsCount++;
            }
        }

        return inversionsCount;
    }

    public static boolean isSolvable(int numberOfRows, int[][] board){
        ArrayList<Integer> puzzleValues = new ArrayList<>();
        int zeroTileRow = -1;

        for(int i = 0; i < numberOfRows; i++){
            for(int j = 0; j < numberOfRows; j++){
                if(board[i][j] != 0)
                    puzzleValues.add(board[i][j]);
                else
                    zeroTileRow = i;
            }
        }

        int inversionsCount = getInversionsCount(puzzleValues);
        boolean boardSizeOdd = numberOfRows / 2 != 0;

        if((inversionsCount == 0 && zeroTileRow == numberOfRows - 1) || boardSizeOdd && (inversionsCount / 2 != 0))
            return true;
        else
            return !boardSizeOdd && (zeroTileRow + inversionsCount / 2 == 0);
    }
}