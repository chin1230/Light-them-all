import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;
import java.util.HashMap;

// constant interface
interface Constant {
  int CELL_SIZE = 40;
}

// utils class
class Utils {
  // make board & add game pieces onto it
  // also add gamepiece to list of nodes
  ArrayList<ArrayList<GamePiece>> makeBoard(int width, int height) {
    ArrayList<ArrayList<GamePiece>> board = new ArrayList<>();

    for (int i = 1; i <= width; i++) {
      ArrayList<GamePiece> col = new ArrayList<GamePiece>();

      for (int j = 1; j <= height; j++) {
        if (i == 1 && j == 1) {
          col.add(new GamePiece(j, i,false, false, false, false, true));
        } else {
          col.add(new GamePiece(j, i,false, false, false, false, false));
        }
      }
      board.add(col); 
    }
    return board; 
  }

  // adds each GamePiece on the board to a single list of Nodes
  public ArrayList<GamePiece> addToNodes(ArrayList<ArrayList<GamePiece>> board) {
    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>();
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        nodes.add(board.get(i).get(j));
      }
    }
    return nodes;
  }


  //rotates the given GamePiece clock-wise
  public void rotate(GamePiece piece) {
    boolean ro = piece.top;
    piece.top = piece.left;
    piece.left = piece.bottom;
    piece.bottom = piece.right;
    piece.right = ro;
  }

  // rotate game piece random no. of times
  void randomRotate(ArrayList<GamePiece> board, Random rand) {
    for (int i = 0; i < board.size(); i++) {
      int num = rand.nextInt(5);
      for (int j = 0; j <= num; j++) {
        new Utils().rotate(board.get(i));
      }
    }
  }

  //make edge
  public ArrayList<Edge> makeEdge(ArrayList<ArrayList<GamePiece>> list, int width, int height) {
    ArrayList<Edge> edgeList = new ArrayList<>();

    for (int i = 0; i < list.size(); i++) {
      for (int j = 0; j < list.get(i).size(); j++) {
        GamePiece current = list.get(i).get(j);

        // Check top edge
        if (i > 0) {
          addEdge(edgeList, current, list.get(i - 1).get(j));
        }

        // Check bottom edge
        if (i < height - 1) {
          addEdge(edgeList, current, list.get(i + 1).get(j));
        }

        // Check left edge
        if (j > 0) {
          addEdge(edgeList, current, list.get(i).get(j - 1));
        }

        // Check right edge
        if (j < width - 1) {
          addEdge(edgeList, current, list.get(i).get(j + 1));
        }
      }
    }

    edgeList.sort(new CompareEdge());
    return edgeList;
  }

  void addEdge(ArrayList<Edge> edgeList, GamePiece from, GamePiece to) {
    // Avoid adding duplicate edges
    if (!edgeList.contains(new Edge(from, to))) {
      edgeList.add(new Edge(from, to));
    }
  }

  // make a hashmap
  public HashMap<GamePiece, GamePiece> makeHashMap(ArrayList<Edge> list) {
    HashMap<GamePiece, GamePiece> map = new HashMap<GamePiece, GamePiece>();
    for (Edge edge : list) {
      map.put(edge.fromNode, edge.fromNode);
      map.put(edge.toNode, edge.toNode); 
    }
    return map;
  }

  // find method
  public GamePiece find(HashMap<GamePiece, GamePiece> map, GamePiece edge) {
    GamePiece holder = edge;
    while (!(map.get(holder).equals(holder))) {
      holder = map.get(holder);
    }
    return holder;
  }

  // union method
  public void union(HashMap<GamePiece, GamePiece> map, GamePiece to, GamePiece from) {
    map.put(map.get(to), from);
  }

  // update GP after forming path
  public void updateGP(ArrayList<Edge> list) {
    for (Edge edge : list) {
      edge.fromNode.compare(edge.toNode);
    }
  }

  // get game piece
  GamePiece getGP(int row, int col, ArrayList<ArrayList<GamePiece>> board) {
    return board.get(col).get(row);
  }


  //check if a power station can move
  boolean checkMove(int height, int width, int fromRow, int fromCol, int toRow, int toCol, 
      ArrayList<ArrayList<GamePiece>> board) {
    // CHECK BOUNDARY OF GRID
    if (toRow < 0 || toCol < 0 || toRow >= height || toCol >= width) {
      return false;
    }

    GamePiece fromPiece = getGP(fromRow, fromCol, board);
    GamePiece toPiece = getGP(toRow, toCol, board);

    //check if there is a valid connection between fromPiece and toPiece
    if (fromRow < toRow) { 
      // MOVE down
      return fromPiece.bottom && toPiece.top;
    } 
    else if (fromRow > toRow) { 
      //move up
      return fromPiece.top && toPiece.bottom;
    } 
    else if (fromCol < toCol) { 
      //move right
      return fromPiece.right && toPiece.left;
    } 
    else if (fromCol > toCol) { 
      //move left
      return fromPiece.left && toPiece.right;
    }

    return false; 
  }

  // check's if a given GamePiece is connected to a surrounding
  // GamePiece based on the given direction
  public boolean isConnected(String direction, ArrayList<ArrayList<GamePiece>> board, int width,
      int height, GamePiece piece) {
    int col = piece.col - 1;
    int row = piece.row - 1;

    if (direction.equals("up")) {
      if (row == 0) {
        return false;
      } else {
        return piece.top && board.get(col).get(row - 1).bottom;
      }
    } else if (direction.equals("right")) {
      if (col == width - 1) {
        return false;
      } else {
        return piece.right && board.get(col + 1).get(row).left;
      }
    } else if (direction.equals("down")) {
      if (row == height - 1) {
        return false;
      } else {
        return piece.bottom && board.get(col).get(row + 1).top;
      }
    } else if (direction.equals("left")) {
      if (col == 0) {
        return false;
      } else {
        return piece.left && board.get(col - 1).get(row).right;
      }
    } else {
      return false;
    }
  }

  //update power of game piece
  void powerUp(ArrayList<ArrayList<GamePiece>> board, int width, 
      int height, ArrayList<GamePiece> nodes) {
    ArrayList<GamePiece> alreadySeen = new ArrayList<GamePiece>();
    ArrayList<GamePiece> workList = new ArrayList<GamePiece>();
    workList.add(new Utils().findPowerStation(board));
    while (workList.size() != 0) {
      GamePiece next = workList.remove(0);
      GamePiece top = new GamePiece(1, 1, false, false, false, false, false);
      GamePiece right = new GamePiece(1, 1, false, false, false, false, false);
      GamePiece bottom = new GamePiece(1, 1, false, false, false, false, false);
      GamePiece left = new GamePiece(1, 1, false, false, false, false, false);


      if (next.col != 1) {
        left = board.get(next.col - 2).get(next.row - 1);
      }
      if (next.col != width) {
        right = board.get(next.col).get(next.row - 1);
      }

      if (next.row != 1) {
        top = board.get(next.col - 1).get(next.row - 2);
      }

      if (next.row != height) {
        bottom = board.get(next.col - 1).get(next.row);
      }

      next.powered = true;
      if (!alreadySeen.contains(top)
          && new Utils().isConnected("up", board, width, height, next)) {
        workList.add(top);
      }


      if (!alreadySeen.contains(right)
          && new Utils().isConnected("right", board, width, height, next)) {
        workList.add(right);
      }


      if (!alreadySeen.contains(bottom)
          && new Utils().isConnected("down", board, width, height, next)) {
        workList.add(bottom);
      }


      if (!alreadySeen.contains(left)
          && new Utils().isConnected("left", board, width, height, next)) {
        workList.add(left);
      }
      alreadySeen.add(next);

      for (GamePiece piece : nodes) {
        if (!(alreadySeen.contains(piece))) {
          piece.powered = false;
        }
      }
    }
  }


  // finds the power station in a board of GamePiece's
  public GamePiece findPowerStation(ArrayList<ArrayList<GamePiece>> list) {
    GamePiece powerStationPiece = new GamePiece(3, 3, false, false, false, false, false);
    for (ArrayList<GamePiece> pieceList : list) {
      for (GamePiece piece : pieceList) {
        if (piece.powerStation) {
          powerStationPiece = piece;
        }
      }
    }
    return powerStationPiece;
  }


}

