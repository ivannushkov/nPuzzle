package com.nPuzzle;

public record Tile(int x, int y) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Tile tile = (Tile) obj;
        return this.x() == tile.x() && this.y() == tile.y();
    }
}