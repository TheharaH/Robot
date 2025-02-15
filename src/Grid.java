import java.util.Scanner;
import java.util.Random;

public class Grid {
    private int[][] gridcell;
    private boolean[][] adjacencyMatrix; // Adjacency matrix represents connections between cells
    private static final int OBSTACLE = 1; // Constant representing an obstacle
    private Robot robot;
    private Goal goal;

    public Grid(int rows, int columns, int initialRobotRow, int initialRobotColumn, int goalRow, int goalColumn) {
        gridcell = new int[rows][columns];
        adjacencyMatrix = new boolean[rows * columns][rows * columns];// `true` indicates a connection between vertices,`false` indicates no connection
        robot = new Robot(initialRobotRow - 1, initialRobotColumn - 1, 0); //-1 since indexing start from 0
        goal = new Goal(goalRow - 1, goalColumn - 1);
        buildAdjacencyMatrix(rows, columns);
    }

    private void buildAdjacencyMatrix(int rows, int columns) {
        int totalCells = rows * columns; //// Calculate the total cells
        for (int i = 0; i < totalCells; i++) {
            int row = i / columns; // Calculate the row index
            int col = i % columns; // Calculate the column index
            if (row > 0)
                adjacencyMatrix[i][i - columns] = true; //connects the current cell and the above cell
            if (row < rows - 1)
                adjacencyMatrix[i][i + columns] = true; //connects the current cell and the below cell
            if (col > 0)
                adjacencyMatrix[i][i - 1] = true; //connects the current cell and the left cell
            if (col < columns - 1)
                adjacencyMatrix[i][i + 1] = true; //connects the current cell and the right cell
        }
    }

    public void printGrid_Structure() {
        System.out.println("Grid Structure:");
        for (int row = 0; row < gridcell.length; row++) { //looping through each row
            for (int col = 0; col < gridcell[0].length; col++) { //looping through each column
                System.out.print("| |   ");
            }
            System.out.println();
        }
    }
    public void printGrid_RobotGoalObstacles() {
        System.out.println("Grid with Robot, Goal positions and obstacle placements:");
        for (int row = 0; row < gridcell.length; row++) {
            for (int col = 0; col < gridcell[0].length; col++) {
                if (row == robot.getCurrentRow() && col == robot.getCurrentCol()) { //checks if it's the robot position
                    System.out.print("|R|   ");
                } else if (row == goal.getRow() && col == goal.getCol()) { //checks if it's the goal position
                    System.out.print("|G|   ");
                } else if (gridcell[row][col] == OBSTACLE) { //checks if it's an obstacle
                    System.out.print("|X|   ");
                } else {
                    System.out.print("| |   ");
                }
            }
            System.out.println();
        }
    }