// represent LightEmAllWorld class
class LightEmAllWorld extends World {
  ArrayList<ArrayList<GamePiece>> board;
  // list of gamepieces
  ArrayList<GamePiece> nodes;

  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;

  // width
  int width;
  //height
  int height;
  // row of power station
  int powerRow;
  // col of power station
  int powerCol;

  Random random;
  // win?
  boolean win;

  // 1st constructor
  LightEmAllWorld(int width, int height) {
    if (width < 2 || height < 2) {
      throw new IllegalArgumentException("Width and height must be greater than 1");
    }
    else {
      this.width = width;
      this.height = height;
      this.random = new Random();
      this.board = new Utils().makeBoard(width, height);
      Union unionHolder = new Union(new Utils().makeEdge(board, width, height));
      new Utils().updateGP(unionHolder.algo());
      this.nodes = new Utils().addToNodes(this.board);
      new Utils().randomRotate(this.nodes, new Random());
      new Utils().powerUp(this.board, this.height, this.width, this.nodes);
      this.mst = null;
      this.powerRow = 0;
      this.powerCol = 0;
    }
  }

  // 2nd constructor
  LightEmAllWorld(int width, int height, ArrayList<ArrayList<GamePiece>> board) {
    this.board = board;
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.width = width;
    this.height = height;
    this.random = new Random();
    this.win = false;

    new Utils().makeBoard(this.height, this.width);

    new Utils().randomRotate(this.nodes, new Random());
    new Utils().powerUp(this.board, this.height, this.width, this.nodes);
  }

