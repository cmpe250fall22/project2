import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Tree {

  Node root;
  boolean avl;
  PrintWriter fw;

  public static void main(String[] args) throws Exception {

    Tree avl = new Tree(true, args[1] + "_avl");
    Tree bst = new Tree(false, args[1] + "_bst");

    File input = new File(args[0]);
    Scanner reader = new Scanner(input);

    String root = reader.nextLine();

    avl.insertNode(root);
    bst.insertNode(root);
    while (reader.hasNext()) {
      String line = reader.nextLine();

      String[] arguments = line.split(" ");

      if (arguments[0].equals("ADDNODE")) {
        avl.insertNode(arguments[1]);
        bst.insertNode(arguments[1]);
      } else if (arguments[0].equals("DELETE")) {
        avl.deleteNode(arguments[1]);
        bst.deleteNode(arguments[1]);
      } else if (arguments[0].equals("SEND")) {
        avl.sendMessage(arguments[1], arguments[2]);
        bst.sendMessage(arguments[1], arguments[2]);
      }
    }

    reader.close();
    avl.fw.close();
    bst.fw.close();

  }

  Tree(boolean avl, String output) throws IOException {
    this.avl = avl;
    this.fw = new PrintWriter(output + ".txt");
  }

  private int height(Node node) {
    return node != null ? node.height : -1;
  }

  public Stack<Node> searchNode(String key) {
    Node node = root;
    Stack<Node> nodes = new Stack<>();

    boolean found = false;
    while (node != null) {
      nodes.push(node);
      if (key.compareTo(node.data) == 0) {
        found = true;
        break;
      } else if (key.compareTo(node.data) < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }
    if (!found)
      throw new IllegalArgumentException("searching a non existing node");

    return nodes;
  }

  private void updateHeight(Node node) {
    int leftChildHeight = height(node.left);
    int rightChildHeight = height(node.right);
    node.height = max(leftChildHeight, rightChildHeight) + 1;
  }

  private int max(int leftChildHeight, int rightChildHeight) {
    if (leftChildHeight > rightChildHeight)
      return leftChildHeight;

    return rightChildHeight;
  }

  private int balanceFactor(Node node) {
    return height(node.right) - height(node.left);
  }

  private Node rotateLeft(Node node) {
    Node rightChild = node.right;

    node.right = rightChild.left;
    rightChild.left = node;

    updateHeight(node);
    updateHeight(rightChild);

    return rightChild;
  }

  private Node rotateRight(Node node) {
    Node leftChild = node.left;

    node.left = leftChild.right;
    leftChild.right = node;

    updateHeight(node);
    updateHeight(leftChild);

    return leftChild;
  }

  private Node rebalance(Node node) throws IOException {
    if (!this.avl)
      return node;
    int balanceFactor = balanceFactor(node);

    // Left-heavy?
    if (balanceFactor < -1) {
      Stack<Node> path = searchNode(node.data);
      if (balanceFactor(node.left) <= 0) { // Case 1
        // Rotate right
        fw.println("Rebalancing: right rotation");
        node = rotateRight(node);
      } else { // Case 2
        // Rotate left-right
        fw.println("Rebalancing: left-right rotation");
        node.left = rotateLeft(node.left);
        node = rotateRight(node);
      }
    }

    // Right-heavy?
    if (balanceFactor > 1) {
      Stack<Node> path = searchNode(node.data);
      if (balanceFactor(node.right) >= 0) { // Case 3
        // Rotate left
        fw.println("Rebalancing: left rotation");
        node = rotateLeft(node);
      } else { // Case 4
        // Rotate right-left
        fw.println("Rebalancing: right-left rotation");
        node.right = rotateRight(node.right);
        node = rotateLeft(node);
      }
    }

    return node;
  }

  Node insertNode(String key) throws IOException {
    Node newNode = new Node(key);

    if (root == null) {
      root = newNode;
      return root;
    }

    boolean inserted = false;

    Node node = root;

    Stack<Node> node_stack = new Stack<>();
    while (!inserted) {
      // Traverse the tree to the left or right depending on the key
      node_stack.push(node);
      fw.println(node.data + ": New node being added with IP:" + key);
      if (key.compareTo(node.data) < 0) {
        if (node.left != null) {
          // Left sub-tree exists --> follow
          node = node.left;
        } else {
          // Left sub-tree does not exist --> insert new node as left child
          node.left = newNode;
          inserted = true;
        }
      } else if (key.compareTo(node.data) > 0) {
        if (node.right != null) {
          // Right sub-tree exists --> follow
          node = node.right;
        } else {
          // Right sub-tree does not exist --> insert new node as right child
          node.right = newNode;
          inserted = true;
        }
      } else {
        throw new IllegalArgumentException("Already contains a node with key " + key);
      }
    }

    while (!node_stack.empty()) {
      node = node_stack.pop();
      updateHeight(node);
      Node res = rebalance(node);
      if (res != node) {
        if (node_stack.empty()) {
          this.root = res;
          break;
        }
        Node parent = node_stack.pop();
        if (parent.left == node) {
          parent.left = res;
        } else
          parent.right = res;
        node_stack.push(parent);
      }
    }
    return newNode;
  }

  private void deleteNodeWithZeroOrOneChild(String key, Node node, Node parent) {
    Node singleChild = node.left != null ? node.left : node.right;

    if (node == root) {
      root = singleChild;
    } else if (key.compareTo(parent.data) < 0) {
      parent.left = singleChild;
    } else {
      parent.right = singleChild;
    }
  }

  Node deleteNode(String key) throws IOException {
    Stack<Node> nodes = searchNode(key);

    Node node = nodes.pop();
    Node node_parent = nodes.pop();
    nodes.push(node_parent);
    nodes.push(node);

    if (node.left == null || node.right == null) {
      fw.println(node_parent.data + ": Leaf Node Deleted: " + key);
      deleteNodeWithZeroOrOneChild(key, node, node_parent);
    } else {

      Node max_left_sub = node.left;
      Node max_right_sub_par = node;
      nodes.push(max_left_sub);
      while (max_left_sub.right != null) {
        max_left_sub = max_left_sub.right;
        max_right_sub_par = max_right_sub_par.right;
        nodes.push(max_left_sub);
      }
      node.data = max_left_sub.data;
      if (max_right_sub_par == node) {
        node.left = null;
      } else {

        max_right_sub_par.right = null;
      }
      nodes.pop();
      if (max_right_sub_par.left == null && max_right_sub_par.right == null)
        max_right_sub_par.height = 0;
      fw.println("Non Leaf Node Deleted; removed: " + key + " replaced: " + node.data);
    }

    Node node_iter = null;
    while (!nodes.empty()) {
      node_iter = nodes.pop();
      updateHeight(node_iter);
      Node res = rebalance(node);
      if (res != node) {
        if (nodes.empty()) {
          root = res;
          break;
        }
        Node parent = nodes.pop();
        if (parent.left == node) {
          parent.left = res;
        } else
          parent.right = res;
        nodes.push(parent);
      }
    }
    return node;
  }

  void sendMessage(String sender, String receiver) throws IOException {

    Stack<Node> sender_stack = searchNode(sender);
    LinkedList<Node> sender_path = (new LinkedList<Node>(sender_stack));
    LinkedList<Node> receiver_path = (new LinkedList<Node>(searchNode(receiver)));

    int diverge_index = 0;

    boolean converged = true;


    while (converged) {
      if (!(diverge_index < receiver_path.size() && diverge_index < sender_path.size())) {
        converged = false;
        break;
      } else if (sender_path.get(diverge_index) == receiver_path.get(diverge_index)) {
        diverge_index += 1;
      } else {
        converged = false;
      }
    }
    if (diverge_index > 0)
      diverge_index -= 1;

    Node temp = sender_stack.pop();
    fw.println(temp.data + ": Sending message to: " + receiver);

    while (true) {
      if (temp == sender_path.get(diverge_index))
        break;

      String previous = temp.data;

      temp = sender_stack.pop();

      fw.println(temp.data + ": Transmission from: " + previous + " receiver: " + receiver + " sender:" + sender);

    }

    for (int i = diverge_index + 1; i < receiver_path.size() - 1; i++) {
      String previous = temp.data;
      temp = receiver_path.get(i);

      fw.println(temp.data + ": Transmission from: " + previous + " receiver: " + receiver + " sender:" + sender);
    }

    temp = receiver_path.get(receiver_path.size() - 1);
    fw.println(temp.data + ": Received message from: " + sender);

  }
}