    public void Obstacle_Placement(double obstacleProbability) {
        Random randomObstacle = new Random();

        for (int row = 0; row < gridcell.length; row++) {
            for (int col = 0; col < gridcell[0].length; col++) {
                // checks if the randomly generated probability is less than the obstacle probability
                if (randomObstacle.nextDouble() < obstacleProbability) {
                    gridcell[row][col] = OBSTACLE;
                }
            }
        }
    }
    class ListNode<T>{
        T data; //holds the data stored
        ListNode<T> next; //holds the reference to the next node
        public ListNode(T data) {
            this.data = data;
            this.next = null; //Initially next node reference is set to null
        }
    }
    class CustomLinkedList<T> {
        private ListNode<T> head;
        public CustomLinkedList() {
            this.head = null;
        }
        public boolean isEmpty() { //checks if the linked list is empty by checking whether the head is null
            return head == null;
        }
        public void add(T data) { //add a new node with the given data to the end of the linked list
            ListNode<T> newNode = new ListNode<>(data);
            if (head == null) {
                head = newNode; // If the list is empty, set the new node as the head
            } else { // Traverse the list to find the last node and adds the new node
                ListNode<T> current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newNode;
            }
        }
        public boolean contains_Data(T data) { //check if the linked list contains a node with the given data
            ListNode<T> current = head;
            while (current != null) {
                if (current.data.equals(data)) {
                    return true; // Returns true if the data is found in any node
                }
                current = current.next;
            }
            return false; // Returns false if the data is not found in any node
        }public ListNode<T> find_Node(T data) { //find the node containing the given data
            ListNode<T> current = head;
            while (current != null) {
                if (current.data.equals(data)) {
                    return current; // Returns the node containing the data if found
                }
                current = current.next;
            }
            return null; // Returns null if the data is not found in any node
        }
        public void remove(T data) { //remove the node containing the given data from the linked list
            if (head == null) { // Check if the list is empty
                return;
            }
            // Check if the head node contains the data to be removed,
            // if so removes the head node by setting the next node to head
            if (head.data.equals(data)) {
                head = head.next;
                return;
            }
            ListNode<T> current = head;
            while (current.next != null) {
                if (current.next.data.equals(data)) {
                    current.next = current.next.next; // Remove the current.next node by setting the current.next.next node to current.next
                    return;
                }
                current = current.next;
            }
        }
        public ListNode<T> getHead() { // Returns the head node of the linked list
            return head;
        }
        public ListNode<T> getTail() { //get the last node (tail) of the linked list
            if (head == null) {
                return null; // Returns null if the list is empty
            }
            ListNode<T> current = head;  // Traverse the list to find the last node
            while (current.next != null) {
                current = current.next;
            }
            return current; // Returns the last node (tail) of the linked list
        }

    }
    public CustomLinkedList<GridPoint> Path_plan() {
        int[][] directions = {
                {-1, 0}, {0, 1}, {1, 0}, {0, -1},};
        // Initialize open and closed lists for A* algorithm
        CustomLinkedList<Node> openList = new CustomLinkedList<>();
        CustomLinkedList<Node> closedList = new CustomLinkedList<>();

        // Create a start node representing the robot's current position
        Node startNode = new Node(robot.getCurrentRow(), robot.getCurrentCol(), null, 0, hDistance(robot.getCurrentRow(), robot.getCurrentCol()));
        openList.add(startNode); // Add the start node to the open list

        while (!openList.isEmpty()) { // Loop until the open list is empty
            Node current = getLowestF_ValueNode(openList); // Get the node with the lowest f-value from the open list
            openList.remove(current);
            closedList.add(current);
            if (current.row == goal.getRow() && current.col == goal.getCol()) {   // Check if current node is the goal node
                return reconstructPath(current);
            }

            for (int[] dir : directions) { // Explore neighboring nodes (up, right, down, left) of the current node
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                if (isValidLocation(newRow, newCol) && gridcell[newRow][newCol] != OBSTACLE) { // Check if the new location is valid and not an obstacle
                    // Calculate the new g, h, and f values for the neighboring node
                    int gValue = current.g + 1;
                    int hValue = hDistance(newRow, newCol);
                    int fValue = gValue + hValue;
                    // Create a new node representing the location
                    Node UpDownLeftRight_Nodes = new Node(newRow, newCol, current, gValue, hValue);
                    if (!closedList.contains_Data(UpDownLeftRight_Nodes)) { // Check if the neighbor node is not in the closed list
                        if (!openList.contains_Data(UpDownLeftRight_Nodes)) { // Check if the neighbor node is not in the open list
                            openList.add(UpDownLeftRight_Nodes);
                        } else{ // Update the node's f-value if it's already in the open list
                            ListNode<Node> openNode = openList.find_Node(UpDownLeftRight_Nodes);
                            if (fValue < openNode.data.f) {
                                openList.remove(openNode.data);
                                openList.add(UpDownLeftRight_Nodes);
                            }
                        }
                    }
                }
            }
        }
        return new CustomLinkedList<>();
    }
    private int hDistance(int row, int col) { // calculates the Manhattan distance heuristic from the given position to the goal position
        return Math.abs(goal.getRow() - row) + Math.abs(goal.getCol() - col);}
    private boolean isValidLocation(int row, int col) { // Check if the row and column positions are within the bounds
        return row >= 0 && row < gridcell.length && col >= 0 && col < gridcell[0].length;
    }
    private Node getLowestF_ValueNode(CustomLinkedList<Node> openList) {
        ListNode<Node> current = openList.getHead(); // Initialize current node to the head of the open list
        Node lowestFNode = current.data;
        while (current != null) { // Iterate through the open list to find the node with the lowest f-value
            if (current.data.f < lowestFNode.f) {
                lowestFNode = current.data;
            }
            current = current.next;
        }
        return lowestFNode;
    }
    private CustomLinkedList<GridPoint> reconstructPath(Node goalNode) {
        CustomLinkedList<GridPoint> path = new CustomLinkedList<>(); // Initialize a linked list to store the path
        Node current = goalNode;  // Start from the goal node and traverse back to the start node
        while (current != null) {
            path.add(new GridPoint(current.row, current.col)); // Add each node's position to the path
            current = current.parent;
        }
        return path;
    }
    public static void main(String[] args) {
        System.out.println("WELCOME TO THE GAME, " +
                "YOU CAN CREATE YOUR OWN GRID BY ENTERING THE NUMBER OF ROWS AND COLUMNS AS YOU PREFER"
        );
        Scanner scanner = new Scanner(System.in);

        int rows, cols;
        do {
            rows = getInt_Input(scanner, "Enter the number of rows, between 4 and 50: ");
            cols = getInt_Input(scanner, "Enter the number of columns, between 4 and 50: ");
            if (rows < 4 || rows > 50 || cols < 4 || cols > 50) {
                System.out.println("Rows and columns must be between 4 and 50.");
            }
        } while (rows < 4 || rows > 50 || cols < 4 || cols > 50);

        Grid grid = new Grid(rows, cols, -1, -1, -1, -1); // Initializing with dummy values for robot and goal positions

        grid.printGrid_Structure(); // Print initial grid with underscores
        System.out.println("YOU CAN LOCATE THE ROBOT IN A PREFERRED CELL BY ENTERING THE STARTING ROWS AND COLUMN " );
        System.out.println("YOU CAN DECIDE THE EBD GOAL POSITION ALSO ENTER THE GOAL ROW AND GOAL COLUMN");
        int startRow = getInt_Input(scanner, "Enter the starting row for the robot: ");
        while (startRow > rows) {
            System.out.println("Starting row must be less than or equal to the total number of rows.");
            startRow = getInt_Input(scanner, "Enter the starting row for the robot: ");
        }

        int startCol = getInt_Input(scanner, "Enter the starting column for the robot: ");
        while (startCol > cols) {
            System.out.println("Starting column must be less than or equal to the total number of columns.");
            startCol = getInt_Input(scanner, "Enter the starting column for the robot: ");
        }

        int goalRow, goalCol;
        boolean sameAsRobotOrAdjacent;
        Grid gridWithRobotAndGoal;
        do {
            goalRow = getInt_Input(scanner, "Enter the goal row: ");
            while (goalRow > rows) {
                System.out.println("Goal row must be less than or equal to the total number of rows.");
                goalRow = getInt_Input(scanner, "Enter the goal row : ");
            }

            goalCol = getInt_Input(scanner, "Enter the goal column: ");
            while (goalCol > cols) {
                System.out.println("Goal column must be less than or equal to the total number of columns.");
                goalCol = getInt_Input(scanner, "Enter the goal column: ");
            }

            sameAsRobotOrAdjacent = (goalRow == startRow && goalCol == startCol);

            // Check if goal point is adjacent to start point
            if (!sameAsRobotOrAdjacent) {
                for (int i = startRow - 1; i <= startRow + 1; i++) {
                    for (int j = startCol - 1; j <= startCol + 1; j++) {
                        if (goalRow == i && goalCol == j) {
                            sameAsRobotOrAdjacent = true;
                            break;
                        }
                    }
                }
            }
            if (sameAsRobotOrAdjacent) {
                System.out.println("Goal cell cannot be the same as or adjacent to the robot cell. Please enter different coordinates.");
            }

        } while (sameAsRobotOrAdjacent);

        // Creating grid with robot and goal
        gridWithRobotAndGoal = new Grid(rows, cols, startRow, startCol, goalRow, goalCol);
        gridWithRobotAndGoal.Obstacle_Placement(0.2);
        gridWithRobotAndGoal.printGrid_RobotGoalObstacles();

        CustomLinkedList<GridPoint> path = gridWithRobotAndGoal.Path_plan(); // invoking the Path plan method
        if (!path.isEmpty()) {
            System.out.println("Path found");
            gridWithRobotAndGoal.Print_Path(path);
        } else {
            System.out.println("No path found.");
        }

        scanner.close();
    }
    private static int getInt_Input(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                if (input > 0) {
                    return input; // Return input if it's a positive integer
                } else {
                    System.out.println("Invalid input. Please enter an integer above 0 but between 4 and 50.");
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
        }
    }
    private void Print_Path(CustomLinkedList<GridPoint> path) {
        System.out.println("Path taken by the robot:");
        char[][] gridWithDirections = new char[gridcell.length][gridcell[0].length];  // Create a copy of the gridcell array to store directional symbols
        for (int i = 0; i < gridWithDirections.length; i++) { // Initialize gridWithDirections with empty spaces
            for (int j = 0; j < gridWithDirections[0].length; j++) {
                gridWithDirections[i][j] = ' ';
            }
        }
        GridPoint robotPoint = path.getHead().data; // Get the head node of the path, which represents the robot point
        gridWithDirections[robotPoint.getRow()][robotPoint.getCol()] = 'R';
        GridPoint goalPoint = path.getTail().data;  // Get the tail node of the path, which represents the goal point
        gridWithDirections[goalPoint.getRow()][goalPoint.getCol()] = 'G';
        ListNode<GridPoint> current = path.getHead();
        GridPoint prevPoint = new GridPoint(robot.getCurrentRow(), robot.getCurrentCol()); // Initialize prevPoint with the robot's initial position

        while (current != null) {
            GridPoint currentPoint = current.data;
            int row = currentPoint.getRow(); // Get the row of the current GridPoint
            int col = currentPoint.getCol();// Get the column of the current GridPoint
            if (currentPoint.getRow() == robot.getCurrentRow() && currentPoint.getCol() == robot.getCurrentCol()) {
                // assigning the directional symbol for the starting position
                if (row == prevPoint.getRow() - 1) { // Looking down
                    gridWithDirections[row][col] = 'v';
                } else if (row == prevPoint.getRow() + 1) { // Looking up
                    gridWithDirections[row][col] = '^';
                } else if (col == prevPoint.getCol() - 1) { // Looking right
                    gridWithDirections[row][col] = '>';
                } else if (col == prevPoint.getCol() + 1) { // Looking left
                    gridWithDirections[row][col] = '<';
                } else {
                    gridWithDirections[row][col] = 'R';
                }
            } else if (row == prevPoint.getRow() - 1 && col == prevPoint.getCol()) {
                gridWithDirections[row][col] = 'v'; //up
            } else if (row == prevPoint.getRow() + 1 && col == prevPoint.getCol()) {
                gridWithDirections[row][col] = '^'; //down
            } else if (row == prevPoint.getRow() && col == prevPoint.getCol() - 1) {
                gridWithDirections[row][col] = '>'; //left
            } else if (row == prevPoint.getRow() && col == prevPoint.getCol() + 1) {
                gridWithDirections[row][col] = '<'; //right
            } else {
                gridWithDirections[row][col] = 'G'; //goal position
            }
            prevPoint = currentPoint; // Update prevPoint
            current = current.next;
        }


        for (int i = 0; i < gridcell.length; i++) {
            for (int j = 0; j < gridcell[0].length; j++) {
                char directionSymbol = gridWithDirections[i][j];
                if (directionSymbol != ' ') {
                    System.out.print("|" + directionSymbol + "|   "); //prints the directional symbol
                } else if (gridcell[i][j] == OBSTACLE) {
                    System.out.print("|X|   "); // Print 'X' for obstacles
                } else {
                    System.out.print("| |   ");
                }
            }
            System.out.println();
        }
        System.out.println("Cell coordinates:");// Print the cell coordinates along with orientation
        current = path.getHead();
        while (current != null) {
            GridPoint point = current.data; // Get the GridPoint representing the current cell in the path
            int row = point.getRow();
            int col = point.getCol();
            char directionSymbol = gridWithDirections[row][col];  // Get the direction symbol for the current cell from the gridWithDirections array
            String direction = "";  // Initialize a string to store the direction
            switch (directionSymbol) {
                case '^':
                    direction = "North"; //facing North
                    break;
                case 'v':
                    direction = "South"; //facing South
                    break;
                case '>':
                    direction = "East"; //facing East
                    break;
                case '<':
                    direction = "West"; //facing West
                    break;
                default:
                    direction = "End";
                    break;
            }
            System.out.println("Orientation of the robot in cell (" + (row + 1) + ", " + (col + 1) + ") is " + direction);
            current = current.next;
        }

    }
    class Node {
        int row; // Row position of the node
        int col; // Column position of the node
        Node parent; // Parent node in the path
        int g; // Cost of reaching this node from the start node
        int h; // Heuristic cost from this node to the goal node
        int f; // Total cost f = g + h