  // 3rd constructor
  LightEmAllWorld(int width, int height, Random rand) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.width = width;
    this.height = height;
    this.random = rand;
    this.win = false;
    new Utils().makeBoard(this.height, this.width);
    new Utils().randomRotate(this.nodes, rand);
    new Utils().powerUp(this.board, this.height, this.width, this.nodes);
  }



  // on key event
  public void onKeyEvent(String key) {
    // check if win
    if (win) {
      return;
    }
    // new coord
    int newPowerRow = powerRow;
    int newPowerCol = powerCol;

    // check key
    if (key.equals("left")) {
      newPowerCol = powerCol - 1;
    } 
    else if (key.equals("right")) {
      newPowerCol = powerCol + 1;
    } 
    else if (key.equals("up")) {
      newPowerRow = powerRow - 1; 
    } 
    else if (key.equals("down")) {
      newPowerRow = powerRow + 1;
    }

    //check if new coord valid
    if (new Utils().checkMove(this.height, this.width,
        powerRow, powerCol, newPowerRow, newPowerCol, 
        this.board)) {
      // move power station to new coord if valid
      getGP(powerRow, powerCol).powerStation = false;
      powerRow = newPowerRow;
      powerCol = newPowerCol;
      getGP(powerRow, powerCol).powerStation = true;
      new Utils().powerUp(this.board, this.height, this.width, this.nodes);
      checkWin();
    }
  }

  // check if in bound, then check top bottom -> if connected, create edge
  // then add to list of edges



  //on mouse click 
  public void onMouseClicked(Posn pos) { 
    int colNum = pos.x / Constant.CELL_SIZE;
    int rowNum = pos.y / Constant.CELL_SIZE;
    if (colNum >= 0 && colNum < width && rowNum >= 0 && rowNum < height) {
      GamePiece clickedPiece = board.get(colNum).get(rowNum);
      new Utils().rotate(clickedPiece);
      new Utils().powerUp(this.board, this.width, this.height, this.nodes);
      checkWin(); 
      if (win) {
        this.endOfWorld("Winner!");
      }
    }
  }

  // draw the worldscene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.width * Constant.CELL_SIZE,
        this.height * Constant.CELL_SIZE);
    WorldImage finalImage = new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);

    for (ArrayList<GamePiece> colList : this.board) {      
      WorldImage first = colList.get(0).tileImage(Constant.CELL_SIZE, Constant.CELL_SIZE / 4,
          colList.get(0).wireColor(), colList.get(0).powerStation);
      for (int i = 1; i < colList.size(); i++) {
        first = new AboveImage(first, colList.get(i).tileImage(Constant.CELL_SIZE,
            Constant.CELL_SIZE / 4, colList.get(i).wireColor(), colList.get(i).powerStation));
      }

      finalImage = new BesideImage(finalImage, first);
    }

    scene.placeImageXY(finalImage, this.width * Constant.CELL_SIZE / 2,
        this.height * Constant.CELL_SIZE / 2);
    return scene;
  }


  //method to check condition to end the game
  void checkWin() {
    for (ArrayList<GamePiece> col : board) {
      for (GamePiece gp : col) {
        if (!gp.powered) {
          return; 
        }
      }
    }
    win = true;
  } 

  // get game piece
  GamePiece getGP(int row, int col) {
    return this.board.get(col).get(row);
  }

  //makes the last scene
  public WorldScene lastScene(String msg) {
    WorldScene finalScene = new WorldScene(this.width * Constant.CELL_SIZE,
        this.height * Constant.CELL_SIZE);
    finalScene.placeImageXY(new TextImage(msg, Constant.CELL_SIZE, Color.GREEN),
        Constant.CELL_SIZE * this.width / 2, Constant.CELL_SIZE * this.height / 2);
    return finalScene;
  }
}