        Node(int row, int col, Node parent, int g, int h) {
            this.row = row;
            this.col = col;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;

        }

        public boolean equals(Object obj) {
            if (this == obj) return true; // Check if the compared object is the same instance as this node
            if (obj == null || getClass() != obj.getClass()) return false; // Check if the compared object is null or is not an instance of Node class
            Node node = (Node) obj; // Cast the compared object to Node type
            return row == node.row && col == node.col; // Check if the row and column positions of this node are equal to the row and column positions of the compared node
        }
    }

    class Goal {
        private int row;
        private int col;

        public Goal(int row, int col) { // Constructor to initialize the goal position
            this.row = row;
            this.col = col;
        }

        public int getRow() { //retrieve the row position of the goal
            return row;
        }

        public int getCol() { //retrieve the column position of the goal
            return col;
        }
    }

    class Robot {
        private int currentRow;
        private int currentCol;

        public Robot(int initialRow, int initialCol, int initialOrientation) { // Constructor to initialize the robot's initial position
            this.currentRow = initialRow;
            this.currentCol = initialCol;
        }

        public int getCurrentRow() { //retrieve the row position of the robot
            return currentRow;
        }

        public int getCurrentCol() { //retrieve the column position of the robot
            return currentCol;
        }
    }
    class GridPoint {
        private int row;
        private int col;
        public GridPoint(int row, int col) { // Constructor to initialize the grid point with its row and column positions
            this.row = row;
            this.col = col;
        }
        public int getRow() { //retrieve the row position of the grid point
            return row;
        }
        public int getCol() { //retrieve the column position of the grid point
            return col;
        }
    }
}