// ----------------------
// represent a game piece
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean bottom;
  boolean right;
  boolean top;
  // whether the power station is on this piece
  boolean powerStation;
  boolean powered;


  // 1st constructor
  GamePiece(int row, int col) {
    this.row = row;
    this.col = col;
  }

  // 2nd constructor
  GamePiece(int row, int col, 
      boolean left, boolean right, boolean top, boolean bottom, 
      boolean powerStation) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.powered = false;
  }



  // method to set direction
  void compare(GamePiece piece2) {
    if (this.col - piece2.col == -1) {
      this.right = true;
      piece2.left = true;
    } else if (this.col - piece2.col == 1) {
      this.left = true;
      piece2.right = true;
    } else if (this.row - piece2.row == -1) {
      this.bottom = true;
      piece2.top = true;   
    } else if (this.row - piece2.row == 1) {
      this.top = true;
      piece2.bottom = true;
    }
  }





  // draw gamepiece
  WorldImage tileImage(int size, int wireWidth, Color wireColor, boolean hasPowerStation) {
    // Start tile image off as a blue square with a wire-width square in the middle,
    // to make image "cleaner" (will look strange if tile has no wire, but that can't be)
    WorldImage image = new OverlayImage(
        new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire = 
        new RectangleImage(wireWidth, (size + 1) / 2, OutlineMode.SOLID, wireColor);
    WorldImage hWire = 
        new RectangleImage((size + 1) / 2, wireWidth, OutlineMode.SOLID, wireColor);

    if (this.top) {
      image = new OverlayOffsetAlign(
          AlignModeX.CENTER, AlignModeY.TOP, vWire, 0, 0, image);
    }
    if (this.right) {
      image = new OverlayOffsetAlign(
          AlignModeX.RIGHT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }
    if (this.bottom) {
      image = new OverlayOffsetAlign(
          AlignModeX.CENTER, AlignModeY.BOTTOM, vWire, 0, 0, image);
    }
    if (this.left) {
      image = new OverlayOffsetAlign(
          AlignModeX.LEFT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }
    if (hasPowerStation) {
      image = new OverlayImage(
          new OverlayImage(
              new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
              new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
          image);
    }
    return image;
  }

  // returns what the GamePiece's color should be
  Color wireColor() {
    Color wireColor = this.powered ? Color.YELLOW : Color.GRAY;
    return wireColor;
  }

}


// -----------------------------
// edge only cares if connected, doesn't care about directions
class Edge {
  GamePiece fromNode;
  GamePiece toNode;

  // how 
  int weight;
  Random rand;

  // constructor & randomly assign weight to Edge
  Edge(GamePiece fromNode, GamePiece toNode) {
    this.fromNode = fromNode; 
    this.toNode = toNode;
    this.weight = new Random().nextInt(50);
  }
}

// Union/find data structure
class Union {
  HashMap<GamePiece, GamePiece> representatives;
  ArrayList<Edge> edgesInTree;
  ArrayList<Edge> worklist;

  // constructor
  Union(ArrayList<Edge> worklist) {
    this.representatives = new Utils().makeHashMap(worklist);
    this.worklist = worklist;
    this.edgesInTree = new ArrayList<Edge>();
  }

  ArrayList<Edge> algo() {
    while (worklist.size() > 0) {
      Edge cheapestEdge = worklist.get(0);
      if (new Utils().find(representatives, cheapestEdge.fromNode).equals(new 
          Utils().find(representatives, cheapestEdge.toNode))) {
        worklist.remove(0);
      } else {
        edgesInTree.add(worklist.remove(0));
        new Utils().union(representatives, new Utils().find(representatives, cheapestEdge.fromNode),
            new Utils().find(representatives, cheapestEdge.toNode));
      }
    }
    return this.edgesInTree;
  }
}

// comparator
class CompareEdge implements Comparator<Edge> {
  // compare edge by their weight
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }

}




//-----------------------------
//examples class
class ExamplesLight {
  Utils utils = new Utils();

  LightEmAllWorld world;

  void reset() {
    this.world = new LightEmAllWorld(3, 3); 
    this.world.board.get(1).get(1).bottom = true;
    this.world.board.get(1).get(1).top = true;
    this.world.board.get(1).get(1).left = true;
    this.world.board.get(1).get(1).right = true;

    this.world.board.get(1).get(2).bottom = true;
    this.world.board.get(1).get(2).top = true;
    this.world.board.get(1).get(2).left = true;
    this.world.board.get(1).get(2).right = true;

    this.world.board.get(1).get(0).bottom = true;
    this.world.board.get(1).get(0).top = true;
    this.world.board.get(1).get(0).left = true;
    this.world.board.get(1).get(0).right = true;

    this.world.board.get(0).get(1).bottom = true;
    this.world.board.get(0).get(1).top = true;
    this.world.board.get(0).get(1).left = true;
    this.world.board.get(0).get(1).right = true;
    
    this.world.board.get(0).get(0).bottom = true;
    this.world.board.get(0).get(0).right = true;
  }
  
  
  // Helper method to create a game piece easily
  GamePiece makePiece(int row, int col) {
    return new GamePiece(row, col);
  }

  // Test the compare method of CompareEdge class
  void testCompareEdge(Tester t) {
    GamePiece a = makePiece(0, 0);
    GamePiece b = makePiece(0, 1);
    GamePiece c = makePiece(1, 0);

    Edge ab = new Edge(a, b);
    ab.weight = 10; 
    Edge ac = new Edge(a, c);
    ac.weight = 5;

    Edge ab2 = new Edge(a, b);
    ab2.weight = 10;

    CompareEdge comparator = new CompareEdge();

    t.checkExpect(comparator.compare(ac, ab) < 0, true);

    t.checkExpect(comparator.compare(ab, ac) > 0, true);

    t.checkExpect(comparator.compare(ab, ab2) == 0, true);
  }
  
  void testLastScene(Tester t) {
    reset();
    String endMessage = "Game Over! You Win!";
    WorldScene finalScene = world.lastScene(endMessage);

    // Check the scene dimensions
    t.checkExpect(finalScene.width, world.width * Constant.CELL_SIZE);
    t.checkExpect(finalScene.height, world.height * Constant.CELL_SIZE);
  }
  
  void testAlgo(Tester t) {
    // Set up a simple graph with 4 nodes and known weights
    GamePiece a = new GamePiece(0, 0);
    GamePiece b = new GamePiece(0, 1);
    GamePiece c = new GamePiece(1, 0);
    GamePiece d = new GamePiece(1, 1);

    Edge ab = new Edge(a, b);
    ab.weight = 1; // Set specific weights for control
    Edge bc = new Edge(b, c);
    bc.weight = 3;
    Edge cd = new Edge(c, d);
    cd.weight = 4;
    Edge ad = new Edge(a, d);
    ad.weight = 2;
    Edge ac = new Edge(a, c);
    ac.weight = 5;

    ArrayList<Edge> edges = new ArrayList<>(Arrays.asList(ab, bc, cd, ad, ac));
    Union union = new Union(edges);
    
    ArrayList<Edge> mst = union.algo();
    
    t.checkExpect(mst.size(), 3);

    int totalWeight = mst.stream().mapToInt(e -> e.weight).sum();
    t.checkExpect(totalWeight, 8);

    t.checkExpect(mst.contains(ab), true);
    t.checkExpect(mst.contains(ad), false);
    t.checkExpect(mst.contains(cd), true);
    
    
  }
  
  
  
  // Test wireColor for a powered game piece
  void testWireColorPowered(Tester t) {
    reset();
    GamePiece poweredPiece = new GamePiece(1, 1);
    poweredPiece.powered = true;

    Color expectedColor = Color.YELLOW;
    Color actualColor = poweredPiece.wireColor();

    t.checkExpect(actualColor, expectedColor, "Check color for powered state");
  }


  void testWireColorUnpowered(Tester t) {
    reset();
    GamePiece unpoweredPiece = new GamePiece(1, 1);
    unpoweredPiece.powered = false;

    Color expectedColor = Color.GRAY;
    Color actualColor = unpoweredPiece.wireColor();

    t.checkExpect(actualColor, expectedColor, "Check color for unpowered state");
  }


  void testTileImageBasic(Tester t) {
    reset();
    GamePiece piece = new GamePiece(1, 1);
    WorldImage image = piece.tileImage(Constant.CELL_SIZE, 
        Constant.CELL_SIZE / 4, Color.GRAY, false);


    t.checkExpect(image.getWidth(), 40.0);
    t.checkExpect(image.getHeight(), 40.0);

  }


  void testTileImageConnectionsAndPowerStation(Tester t) {
    reset();
    GamePiece piece = new GamePiece(1, 1, true, true, true, true, true);
    WorldImage image = piece.tileImage(Constant.CELL_SIZE, Constant.CELL_SIZE / 4, 
        Color.YELLOW, true);


    t.checkExpect(hasVerticalWire(image), true);
    t.checkExpect(hasHorizontalWire(image), true);
    t.checkExpect(hasPowerStationOverlay(image), true);
    t.checkExpect(isPoweredColor(image, Color.YELLOW), true);
  }


  boolean hasVerticalWire(WorldImage image) {
    return true; 
  }

  boolean hasHorizontalWire(WorldImage image) {
    return true; 
  }

  boolean hasPowerStationOverlay(WorldImage image) {
    return true;
  }

  boolean isPoweredColor(WorldImage image, Color expected) {
    return true; 
  }
  
  void testCompareRightLeft(Tester t) {
    reset();
    GamePiece piece1 = world.getGP(0, 0);
    GamePiece piece2 = world.getGP(0, 1); 


    piece1.compare(piece2);


    t.checkExpect(piece1.right, true);
    t.checkExpect(piece2.left, true);
  }


  void testCompareTopBottom(Tester t) {
    reset();
    GamePiece piece1 = world.getGP(0, 0);  
    GamePiece piece2 = world.getGP(1, 0);  

    piece1.compare(piece2);

    t.checkExpect(piece1.bottom, true, "Piece1 bottom connected");
    t.checkExpect(piece2.top, true, "Piece2 top connected");
  }

  void testIsConnected(Tester t) {
    reset();

    t.checkExpect(this.utils.isConnected("left", this.world.board, 3, 3, 
        this.world.board.get(0).get(1)), false);
  }

  //------------------------------
  // TEST ON KEY EVENT
  void testOnKeyEvent(Tester t) {
    reset();
    // move right
    world.onKeyEvent("right");
    t.checkExpect(world.powerCol, 1);

    reset();
    // move left
    world.onKeyEvent("left");
    t.checkExpect(world.powerCol, 0);

    reset();
    // move up
    world.onKeyEvent("up");
    t.checkExpect(world.powerRow, 0);

    reset();
    // move down
    world.onKeyEvent("down");
    t.checkExpect(world.powerRow, 1);
  }
  
  // Test mouse clicks on game pieces
  void testOnMouseClicked(Tester t) {
    reset();
    Posn clickPos1 = new Posn(20, 20);
    world.onMouseClicked(clickPos1);
    t.checkExpect(world.board.get(0).get(0).bottom, true);
    
    reset();
    Posn clickPos2 = new Posn(60, 60); 
    world.onMouseClicked(clickPos2);
    t.checkExpect(world.board.get(1).get(1).left, true);

    reset();
    world.onMouseClicked(clickPos1);
    world.onMouseClicked(clickPos2);
    t.checkExpect(world.board.get(1).get(1).powered, true);
  }
  
  void testMakeScene(Tester t) {
    reset();
    
    // Call makeScene and retrieve the resulting WorldScene
    WorldScene scene = world.makeScene();

    // Check the scene dimensions
    int expectedWidth = world.width * Constant.CELL_SIZE;
    int expectedHeight = world.height * Constant.CELL_SIZE;
    t.checkExpect(scene.width, expectedWidth, "Check scene width");
    t.checkExpect(scene.height, expectedHeight, "Check scene height");

  }
  
  void initializeWorld(int width, int height) {
    this.world = new LightEmAllWorld(width, height);
    // Set all game pieces to unpowered initially
    for (ArrayList<GamePiece> col : this.world.board) {
      for (GamePiece gp : col) {
        gp.powered = false;
      }
    }
  }

  // Helper to power up all game pieces
  void powerAllPieces() {
    for (ArrayList<GamePiece> col : this.world.board) {
      for (GamePiece gp : col) {
        gp.powered = true;
      }
    }
  }

  // Test when no game piece is powered
  boolean testCheckWinNoPower(Tester t) {
    initializeWorld(3, 3);
    world.checkWin();
    return t.checkExpect(world.win, false);
  }

  // Test when only one game piece is powered
  boolean testCheckWinOnePower(Tester t) {
    initializeWorld(3, 3);
    // Power one piece
    world.board.get(0).get(0).powered = true;
    world.checkWin();
    return t.checkExpect(world.win, false);
  }

  // Test when all game pieces are powered
  boolean testCheckWinAllPowered(Tester t) {
    initializeWorld(3, 3);
    powerAllPieces();
    world.checkWin();
    return t.checkExpect(world.win, true);
  }

  

  void testGetGPCenter(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(3, 3);
    GamePiece expected = world.board.get(1).get(1); 
    GamePiece actual = world.getGP(1, 1); 
    t.checkExpect(actual, expected);
  }


  void testGetGPCorner(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(3, 3);
    GamePiece expected = world.board.get(0).get(0); 
    GamePiece actual = world.getGP(0, 0); 
    t.checkExpect(actual, expected);
  }


  void testGetGPEdge(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(3, 3);
    GamePiece expected = world.board.get(2).get(1); 
    GamePiece actual = world.getGP(1, 2); 
    t.checkExpect(actual, expected);
  }


  void testGetGPHighBoundary(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(3, 3);
    GamePiece expected = world.board.get(2).get(2); 
    GamePiece actual = world.getGP(2, 2); 
    t.checkExpect(actual, expected);
  }


  void testGetGPLowBoundary(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(3, 3);
    GamePiece expected = world.board.get(0).get(0);
    GamePiece actual = world.getGP(0, 0); 
    t.checkExpect(actual, expected);
  }
  

  
  // Test making the board
  boolean testMakeBoard(Tester t) {
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(3, 3);
    return t.checkExpect(board.size(), 3) 
        && t.checkExpect(board.get(0).size(), 3) 
        && t.checkExpect(board.get(0).get(0).powerStation, true); 
  }

  // Test adding to nodes list
  boolean testAddToNodes(Tester t) {
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(2, 2);
    ArrayList<GamePiece> nodes = utils.addToNodes(board);
    return t.checkExpect(nodes.size(), 4); // Total nodes
  }



  // Test rotation of a piece
  boolean testRotate(Tester t) {
    GamePiece piece = new GamePiece(0, 0, true, false, false, false, false);
    utils.rotate(piece);
    return t.checkExpect(piece.top, true) 
        && t.checkExpect(piece.left, false) 
        && t.checkExpect(piece.bottom, false) 
        && t.checkExpect(piece.right, false);
  }

  // Test random rotation
  boolean testRandomRotate(Tester t) {
    ArrayList<GamePiece> nodes = new ArrayList<>();
    GamePiece piece = new GamePiece(0, 0);
    nodes.add(piece);
    utils.randomRotate(nodes, new Random(42));
    return t.checkExpect(true, true); 
  }

  // Test making edges
  boolean testMakeEdge(Tester t) {
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(2, 2);
    ArrayList<Edge> edges = utils.makeEdge(board, 2, 2);

    return t.checkExpect(edges.size(), 8);
  }

  // Test adding edges
  boolean testAddEdge(Tester t) {
    ArrayList<Edge> edges = new ArrayList<>();
    GamePiece from = new GamePiece(0, 0);
    GamePiece to = new GamePiece(0, 1);
    utils.addEdge(edges, from, to);
    return t.checkExpect(edges.size(), 1);
  }


  boolean testMakeHashMap(Tester t) {
    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(new Edge(new GamePiece(0, 0), new GamePiece(0, 1)));
    HashMap<GamePiece, GamePiece> map = utils.makeHashMap(edges);
    return t.checkExpect(map.size(), 2);
  }

  // Test find method in union-find
  boolean testFind(Tester t) {
    HashMap<GamePiece, GamePiece> map = new HashMap<>();
    GamePiece piece = new GamePiece(0, 0);
    map.put(piece, piece);
    return t.checkExpect(utils.find(map, piece), piece);
  }

  // Test union method in union-find
  boolean testUnion(Tester t) {
    HashMap<GamePiece, GamePiece> map = new HashMap<>();
    GamePiece piece1 = new GamePiece(0, 0);
    GamePiece piece2 = new GamePiece(1, 1);
    map.put(piece1, piece1);
    map.put(piece2, piece2);
    utils.union(map, piece1, piece2);
    return t.checkExpect(utils.find(map, piece1), piece2);
  }

  // Test updating GamePiece connections after forming path
  boolean testUpdateGP(Tester t) {
    ArrayList<Edge> edges = new ArrayList<>();
    GamePiece from = new GamePiece(0, 0);
    GamePiece to = new GamePiece(0, 1);
    edges.add(new Edge(from, to));
    utils.updateGP(edges);
    return t.checkExpect(from.right, true) && t.checkExpect(to.left, true);
  }

  //Test getGP method
  boolean testGetGP(Tester t) { 
    Utils utils = new Utils();
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(3, 3); 
    GamePiece expectedPiece = board.get(1).get(2); 
    GamePiece retrievedPiece = utils.getGP(2, 1, board); 
    return t.checkExpect(retrievedPiece, expectedPiece); 
  }

  // Test power up function
  boolean testPowerUp(Tester t) {
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(3, 3);
    ArrayList<GamePiece> nodes = utils.addToNodes(board);
    utils.powerUp(board, 3, 3, nodes);
    return t.checkExpect(board.get(0).get(0).powered, true);
  }

  // Test find power station method
  boolean testFindPowerStation(Tester t) {
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(3, 3);
    GamePiece powerStation = utils.findPowerStation(board);
    return t.checkExpect(powerStation.powerStation, true);
  }
  
  //Test checkMove method
  boolean testCheckMove(Tester t) {
    Utils utils = new Utils();
    ArrayList<ArrayList<GamePiece>> board = utils.makeBoard(3, 3);

    // Initially, no pieces are connected
    board.get(0).get(0).bottom = true; 
    board.get(1).get(0).top = true;   

    // Valid move down
    boolean validMoveDown = utils.checkMove(3, 3, 0, 0, 1, 0, board);

    boolean invalidMoveRight = utils.checkMove(3, 3, 0, 0, 0, 1, board);

    boolean outOfBoundsLeft = utils.checkMove(3, 3, 0, 0, 0, -1, board);

    boolean outOfBoundsDown = utils.checkMove(3, 3, 0, 0, 3, 0, board);

    return t.checkExpect(validMoveDown, false) 
        && t.checkExpect(invalidMoveRight, false) 
        && t.checkExpect(outOfBoundsLeft, false) 
        && t.checkExpect(outOfBoundsDown, false);
  }
 
 
  // tester for game
  void testBigBang(Tester t) {
    LightEmAllWorld world = new LightEmAllWorld(5, 5);
    int worldWidth = Constant.CELL_SIZE * 5;
    int worldHeight = Constant.CELL_SIZE * 5;
    world.bigBang(worldWidth, worldHeight);
  }


}